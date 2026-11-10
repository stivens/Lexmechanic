package lexmechanic.servitor.translation

import lexmechanic.noosphere.{Gender, Language}

final case class Mandate(game: String, language: Language, captain: Gender, instructions: String)
