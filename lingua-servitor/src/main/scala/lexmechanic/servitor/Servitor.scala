package lexmechanic.servitor

import io.circe.parser.decode
import io.circe.syntax.*
import lexmechanic.noosphere.*
import lexmechanic.servitor.desktop.Pictcaptor
import lexmechanic.servitor.http.{Request, Resources, Response}
import lexmechanic.servitor.image.Png
import lexmechanic.servitor.runtime.{Log, ServoSkull, failureOf, reason}
import lexmechanic.servitor.translation.Xenolinguist

import java.nio.file.Files
import scala.util.{Try, Using}

final class Servitor(initial: Config):
  private val translationShot = Config.cacheDir.resolve("translation.png")
  private val calibrationShot = Config.cacheDir.resolve("calibration.png")
  private val trustedHosts = Set(s"127.0.0.1:${initial.port}", s"localhost:${initial.port}")

  private var config = initial
  private var history = List.empty[Entry]
  // Not derived from history: ids must survive /clear, as the UI renders each id only once.
  private var nextEntryId = 1
  private var busy = false
  private var error = Option.empty[String]
  private var learning = false
  private var hotkeyUnavailable = Option.empty[HotkeyProblem]

  private def state: State = synchronized {
    State(
      busy = busy,
      error = error,
      region = config.region,
      game = config.game,
      language = config.language,
      interfaceLanguage = config.interfaceLanguage,
      instructions = config.activeInstructions,
      hotkey = HotkeyState(config.hotkey, learning, hotkeyUnavailable),
      history = history,
      provider = config.provider,
      model = config.model,
      apiKey = config.storedKey,
      apiKeyFromEnvironment = config.storedKey.isEmpty && config.environmentKey.isDefined,
      captain = config.captain,
      port = config.port,
      restartRequired = config.port != initial.port,
      captureCommand = config.captureCommand,
      defaultCaptureCommand = Config.defaultCaptureCommand,
      openCommand = config.openCommand,
      defaultOpenCommand = Config.defaultOpenCommand
    )
  }

  def hotkeyProblem(problem: Option[HotkeyProblem]): Unit = synchronized {
    hotkeyUnavailable = problem
    if problem.isDefined then learning = false
  }

  def onKey(code: Int): Unit =
    val isHotkey = synchronized {
      if learning then
        if code == EvdevKeys.KeyEsc then learning = false
        else if EvdevKeys.bindable(code) then
          learning = false
          error = failureOf(updateConfig(_.copy(hotkey = Some(code))))
        false
      else config.hotkey.contains(code)
    }
    if isHotkey && !trigger() then Log.info("Hotkey ignored: translation already in progress")

  private def trigger(): Boolean =
    val started = synchronized {
      val idle = !busy
      if idle then
        busy = true
        error = None
      idle
    }
    if started then
      ServoSkull.run("translate") {
        val failure = failureOf(translateScreen())
        failure.foreach(e => Log.info(s"Error: $e"))
        synchronized {
          error = failure
          busy = false
        }
      }
    started

  private def translateScreen(): Unit =
    val current = synchronized(config)
    val mandate = current.mandate
    val dialogue = Using.resource(current.voxLink.fold(e => throw RuntimeException(e), identity)) { link =>
      val screenshot = Pictcaptor.fullScreen(current.captureCommand, translationShot)
      Xenolinguist(link).translate(Png.read(screenshot, current.region), mandate)
    }
    synchronized {
      history = (Entry(nextEntryId, dialogue, mandate.language) :: history).take(50)
      nextEntryId += 1
    }

  private def startLearning(): Boolean = synchronized {
    learning = hotkeyUnavailable.isEmpty
    learning
  }

  private def updateConfig(change: Config => Config): Unit = synchronized {
    val updated = change(config)
    Config.save(updated)
    config = updated
  }

  def route(request: Request): Response =
    // Host check stops DNS rebinding; cross-site forms cannot set the custom header.
    if !request.headers.get("host").exists(trustedHosts) then Response.text(403, "Forbidden host")
    else if request.method == "POST" && !request.headers.contains(Api.CsrfHeader.toLowerCase) then
      Response.text(403, "Missing header")
    else endpoint(request)

  private def currentState(status: Int = 200) = Response.json(status, state.asJson.noSpaces)

  private def act(action: => Option[(Int, String)]): Response =
    val failure = Try(action).fold(e => Some(500 -> e.reason), identity)
    synchronized { error = failure.map(_._2) }
    currentState(failure.fold(200)(_._1))

  private def nonBlank(text: String): Option[String] = Option(text.trim).filter(_.nonEmpty)

  private def endpoint(request: Request): Response = (request.method, request.path) match
    case ("GET", "/state")                                       => currentState()
    case ("GET", "/screenshot") if Files.exists(calibrationShot) =>
      Response(200, "image/png", Files.readAllBytes(calibrationShot))
    case ("POST", "/capture")   => currentState(if trigger() then 200 else 409)
    case ("POST", "/calibrate") =>
      act {
        Pictcaptor.fullScreen(synchronized(config).captureCommand, calibrationShot)
        None
      }
    case ("POST", "/region") =>
      act {
        decode[Region](request.bodyText) match
          case Right(region) if region.width > 0 && region.height > 0 =>
            updateConfig(_.copy(region = Some(region)))
            None
          case _ => Some(400 -> "Invalid region")
      }
    case ("POST", "/settings") =>
      act {
        decode[Settings](request.bodyText) match
          case Right(settings) if settings.captureCommand.exists(!_.exists(_.contains("{file}"))) =>
            Some(400 -> "The screenshot command must contain {file}")
          case Right(settings) if settings.port.exists(p => p < 1 || p > 65535) =>
            Some(400 -> "The port must be between 1 and 65535")
          case Right(settings) =>
            updateConfig(c =>
              val provider = settings.provider.getOrElse(c.provider)
              // A model name belongs to one provider.
              val keptModel = if provider == c.provider then c.model else None
              c.copy(
                provider = provider,
                model = settings.model.map(nonBlank).getOrElse(keptModel),
                apiKeys = settings.apiKey.map(nonBlank).fold(c.apiKeys) {
                  case Some(key) => c.apiKeys.updated(provider.keyVariable, key)
                  case None      => c.apiKeys - provider.keyVariable
                },
                game = settings.game.fold(c.game)(_.trim),
                language = settings.language.orElse(c.language),
                interfaceLanguage = settings.interfaceLanguage.orElse(c.interfaceLanguage),
                captain = settings.captain.getOrElse(c.captain),
                // Text matching the default is stored as unset, so later changes to the default still reach it.
                instructions =
                  settings.instructions.fold(c.instructions)(i => Option(i.trim).filter(_ != Instructions.default)),
                port = settings.port.getOrElse(c.port),
                captureCommand = settings.captureCommand.getOrElse(c.captureCommand),
                openCommand = settings.openCommand.getOrElse(c.openCommand)
              )
            )
            None
          case Left(e) => Some(400 -> e.reason)
      }
    case ("POST", "/dismiss") => act(None)
    case ("POST", "/clear")   =>
      act {
        synchronized { history = Nil }
        None
      }
    case ("POST", "/hotkey/learn")  => currentState(if startLearning() then 200 else 409)
    case ("POST", "/hotkey/cancel") =>
      synchronized { learning = false }
      currentState()
    case ("GET", path) if Resources.web.contains(path) => Resources.web(path)
    case _                                             => Response.text(404, "Not found")
