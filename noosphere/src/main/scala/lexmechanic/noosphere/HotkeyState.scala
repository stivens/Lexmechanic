package lexmechanic.noosphere

import io.circe.Codec

final case class HotkeyState(button: Option[Int], learning: Boolean, unavailable: Option[HotkeyProblem])
    derives Codec.AsObject

object HotkeyState:
  val learnSeconds = 5
