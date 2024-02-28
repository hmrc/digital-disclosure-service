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

sealed trait YourLegalInterpretation

object YourLegalInterpretation extends Enumerable.Implicits {

  case object YourResidenceStatus extends WithName("yourResidenceStatus") with YourLegalInterpretation
  case object YourDomicileStatus extends WithName("yourDomicileStatus") with YourLegalInterpretation
  case object TheRemittanceBasis extends WithName("theRemittanceBasis") with YourLegalInterpretation
  case object HowIncomeArisingInATrust extends WithName("howIncomeArisingInATrust") with YourLegalInterpretation
  case object TheTransferOfAssets extends WithName("theTransferOfAssets") with YourLegalInterpretation
  case object HowIncomeArisingInAnOffshore extends WithName("howIncomeArisingInAnOffshore") with YourLegalInterpretation
  case object InheritanceTaxIssues extends WithName("inheritanceTaxIssues") with YourLegalInterpretation
  case object WhetherIncomeShouldBeTaxed extends WithName("whetherIncomeShouldBeTaxed") with YourLegalInterpretation
  case object AnotherIssue extends WithName("anotherIssue") with YourLegalInterpretation
  case object NoExclusion extends WithName("noExclusion") with YourLegalInterpretation

  val values: Seq[YourLegalInterpretation] = Seq(
    YourResidenceStatus,
    YourDomicileStatus,
    TheRemittanceBasis,
    HowIncomeArisingInATrust,
    TheTransferOfAssets,
    HowIncomeArisingInAnOffshore,
    InheritanceTaxIssues,
    WhetherIncomeShouldBeTaxed,
    AnotherIssue,
    NoExclusion
  )

  implicit val enumerable: Enumerable[YourLegalInterpretation] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
