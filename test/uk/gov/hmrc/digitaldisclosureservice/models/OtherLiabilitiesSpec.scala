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

import models.OtherLiabilityIssues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class OtherLiabilitiesSpec extends AnyWordSpec with Matchers {

  "OtherLiabilities" should {
    "convert to XML correctly with minimal fields" in {
      val otherLiabilities = OtherLiabilities()

      val xml = otherLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("otherLiabilities")
      (xml \ "issues").isEmpty shouldBe true
      (xml \ "inheritanceGift").isEmpty shouldBe true
      (xml \ "other").isEmpty shouldBe true
      (xml \ "taxCreditsReceived").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with minimal fields" in {
      val otherLiabilities = OtherLiabilities()

      val json = Json.toJson(otherLiabilities)
      val parsed = json.validate[OtherLiabilities]

      parsed shouldBe JsSuccess(otherLiabilities)
    }

    "convert to XML correctly with all fields populated" in {
      val issues: Set[OtherLiabilityIssues] = Set(
        OtherLiabilityIssues.InheritanceTaxIssues,
        OtherLiabilityIssues.VatIssues
      )

      val otherLiabilities = OtherLiabilities(
        issues = Some(issues),
        inheritanceGift = Some("Test inheritance gift"),
        other = Some("Test other liability"),
        taxCreditsReceived = Some(true)
      )

      val xml = otherLiabilities.toXml

      xml.headOption.map(_.label) shouldBe Some("otherLiabilities")
      (xml \ "issues").nonEmpty shouldBe true
      (xml \ "issues" \ "issue").size shouldBe 2
      (xml \ "inheritanceGift").text shouldBe "Test inheritance gift"
      (xml \ "other").text shouldBe "Test other liability"
      (xml \ "taxCreditsReceived").text shouldBe "true"
    }

    "serialize and deserialize correctly with all fields populated" in {
      val issues: Set[OtherLiabilityIssues] = Set(
        OtherLiabilityIssues.InheritanceTaxIssues,
        OtherLiabilityIssues.VatIssues
      )

      val otherLiabilities = OtherLiabilities(
        issues = Some(issues),
        inheritanceGift = Some("Test inheritance gift"),
        other = Some("Test other liability"),
        taxCreditsReceived = Some(true)
      )

      val json = Json.toJson(otherLiabilities)
      val parsed = json.validate[OtherLiabilities]

      parsed shouldBe JsSuccess(otherLiabilities)
    }
  }
}