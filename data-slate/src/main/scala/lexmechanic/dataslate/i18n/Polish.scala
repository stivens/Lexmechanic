package lexmechanic.dataslate.i18n

import lexmechanic.noosphere.HotkeyProblem

object Polish extends Messages:
  val name = "Polski"
  val lang = "pl"
  override val languageNames = Map("szl" -> "śląski", "csb" -> "kaszubski")

  val statusOffline = "Demon nie odpowiada"
  val statusBusy = "Kogitowanie…"
  val statusFailed = "Błąd"
  val statusReady = "Gotowy"

  val original = "Oryginał"
  val clear = "Wyczyść"
  val calibrate = "Kalibruj"
  val translate = "Tłumacz"

  val game = "Gra"
  val anyGame = "Dowolna gra"
  val translateInto = "Tłumacz na"
  val interfaceLanguage = "Interfejs"
  val hotkey = "Skrót"
  val hotkeyLearnHint = "Kliknij, a potem naciśnij przycisk myszy lub klawisz, którego chcesz używać"
  val hotkeyCancelHint = "Kliknij, aby anulować"
  val instructions = "Instrukcje"
  val instructionsHint =
    "Dodatkowe wskazówki dla Plugawej Inteligencji, zapisywane po wyjściu z pola. Gra, język docelowy i format odpowiedzi są dodawane zawsze."
  val restoreDefault = "Przywróć domyślne"
  val pressAButton = "Naciśnij przycisk…"
  val noHotkey = "Brak"
  def hotkeyUnavailable(problem: HotkeyProblem) = problem match
    case HotkeyProblem.NoDevices =>
      "Skrót niedostępny: nie znaleziono urządzeń wejściowych (evdev działa tylko na Linuksie) – użyj przycisku Tłumacz lub `lexmechanic trigger`."
    case HotkeyProblem.NoPermission =>
      "Skrót niedostępny: brak dostępu do urządzeń z przyciskami – uruchom `sudo usermod -aG input $USER` i zaloguj się ponownie."
    case HotkeyProblem.NoInputMonitoring =>
      "Skrót niedostępny: zezwól Lexmechanikowi (lub terminalowi, w którym działa) na Monitorowanie wprowadzania w Ustawieniach systemowych > Prywatność i ochrona, a potem uruchom go ponownie."

  val noRegion =
    "Nie skalibrowano jeszcze obszaru – wysyłany jest cały ekran. Użyj przycisku Kalibruj, aby zaznaczyć okno dialogowe."
  val emptyHistory = "Naciśnij skrót, gdy na ekranie jest dialog."
  val earlier = "Wcześniej"

  val calibrationInstructions = "Zaznacz okno dialogowe, przeciągając po nim, a potem zapisz."
  val cancel = "Anuluj"
  val saveRegion = "Zapisz obszar"
  val fullScreenshot = "Pełny zrzut ekranu"

  val middleClick = "Środkowy przycisk"
  val mouseBack = "Mysz 4 (wstecz)"
  val mouseForward = "Mysz 5 (dalej)"
  def mouseButton(number: Int) = s"Mysz $number"
  def key(code: Int) = s"Klawisz $code"
