package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.{banner, post, state}
import lexmechanic.dataslate.i18n.I18n.t
import lexmechanic.dataslate.i18n.Messages
import lexmechanic.noosphere.*

object Reading:
  private val setupShown = state.map(_.exists(_.history.isEmpty)).distinct

  def view = mainTag(
    cls := "reading",
    child(setup) <-- setupShown,
    // The setup checklist already lists a hotkey problem.
    child.maybe <-- Capture.hotkeyProblem
      .combineWithFn(setupShown)((problem, shown) => if shown then None else problem)
      .splitOption((_, problem) => banner("status")(text <-- t(problem)(_.hotkeyUnavailable(_)))),
    entries(_.take(1)),
    child(div(cls := "history-title", text <-- t(_.earlier))) <-- state.map(_.exists(_.history.sizeIs > 1)),
    sectionTag(cls := "history", entries(_.drop(1))),
    child(
      button(cls := "quiet clear", text <-- t(_.clearHistory), onClick --> { _ => post("/clear") })
    ) <-- state.map(_.exists(_.history.nonEmpty))
  )

  private def setup = sectionTag(
    cls := "setup",
    h2(text <-- t(_.setupTitle)),
    ol(
      step(
        _.setupKey,
        state.map(_.exists(_.apiKeySource != KeySource.Missing)),
        t(state)((m, current) => current.fold("")(s => SettingsPage.keyStatus(m, s).getOrElse(s.provider.toString))),
        button(text <-- t(_.openSettings), onClick --> { _ => SettingsPage.openAi() })
      ),
      step(_.region, Capture.region.map(_.isDefined), Capture.regionText, Capture.calibrateButton),
      step(_.hotkey, Capture.usableHotkey.map(_.isDefined), Capture.hotkeyText, Capture.hotkeyButton)
    ),
    p(
      cls := "next",
      text <-- t(Capture.usableHotkey)((m, key) => m.pressHotkey(key.fold(m.translate)(m.keyName)))
    )
  )

  private def step(name: Messages => String, done: Signal[Boolean], detail: Signal[String], action: HtmlElement) = li(
    cls("done") <-- done,
    span(cls := "mark", aria.hidden := true, text <-- done.foldBoolean("✓", "!")),
    div(cls := "step-text", div(cls := "step-name", text <-- t(name)), div(cls := "step-detail", text <-- detail)),
    action
  )

  private def entries(select: List[Entry] => List[Entry]) =
    children <-- state.map(_.toList.flatMap(s => select(s.history))).split(_.id)((_, entry, _) => renderEntry(entry))

  private def renderEntry(entry: Entry) =
    val dialogue = entry.dialogue
    articleTag(
      cls := "entry",
      lang := entry.language.code,
      dialogue.speaker.map(div(cls := "speaker", _)),
      p(cls := "translation", dialogue.text.translation),
      Option.when(dialogue.answers.nonEmpty)(
        ol(cls := "answers", dialogue.answers.map(a => li(a.translation)))
      )
    )
