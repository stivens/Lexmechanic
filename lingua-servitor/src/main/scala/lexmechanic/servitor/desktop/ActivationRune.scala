package lexmechanic.servitor.desktop

import lexmechanic.noosphere.HotkeyProblem
import lexmechanic.servitor.runtime.Log

import scala.scalanative.meta.LinktimeInfo

/** Global hotkey listener; codes are evdev codes on every platform, so config.json and the UI need no per-OS cases. */
object ActivationRune:
  def listen(onPress: Int => Unit, onProblem: Option[HotkeyProblem] => Unit): Unit =
    val report = (problem: Option[HotkeyProblem]) =>
      Log.info(s"Hotkey: ${problem.fold("listening")(_.description)}")
      onProblem(problem)
    if LinktimeInfo.isMac then QuartzRune.listen(onPress, report)
    else EvdevRune.listen(onPress, report)
