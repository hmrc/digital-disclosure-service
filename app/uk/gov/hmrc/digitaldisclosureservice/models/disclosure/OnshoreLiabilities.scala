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

package models.disclosure

import play.api.libs.json.{Json, OFormat}
import models._
import models.WhatOnshoreLiabilitiesDoYouNeedToDisclose._
import scala.xml._

final case class OnshoreLiabilities(
                                     behaviour: Option[Set[WhyAreYouMakingThisOnshoreDisclosure]] = None,
                                     excuseForNotNotifying: Option[ReasonableExcuseOnshore] = None,
                                     reasonableCare: Option[ReasonableCareOnshore] = None,
                                     excuseForNotFiling: Option[ReasonableExcuseForNotFilingOnshore] = None,
                                     whatLiabilities: Option[Set[WhatOnshoreLiabilitiesDoYouNeedToDisclose]] = None,
                                     whichYears: Option[Set[OnshoreYears]] = None,
                                     youHaveNotIncludedTheTaxYear: Option[String] = None,
                                     youHaveNotSelectedCertainTaxYears: Option[String] = None,
                                     taxBeforeThreeYears: Option[String] = None,
                                     taxBeforeFiveYears: Option[String] = None,
                                     taxBeforeNineteenYears: Option[String] = None,
                                     disregardedCDF: Option[Boolean] = None,
                                     taxYearLiabilities: Option[Map[String, OnshoreTaxYearWithLiabilities]] = None,
                                     lettingDeductions: Option[Map[String, BigInt]] = None,
                                     lettingProperties: Option[Seq[LettingProperty]] = None,
                                     memberOfLandlordAssociations: Option[Boolean] = None,
                                     landlordAssociations: Option[String] = None,
                                     howManyProperties: Option[String] = None,
                                     corporationTaxLiabilities: Option[Seq[CorporationTaxLiability]] = None,
                                     directorLoanAccountLiabilities: Option[Seq[DirectorLoanAccountLiabilities]] = None
                                   ) {
  def toXml: NodeSeq = {
    <onshoreLiabilities>
      {behaviour.map(b =>
      <behaviour>
        {b.map(reason => <reason>{reason.toXml}</reason>)}
      </behaviour>
    ).getOrElse(NodeSeq.Empty)}
      {excuseForNotNotifying.map(excuse => <excuseForNotNotifying>{excuse.toXml}</excuseForNotNotifying>).getOrElse(NodeSeq.Empty)}
      {reasonableCare.map(care => <reasonableCare>{care.toXml}</reasonableCare>).getOrElse(NodeSeq.Empty)}
      {excuseForNotFiling.map(excuse => <excuseForNotFiling>{excuse.toXml}</excuseForNotFiling>).getOrElse(NodeSeq.Empty)}
      {whatLiabilities.map(liabilities =>
      <whatLiabilities>
        {liabilities.map(liability => <liability>{liability.toXml}</liability>)}
      </whatLiabilities>
    ).getOrElse(NodeSeq.Empty)}
      {whichYears.map(years =>
      <whichYears>
        {years.map(year => <year>{year.toXml}</year>)}
      </whichYears>
    ).getOrElse(NodeSeq.Empty)}
      {youHaveNotIncludedTheTaxYear.map(year => <youHaveNotIncludedTheTaxYear>{year}</youHaveNotIncludedTheTaxYear>).getOrElse(NodeSeq.Empty)}
      {youHaveNotSelectedCertainTaxYears.map(years => <youHaveNotSelectedCertainTaxYears>{years}</youHaveNotSelectedCertainTaxYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeThreeYears.map(tax => <taxBeforeThreeYears>{tax}</taxBeforeThreeYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeFiveYears.map(tax => <taxBeforeFiveYears>{tax}</taxBeforeFiveYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeNineteenYears.map(tax => <taxBeforeNineteenYears>{tax}</taxBeforeNineteenYears>).getOrElse(NodeSeq.Empty)}
      {disregardedCDF.map(cdf => <disregardedCDF>{cdf}</disregardedCDF>).getOrElse(NodeSeq.Empty)}
      {taxYearLiabilities.map(liabilities =>
      <taxYearLiabilities>
        {liabilities.map { case (year, liability) =>
        <taxYear year={year}>{liability.toXml}</taxYear>
      }}
      </taxYearLiabilities>
    ).getOrElse(NodeSeq.Empty)}
      {lettingDeductions.map(deductions =>
      <lettingDeductions>
        {deductions.map { case (year, amount) =>
        <deduction year={year}>{amount}</deduction>
      }}
      </lettingDeductions>
    ).getOrElse(NodeSeq.Empty)}
      {lettingProperties.map(properties =>
      <lettingProperties>
        {properties.map(property => <property>{property.toXml}</property>)}
      </lettingProperties>
    ).getOrElse(NodeSeq.Empty)}
      {memberOfLandlordAssociations.map(member => <memberOfLandlordAssociations>{member}</memberOfLandlordAssociations>).getOrElse(NodeSeq.Empty)}
      {landlordAssociations.map(associations => <landlordAssociations>{associations}</landlordAssociations>).getOrElse(NodeSeq.Empty)}
      {howManyProperties.map(count => <howManyProperties>{count}</howManyProperties>).getOrElse(NodeSeq.Empty)}
      {corporationTaxLiabilities.map(liabilities =>
      <corporationTaxLiabilities>
        {liabilities.map(liability => <liability>{liability.toXml}</liability>)}
      </corporationTaxLiabilities>
    ).getOrElse(NodeSeq.Empty)}
      {directorLoanAccountLiabilities.map(liabilities =>
      <directorLoanAccountLiabilities>
        {liabilities.map(liability => <liability>{liability.toXml}</liability>)}
      </directorLoanAccountLiabilities>
    ).getOrElse(NodeSeq.Empty)}
    </onshoreLiabilities>
  }
}

object OnshoreLiabilities {
  implicit val format: OFormat[OnshoreLiabilities] = Json.format[OnshoreLiabilities]
}