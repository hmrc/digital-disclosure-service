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

import play.api.libs.json._
import scala.xml._

final case class TaxYearLiabilities(
                                     income: BigInt,
                                     chargeableTransfers: BigInt,
                                     capitalGains: BigInt,
                                     unpaidTax: BigInt,
                                     interest: BigInt,
                                     penaltyRate: BigDecimal,
                                     penaltyRateReason: String,
                                     undeclaredIncomeOrGain: Option[String] = None,
                                     foreignTaxCredit: Boolean
                                   ) {
  def toXml: NodeSeq =
    <taxYearLiabilities>
      <income>{income}</income>
      <chargeableTransfers>{chargeableTransfers}</chargeableTransfers>
      <capitalGains>{capitalGains}</capitalGains>
      <unpaidTax>{unpaidTax}</unpaidTax>
      <interest>{interest}</interest>
      <penaltyRate>{penaltyRate}</penaltyRate>
      <penaltyRateReason>{penaltyRateReason}</penaltyRateReason>
      {undeclaredIncomeOrGain.map(value => <undeclaredIncomeOrGain>{value}</undeclaredIncomeOrGain>).getOrElse(NodeSeq.Empty)}
      <foreignTaxCredit>{foreignTaxCredit}</foreignTaxCredit>
    </taxYearLiabilities>
}

object TaxYearLiabilities {
  implicit val format: Format[TaxYearLiabilities] = Json.format[TaxYearLiabilities]
}
