package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.*
import lexmechanic.dataslate.i18n.I18n.{messages, t}
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
          field.control,
          typ := "text",
          spellCheck := false,
          placeholder <-- t(_.anyGame),
          disabled <-- offline,
          field.text(_.game, game => Some(Settings(game = Some(game))))
        )
      },
      row(_.translateInto)(
        choice(
          Language.all,
          _.code,
          l => text <-- t(_.languageLabel(l)),
          state.map(_.flatMap(_.language).fold("")(_.code)),
          l => Settings(language = Some(l))
        )
      ),
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
      row(_.interfaceLanguage)(
        choice(Messages.all, _.lang, _.name, messages.map(_.lang), m => Settings(interfaceLanguage = Some(m.lang)))
      )
    ),
    section(_.sectionSystem, collapsed = true)(
      row(_.port) { field =>
        Seq(
          input(
            field.control,
            field.describedBy,
            typ := "number",
            minAttr := "1",
            maxAttr := "65535",
            disabled <-- offline,
            field.text(_.port.toString, _.toIntOption.map(port => Settings(port = Some(port))))
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
        field.control,
        typ := "text",
        spellCheck := false,
        disabled <-- offline,
        placeholder <-- state.map(_.fold("")(_.provider.defaultModel)),
        field.text(_.model.getOrElse(""), model => Some(Settings(model = Some(model))))
      )
    },
    row(_.apiKey) { field =>
      Seq(
        input(
          field.control,
          field.describedBy,
          typ := "password",
          autoComplete := "off",
          spellCheck := false,
          disabled <-- offline,
          placeholder <-- t(state.map(_.fold("")(_.provider.keyVariable)))(_.apiKeyFallback(_)),
          field.text(_.apiKey.getOrElse(""), key => Some(Settings(apiKey = Some(key))))
        ),
        child.maybe <-- t(state)((m, current) => current.flatMap(keyStatus(m, _)))
          .splitOption((_, status) => div(field.hint, text <-- status))
      )
    }
  )

  def openAi(): Unit =
    openSettings()
    aiSection.ref.scrollIntoView()

  def hasKey(state: State): Boolean = state.apiKey.isDefined || state.apiKeyFromEnvironment

  def keyStatus(m: Messages, state: State): Option[String] =
    Option.when(state.apiKey.isEmpty)(
      if state.apiKeyFromEnvironment then m.apiKeyFromEnvironment(state.provider.keyVariable) else m.noApiKey
    )

  private final class Field(val id: String, saved: Observer[Unit]):
    def text(read: State => String, settings: String => Option[Settings]) = savedText(read, settings, save)
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
    div(
      cls := "row",
      Option.when(wide)(cls := "wide"),
      div(
        cls := "row-label",
        label(forId := field.id, text <-- t(name)),
        span(cls := "saved", aria.live := "polite", text <-- t(shown)((m, show) => if show then m.saved else ""))
      ),
      div(cls := "row-control", content(field))
    )

  private def staticRow(name: Messages => String)(content: Modifier[HtmlElement]*) = div(
    cls := "row",
    div(cls := "row-label", span(text <-- t(name))),
    div(cls := "row-control", content)
  )

  private def choice[E](
      values: Seq[E],
      key: E => String,
      label: E => Modifier[HtmlElement],
      current: Signal[String],
      save: E => Settings
  )(field: Field) = select(
    field.control,
    disabled <-- offline,
    values.map(e => option(value := key(e), label(e))),
    controlled(
      value <-- current,
      onChange.mapToValue --> { k => values.find(key(_) == k).foreach(e => field.save(save(e))) }
    )
  )

  private def enumChoice[E](
      values: Array[E],
      read: State => E,
      label: E => Modifier[HtmlElement],
      save: E => Settings
  ) =
    choice(values.toSeq, _.toString, label, state.map(_.fold("")(read(_).toString)), save)

  private def textBlock(
      hint: Messages => String,
      read: State => String,
      default: State => String,
      settings: String => Settings,
      code: Boolean = false
  )(field: Field): Modifier[HtmlElement] = Seq(
    textArea(
      field.control,
      field.describedBy,
      Option.when(code)(cls := "code"),
      rows := 6,
      spellCheck := false,
      disabled <-- offline,
      field.text(read, settings.andThen(Some(_)))
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
