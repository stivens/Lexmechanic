package lexmechanic.noosphere

import io.circe.derivation.ConfiguredCodec

final case class Settings(
    provider: Option[Heretek] = None,
    model: Option[String] = None,
    apiKey: Option[String] = None,
    game: Option[String] = None,
    language: Option[Language] = None,
    interfaceLanguage: Option[String] = None,
    captain: Option[Gender] = None,
    instructions: Option[String] = None,
    port: Option[Int] = None,
    captureCommand: Option[List[String]] = None,
    openCommand: Option[List[String]] = None
) derives ConfiguredCodec
