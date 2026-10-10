package lexmechanic.noosphere

object EvdevKeys:
  val BtnLeft = 272
  private val BtnRight = 273
  val BtnMiddle = 274
  val BtnSide = 275
  val BtnExtra = 276
  private val BtnDigitizer = 320 to 335

  val extraMouseButtons: Map[Int, Int] = Map(277 -> 6, 278 -> 7, 279 -> 8)

  /** Labels printed on the keys themselves, so they are not translated. */
  val keyboardLabels: Map[Int, String] = Map(
    70 -> "Scroll Lock",
    110 -> "Insert",
    119 -> "Pause",
    87 -> "F11",
    88 -> "F12"
  ) ++ (1 to 10).map(n => (58 + n) -> s"F$n") ++ (13 to 20).map(n => (170 + n) -> s"F$n")

  /** Clicks and touchscreen taps are excluded: they would bind the very tap used to operate the UI. */
  def bindable(code: Int): Boolean = code != BtnLeft && code != BtnRight && !BtnDigitizer.contains(code)
