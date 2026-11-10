package lexmechanic.dataslate

import com.raquo.laminar.api.L.*
import lexmechanic.dataslate.App.{offline, state}
import lexmechanic.dataslate.i18n.I18n.{messages, t}
import lexmechanic.dataslate.i18n.Messages
import lexmechanic.noosphere.*

object Pickers:
  def choice[E](values: Seq[E], key: E => String, label: E => Modifier[HtmlElement], current: Signal[Option[E]])(
      save: E => Unit
  ) = select(
    disabled <-- offline,
    values.map(e => option(value := key(e), label(e))),
    controlled(
      value <-- current.map(_.fold("")(key)),
      onChange.mapToValue --> { k => values.find(key(_) == k).foreach(save) }
    )
  )

  def targetLanguage(save: Settings => Unit) = choice(
    Language.all,
    _.code,
    l => text <-- t(_.languageLabel(l)),
    state.map(_.flatMap(_.language))
  )(l => save(Settings(language = Some(l))))

  def interfaceLanguage(save: Settings => Unit) =
    choice(Messages.all, _.lang, _.name, messages.map(Some(_)))(m => save(Settings(interfaceLanguage = Some(m.lang))))
