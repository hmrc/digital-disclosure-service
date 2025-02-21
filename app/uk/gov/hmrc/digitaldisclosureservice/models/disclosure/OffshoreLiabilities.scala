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
import scala.xml._

final case class OffshoreLiabilities(
                                      behaviour: Option[Set[WhyAreYouMakingThisDisclosure]] = None,
                                      excuseForNotNotifying: Option[WhatIsYourReasonableExcuse] = None,
                                      reasonableCare: Option[WhatReasonableCareDidYouTake] = None,
                                      excuseForNotFiling: Option[WhatIsYourReasonableExcuseForNotFilingReturn] = None,
                                      whichYears: Option[Set[OffshoreYears]] = None,
                                      youHaveNotIncludedTheTaxYear: Option[String] = None,
                                      youHaveNotSelectedCertainTaxYears: Option[String] = None,
                                      taxBeforeFiveYears: Option[String] = None,
                                      taxBeforeSevenYears: Option[String] = None,
                                      taxBeforeNineteenYears: Option[String] = None,
                                      missingYear: Option[String] = None,
                                      missingYears: Option[String] = None,
                                      disregardedCDF: Option[Boolean] = None,
                                      taxYearLiabilities: Option[Map[String, TaxYearWithLiabilities]] = None,
                                      taxYearForeignTaxDeductions: Option[Map[String, BigInt]] = None,
                                      countryOfYourOffshoreLiability: Option[Map[String, CountryOfYourOffshoreLiability]] = None,
                                      legalInterpretation: Option[Set[YourLegalInterpretation]] = None,
                                      otherInterpretation: Option[String] = None,
                                      notIncludedDueToInterpretation: Option[HowMuchTaxHasNotBeenIncluded] = None,
                                      maximumValueOfAssets: Option[TheMaximumValueOfAllAssets] = None
                                    ) {
  def toXml: NodeSeq = {
    <offshoreLiabilities>
      {behaviour.map(b =>
      <behaviour>
        {b.map(reason => <reason>{reason.toXml}</reason>)}
      </behaviour>
    ).getOrElse(NodeSeq.Empty)}
      {excuseForNotNotifying.map(excuse => <excuseForNotNotifying>{excuse.toXml}</excuseForNotNotifying>).getOrElse(NodeSeq.Empty)}
      {reasonableCare.map(care => <reasonableCare>{care.toXml}</reasonableCare>).getOrElse(NodeSeq.Empty)}
      {excuseForNotFiling.map(excuse => <excuseForNotFiling>{excuse.toXml}</excuseForNotFiling>).getOrElse(NodeSeq.Empty)}
      {whichYears.map(years =>
      <whichYears>
        {years.map(year => <year>{year.toXml}</year>)}
      </whichYears>
    ).getOrElse(NodeSeq.Empty)}
      {youHaveNotIncludedTheTaxYear.map(year => <youHaveNotIncludedTheTaxYear>{year}</youHaveNotIncludedTheTaxYear>).getOrElse(NodeSeq.Empty)}
      {youHaveNotSelectedCertainTaxYears.map(years => <youHaveNotSelectedCertainTaxYears>{years}</youHaveNotSelectedCertainTaxYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeFiveYears.map(tax => <taxBeforeFiveYears>{tax}</taxBeforeFiveYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeSevenYears.map(tax => <taxBeforeSevenYears>{tax}</taxBeforeSevenYears>).getOrElse(NodeSeq.Empty)}
      {taxBeforeNineteenYears.map(tax => <taxBeforeNineteenYears>{tax}</taxBeforeNineteenYears>).getOrElse(NodeSeq.Empty)}
      {missingYear.map(year => <missingYear>{year}</missingYear>).getOrElse(NodeSeq.Empty)}
      {missingYears.map(years => <missingYears>{years}</missingYears>).getOrElse(NodeSeq.Empty)}
      {disregardedCDF.map(cdf => <disregardedCDF>{cdf}</disregardedCDF>).getOrElse(NodeSeq.Empty)}
      {taxYearLiabilities.map(liabilities =>
      <taxYearLiabilities>
        {liabilities.map { case (year, liability) =>
        <taxYear year={year}>{liability.toXml}</taxYear>
      }}
      </taxYearLiabilities>
    ).getOrElse(NodeSeq.Empty)}
      {taxYearForeignTaxDeductions.map(deductions =>
      <taxYearForeignTaxDeductions>
        {deductions.map { case (year, amount) =>
        <deduction year={year}>{amount}</deduction>
      }}
      </taxYearForeignTaxDeductions>
    ).getOrElse(NodeSeq.Empty)}
      {countryOfYourOffshoreLiability.map(countries =>
      <countryOfYourOffshoreLiability>
        {countries.map { case (country, liability) =>
        <country name={country}>{liability.toXml}</country>
      }}
      </countryOfYourOffshoreLiability>
    ).getOrElse(NodeSeq.Empty)}
      {legalInterpretation.map(interpretations =>
      <legalInterpretation>
        {interpretations.map(interpretation => <interpretation>{interpretation.toXml}</interpretation>)}
      </legalInterpretation>
    ).getOrElse(NodeSeq.Empty)}
      {otherInterpretation.map(other => <otherInterpretation>{other}</otherInterpretation>).getOrElse(NodeSeq.Empty)}
      {notIncludedDueToInterpretation.map(notIncluded => <notIncludedDueToInterpretation>{notIncluded.toXml}</notIncludedDueToInterpretation>).getOrElse(NodeSeq.Empty)}
      {maximumValueOfAssets.map(value => <maximumValueOfAssets>{value.toXml}</maximumValueOfAssets>).getOrElse(NodeSeq.Empty)}
    </offshoreLiabilities>
  }
}

object OffshoreLiabilities {
  implicit val format: OFormat[OffshoreLiabilities] = Json.format[OffshoreLiabilities]
}