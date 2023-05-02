/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package viewmodels

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Key
import models._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

import scala.language.implicitConversions

object implicits extends ImplicitConversions

trait ImplicitConversions {

  implicit def stringToText(string: String)(implicit messages: Messages): Text =
    Text(messages(string))

  implicit def stringToKey(string: String)(implicit messages: Messages): Key =
    Key(content = HtmlContent(messages(string)))

  implicit def stringOptionToText(stringOpt: Option[String])(implicit messages: Messages): Text =
    stringOpt.map(stringToText).getOrElse(Text("-"))

  implicit def booleanToText(boolean: Boolean)(implicit messages: Messages): Text = {
    if (boolean) Text(messages("service.yes"))
    else Text(messages("service.no"))
  }

  implicit def booleanOptionToText(booleanOption: Option[Boolean])(implicit messages: Messages): Text =
    booleanOption.map(booleanToText).getOrElse(Text("-"))

  implicit def YesNoOrUnsureToText(yesNoOrUnsure: YesNoOrUnsure)(implicit messages: Messages): Text = 
    yesNoOrUnsure match {
      case YesNoOrUnsure.Yes => Text(messages("service.yes"))
      case YesNoOrUnsure.No => Text(messages("service.no"))
      case YesNoOrUnsure.Unsure => Text(messages("service.unsure"))
    }

  implicit def yesNoOrUnsureOptionToText(yesNoOrUnsureOption: Option[YesNoOrUnsure])(implicit messages: Messages): Text =
    yesNoOrUnsureOption.map(YesNoOrUnsureToText).getOrElse(Text("-"))
    
}
