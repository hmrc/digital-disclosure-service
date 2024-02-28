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

sealed trait TypeOfMortgageDidYouHave

object TypeOfMortgageDidYouHave extends Enumerable.Implicits {

  case object CapitalRepayment extends WithName("capitalRepayment") with TypeOfMortgageDidYouHave
  case object InterestOnly extends WithName("interestOnly") with TypeOfMortgageDidYouHave
  case object Other extends WithName("other") with TypeOfMortgageDidYouHave

  val values: Seq[TypeOfMortgageDidYouHave] = Seq(
    CapitalRepayment,
    InterestOnly,
    Other
  )

  implicit val enumerable: Enumerable[TypeOfMortgageDidYouHave] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
