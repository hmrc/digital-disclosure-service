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

sealed trait IncomeOrGainSource

object IncomeOrGainSource extends Enumerable.Implicits {

  case object Dividends extends WithName("dividends") with IncomeOrGainSource
  case object Interest extends WithName("interest") with IncomeOrGainSource
  case object PropertyIncome extends WithName("propertyIncome") with IncomeOrGainSource
  case object ResidentialPropertyGain extends WithName("residentialPropertyGain") with IncomeOrGainSource
  case object SelfEmploymentIncome extends WithName("selfEmploymentIncome") with IncomeOrGainSource
  case object OtherGains extends WithName("otherGains") with IncomeOrGainSource
  case object SomewhereElse extends WithName("somewhereElse") with IncomeOrGainSource

  val values: Seq[IncomeOrGainSource] = Seq(
    Dividends,
    Interest,
    PropertyIncome,
    ResidentialPropertyGain,
    SelfEmploymentIncome,
    OtherGains,
    SomewhereElse
  )

  implicit val enumerable: Enumerable[IncomeOrGainSource] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
