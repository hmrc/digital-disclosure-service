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

class OnshoreLiabilitiesSpec extends AnyWordSpec with Matchers {

  "OnshoreLiabilities" should {
    "convert to XML correctly with minimal fields" in {
      val onshoreLiabilities = OnshoreLiabilities()

      val xml = onshoreLiabilities.toXml

      xml.headOption.map(_.label)                         shouldBe Some("onshoreLiabilities")
      (xml \ "behaviour").isEmpty                         shouldBe true
      (xml \ "excuseForNotNotifying").isEmpty             shouldBe true
      (xml \ "reasonableCare").isEmpty                    shouldBe true
      (xml \ "excuseForNotFiling").isEmpty                shouldBe true
      (xml \ "whatLiabilities").isEmpty                   shouldBe true
      (xml \ "whichYears").isEmpty                        shouldBe true
      (xml \ "youHaveNotIncludedTheTaxYear").isEmpty      shouldBe true
      (xml \ "youHaveNotSelectedCertainTaxYears").isEmpty shouldBe true
      (xml \ "taxBeforeThreeYears").isEmpty               shouldBe true
      (xml \ "taxBeforeFiveYears").isEmpty                shouldBe true
      (xml \ "taxBeforeNineteenYears").isEmpty            shouldBe true
      (xml \ "disregardedCDF").isEmpty                    shouldBe true
      (xml \ "taxYearLiabilities").isEmpty                shouldBe true
      (xml \ "lettingDeductions").isEmpty                 shouldBe true
      (xml \ "lettingProperties").isEmpty                 shouldBe true
      (xml \ "memberOfLandlordAssociations").isEmpty      shouldBe true
      (xml \ "landlordAssociations").isEmpty              shouldBe true
      (xml \ "howManyProperties").isEmpty                 shouldBe true
      (xml \ "corporationTaxLiabilities").isEmpty         shouldBe true
      (xml \ "directorLoanAccountLiabilities").isEmpty    shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val onshoreLiabilities = OnshoreLiabilities()

      val json   = Json.toJson(onshoreLiabilities)
      val parsed = json.validate[OnshoreLiabilities]

      parsed shouldBe JsSuccess(onshoreLiabilities)
    }

    "convert to XML correctly with some fields populated" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)),
        excuseForNotNotifying = Some(ReasonableExcuseOnshore("test excuse", "test years")),
        youHaveNotIncludedTheTaxYear = Some("test year"),
        youHaveNotSelectedCertainTaxYears = Some("test years"),
        taxBeforeThreeYears = Some("test tax"),
        taxBeforeFiveYears = Some("test tax"),
        taxBeforeNineteenYears = Some("test tax"),
        disregardedCDF = Some(true),
        memberOfLandlordAssociations = Some(false),
        landlordAssociations = Some("test associations"),
        howManyProperties = Some("test properties")
      )

      val xml = onshoreLiabilities.toXml

      xml.headOption.map(_.label)                      shouldBe Some("onshoreLiabilities")
      (xml \ "behaviour").nonEmpty                     shouldBe true
      (xml \ "excuseForNotNotifying").nonEmpty         shouldBe true
      (xml \ "youHaveNotIncludedTheTaxYear").text      shouldBe "test year"
      (xml \ "youHaveNotSelectedCertainTaxYears").text shouldBe "test years"
      (xml \ "taxBeforeThreeYears").text               shouldBe "test tax"
      (xml \ "taxBeforeFiveYears").text                shouldBe "test tax"
      (xml \ "taxBeforeNineteenYears").text            shouldBe "test tax"
      (xml \ "disregardedCDF").text                    shouldBe "true"
      (xml \ "memberOfLandlordAssociations").text      shouldBe "false"
      (xml \ "landlordAssociations").text              shouldBe "test associations"
      (xml \ "howManyProperties").text                 shouldBe "test properties"
    }

    "serialize and deserialize correctly with some fields populated" in {
      val onshoreLiabilities = OnshoreLiabilities(
        behaviour = Some(Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse)),
        excuseForNotNotifying = Some(ReasonableExcuseOnshore("test excuse", "test years")),
        youHaveNotIncludedTheTaxYear = Some("test year"),
        youHaveNotSelectedCertainTaxYears = Some("test years"),
        taxBeforeThreeYears = Some("test tax"),
        taxBeforeFiveYears = Some("test tax"),
        taxBeforeNineteenYears = Some("test tax"),
        disregardedCDF = Some(true),
        memberOfLandlordAssociations = Some(false),
        landlordAssociations = Some("test associations"),
        howManyProperties = Some("test properties")
      )

      val json   = Json.toJson(onshoreLiabilities)
      val parsed = json.validate[OnshoreLiabilities]

      parsed shouldBe JsSuccess(onshoreLiabilities)
    }
  }
}
