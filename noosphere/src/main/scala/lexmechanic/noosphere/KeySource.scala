package lexmechanic.noosphere

import io.circe.Codec

enum KeySource:
  case Stored, Environment, Missing

object KeySource:
  given Codec[KeySource] = nameCodec(values, "key source")
