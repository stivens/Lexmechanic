package lexmechanic.noosphere

import io.circe.Codec

enum HotkeyProblem(val description: String):
  case NoDevices
      extends HotkeyProblem(
        "no input devices found (evdev is Linux-only) - use Translate or `lexmechanic trigger`"
      )
  case NoPermission
      extends HotkeyProblem(
        "input devices with buttons are not readable - run `sudo usermod -aG input $USER` and log in again"
      )
  case NoInputMonitoring
      extends HotkeyProblem(
        "allow Lexmechanic, or the terminal running it, under Input Monitoring in System Settings > Privacy & Security, then restart it"
      )

object HotkeyProblem:
  given Codec[HotkeyProblem] = nameCodec(values, "hotkey problem")
