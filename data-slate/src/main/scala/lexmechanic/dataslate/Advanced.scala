package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.{field, offline, saveSettings, savedText, state}
import lexmechanic.dataslate.i18n.I18n.t
import lexmechanic.dataslate.i18n.Messages
import lexmechanic.noosphere.*

object Advanced:
  private val open = Var(false)

  // Material Symbols "settings" (Apache 2.0).
  private val CogPath =
    "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.49.49 0 0 0-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54a.48.48 0 0 0-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96a.48.48 0 0 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58a.49.49 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32a.48.48 0 0 0-.12-.61l-2.01-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"

  def toggle = button(
    cls := "icon",
    aria.label <-- t(_.advanced),
    title <-- t(_.advanced),
    aria.expanded <-- open.signal,
    svg.svg(svg.viewBox := "0 0 24 24", svg.path(svg.fillRule := "evenodd", svg.d := CogPath)),
    onClick --> { _ => open.update(!_) }
  )

  def panel = div(
    cls := "advanced",
    cls("open") <-- open.signal,
    div(cls := "hint", text <-- t(_.advancedHint)),
    div(
      cls := "advanced-fields",
      field(
        _.provider,
        enumSelect(Heretek.values, _.provider, _.toString, h => Settings(provider = Some(h)))
      ),
      field(
        _.model,
        input(
          typ := "text",
          spellCheck := false,
          disabled <-- offline,
          placeholder <-- state.map(_.fold("")(_.provider.defaultModel)),
          savedText(_.model.getOrElse(""), model => Settings(model = Some(model)))
        )
      ),
      field(
        _.apiKey,
        input(
          typ := "password",
          autoComplete := "off",
          spellCheck := false,
          disabled <-- offline,
          placeholder <-- t(state.map(_.fold("")(_.provider.keyVariable)))(_.apiKeyFallback(_)),
          savedText(_.apiKey.getOrElse(""), key => Settings(apiKey = Some(key)))
        )
      ),
      field(
        _.captain,
        enumSelect(Gender.values, _.captain, g => text <-- t(_.gender(g)), g => Settings(captain = Some(g)))
      ),
      field(
        _.port,
        input(
          typ := "number",
          minAttr := "1",
          maxAttr := "65535",
          disabled <-- offline,
          savedText(_.port.toString, port => Settings(port = port.toIntOption))
        )
      )
    ),
    child(div(cls := "hint", text <-- t(_.portRestart))) <-- state.map(_.exists(_.restartRequired)),
    div(
      cls := "commands",
      command(
        _.captureCommand,
        _.captureCommandHint,
        _.captureCommand,
        _.defaultCaptureCommand,
        c => Settings(captureCommand = Some(c))
      ),
      command(
        _.openCommand,
        _.openCommandHint,
        _.openCommand,
        _.defaultOpenCommand,
        c => Settings(openCommand = Some(c))
      )
    )
  )

  private def enumSelect[E](
      values: Array[E],
      read: State => E,
      optionText: E => Modifier[HtmlElement],
      save: E => Settings
  ) = select(
    disabled <-- offline,
    values.toList.map(e => option(value := e.toString, optionText(e))),
    controlled(
      value <-- state.map(_.fold("")(read(_).toString)),
      onChange.mapToValue --> { name => values.find(_.toString == name).foreach(e => saveSettings(save(e))) }
    )
  )

  private def command(
      heading: Messages => String,
      hint: Messages => String,
      read: State => List[String],
      default: State => List[String],
      settings: List[String] => Settings
  ) = div(
    cls := "command",
    label(
      span(cls := "field-label", text <-- t(heading)),
      textArea(
        rows := 6,
        spellCheck := false,
        disabled <-- offline,
        savedText(read(_).mkString("\n"), text => settings(text.linesIterator.map(_.trim).filter(_.nonEmpty).toList))
      )
    ),
    div(cls := "hint", text <-- t(hint)),
    button(
      text <-- t(_.restoreDefault),
      disabled <-- state.map(_.forall(s => read(s) == default(s))),
      onClick(_.sample(state)) --> { s => s.foreach(s => saveSettings(settings(default(s)))) }
    )
  )
