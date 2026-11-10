package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.{Gender, HotkeyProblem, Region}

object Polish extends Messages:
  val name = "Polski"
  val lang = "pl"
  override val languageNames = Map("szl" -> "śląski", "csb" -> "kaszubski")

  val statusOffline = "Demon nie odpowiada"
  val statusBusy = "Kogitowanie…"
  val statusFailed = "Błąd"
  val statusReady = "Gotowy"

  val offlineBanner = "Demon nie odpowiada. Uruchom lexmechanic – strona połączy się sama."
  val dismiss = "Zamknij"

  val calibrate = "Kalibruj"
  val translate = "Tłumacz"
  val settings = "Ustawienia"
  val back = "Wróć"

  val game = "Gra"
  val anyGame = "Dowolna gra"
  val translateInto = "Tłumacz na"
  val interfaceLanguage = "Język"
  val uiLanguage = "Język interfejsu"
  val hotkey = "Skrót"
  val change = "Zmień"
  val instructions = "Instrukcje"
  val instructionsHint =
    "Dodatkowe wskazówki dla Plugawej Inteligencji. Gra, język docelowy i format odpowiedzi są dodawane zawsze."
  val restoreDefault = "Przywróć domyślne"
  val saved = "Zapisano"

  val sectionTranslation = "Tłumaczenie"
  val sectionCapture = "Przechwytywanie"
  val sectionAi = "AI"
  val sectionInterface = "Interfejs"
  val sectionSystem = "System"

  val region = "Obszar dialogu"
  val regionNotSet = "Nie ustawiono – wysyłany jest cały ekran"
  def regionSet(region: Region) = s"${region.width}×${region.height} px w punkcie ${region.x}, ${region.y}"

  val provider = "Dostawca AI"
  val model = "Model"
  val apiKey = "Klucz API"
  def apiKeyFallback(variable: String) = s"lub ustaw $variable"
  def apiKeyFromEnvironment(variable: String) = s"Używany jest $variable ze zmiennych środowiskowych"
  val noApiKey = "Brak klucza – tłumaczenia się nie powiodą"
  val captain = "Postać gracza"
  def gender(gender: Gender) = gender match
    case Gender.Male   => "Mężczyzna"
    case Gender.Female => "Kobieta"
  val port = "Port"
  val portRestart = "Nowy port zacznie działać po ponownym uruchomieniu demona."
  val captureCommand = "Polecenie zrzutu ekranu"
  val captureCommandHint = "Jeden argument w wierszu; {file} jest zastępowane ścieżką, pod którą ma trafić zrzut."
  val openCommand = "Otwieraj interfejs przez"
  val openCommandHint =
    "Jeden argument w wierszu; {url} jest zastępowane adresem tej strony. Zostaw puste, aby niczego nie otwierać."
  def pressAButton(seconds: Int) = s"Naciśnij przycisk ($seconds s)…"
  val noHotkey = "Brak"
  def hotkeyUnavailable(problem: HotkeyProblem) = problem match
    case HotkeyProblem.NoDevices =>
      "Skrót niedostępny: nie znaleziono urządzeń wejściowych – użyj przycisku Tłumacz lub `lexmechanic trigger`."
    case HotkeyProblem.NoPermission =>
      "Skrót niedostępny: brak dostępu do urządzeń z przyciskami – uruchom `sudo usermod -aG input $USER` i zaloguj się ponownie."
    case HotkeyProblem.NoInputMonitoring =>
      "Skrót niedostępny: zezwól Lexmechanikowi (lub terminalowi, w którym działa) na Monitorowanie wprowadzania w Ustawieniach systemowych > Prywatność i ochrona, a potem uruchom go ponownie."

  val setupTitle = "Przygotowanie"
  val setupKey = "Klucz dostawcy AI"
  val openSettings = "Otwórz ustawienia"
  def pressHotkey(key: String) = s"Gdy na ekranie jest dialog, naciśnij: $key."

  val earlier = "Wcześniej"
  val clearHistory = "Wyczyść historię"

  val calibrationInstructions = "Zaznacz okno dialogowe, przeciągając po nim, a potem zapisz."
  val cancel = "Anuluj"
  val saveRegion = "Zapisz obszar"
  val fullScreenshot = "Pełny zrzut ekranu"

  val middleClick = "Środkowy przycisk"
  val mouseBack = "Mysz 4 (wstecz)"
  val mouseForward = "Mysz 5 (dalej)"
  def mouseButton(number: Int) = s"Mysz $number"
  def key(code: Int) = s"Klawisz $code"
