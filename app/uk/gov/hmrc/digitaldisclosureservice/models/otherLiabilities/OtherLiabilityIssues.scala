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

sealed trait OtherLiabilityIssues

object OtherLiabilityIssues extends Enumerable.Implicits {

  case object EmployerLiabilities extends WithName("employerLiabilities") with OtherLiabilityIssues
  case object VatIssues extends WithName("vatIssues") with OtherLiabilityIssues
  case object InheritanceTaxIssues extends WithName("inheritanceTaxIssues") with OtherLiabilityIssues
  case object Class2National extends WithName("class2National") with OtherLiabilityIssues
  case object Other extends WithName("other") with OtherLiabilityIssues
  case object NoExclusion extends WithName("noExclusion") with OtherLiabilityIssues

  val values: Seq[OtherLiabilityIssues] = Seq(
    EmployerLiabilities,
    VatIssues,
    InheritanceTaxIssues,
    Class2National,
    Other,
    NoExclusion
  )

  implicit val enumerable: Enumerable[OtherLiabilityIssues] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
