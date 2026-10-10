package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.HotkeyProblem

object Silesian extends Messages:
  val name = "Ślōnsko godka"
  val lang = "szl"
  // Browsers accept the szl locale but have no Silesian language names, so they fall back to English ones.
  override val languageNamesLocale = None

  val statusOffline = "Dymōn niy ôdpowiadŏ"
  val statusBusy = "Rachowanie…"
  val statusFailed = "Feler"
  val statusReady = "Fertich"

  val original = "Ôryginał"
  val clear = "Wypucuj"
  val calibrate = "Ustŏw"
  val translate = "Przełōż"

  val game = "Szpil"
  val anyGame = "Jaki bądź szpil"
  val translateInto = "Przełōż na"
  val interfaceLanguage = "Jynzyk aplikacyje"
  val hotkey = "Skrōt"
  val hotkeyLearnHint = "Kliknij, a potym naciś knefel myszy abo klawisz, kerego chcesz używać"
  val hotkeyCancelHint = "Kliknij, coby ôdwołać"
  val instructions = "Instrukcyje"
  val instructionsHint =
    "Dodatkowe wskazōwki dlŏ Plugawyj Inteligyncyje, spamiyntywane, jak wyjdziesz z pola. Szpil, docelowy jynzyk i format ôdpowiedzi sōm zawdy przidŏwane."
  val restoreDefault = "Prziwrōć wychodne"
  val pressAButton = "Naciś knefel…"
  val noHotkey = "Żŏdyn"
  def hotkeyUnavailable(problem: HotkeyProblem) = problem match
    case HotkeyProblem.NoDevices =>
      "Skrōt niydostympny: niy szło znojś żŏdnych urzōndzyń wejściowych (evdev działŏ ino na Linuksie) – użyj knefla Przełōż abo `lexmechanic trigger`."
    case HotkeyProblem.NoPermission =>
      "Skrōt niydostympny: niy ma dostympu do urzōndzyń z kneflami – puść `sudo usermod -aG input $USER` i zaloguj sie zaś."
    case HotkeyProblem.NoInputMonitoring =>
      "Skrōt niydostympny: dej Lexmechanikowi (abo terminalowi, w kerym łazi) zgoda na Monitorowanie wprowadzania w Ustawieniach systymowych > Prywatność i ochrona, a potym puść go zaś."

  val noRegion =
    "Ôbszar jeszcze niy je skalibrowany – posyłany je cołki ekran. Użyj knefla Ustŏw, coby ôznaczyć ôkno dialogu."
  val emptyHistory = "Naciś skrōt, jak na ekranie je dialog."
  val earlier = "Wczaśnij"

  val calibrationInstructions = "Ôznacz ôkno dialogu, a potym spamiyntej."
  val cancel = "Ôdwołej"
  val saveRegion = "Spamiyntej ôbszar"
  val fullScreenshot = "Cołki zrzut ekranu"

  val middleClick = "Postrzodkowy knefel"
  val mouseBack = "Mysz 4 (nazŏd)"
  val mouseForward = "Mysz 5 (dalij)"
  def mouseButton(number: Int) = s"Mysz $number"
  def key(code: Int) = s"Klawisz $code"
