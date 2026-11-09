package lexmechanic.dataslate

import com.raquo.airstream.web.WebStorageVar
import com.raquo.laminar.api.L.*
import io.circe.parser.decode
import io.circe.syntax.*
import lexmechanic.dataslate.i18n.I18n.t
import lexmechanic.dataslate.i18n.{BrowserLocale, I18n, Messages}
import lexmechanic.noosphere.*
import org.scalajs.dom

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

object App:
  private val server = Var(Option.empty[State])
  private val pending = Var(0)
  private val settingsOpen = Var(false)
  private val postAnswered = EventBus[Unit]()
  private val showOriginal = WebStorageVar.localStorage("lexmechanic.original", syncOwner = None).bool(default = true)

  private[dataslate] val state: Signal[Option[State]] = server.signal.distinct

  private val status: Signal[ServitorStatus] = state.combineWith(pending.signal).map {
    case (None, _)                                     => ServitorStatus.Offline
    case (Some(s), requests) if s.busy || requests > 0 => ServitorStatus.Busy
    case (Some(s), _) if s.error.isDefined             => ServitorStatus.Failed
    case _                                             => ServitorStatus.Ready
  }

  private[dataslate] val offline = state.map(_.isEmpty)

  private[dataslate] val actionsDisabled = status.map(s => s == ServitorStatus.Busy || s == ServitorStatus.Offline)

  private val polling: EventStream[Option[State]] =
    // Restarting on each POST answer drops an in-flight poll, which may carry the state from before the POST.
    EventStream.merge(EventStream.periodic(600), postAnswered.events).flatMapSwitch { _ =>
      FetchStream.get("/state").map(decode[State](_).toOption).recover { case _ => Some(None) }
    }

  def post(path: String, body: String = ""): Future[Boolean] =
    val init = new dom.RequestInit {}
    init.method = dom.HttpMethod.POST
    init.headers = js.Dictionary(Api.CsrfHeader -> "1")
    init.body = body
    pending.update(_ + 1)
    val result =
      for
        response <- dom.fetch(path, init).toFuture
        text <- response.text().toFuture
      yield
        decode[State](text).foreach(s => server.set(Some(s)))
        postAnswered.emit(())
        response.ok
    result.onComplete(_ => pending.update(_ - 1))
    result

  private def saveBrowserDefaults(state: State): Unit =
    saveSettings(
      Settings(
        language = Option.when(state.language.isEmpty)(BrowserLocale.language),
        interfaceLanguage = Option.when(state.interfaceLanguage.isEmpty)(I18n.language.now())
      )
    )

  private[dataslate] def saveSettings(settings: Settings, saved: Observer[Unit] = Observer.empty): Future[Boolean] =
    val result = post("/settings", settings.asJson.noSpaces)
    result.foreach(ok => if ok then saved.onNext(()))
    result

  // Polls must not clobber unsaved typing, nor a failed poll blank it; a save re-sets it even when the
  // server's normalized value equals the previous one.
  private[dataslate] def savedText(read: State => String, settings: String => Settings, saved: Observer[Unit]) =
    val resync = EventBus[String]()
    Seq(
      value <-- EventStream.merge(state.changes.collectSome.map(read).distinct, resync.events),
      onChange.mapToValue --> { text =>
        saveSettings(settings(text), saved).foreach(_ => server.now().foreach(s => resync.emit(read(s))))
      }
    )

  private[dataslate] def openSettings(): Unit = settingsOpen.set(true)

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(dom.document.getElementById("app"), view)

  private def view = div(
    cls := "shell",
    cls("show-original") <-- showOriginal.signal,
    polling --> server.writer,
    state.changes.collectSome.map(_.interfaceLanguage).collectSome.distinct --> I18n.language.writer,
    state.changes.collectSome
      .filter(s => s.language.isEmpty || s.interfaceLanguage.isEmpty)
      .take(1) --> saveBrowserDefaults,
    I18n.messages --> { m => dom.document.documentElement.setAttribute("lang", m.lang) },
    documentEvents(_.onKeyDown.filter(_.key == "Escape")) --> { _ =>
      if Calibration.isOpen then Calibration.close() else settingsOpen.set(false)
    },
    topBar,
    banner,
    Reading.view.amend(hidden <-- settingsOpen.signal),
    SettingsPage.view.amend(hidden <-- settingsOpen.signal.map(!_)),
    Calibration.overlay
  )

  private def topBar = headerTag(
    cls := "topbar",
    div(
      cls := "title",
      span(
        hidden <-- settingsOpen.signal,
        img(src := "/emblem.svg", alt := ""),
        span(cls := "wordmark", "Lexmechanic")
      ),
      span(
        hidden <-- settingsOpen.signal.map(!_),
        button(cls := "back", text <-- t(_.back), onClick --> { _ => settingsOpen.set(false) }),
        span(text <-- t(_.settings))
      )
    ),
    div(
      cls := "status",
      dataAttr("state") <-- status.map(_.toString.toLowerCase),
      span(cls := "dot"),
      span(cls := "status-label", text <-- t(status)((m, s) => s.label(m)))
    ),
    button(
      cls := "switch",
      role := "switch",
      hidden <-- settingsOpen.signal,
      aria.checked <-- showOriginal.signal.map(_.toString),
      span(cls := "track"),
      span(text <-- t(_.original)),
      onClick --> { _ => showOriginal.update(!_) }
    ),
    button(
      cls := "primary",
      text <-- t(_.translate),
      disabled <-- actionsDisabled,
      onClick --> { _ => post("/capture") }
    ),
    iconButton(_.settings)(
      aria.expanded <-- settingsOpen.signal,
      svg.svg(svg.viewBox := "0 0 24 24", svg.path(svg.fillRule := "evenodd", svg.d := CogPath)),
      onClick --> { _ => settingsOpen.update(!_) }
    )
  )

  // Material Symbols "settings" (Apache 2.0).
  private val CogPath =
    "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.49.49 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.48.48 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96a.48.48 0 0 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32a.48.48 0 0 0-.12-.61l-2.01-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"

  private def banner = div(
    cls := "banners",
    child(div(cls := "banner", role := "alert", text <-- t(_.offlineBanner))) <-- offline,
    child.maybe <-- state
      .map(_.flatMap(_.error))
      .splitOption((_, message) =>
        div(
          cls := "banner",
          role := "alert",
          span(text <-- message),
          iconButton(_.dismiss)(cls := "quiet dismiss", "✕", onClick --> { _ => post("/dismiss") })
        )
      )
  )

  private def iconButton(label: Messages => String)(content: Modifier[Button]*) =
    button(cls := "icon", aria.label <-- t(label), title <-- t(label), content)
