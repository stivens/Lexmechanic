package lexmechanic.noosphere

import io.circe.Codec

enum Heretek(val keyVariable: String, val defaultModel: String):
  case Gemini extends Heretek("GEMINI_API_KEY", "gemini-3.5-flash-lite")
  case OpenRouter extends Heretek("OPENROUTER_API_KEY", "google/gemma-4-31b-it:free")
  case Mistral extends Heretek("MISTRAL_API_KEY", "mistral-small-latest")
  case Claude extends Heretek("ANTHROPIC_API_KEY", "claude-opus-5-5")

object Heretek:
  given Codec[Heretek] = nameCodec(values, "provider")
