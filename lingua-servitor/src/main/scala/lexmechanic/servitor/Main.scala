package lexmechanic.servitor

import lexmechanic.noosphere.*
import lexmechanic.servitor.desktop.ActivationRune
import lexmechanic.servitor.http.VoxCaster
import lexmechanic.servitor.image.Png
import lexmechanic.servitor.runtime.{Log, failureOf, reason}
import lexmechanic.servitor.translation.Xenolinguist
import sttp.client4.*

import java.nio.file.Paths

private val usage =
  s"""Usage:
     |  lexmechanic                       start the daemon (hotkey listener + UI server)
     |  lexmechanic trigger               ask the running daemon to translate the screen now
     |  lexmechanic translate <file.png> [--lang <code>]
     |                                    translate a single screenshot and print the result;
     |                                    --lang (e.g. pl, de, szl) overrides the UI's language
     |
     |Settings, including the AI provider and its API key, can be changed in the UI and live in ${Config.file}""".stripMargin

@main def main(args: String*): Unit =
  failureOf {
    args match
      case Seq()                                  => run()
      case Seq("trigger")                         => trigger()
      case Seq("translate", path)                 => translate(path, None)
      case Seq("translate", path, "--lang", code) => translate(path, Some(code))
      case Seq("translate", "--lang", code, path) => translate(path, Some(code))
      case _                                      => fail(usage, exitCode = 2)
  }.foreach(fail(_))

private def translate(path: String, languageTag: Option[String]): Unit =
  val language = languageTag.map(tag => Language.forTag(tag).getOrElse(fail(s"Unknown language: $tag")))
  val config = Config.load()
  val mandate = config.copy(language = language.orElse(config.language)).mandate
  val xenolinguist = Xenolinguist(config.voxLink.fold(fail(_), identity))
  println(render(xenolinguist.translate(Png.read(Paths.get(path), None), mandate)))

private def run(): Unit =
  val config = Config.load()
  val servitor = Servitor(config)
  Log.info(s"Config: ${Config.file}")
  ActivationRune.listen(servitor.onKey, servitor.hotkeyProblem)
  VoxCaster.serve(config.port, openUi(config.openCommand))(servitor.route)

private def openUi(command: List[String])(url: String): Unit =
  if command.nonEmpty then
    failureOf(ProcessBuilder(command.map(_.replace("{url}", url))*).inheritIO().start())
      .foreach(e => Log.info(s"Could not open the UI: $e"))

private def trigger(): Unit =
  val port = Config.load().port
  val backend = DefaultSyncBackend()
  val status =
    try basicRequest.post(uri"http://127.0.0.1:$port/capture").header(Api.CsrfHeader, "1").send(backend).code.code
    catch case e: Exception => fail(s"Daemon not running on port $port (${e.reason})")
    finally backend.close()
  status match
    case 200   => println("Translation started")
    case 409   => fail("Translation already in progress")
    case other => fail(s"Daemon refused the request (HTTP $other)")

private def fail(message: String, exitCode: Int = 1): Nothing =
  System.err.println(message)
  sys.exit(exitCode)

private def render(dialogue: Dialogue): String =
  val text = List(dialogue.text.translation, s"  (${dialogue.text.original})")
  val answers = dialogue.answers.flatMap(a => List("", s"  ${a.translation}", s"    (${a.original})"))
  (dialogue.speaker.map(_ + ":").toList ++ text ++ answers).mkString("\n")
