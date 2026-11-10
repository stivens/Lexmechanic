package lexmechanic.servitor.translation

import lexmechanic.noosphere.*
import sttp.ai.claude.ClaudeSyncClient
import sttp.ai.claude.config.ClaudeConfig
import sttp.ai.openai.OpenAISyncClient
import sttp.ai.openai.config.OpenAIConfig
import sttp.client4.*
import sttp.model.Uri
import sttp.tapir.Schema

import java.util.Base64

trait VoxLink extends AutoCloseable:
  def ask(system: String, png: Array[Byte], instruction: String): Dialogue

object VoxLink:
  private given Schema[Line] = Schema.derived
  private given Schema[Dialogue] = Schema.derived

  // A spent free-tier daily quota also answers 429, and every retry re-uploads the screenshot.
  private val MaxRetries = 1

  def apply(heretek: Heretek, key: String, model: String): VoxLink =
    def openAi(baseUrl: Uri) =
      OpenAiVoxLink(OpenAISyncClient(OpenAIConfig(apiKey = key, baseUrl = baseUrl, maxRetries = MaxRetries)), model)
    heretek match
      case Heretek.Gemini     => openAi(uri"https://generativelanguage.googleapis.com/v1beta/openai")
      case Heretek.OpenRouter => openAi(uri"https://openrouter.ai/api/v1")
      case Heretek.Mistral    => openAi(uri"https://api.mistral.ai/v1")
      case Heretek.Claude => ClaudeVoxLink(ClaudeSyncClient(ClaudeConfig(apiKey = key, maxRetries = MaxRetries)), model)

  private final class OpenAiVoxLink(client: OpenAISyncClient, model: String) extends VoxLink:
    import sttp.ai.openai.requests.completions.chat.ChatRequestBody.ChatBody
    import sttp.ai.openai.requests.completions.chat.ChatRequestBody.ChatCompletionModel.CustomChatCompletionModel
    import sttp.ai.openai.requests.completions.chat.message.Content.{ArrayContent, ContentPart, ImageUrlDetails}
    import sttp.ai.openai.requests.completions.chat.message.Message

    def ask(system: String, png: Array[Byte], instruction: String): Dialogue =
      val image = s"data:image/png;base64,${Base64.getEncoder.encodeToString(png)}"
      val body = ChatBody(
        model = CustomChatCompletionModel(model),
        messages = Seq(
          Message.System(system),
          Message.User(ArrayContent(Seq(ContentPart.ImageUrl(ImageUrlDetails(image)), ContentPart.Text(instruction))))
        )
      )
      client.createChatCompletionAs[Dialogue](body)

    def close(): Unit = client.close()

  private final class ClaudeVoxLink(client: ClaudeSyncClient, model: String) extends VoxLink:
    import sttp.ai.claude.models.{ContentBlock, Effort, Message}
    import sttp.ai.claude.requests.MessageRequest

    def ask(system: String, png: Array[Byte], instruction: String): Dialogue =
      val request = MessageRequest
        .withSystem(
          model = model,
          system = system,
          messages = List(
            Message.user(
              List(
                ContentBlock.image("image/png", Base64.getEncoder.encodeToString(png)),
                ContentBlock.text(instruction)
              )
            )
          ),
          maxTokens = 16000
        )
        .withEffort(Effort.Low)
      client.createMessageAs[Dialogue](request)

    def close(): Unit = client.close()
