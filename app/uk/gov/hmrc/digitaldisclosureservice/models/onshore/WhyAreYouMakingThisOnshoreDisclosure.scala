/*
 * Copyright 2024 HM Revenue & Customs
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

package models

sealed trait WhyAreYouMakingThisOnshoreDisclosure

object WhyAreYouMakingThisOnshoreDisclosure extends Enumerable.Implicits {

  case object DidNotNotifyHasExcuse extends WithName("didNotNotifyHasExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object InaccurateReturnWithCare
      extends WithName("inaccurateReturnWithCare")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object NotFileHasExcuse extends WithName("notFileHasExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object InaccurateReturnNoCare
      extends WithName("inaccurateReturnNoCare")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DidNotNotifyNoExcuse extends WithName("didNotNotifyNoExcuse") with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberatelyDidNotNotify
      extends WithName("deliberatelyDidNotNotify")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberateInaccurateReturn
      extends WithName("deliberateInaccurateReturn")
      with WhyAreYouMakingThisOnshoreDisclosure
  case object DeliberatelyDidNotFile
      extends WithName("deliberatelyDidNotFile")
      with WhyAreYouMakingThisOnshoreDisclosure

  val values: Seq[WhyAreYouMakingThisOnshoreDisclosure] = Seq(
    DidNotNotifyHasExcuse,
    InaccurateReturnWithCare,
    NotFileHasExcuse,
    InaccurateReturnNoCare,
    DidNotNotifyNoExcuse,
    DeliberatelyDidNotNotify,
    DeliberateInaccurateReturn,
    DeliberatelyDidNotFile
  )

  implicit val enumerable: Enumerable[WhyAreYouMakingThisOnshoreDisclosure] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
