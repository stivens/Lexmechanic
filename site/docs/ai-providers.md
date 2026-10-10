---
nav_order: 4
title: AI providers
---

Lexmechanic sends the dialogue image to an AI model of your choice, using your own API key. Pick the provider in the
advanced settings (the cog icon) and paste its key there.

| Provider | Default model | Key variable | Cost |
|---|---|---|---|
| [Gemini](https://aistudio.google.com/apikey) (default) | `gemini-3.5-flash-lite` | `GEMINI_API_KEY` | Free tier |
| [OpenRouter](https://openrouter.ai/keys) | `google/gemma-4-31b-it:free` | `OPENROUTER_API_KEY` | Free models |
| [Mistral](https://console.mistral.ai/api-keys) | `mistral-small-latest` | `MISTRAL_API_KEY` | Free tier |
| [Claude](https://console.anthropic.com/) | `claude-opus-5-5` | `ANTHROPIC_API_KEY` | Paid |

- **Which one:** start with Gemini. If you hit its rate limits, switch to OpenRouter or Mistral.
- **Model:** leave the field empty to use the default above, or enter any model the provider offers that accepts images.
- **Keys:** the key in the settings wins. Without one, the daemon falls back to the key variable from its environment,
  which a daemon started by autostart or a desktop launcher does not see.
- **Storage:** keys are saved in plain text in `~/.config/lexmechanic/config.json`, together with the other settings.
