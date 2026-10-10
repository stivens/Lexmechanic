package lexmechanic.servitor.desktop

import lexmechanic.noosphere.HotkeyProblem
import lexmechanic.servitor.runtime.{Log, ServoSkull}

import java.io.{BufferedInputStream, DataInputStream, File, FileInputStream, IOException}
import java.nio.file.{Files, Path}
import java.nio.{ByteBuffer, ByteOrder}
import java.util.concurrent.ConcurrentHashMap
import scala.util.{Try, Using}

/** Listens on Linux evdev devices, so it works under Wayland and regardless of which window has focus. */
private[desktop] object EvdevRune:
  private val EventSize = 24 // struct input_event on 64-bit Linux
  private val EvKey = 1
  private val Pressed = 1
  // Mice come and go (USB replug, Bluetooth reconnecting after sleep), so devices are rescanned rather than found once.
  private val RescanMillis = 3000L

  def listen(onPress: Int => Unit, onProblem: Option[HotkeyProblem] => Unit): Unit =
    val watched = ConcurrentHashMap.newKeySet[String]()
    ServoSkull.run("hotkey-scan") {
      var reported = Option.empty[Option[HotkeyProblem]]
      while true do
        val devices = Option(File("/dev/input").listFiles()).toList.flatten.filter(_.getName.startsWith("event"))
        val (readable, unreadable) = devices.partition(_.canRead)
        val problem =
          if devices.isEmpty then Some(HotkeyProblem.NoDevices)
          // Some devices (e.g. Steam's virtual gamepad) are readable without the input group, so check the unreadable ones.
          else if unreadable.exists(hasKeys) then Some(HotkeyProblem.NoPermission)
          else None
        if !reported.contains(problem) then
          onProblem(problem)
          reported = Some(problem)
        for device <- readable if !watched.contains(device.getName) && hasKeys(device) && watched.add(device.getName) do
          ServoSkull.run(s"hotkey-${device.getName}") {
            try watch(device, onPress)
            finally watched.remove(device.getName)
          }
        Thread.sleep(RescanMillis)
    }

  // Skips devices that never report key or button presses, such as the Deck's motion sensors, which stream constantly.
  private def hasKeys(device: File): Boolean =
    val capabilities = Path.of(s"/sys/class/input/${device.getName}/device/capabilities/key")
    Try(Files.readString(capabilities).exists(c => c != '0' && !c.isWhitespace)).getOrElse(true)

  private def watch(device: File, onPress: Int => Unit): Unit =
    try
      Using.resource(DataInputStream(BufferedInputStream(FileInputStream(device), EventSize * 64))) { in =>
        val event = ByteBuffer.allocate(EventSize).order(ByteOrder.LITTLE_ENDIAN)
        while true do
          in.readFully(event.array())
          if event.getShort(16) == EvKey && event.getInt(20) == Pressed then onPress(event.getShort(18) & 0xffff)
      }
    catch case e: IOException => Log.info(s"Hotkey: stopped reading ${device.getName}: ${e.getMessage}")
