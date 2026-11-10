package lexmechanic.noosphere

import io.circe.Codec

final case class State(
    busy: Boolean,
    error: Option[String],
    region: Option[Region],
    game: String,
    language: Option[Language],
    interfaceLanguage: Option[String],
    instructions: String,
    hotkey: HotkeyState,
    history: List[Entry],
    provider: Heretek,
    model: Option[String],
    apiKey: Option[String],
    captain: Gender,
    port: Int,
    restartRequired: Boolean,
    captureCommand: List[String],
    defaultCaptureCommand: List[String],
    openCommand: List[String],
    defaultOpenCommand: List[String]
) derives Codec.AsObject
