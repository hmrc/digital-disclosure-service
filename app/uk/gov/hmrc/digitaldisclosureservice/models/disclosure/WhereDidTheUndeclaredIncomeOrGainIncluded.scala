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

sealed trait WhereDidTheUndeclaredIncomeOrGainIncluded

object WhereDidTheUndeclaredIncomeOrGainIncluded extends Enumerable.Implicits {

  case object Dividends extends WithName("dividends") with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object Interest extends WithName("interest") with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object PropertyIncome extends WithName("propertyIncome") with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object ResidentialPropertyGain
      extends WithName("residentialPropertyGain")
      with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object SelfEmploymentIncome
      extends WithName("selfEmploymentIncome")
      with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object OtherGains extends WithName("otherGains") with WhereDidTheUndeclaredIncomeOrGainIncluded
  case object SomewhereElse extends WithName("somewhereElse") with WhereDidTheUndeclaredIncomeOrGainIncluded

  val values: Seq[WhereDidTheUndeclaredIncomeOrGainIncluded] = Seq(
    Dividends,
    Interest,
    PropertyIncome,
    ResidentialPropertyGain,
    SelfEmploymentIncome,
    OtherGains,
    SomewhereElse
  )

  implicit val enumerable: Enumerable[WhereDidTheUndeclaredIncomeOrGainIncluded] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
