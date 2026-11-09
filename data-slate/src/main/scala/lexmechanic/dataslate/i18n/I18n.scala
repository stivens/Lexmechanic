package lexmechanic.dataslate.i18n

import com.raquo.airstream.web.WebStorageVar
import com.raquo.laminar.api.L.*

object I18n:
  // Remembered so the page keeps its language while the daemon is offline.
  val language: WebStorageVar[String] =
    WebStorageVar.localStorage("lexmechanic.ui-language", syncOwner = None).text(default = BrowserLocale.interface.lang)

  val messages: Signal[Messages] = language.signal.map(Messages.forLanguage).distinct

  def t(message: Messages => String): Signal[String] = messages.map(message)

  def t[A, B](value: Signal[A])(message: (Messages, A) => B): Signal[B] =
    messages.combineWithFn(value)(message)
