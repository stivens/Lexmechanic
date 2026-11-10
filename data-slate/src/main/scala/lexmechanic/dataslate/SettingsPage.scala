package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.*
import lexmechanic.dataslate.i18n.I18n.t
import lexmechanic.dataslate.i18n.Messages
import lexmechanic.noosphere.*

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.Success

object SettingsPage:
  lazy val view = mainTag(
    cls := "settings-page",
    section(_.sectionTranslation)(
      row(_.game) { field =>
        input(
          typ := "text",
          spellCheck := false,
          placeholder <-- t(_.anyGame),
          field.text(_.game, game => Settings(game = Some(game)))
        )
      },
      row(_.translateInto)(field => Pickers.targetLanguage(field.save).amend(field.control)),
      row(_.captain)(
        enumChoice(Gender.values, _.captain, g => text <-- t(_.gender(g)), g => Settings(captain = Some(g)))
      ),
      row(_.instructions, wide = true)(
        textBlock(_.instructionsHint, _.instructions, _ => Instructions.default, i => Settings(instructions = Some(i)))
      )
    ),
    section(_.sectionCapture)(
      staticRow(_.region)(span(cls := "value", text <-- Capture.regionText), Capture.calibrateButton),
      staticRow(_.hotkey)(span(cls := "value", text <-- Capture.hotkeyText), Capture.hotkeyButton)
    ),
    aiSection,
    section(_.sectionInterface)(
      row(_.interfaceLanguage)(field => Pickers.interfaceLanguage(field.save).amend(field.control))
    ),
    section(_.sectionSystem, collapsed = true)(
      row(_.port) { field =>
        Seq(
          input(
            field.describedBy,
            typ := "number",
            minAttr := "1",
            maxAttr := "65535",
            field.parsed(_.port.toString, _.toIntOption.map(port => Settings(port = Some(port))))
          ),
          child(div(field.hint, text <-- t(_.portRestart))) <-- state.map(_.exists(_.restartRequired))
        )
      },
      row(_.captureCommand, wide = true)(
        command(
          _.captureCommandHint,
          _.captureCommand,
          _.defaultCaptureCommand,
          c => Settings(captureCommand = Some(c))
        )
      ),
      row(_.openCommand, wide = true)(
        command(_.openCommandHint, _.openCommand, _.defaultOpenCommand, c => Settings(openCommand = Some(c)))
      )
    )
  )

  private lazy val aiSection = section(_.sectionAi)(
    row(_.provider)(enumChoice(Heretek.values, _.provider, _.toString, h => Settings(provider = Some(h)))),
    row(_.model) { field =>
      input(
        typ := "text",
        spellCheck := false,
        placeholder <-- state.map(_.fold("")(_.provider.defaultModel)),
        field.text(_.model.getOrElse(""), model => Settings(model = Some(model)))
      )
    },
    row(_.apiKey) { field =>
      Seq(
        input(
          field.describedBy,
          typ := "password",
          autoComplete := "off",
          spellCheck := false,
          placeholder <-- t(state.map(_.fold("")(_.provider.keyVariable)))(_.apiKeyFallback(_)),
          field.text(_.apiKey.getOrElse(""), key => Settings(apiKey = Some(key)))
        ),
        child.maybe <-- t(state)((m, current) => current.flatMap(keyStatus(m, _)))
          .splitOption((_, status) => div(field.hint, text <-- status))
      )
    }
  )

  def openAi(): Unit =
    openSettings()
    aiSection.ref.scrollIntoView()

  def keyStatus(m: Messages, state: State): Option[String] = state.apiKeySource match
    case KeySource.Stored      => None
    case KeySource.Environment => Some(m.apiKeyFromEnvironment(state.provider.keyVariable))
    case KeySource.Missing     => Some(m.noApiKey)

  private final class Field(val id: String, saved: Observer[Unit]):
    def text(read: State => String, settings: String => Settings) = parsed(read, settings.andThen(Some(_)))
    def parsed(read: State => String, settings: String => Option[Settings]): Modifier[HtmlElement] =
      Seq(control, disabled <-- offline) ++ savedText(read, settings, save)
    def save(settings: Settings): Future[Boolean] =
      saveSettings(settings).andThen { case Success(true) => saved.onNext(()) }
    def control: Modifier[HtmlElement] = idAttr := id
    def describedBy: Modifier[HtmlElement] = aria.describedBy := s"$id-hint"
    def hint: Modifier[HtmlElement] = Seq(cls := "hint", idAttr := s"$id-hint")

  private var fields = 0

  private def section(title: Messages => String, collapsed: Boolean = false)(rows: HtmlElement*) =
    val heading = h2(text <-- t(title))
    sectionTag(cls := "settings-section", if collapsed then detailsTag(summaryTag(heading), rows) else heading +: rows)

  private def row(name: Messages => String, wide: Boolean = false)(content: Field => Modifier[HtmlElement]) =
    fields += 1
    val saved = EventBus[Unit]()
    val field = Field(s"setting-$fields", saved.writer)
    val shown = saved.events
      .flatMapSwitch(_ => EventStream.merge(EventStream.fromValue(true), EventStream.delay(1500, false)))
      .startWith(false)
    layout(
      wide,
      Seq(
        label(forId := field.id, text <-- t(name)),
        span(cls := "saved", aria.live := "polite", text <-- t(shown)((m, show) => if show then m.saved else ""))
      ),
      content(field)
    )

  private def staticRow(name: Messages => String)(content: Modifier[HtmlElement]*) =
    layout(wide = false, span(text <-- t(name)), content)

  private def layout(wide: Boolean, caption: Modifier[HtmlElement], control: Modifier[HtmlElement]) = div(
    cls := "row",
    Option.when(wide)(cls := "wide"),
    div(cls := "row-label", caption),
    div(cls := "row-control", control)
  )

  private def enumChoice[E](
      values: Array[E],
      read: State => E,
      label: E => Modifier[HtmlElement],
      settings: E => Settings
  )(field: Field) =
    Pickers
      .choice(values.toSeq, _.toString, label, state.map(_.map(read)))(e => field.save(settings(e)))
      .amend(field.control)

  private def textBlock(
      hint: Messages => String,
      read: State => String,
      default: State => String,
      settings: String => Settings,
      code: Boolean = false
  )(field: Field): Modifier[HtmlElement] = Seq(
    textArea(
      field.describedBy,
      Option.when(code)(cls := "code"),
      rows := 6,
      spellCheck := false,
      field.text(read, settings)
    ),
    div(field.hint, text <-- t(hint)),
    button(
      text <-- t(_.restoreDefault),
      disabled <-- state.map(_.forall(s => read(s) == default(s))),
      onClick(_.sample(state)) --> { _.foreach(s => field.save(settings(default(s)))) }
    )
  )

  private def command(
      hint: Messages => String,
      read: State => List[String],
      default: State => List[String],
      settings: List[String] => Settings
  ) = textBlock(
    hint,
    read(_).mkString("\n"),
    default(_).mkString("\n"),
    text => settings(text.linesIterator.map(_.trim).filter(_.nonEmpty).toList),
    code = true
  )
