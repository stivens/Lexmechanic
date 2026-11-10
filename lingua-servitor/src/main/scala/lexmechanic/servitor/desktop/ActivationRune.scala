package lexmechanic.servitor.desktop

import lexmechanic.noosphere.HotkeyProblem
import lexmechanic.servitor.runtime.Log

import scala.scalanative.meta.LinktimeInfo

/** Codes are evdev codes on every platform, so config.json and the UI need no per-OS cases. */
object ActivationRune:
  def listen(onPress: Int => Unit, onProblem: Option[HotkeyProblem] => Unit): Unit =
    var reported = Option.empty[Option[HotkeyProblem]]
    def report(problem: Option[HotkeyProblem]): Unit =
      if !reported.contains(problem) then
        reported = Some(problem)
        Log.info(s"Hotkey: ${problem.fold("listening")(_.description)}")
        onProblem(problem)
    if LinktimeInfo.isMac then QuartzRune.listen(onPress, report)
    else EvdevRune.listen(onPress, report)
