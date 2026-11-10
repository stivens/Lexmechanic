package lexmechanic.noosphere

object EvdevKeys:
  val KeyEsc = 1
  val BtnLeft = 272
  private val BtnRight = 273
  val BtnMiddle = 274
  val BtnSide = 275
  val BtnExtra = 276
  private val BtnDigitizer = 320 to 335

  private val functionKeys = (59 to 68) ++ (87 to 88) ++ (183 to 190)

  val extraMouseButtons: Map[Int, Int] = Map(277 -> 6, 278 -> 7, 279 -> 8)

  /** Labels printed on the keys themselves, so they are not translated. */
  val keyboardLabels: Map[Int, String] = Map(
    70 -> "Scroll Lock",
    110 -> "Insert",
    119 -> "Pause"
  ) ++ functionKeys.zipWithIndex.map((code, i) => code -> s"F${i + 1}")

  def functionKey(n: Int): Int = functionKeys(n - 1)

  /** Clicks and touchscreen taps are excluded: they would bind the very tap used to operate the UI. */
  def bindable(code: Int): Boolean = code != BtnLeft && code != BtnRight && !BtnDigitizer.contains(code)
