package lexmechanic.servitor

import io.circe.derivation.ConfiguredCodec
import io.circe.parser.decode
import io.circe.syntax.*
import lexmechanic.noosphere.{*, given}
import lexmechanic.servitor.desktop.Pictcaptor
import lexmechanic.servitor.translation.{Gender, Heretek, Mandate}

import java.nio.file.{Files, Path, Paths}
import scala.scalanative.meta.LinktimeInfo
import scala.util.Properties

final case class Config(
    provider: Heretek = Heretek.Gemini,
    model: Option[String] = None,
    apiKeys: Map[String, String] = Map.empty,
    game: String = "Warhammer 40,000: Rogue Trader",
    language: Option[Language] = None,
    interfaceLanguage: Option[String] = None,
    captain: Gender = Gender.Male,
    instructions: Option[String] = None,
    port: Int = 7040,
    captureCommand: Option[List[String]] = None,
    openCommand: List[String] = Config.defaultOpenCommand,
    region: Option[Region] = None,
    hotkey: Option[Int] = Some(EvdevKeys.BtnExtra)
) derives ConfiguredCodec:
  // Unset until the UI saves the browser's language; translations before that need some target.
  def targetLanguage: Language = language.getOrElse(Language.fallback)

  def activeInstructions: String = instructions.getOrElse(Instructions.default)

  def activeCaptureCommand: List[String] = captureCommand.getOrElse(Pictcaptor.defaultCommand)

  def mandate: Mandate = Mandate(game, targetLanguage, captain, activeInstructions)

object Config:
  private val defaultOpenCommand = List(if LinktimeInfo.isMac then "open" else "xdg-open", "{url}")

  private def xdgDir(variable: String, fallback: String): Path =
    sys.env.get(variable).map(Paths.get(_)).getOrElse(Paths.get(Properties.userHome, fallback)).resolve("lexmechanic")

  val file: Path = xdgDir("XDG_CONFIG_HOME", ".config").resolve("config.json")
  val cacheDir: Path = xdgDir("XDG_CACHE_HOME", ".cache")

  def load(): Config =
    if Files.exists(file) then
      decode[Config](Files.readString(file))
        .fold(e => throw RuntimeException(s"Invalid $file: ${e.getMessage}"), identity)
    else
      val config = Config()
      save(config)
      config

  def save(config: Config): Unit =
    Files.createDirectories(file.getParent)
    Files.writeString(file, config.asJson.spaces2)
