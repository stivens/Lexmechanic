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
  private val instructionsOpen = Var(false)
  private val postAnswered = EventBus[Unit]()
  private val showOriginal = WebStorageVar.localStorage("lexmechanic.original", syncOwner = None).bool(default = true)

  private[dataslate] val state: Signal[Option[State]] = server.signal.distinct

  private val hotkey: Signal[Option[HotkeyState]] = state.map(_.map(_.hotkey)).distinct

  private val status: Signal[ServitorStatus] = state.combineWith(pending.signal).map {
    case (None, _)                                     => ServitorStatus.Offline
    case (Some(s), requests) if s.busy || requests > 0 => ServitorStatus.Busy
    case (Some(s), _) if s.error.isDefined             => ServitorStatus.Failed
    case _                                             => ServitorStatus.Ready
  }

  private[dataslate] val offline = state.map(_.isEmpty)

  private val actionsDisabled = status.map(s => s == ServitorStatus.Busy || s == ServitorStatus.Offline)

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

  private[dataslate] def saveSettings(settings: Settings): Future[Boolean] = post("/settings", settings.asJson.noSpaces)

  // Polls must not clobber unsaved typing, nor a failed poll blank it; a save re-sets it even when the
  // server's normalized value equals the previous one.
  private[dataslate] def savedText(read: State => String, settings: String => Settings) =
    val saved = EventBus[String]()
    Seq(
      value <-- EventStream.merge(state.changes.collectSome.map(read).distinct, saved.events),
      onChange.mapToValue --> { text =>
        saveSettings(settings(text)).foreach(_ => server.now().foreach(s => saved.emit(read(s))))
      }
    )

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
    header,
    settings,
    Advanced.panel,
    instructionsEditor,
    mainView,
    Calibration.overlay(state.map(_.flatMap(_.region))),
    child.maybe <-- state.map(_.flatMap(_.error)).splitOption((_, message) => div(cls := "error", text <-- message))
  )

  private def header = headerTag(
    div(cls := "title", img(src := "/emblem.svg", alt := ""), "Lexmechanic"),
    div(
      cls := "status",
      dataAttr("state") <-- status.map(_.toString.toLowerCase),
      span(cls := "dot"),
      span(text <-- t(status)((m, s) => s.label(m)))
    ),
    button(
      cls := "switch",
      role := "switch",
      aria.checked <-- showOriginal.signal.map(_.toString),
      span(cls := "track"),
      span(text <-- t(_.original)),
      onClick --> { _ => showOriginal.update(!_) }
    ),
    button(
      text <-- t(_.clear),
      disabled <-- state.map(_.forall(_.history.isEmpty)),
      onClick --> { _ => post("/clear") }
    ),
    button(
      text <-- t(_.calibrate),
      disabled <-- actionsDisabled,
      onClick --> { _ => post("/calibrate").foreach(ok => if ok then Calibration.open()) }
    ),
    button(
      text <-- t(_.translate),
      disabled <-- actionsDisabled,
      onClick --> { _ => post("/capture") }
    ),
    Advanced.toggle
  )

  private def settings = div(
    cls := "settings",
    field(
      _.game,
      input(
        typ := "text",
        spellCheck := false,
        placeholder <-- t(_.anyGame),
        disabled <-- offline,
        savedText(_.game, game => Settings(game = Some(game)))
      )
    ).amend(cls := "game"),
    field(
      _.translateInto,
      select(
        disabled <-- offline,
        Language.all.map(l => option(value := l.code, text <-- t(_.languageLabel(l)))),
        controlled(
          value <-- state.map(_.flatMap(_.language).fold("")(_.code)),
          onChange.mapToValue --> { code => saveSettings(Settings(language = Language.byCode.get(code))) }
        )
      )
    ),
    div(
      cls := "field",
      span(cls := "field-label", text <-- t(_.hotkey)),
      button(
        dataAttr("learning") <-- hotkey.map(_.exists(_.learning).toString),
        disabled <-- hotkey.map(_.forall(_.unavailable.isDefined)),
        title <-- t(hotkey) { (m, current) =>
          current.fold("") { h =>
            h.unavailable.fold(if h.learning then m.hotkeyCancelHint else m.hotkeyLearnHint)(m.hotkeyUnavailable)
          }
        },
        text <-- t(hotkey) { (m, current) =>
          current.fold("…")(h => if h.learning then m.pressAButton else h.button.fold(m.noHotkey)(m.keyName))
        },
        onClick(_.sample(hotkey)) --> { h => post(if h.exists(_.learning) then "/hotkey/cancel" else "/hotkey/learn") }
      )
    ),
    field(
      _.interfaceLanguage,
      select(
        disabled <-- offline,
        Messages.all.map(m => option(value := m.lang, m.name)),
        controlled(
          value <-- I18n.messages.map(_.lang),
          onChange.mapToValue --> { lang => saveSettings(Settings(interfaceLanguage = Some(lang))) }
        )
      )
    ),
    button(
      text <-- t(_.instructions),
      aria.expanded <-- instructionsOpen.signal,
      onClick --> { _ => instructionsOpen.update(!_) }
    )
  )

  private[dataslate] def field(name: Messages => String, control: HtmlElement) = label(
    cls := "field",
    span(cls := "field-label", text <-- t(name)),
    control
  )

  private def instructionsEditor = div(
    cls := "prompt-editor",
    cls("open") <-- instructionsOpen.signal,
    div(cls := "hint", text <-- t(_.instructionsHint)),
    textArea(
      rows := 6,
      spellCheck := false,
      disabled <-- offline,
      savedText(_.instructions, text => Settings(instructions = Some(text)))
    ),
    button(
      text <-- t(_.restoreDefault),
      disabled <-- state.map(_.forall(_.instructions == Instructions.default)),
      onClick --> { _ => saveSettings(Settings(instructions = Some(Instructions.default))) }
    )
  )

  private def mainView = mainTag(
    child.maybe <-- hotkey
      .map(_.flatMap(_.unavailable))
      .splitOption((_, problem) => div(cls := "hint", text <-- t(problem)(_.hotkeyUnavailable(_)))),
    child(div(cls := "hint", text <-- t(_.noRegion))) <-- state.map(_.exists(_.region.isEmpty)),
    child(div(cls := "empty", text <-- t(_.emptyHistory))) <-- state.map(_.exists(_.history.isEmpty)),
    entries(_.take(1)),
    child(div(cls := "history-title", text <-- t(_.earlier))) <-- state.map(_.exists(_.history.sizeIs > 1)),
    sectionTag(
      cls := "history",
      entries(_.drop(1))
    )
  )

  private def entries(select: List[Entry] => List[Entry]) =
    children <-- state.map(_.toList.flatMap(s => select(s.history))).split(_.id)((_, entry, _) => renderEntry(entry))

  private def renderEntry(entry: Entry) =
    val dialogue = entry.dialogue
    val translationLang = lang := entry.language.code
    // The source language is detected by the model and not reported, so it is marked unknown.
    val originalLang = lang := ""
    articleTag(
      cls := "entry",
      dialogue.speaker.map(div(cls := "speaker", _)),
      p(cls := "translation", translationLang, dialogue.text.translation),
      p(cls := "original", originalLang, dialogue.text.original),
      Option.when(dialogue.answers.nonEmpty)(
        ol(
          dialogue.answers.map(a =>
            li(
              div(cls := "translation", translationLang, a.translation),
              div(cls := "original", originalLang, a.original)
            )
          )
        )
      )
    )
