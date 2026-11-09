package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.{Gender, HotkeyProblem, Region}

object Silesian extends Messages:
  val name = "Ślōnsko godka"
  val lang = "szl"
  // Browsers accept the szl locale but have no Silesian language names, so they fall back to English ones.
  override val languageNamesLocale = None

  val statusOffline = "Dymōn niy ôdpowiadŏ"
  val statusBusy = "Rachowanie…"
  val statusFailed = "Feler"
  val statusReady = "Fertich"

  val offlineBanner = "Dymōn niy ôdpowiadŏ. Puść lexmechanic – strōna sama sie połōnczy."
  val dismiss = "Zawrzij"

  val original = "Ôryginał"
  val calibrate = "Ustŏw"
  val translate = "Przełōż"
  val settings = "Ustawiynia"
  val back = "Nazŏd"

  val game = "Szpil"
  val anyGame = "Jaki bądź szpil"
  val translateInto = "Przełōż na"
  val interfaceLanguage = "Jynzyk"
  val hotkey = "Skrōt"
  val change = "Zmiyń"
  val instructions = "Instrukcyje"
  val instructionsHint =
    "Dodatkowe wskazōwki dlŏ Plugawyj Inteligyncyje. Szpil, docelowy jynzyk i format ôdpowiedzi sōm zawdy przidŏwane."
  val restoreDefault = "Prziwrōć wychodne"
  val saved = "Spamiyntane"

  val sectionTranslation = "Przekłŏd"
  val sectionCapture = "Zrzut"
  val sectionAi = "AI"
  val sectionInterface = "Aplikacyjŏ"
  val sectionSystem = "Systym"

  val region = "Ôbszar dialogu"
  val regionNotSet = "Niy ustawiōny – posyłany je cołki ekran"
  def regionSet(region: Region) = s"${region.width}×${region.height} px przi ${region.x}, ${region.y}"

  val provider = "Dostawca AI"
  val model = "Model"
  val apiKey = "Klucz API"
  def apiKeyFallback(variable: String) = s"abo ustŏw $variable"
  def apiKeyFromEnvironment(variable: String) = s"Używany je $variable ze zmiynnych strzodowiska"
  val noApiKey = "Niy ma klucza – przekłŏdanie niy pōjdzie"
  val captain = "Postać graczŏ"
  def gender(gender: Gender) = gender match
    case Gender.Male   => "Chop"
    case Gender.Female => "Baba"
  val port = "Port"
  val portRestart = "Nowy port zacznie działać, jak zaś puścisz dymōna."
  val captureCommand = "Polecynie zrzutu ekranu"
  val captureCommandHint = "Jedyn argumynt na wiersz; {file} je zastympowane drōgōm, kaj mŏ trefić zrzut."
  val openCommand = "Ôtwiyrej aplikacyjõ bez"
  val openCommandHint =
    "Jedyn argumynt na wiersz; {url} je zastympowane adresym tyj strōny. Ôstŏw prōzne, coby nic niy ôtwiyrać."
  val pressAButton = "Naciś knefel…"
  val noHotkey = "Żŏdyn"
  def hotkeyUnavailable(problem: HotkeyProblem) = problem match
    case HotkeyProblem.NoDevices =>
      "Skrōt niydostympny: niy szło znojś żŏdnych urzōndzyń wejściowych (evdev działŏ ino na Linuksie) – użyj knefla Przełōż abo `lexmechanic trigger`."
    case HotkeyProblem.NoPermission =>
      "Skrōt niydostympny: niy ma dostympu do urzōndzyń z kneflami – puść `sudo usermod -aG input $USER` i zaloguj sie zaś."

  val setupTitle = "Rychtowanie"
  val setupKey = "Klucz dostawcy AI"
  val openSettings = "Ôtwōrz ustawiynia"
  def pressHotkey(key: String) = s"Jak na ekranie je dialog, naciś: $key."

  val earlier = "Wczaśnij"
  val clearHistory = "Wypucuj historyjõ"

  val calibrationInstructions = "Ôznacz ôkno dialogu, a potym spamiyntej."
  val cancel = "Ôdwołej"
  val saveRegion = "Spamiyntej ôbszar"
  val fullScreenshot = "Cołki zrzut ekranu"

  val middleClick = "Postrzodkowy knefel"
  val mouseBack = "Mysz 4 (nazŏd)"
  val mouseForward = "Mysz 5 (dalij)"
  def mouseButton(number: Int) = s"Mysz $number"
  def key(code: Int) = s"Klawisz $code"
