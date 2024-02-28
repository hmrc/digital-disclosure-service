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

sealed trait WhyAreYouMakingThisDisclosure

object WhyAreYouMakingThisDisclosure extends Enumerable.Implicits {

  case object DidNotNotifyHasExcuse extends WithName("didNotNotifyHasExcuse") with WhyAreYouMakingThisDisclosure
  case object InaccurateReturnWithCare extends WithName("inaccurateReturnWithCare") with WhyAreYouMakingThisDisclosure
  case object NotFileHasExcuse extends WithName("notFileHasExcuse") with WhyAreYouMakingThisDisclosure
  case object InaccurateReturnNoCare extends WithName("inaccurateReturnNoCare") with WhyAreYouMakingThisDisclosure
  case object DidNotNotifyNoExcuse extends WithName("didNotNotifyNoExcuse") with WhyAreYouMakingThisDisclosure
  case object DeliberatelyDidNotNotify extends WithName("deliberatelyDidNotNotify") with WhyAreYouMakingThisDisclosure
  case object DeliberateInaccurateReturn
      extends WithName("deliberateInaccurateReturn")
      with WhyAreYouMakingThisDisclosure
  case object DeliberatelyDidNotFile extends WithName("deliberatelyDidNotFile") with WhyAreYouMakingThisDisclosure

  val values: Seq[WhyAreYouMakingThisDisclosure] = Seq(
    DidNotNotifyHasExcuse,
    InaccurateReturnWithCare,
    NotFileHasExcuse,
    InaccurateReturnNoCare,
    DidNotNotifyNoExcuse,
    DeliberatelyDidNotNotify,
    DeliberateInaccurateReturn,
    DeliberatelyDidNotFile
  )

  implicit val enumerable: Enumerable[WhyAreYouMakingThisDisclosure] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
