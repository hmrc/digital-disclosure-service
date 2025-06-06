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

import scala.xml._

sealed trait WhatOnshoreLiabilitiesDoYouNeedToDisclose {
  def toXml: String = this.toString
}

object WhatOnshoreLiabilitiesDoYouNeedToDisclose extends Enumerable.Implicits {

  case object BusinessIncome
      extends WithName("businessIncomeLiabilities")
      with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object Gains extends WithName("capitalGainsTaxLiabilities") with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object CorporationTax
      extends WithName("company.corporationTaxLiabilities")
      with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object DirectorLoan
      extends WithName("company.directorLoanLiabilities")
      with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object LettingIncome
      extends WithName("lettingIncomeFromResidential")
      with WhatOnshoreLiabilitiesDoYouNeedToDisclose
  case object NonBusinessIncome extends WithName("nonBusinessIncome") with WhatOnshoreLiabilitiesDoYouNeedToDisclose

  val values: Seq[WhatOnshoreLiabilitiesDoYouNeedToDisclose] = Seq(
    BusinessIncome,
    Gains,
    CorporationTax,
    DirectorLoan,
    LettingIncome,
    NonBusinessIncome
  )

  implicit val enumerable: Enumerable[WhatOnshoreLiabilitiesDoYouNeedToDisclose] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
