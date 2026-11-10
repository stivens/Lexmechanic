package lexmechanic.noosphere

import io.circe.Codec

final case class Language(code: String, english: String, native: String, description: Option[String] = None):
  def describe: String = description.getOrElse(if native == english then english else s"$english ($native)")

object Language:
  val fallback: Language = Language("en-US", "English (US)", "English (US)")

  // The first eleven are pinned; the rest follow by approximate total speakers.
  val all: List[Language] = List(
    fallback,
    Language("en-GB", "English (UK)", "English (UK)"),
    Language("pl", "Polish", "Polski"),
    Language(
      "szl",
      "Silesian",
      "Ślōnsko godka",
      Some(
        "Ślōnsko godka (tyż: ślōnsko mowa, ślōnski jynzyk, ślōnsko rzecz, ślōnsko szpracha; miyndzy Ślōnzokami: pō naszymu) – godka, keryj używajōm rdzynne miyszkańce Gōrnego Ślōnska a reliktowo tajla miyszkancōw Dolnego Ślōnska"
      )
    ),
    Language("de", "German", "Deutsch"),
    Language("fr", "French", "Français"),
    Language("es", "Spanish", "Español"),
    Language("pt", "Portuguese", "Português"),
    Language("zh-Hans", "Chinese (Simplified)", "简体中文"),
    Language("ja", "Japanese", "日本語"),
    Language("ko", "Korean", "한국어"),
    Language("hi", "Hindi", "हिन्दी"),
    Language("ar", "Arabic", "العربية"),
    Language("bn", "Bengali", "বাংলা"),
    Language("ru", "Russian", "Русский"),
    Language("id", "Indonesian", "Bahasa Indonesia"),
    Language("ur", "Urdu", "اردو"),
    Language("mr", "Marathi", "मराठी"),
    Language("te", "Telugu", "తెలుగు"),
    Language("tr", "Turkish", "Türkçe"),
    Language("ta", "Tamil", "தமிழ்"),
    Language("sw", "Swahili", "Kiswahili"),
    Language("vi", "Vietnamese", "Tiếng Việt"),
    Language("fil", "Filipino", "Filipino"),
    Language("ms", "Malay", "Bahasa Melayu"),
    Language("ha", "Hausa", "Hausa"),
    Language("fa", "Persian", "فارسی"),
    Language("pa", "Punjabi", "ਪੰਜਾਬੀ"),
    Language("it", "Italian", "Italiano"),
    Language("jv", "Javanese", "Basa Jawa"),
    Language("gu", "Gujarati", "ગુજરાતી"),
    Language("th", "Thai", "ไทย"),
    Language("am", "Amharic", "አማርኛ"),
    Language("kn", "Kannada", "ಕನ್ನಡ"),
    Language("yo", "Yoruba", "Yorùbá"),
    Language("zh-Hant", "Chinese (Traditional)", "繁體中文"),
    Language("my", "Burmese", "မြန်မာ"),
    Language("or", "Odia", "ଓଡ଼ିଆ"),
    Language("ps", "Pashto", "پښتو"),
    Language("uk", "Ukrainian", "Українська"),
    Language("ml", "Malayalam", "മലയാളം"),
    Language("om", "Oromo", "Afaan Oromoo"),
    Language("uz", "Uzbek", "Oʻzbekcha"),
    Language("sd", "Sindhi", "سنڌي"),
    Language("su", "Sundanese", "Basa Sunda"),
    Language("ne", "Nepali", "नेपाली"),
    Language("ig", "Igbo", "Igbo"),
    Language("ceb", "Cebuano", "Cebuano"),
    Language("zu", "Zulu", "isiZulu"),
    Language("ro", "Romanian", "Română"),
    Language("nl", "Dutch", "Nederlands"),
    Language("mg", "Malagasy", "Malagasy"),
    Language("az", "Azerbaijani", "Azərbaycanca"),
    Language("so", "Somali", "Soomaali"),
    Language("ku", "Kurdish", "Kurdî"),
    Language("xh", "Xhosa", "isiXhosa"),
    Language("km", "Khmer", "ខ្មែរ"),
    Language("af", "Afrikaans", "Afrikaans"),
    Language("si", "Sinhala", "සිංහල"),
    Language("as", "Assamese", "অসমীয়া"),
    Language("kk", "Kazakh", "Қазақ тілі"),
    Language("rw", "Kinyarwanda", "Ikinyarwanda"),
    Language("sn", "Shona", "ChiShona"),
    Language("ht", "Haitian Creole", "Kreyòl ayisyen"),
    Language("hu", "Hungarian", "Magyar"),
    Language("el", "Greek", "Ελληνικά"),
    Language("sv", "Swedish", "Svenska"),
    Language("sr", "Serbian", "Српски"),
    Language("cs", "Czech", "Čeština"),
    Language("ug", "Uyghur", "ئۇيغۇرچە"),
    Language("he", "Hebrew", "עברית"),
    Language("ti", "Tigrinya", "ትግርኛ"),
    Language("ca", "Catalan", "Català"),
    Language("tg", "Tajik", "Тоҷикӣ"),
    Language("bg", "Bulgarian", "Български"),
    Language("qu", "Quechua", "Runasimi"),
    Language("sk", "Slovak", "Slovenčina"),
    Language("sq", "Albanian", "Shqip"),
    Language("tk", "Turkmen", "Türkmençe"),
    Language("gn", "Guarani", "Avañeʼẽ"),
    Language("hy", "Armenian", "Հայերեն"),
    Language("da", "Danish", "Dansk"),
    Language("fi", "Finnish", "Suomi"),
    Language("hr", "Croatian", "Hrvatski"),
    Language("mn", "Mongolian", "Монгол"),
    Language("lo", "Lao", "ລາວ"),
    Language("be", "Belarusian", "Беларуская"),
    Language("ky", "Kyrgyz", "Кыргызча"),
    Language("nb", "Norwegian", "Norsk"),
    Language("ka", "Georgian", "ქართული"),
    Language("lt", "Lithuanian", "Lietuvių"),
    Language("gl", "Galician", "Galego"),
    Language("bs", "Bosnian", "Bosanski"),
    Language("sl", "Slovenian", "Slovenščina"),
    Language("eo", "Esperanto", "Esperanto"),
    Language("mk", "Macedonian", "Македонски"),
    Language("lv", "Latvian", "Latviešu"),
    Language("ga", "Irish", "Gaeilge"),
    Language("et", "Estonian", "Eesti"),
    Language("eu", "Basque", "Euskara"),
    Language("cy", "Welsh", "Cymraeg"),
    Language("lb", "Luxembourgish", "Lëtzebuergesch"),
    Language("mt", "Maltese", "Malti"),
    Language("is", "Icelandic", "Íslenska"),
    Language("csb", "Kashubian", "Kaszëbsczi"),
    Language("la", "Latin", "Latina")
  )

  private val byCode: Map[String, Language] = all.map(l => l.code -> l).toMap

  given Codec[Language] = lookupCodec(all, "language")(_.code)

  def primarySubtag(code: String): String = code.takeWhile(_ != '-')

  /** Matches a BCP 47 tag such as `pl-PL`, `zh-Hant-TW` or `nb-NO`. */
  def forTag(tag: String): Option[Language] =
    val language :: subtags = tag.split('-').toList: @unchecked
    val primary = language.toLowerCase
    val script = subtags.find(s => s.length == 4 && s.forall(_.isLetter)).map(_.toLowerCase)
    val region = subtags.find(s => s.length == 2 && s.forall(_.isLetter)).fold("")(_.toUpperCase)
    val code = (primary, script, region) match
      case ("zh", Some("hant"), _) | ("zh", _, "TW" | "HK" | "MO") => "zh-Hant"
      case ("zh", _, _)                                            => "zh-Hans"
      case ("no" | "nn", _, _)                                     => "nb"
      case ("tl", _, _)                                            => "fil"
      case _                                                       => s"$primary-$region"
    byCode.get(code).orElse(all.find(l => primarySubtag(l.code) == primary))
