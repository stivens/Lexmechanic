package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.*

import scala.scalajs.js

trait Messages:
  val lang: String
  val name: String

  /** None shows languages by their own names only. */
  def languageNamesLocale: Option[String] = Some(lang)

  /** For languages the browser has no name for in `languageNamesLocale`. */
  val languageNames: Map[String, String] = Map.empty

  val statusOffline: String
  val statusBusy: String
  val statusFailed: String
  val statusReady: String

  val original: String
  val clear: String
  val calibrate: String
  val translate: String

  val game: String
  val anyGame: String
  val translateInto: String
  val interfaceLanguage: String
  val hotkey: String
  val hotkeyLearnHint: String
  val hotkeyCancelHint: String
  val pressAButton: String
  val noHotkey: String
  def hotkeyUnavailable(problem: HotkeyProblem): String
  val instructions: String
  val instructionsHint: String
  val restoreDefault: String

  val advanced: String
  val advancedHint: String
  val provider: String
  val model: String
  val apiKey: String
  def apiKeyFallback(variable: String): String
  val captain: String
  def gender(gender: Gender): String
  val port: String
  val portRestart: String
  val captureCommand: String
  val captureCommandHint: String
  val openCommand: String
  val openCommandHint: String

  val noRegion: String
  val emptyHistory: String
  val earlier: String

  val calibrationInstructions: String
  val cancel: String
  val saveRegion: String
  val fullScreenshot: String

  val middleClick: String
  val mouseBack: String
  val mouseForward: String
  def mouseButton(number: Int): String
  def key(code: Int): String

  def keyName(code: Int): String = code match
    case EvdevKeys.BtnMiddle => middleClick
    case EvdevKeys.BtnSide   => mouseBack
    case EvdevKeys.BtnExtra  => mouseForward
    case c                   =>
      EvdevKeys.extraMouseButtons.get(c).map(mouseButton).orElse(EvdevKeys.keyboardLabels.get(c)).getOrElse(key(c))

  private lazy val browserLanguageNames = languageNamesLocale.map(locale =>
    js.Dynamic.newInstance(js.Dynamic.global.Intl.DisplayNames)(
      locale,
      js.Dynamic.literal(`type` = "language", fallback = "none")
    )
  )

  private def languageName(language: Language): Option[String] =
    languageNames
      .get(language.code)
      .orElse(browserLanguageNames.flatMap(_.of(language.code).asInstanceOf[js.UndefOr[String]].toOption))

  private[i18n] def speaks(code: String): Boolean = Language.primarySubtag(code).equalsIgnoreCase(lang)

  def languageLabel(language: Language): String =
    languageName(language)
      .filterNot(name => speaks(language.code) || name.equalsIgnoreCase(language.native))
      .fold(language.native)(name => s"${language.native} — $name")

object Messages:
  val all: List[Messages] = List(English, Polish, Silesian)

  def forTag(tag: String): Option[Messages] = Language.forTag(tag).flatMap(l => all.find(_.speaks(l.code)))

  def forLanguage(code: String): Messages = forTag(code).getOrElse(English)
