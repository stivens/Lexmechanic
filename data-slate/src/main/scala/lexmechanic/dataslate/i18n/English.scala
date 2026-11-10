package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.{Gender, HotkeyProblem, Region}

object English extends Messages:
  val name = "English"
  val lang = "en"

  val statusOffline = "Daemon offline"
  val statusBusy = "Cogitating…"
  val statusFailed = "Error"
  val statusReady = "Ready"

  val offlineBanner = "The daemon is not responding. Start lexmechanic - this page reconnects on its own."
  val dismiss = "Dismiss"

  val calibrate = "Calibrate"
  val translate = "Translate"
  val settings = "Settings"
  val back = "Back"

  val game = "Game"
  val anyGame = "Any game"
  val translateInto = "Translate into"
  val interfaceLanguage = "Language"
  val uiLanguage = "UI language"
  val hotkey = "Hotkey"
  val change = "Change"
  def pressAButton(seconds: Int) = s"Press a button ($seconds s)…"
  val noHotkey = "None"
  def hotkeyUnavailable(problem: HotkeyProblem) = s"Hotkey unavailable: ${problem.description}."
  val instructions = "Instructions"
  val instructionsHint =
    "Extra guidance for the Abominable Intelligence. The game, target language and answer format are always added."
  val restoreDefault = "Restore default"
  val saved = "Saved"

  val sectionTranslation = "Translation"
  val sectionCapture = "Capture"
  val sectionAi = "AI"
  val sectionInterface = "Interface"
  val sectionSystem = "System"

  val region = "Dialogue region"
  val regionNotSet = "Not set - the whole screen is sent"
  def regionSet(region: Region) = s"${region.width}×${region.height} px at ${region.x}, ${region.y}"

  val provider = "AI provider"
  val model = "Model"
  val apiKey = "API key"
  def apiKeyFallback(variable: String) = s"or set $variable"
  def apiKeyFromEnvironment(variable: String) = s"Using $variable from the environment"
  val noApiKey = "No key - translations will fail"
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

  val setupTitle = "Setup"
  val setupKey = "AI provider key"
  val openSettings = "Open settings"
  def pressHotkey(key: String) = s"Press $key while a dialogue is on screen."

  val earlier = "Earlier"
  val clearHistory = "Clear history"

  val calibrationInstructions = "Drag over the dialogue window, then save."
  val cancel = "Cancel"
  val saveRegion = "Save region"
  val fullScreenshot = "Full screenshot"

  val middleClick = "Middle click"
  val mouseBack = "Mouse 4 (back)"
  val mouseForward = "Mouse 5 (forward)"
  def mouseButton(number: Int) = s"Mouse $number"
  def key(code: Int) = s"Key $code"
