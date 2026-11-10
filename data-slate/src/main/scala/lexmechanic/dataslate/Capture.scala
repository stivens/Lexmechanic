package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.{actionsDisabled, post, state}
import lexmechanic.dataslate.i18n.I18n.t
import lexmechanic.noosphere.*

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Capture:
  val region: Signal[Option[Region]] = state.map(_.flatMap(_.region)).distinct

  private val hotkey: Signal[Option[HotkeyState]] = state.map(_.map(_.hotkey)).distinct

  val usableHotkey: Signal[Option[Int]] = hotkey.map(_.filter(_.unavailable.isEmpty).flatMap(_.button))

  val hotkeyProblem: Signal[Option[HotkeyProblem]] = hotkey.map(_.flatMap(_.unavailable))

  def regionText: Signal[String] = t(region)((m, region) => region.fold(m.regionNotSet)(m.regionSet))

  def calibrateButton = button(
    text <-- t(_.calibrate),
    disabled <-- actionsDisabled,
    onClick --> { _ => post("/calibrate").foreach(ok => if ok then Calibration.open()) }
  )

  def hotkeyText: Signal[String] = t(hotkey) { (m, current) =>
    current.fold("…") { h =>
      if h.learning then m.pressAButton(HotkeyState.learnSeconds)
      else h.unavailable.fold(h.button.fold(m.noHotkey)(m.keyName))(m.hotkeyUnavailable)
    }
  }

  def hotkeyButton = button(
    dataAttr("learning") <-- hotkey.map(_.exists(_.learning).toString),
    disabled <-- hotkey.map(_.forall(_.unavailable.isDefined)),
    text <-- t(hotkey)((m, current) => if current.exists(_.learning) then m.cancel else m.change),
    onClick(_.sample(hotkey)) --> { h => post(if h.exists(_.learning) then "/hotkey/cancel" else "/hotkey/learn") }
  )
