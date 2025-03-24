/*
 * Copyright 2025 HM Revenue & Customs
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

import models._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class OffshoreLiabilitiesSpec extends AnyWordSpec with Matchers {

  "OffshoreLiabilities" should {
    "convert to XML correctly with all fields populated" in {
      val offshoreLiabilities = createFullOffshoreLiabilities()

      val xml = offshoreLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("offshoreLiabilities")
      (xml \ "behaviour").nonEmpty shouldBe true
      (xml \ "excuseForNotNotifying").nonEmpty shouldBe true
      (xml \ "reasonableCare").nonEmpty shouldBe true
      (xml \ "excuseForNotFiling").nonEmpty shouldBe true
      (xml \ "whichYears").nonEmpty shouldBe true
      (xml \ "youHaveNotIncludedTheTaxYear").nonEmpty shouldBe true
      (xml \ "youHaveNotSelectedCertainTaxYears").nonEmpty shouldBe true
      (xml \ "taxBeforeFiveYears").nonEmpty shouldBe true
      (xml \ "taxBeforeSevenYears").nonEmpty shouldBe true
      (xml \ "taxBeforeNineteenYears").nonEmpty shouldBe true
      (xml \ "missingYear").nonEmpty shouldBe true
      (xml \ "missingYears").nonEmpty shouldBe true
      (xml \ "disregardedCDF").nonEmpty shouldBe true
      (xml \ "taxYearLiabilities").nonEmpty shouldBe true
      (xml \ "taxYearForeignTaxDeductions").nonEmpty shouldBe true
      (xml \ "countryOfYourOffshoreLiability").nonEmpty shouldBe true
      (xml \ "legalInterpretation").nonEmpty shouldBe true
      (xml \ "otherInterpretation").nonEmpty shouldBe true
      (xml \ "notIncludedDueToInterpretation").nonEmpty shouldBe true
      (xml \ "maximumValueOfAssets").nonEmpty shouldBe true
    }

    "convert to XML correctly with minimal fields" in {
      val offshoreLiabilities = OffshoreLiabilities()

      val xml = offshoreLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("offshoreLiabilities")
      (xml \ "behaviour").isEmpty shouldBe true
      (xml \ "excuseForNotNotifying").isEmpty shouldBe true
      (xml \ "reasonableCare").isEmpty shouldBe true
      (xml \ "excuseForNotFiling").isEmpty shouldBe true
      (xml \ "whichYears").isEmpty shouldBe true
      (xml \ "youHaveNotIncludedTheTaxYear").isEmpty shouldBe true
      (xml \ "youHaveNotSelectedCertainTaxYears").isEmpty shouldBe true
      (xml \ "taxBeforeFiveYears").isEmpty shouldBe true
      (xml \ "taxBeforeSevenYears").isEmpty shouldBe true
      (xml \ "taxBeforeNineteenYears").isEmpty shouldBe true
      (xml \ "missingYear").isEmpty shouldBe true
      (xml \ "missingYears").isEmpty shouldBe true
      (xml \ "disregardedCDF").isEmpty shouldBe true
      (xml \ "taxYearLiabilities").isEmpty shouldBe true
      (xml \ "taxYearForeignTaxDeductions").isEmpty shouldBe true
      (xml \ "countryOfYourOffshoreLiability").isEmpty shouldBe true
      (xml \ "legalInterpretation").isEmpty shouldBe true
      (xml \ "otherInterpretation").isEmpty shouldBe true
      (xml \ "notIncludedDueToInterpretation").isEmpty shouldBe true
      (xml \ "maximumValueOfAssets").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with all fields populated" in {
      val offshoreLiabilities = createFullOffshoreLiabilities()

      val json = Json.toJson(offshoreLiabilities)
      val parsed = json.validate[OffshoreLiabilities]

      parsed shouldBe JsSuccess(offshoreLiabilities)
    }

    "serialize and deserialize correctly with minimal fields" in {
      val offshoreLiabilities = OffshoreLiabilities()

      val json = Json.toJson(offshoreLiabilities)
      val parsed = json.validate[OffshoreLiabilities]

      parsed shouldBe JsSuccess(offshoreLiabilities)
    }
  }

  private def createFullOffshoreLiabilities(): OffshoreLiabilities = {
    val behaviour: Set[WhyAreYouMakingThisDisclosure] = Set(
      WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse,
      WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare
    )

    val excuse = WhatIsYourReasonableExcuse(
      excuse = "test excuse",
      years = "test years"
    )

    val reasonableCare = WhatReasonableCareDidYouTake(
      reasonableCare = "test care",
      yearsThisAppliesTo = "test years"
    )

    val excuseForNotFiling = WhatIsYourReasonableExcuseForNotFilingReturn(
      reasonableExcuse = "test excuse",
      yearsThisAppliesTo = "test years"
    )

    val whichYears: Set[OffshoreYears] = Set(
      TaxYearStarting(2018),
      ReasonableExcusePriorTo
    )

    val taxYearLiabilities = Map(
      "2018" -> TaxYearWithLiabilities(
        taxYear = TaxYearStarting(2018),
        taxYearLiabilities = TaxYearLiabilities(
          income = BigInt(1),
          chargeableTransfers = BigInt(1),
          capitalGains = BigInt(1),
          unpaidTax = BigInt(1),
          interest = BigInt(1),
          penaltyRate = BigDecimal(1),
          penaltyRateReason = "test",
          undeclaredIncomeOrGain = Some("test"),
          foreignTaxCredit = true
        )
      )
    )

    val taxYearForeignTaxDeductions = Map(
      "2018" -> BigInt(1)
    )

    val countryOfYourOffshoreLiability = Map(
      "test" -> CountryOfYourOffshoreLiability(
        alpha3 = "tst",
        name = "test"
      )
    )

    val legalInterpretation: Set[YourLegalInterpretation] = Set(
      YourLegalInterpretation.YourResidenceStatus,
      YourLegalInterpretation.TheRemittanceBasis
    )

    OffshoreLiabilities(
      behaviour = Some(behaviour),
      excuseForNotNotifying = Some(excuse),
      reasonableCare = Some(reasonableCare),
      excuseForNotFiling = Some(excuseForNotFiling),
      whichYears = Some(whichYears),
      youHaveNotIncludedTheTaxYear = Some("test"),
      youHaveNotSelectedCertainTaxYears = Some("test"),
      taxBeforeFiveYears = Some("test"),
      taxBeforeSevenYears = Some("test"),
      taxBeforeNineteenYears = Some("test"),
      missingYear = Some("test"),
      missingYears = Some("test"),
      disregardedCDF = Some(true),
      taxYearLiabilities = Some(taxYearLiabilities),
      taxYearForeignTaxDeductions = Some(taxYearForeignTaxDeductions),
      countryOfYourOffshoreLiability = Some(countryOfYourOffshoreLiability),
      legalInterpretation = Some(legalInterpretation),
      otherInterpretation = Some("test"),
      notIncludedDueToInterpretation = Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
      maximumValueOfAssets = Some(TheMaximumValueOfAllAssets.Below500k)
    )
  }
}