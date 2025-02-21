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

final case class OnshoreTaxYearLiabilities(
                                            nonBusinessIncome: Option[BigInt] = None,
                                            businessIncome: Option[BigInt] = None,
                                            lettingIncome: Option[BigInt] = None,
                                            gains: Option[BigInt],
                                            unpaidTax: BigInt,
                                            niContributions: BigInt,
                                            interest: BigInt,
                                            penaltyRate: BigDecimal,
                                            penaltyRateReason: String,
                                            undeclaredIncomeOrGain: Option[String] = None,
                                            residentialTaxReduction: Option[Boolean]
                                          ) {
  def toXml: NodeSeq = {
    <onshoreTaxYearLiabilities>
      {nonBusinessIncome.map(income => <nonBusinessIncome>{income}</nonBusinessIncome>).getOrElse(NodeSeq.Empty)}
      {businessIncome.map(income => <businessIncome>{income}</businessIncome>).getOrElse(NodeSeq.Empty)}
      {lettingIncome.map(income => <lettingIncome>{income}</lettingIncome>).getOrElse(NodeSeq.Empty)}
      {gains.map(g => <gains>{g}</gains>).getOrElse(NodeSeq.Empty)}
      <unpaidTax>{unpaidTax}</unpaidTax>
      <niContributions>{niContributions}</niContributions>
      <interest>{interest}</interest>
      <penaltyRate>{penaltyRate}</penaltyRate>
      <penaltyRateReason>{penaltyRateReason}</penaltyRateReason>
      {undeclaredIncomeOrGain.map(income => <undeclaredIncomeOrGain>{income}</undeclaredIncomeOrGain>).getOrElse(NodeSeq.Empty)}
      {residentialTaxReduction.map(reduction => <residentialTaxReduction>{reduction}</residentialTaxReduction>).getOrElse(NodeSeq.Empty)}
    </onshoreTaxYearLiabilities>
  }
}

object OnshoreTaxYearLiabilities {
  implicit val format = Json.format[OnshoreTaxYearLiabilities]
}