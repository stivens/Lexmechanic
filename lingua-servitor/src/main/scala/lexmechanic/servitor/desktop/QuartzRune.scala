package lexmechanic.servitor.desktop

import lexmechanic.noosphere.{EvdevKeys, HotkeyProblem}
import lexmechanic.servitor.runtime.ServoSkull

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

private[desktop] object QuartzRune:
  private val SessionEventTap = 1.toUInt
  private val HeadInsert = 0.toUInt
  private val ListenOnly = 1.toUInt
  private val KeyDown = 10
  private val OtherMouseDown = 25
  private val DisabledByTimeout = 0xfffffffe
  private val DisabledByUserInput = 0xffffffff
  private val MouseButtonNumber = 3.toUInt
  private val KeyboardAutorepeat = 8.toUInt
  private val KeyboardKeycode = 9.toUInt
  private val RetryMillis = 3000L

  // The callback is a C function pointer and cannot capture, so its state lives here.
  @volatile private var handler: Int => Unit = _ => ()
  @volatile private var tap: Ptr[Byte] = null

  private val callback: Quartz.EventTapCallback = (_: Ptr[Byte], kind: UInt, event: Ptr[Byte], _: Ptr[Byte]) =>
    kind.toInt match
      case DisabledByTimeout | DisabledByUserInput => Quartz.CGEventTapEnable(tap, true)
      case OtherMouseDown                          =>
        // macOS numbers mouse buttons from 0 (left) in evdev's order: BTN_LEFT, BTN_RIGHT, BTN_MIDDLE, BTN_SIDE...
        handler(EvdevKeys.BtnLeft + Quartz.CGEventGetIntegerValueField(event, MouseButtonNumber).toInt)
      case KeyDown if Quartz.CGEventGetIntegerValueField(event, KeyboardAutorepeat) == 0 =>
        MacKeys.toEvdev.get(Quartz.CGEventGetIntegerValueField(event, KeyboardKeycode).toInt).foreach(handler)
      case _ => ()
    event

  def listen(onPress: Int => Unit, onProblem: Option[HotkeyProblem] => Unit): Unit =
    handler = onPress
    ServoSkull.run("hotkey") {
      val mask = (1L << KeyDown | 1L << OtherMouseDown).toULong
      def create() = Quartz.CGEventTapCreate(SessionEventTap, HeadInsert, ListenOnly, mask, callback, null)
      tap = create()
      if tap == null then
        // Adds the responsible app (the terminal, unless launched as an .app) to the Input Monitoring list, prompting once.
        Quartz.CGRequestListenEventAccess()
        onProblem(Some(HotkeyProblem.NoInputMonitoring))
      while tap == null do
        Thread.sleep(RetryMillis)
        tap = create()
      onProblem(None)
      val source = Quartz.CFMachPortCreateRunLoopSource(null, tap, 0)
      Quartz.CFRunLoopAddSource(Quartz.CFRunLoopGetCurrent(), source, Quartz.kCFRunLoopCommonModes)
      Quartz.CFRunLoopRun()
    }

@extern
private object Quartz:
  type EventTapCallback = CFuncPtr4[Ptr[Byte], UInt, Ptr[Byte], Ptr[Byte], Ptr[Byte]]

  def CGEventTapCreate(
      tap: UInt,
      place: UInt,
      options: UInt,
      eventsOfInterest: ULong,
      callback: EventTapCallback,
      userInfo: Ptr[Byte]
  ): Ptr[Byte] = extern
  def CGEventTapEnable(tap: Ptr[Byte], enable: Boolean): Unit = extern
  def CGEventGetIntegerValueField(event: Ptr[Byte], field: UInt): Long = extern
  def CGRequestListenEventAccess(): Boolean = extern
  def CFMachPortCreateRunLoopSource(allocator: Ptr[Byte], port: Ptr[Byte], order: CLong): Ptr[Byte] = extern
  def CFRunLoopGetCurrent(): Ptr[Byte] = extern
  def CFRunLoopAddSource(loop: Ptr[Byte], source: Ptr[Byte], mode: Ptr[Byte]): Unit = extern
  @blocking def CFRunLoopRun(): Unit = extern
  var kCFRunLoopCommonModes: Ptr[Byte] = extern
