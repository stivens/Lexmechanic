package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.{Gender, HotkeyProblem}

object English extends Messages:
  val name = "English"
  val lang = "en"

  val statusOffline = "Daemon offline"
  val statusBusy = "Cogitating…"
  val statusFailed = "Error"
  val statusReady = "Ready"

  val original = "Original"
  val clear = "Clear"
  val calibrate = "Calibrate"
  val translate = "Translate"

  val game = "Game"
  val anyGame = "Any game"
  val translateInto = "Translate into"
  val interfaceLanguage = "Interface"
  val hotkey = "Hotkey"
  val hotkeyLearnHint = "Click, then press the mouse button or key to use"
  val hotkeyCancelHint = "Click to cancel"
  val pressAButton = "Press a button…"
  val noHotkey = "None"
  def hotkeyUnavailable(problem: HotkeyProblem) = s"Hotkey unavailable: ${problem.description}."
  val instructions = "Instructions"
  val instructionsHint =
    "Extra guidance for the Abominable Intelligence, saved when you leave the box. The game, target language and answer format are always added."
  val restoreDefault = "Restore default"

  val advanced = "Advanced settings"
  val advancedHint = "Each field is saved when you leave it."
  val provider = "AI provider"
  val model = "Model"
  val apiKey = "API key"
  def apiKeyFallback(variable: String) = s"or set $variable"
  val captain = "Player character"
  def gender(gender: Gender) = gender match
    case Gender.Male   => "Male"
    case Gender.Female => "Female"
  val port = "Port"
  val portRestart = "The new port takes effect after the daemon restarts."
  val captureCommand = "Screenshot command"
  val captureCommandHint = "One argument per line; {file} is replaced with the path to save the screenshot to."
  val openCommand = "Open the UI with"
  val openCommandHint =
    "One argument per line; {url} is replaced with this page's address. Leave empty to open nothing."

  val noRegion = "No region calibrated yet - the whole screen is sent. Use Calibrate to select the dialogue window."
  val emptyHistory = "Press the hotkey while a dialogue is on screen."
  val earlier = "Earlier"

  val calibrationInstructions = "Drag over the dialogue window, then save."
  val cancel = "Cancel"
  val saveRegion = "Save region"
  val fullScreenshot = "Full screenshot"

  val middleClick = "Middle click"
  val mouseBack = "Mouse 4 (back)"
  val mouseForward = "Mouse 5 (forward)"
  def mouseButton(number: Int) = s"Mouse $number"
  def key(code: Int) = s"Key $code"
