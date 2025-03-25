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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class CaseReferenceSpec extends AnyWordSpec with Matchers {

  "CaseReference" should {
    "convert to XML correctly" when {
      "all fields are populated" in {
        val caseReference = CaseReference(
          doYouHaveACaseReference = Some(true),
          whatIsTheCaseReference = Some("ABC123456")
        )

        val xml = caseReference.toXml

        xml.headOption.map(_.label)            shouldBe Some("caseReference")
        (xml \ "doYouHaveACaseReference").text shouldBe "true"
        (xml \ "whatIsTheCaseReference").text  shouldBe "ABC123456"
      }

      "no fields are populated" in {
        val caseReference = CaseReference()

        val xml = caseReference.toXml

        xml.headOption.map(_.label)               shouldBe Some("caseReference")
        (xml \ "doYouHaveACaseReference").isEmpty shouldBe true
        (xml \ "whatIsTheCaseReference").isEmpty  shouldBe true
      }
    }

    "serialize and deserialize correctly" when {
      "all fields are populated" in {
        val caseReference = CaseReference(
          doYouHaveACaseReference = Some(true),
          whatIsTheCaseReference = Some("ABC123456")
        )

        val json   = Json.toJson(caseReference)
        val parsed = json.validate[CaseReference]

        parsed                                         shouldBe JsSuccess(caseReference)
        (json \ "doYouHaveACaseReference").as[Boolean] shouldBe true
        (json \ "whatIsTheCaseReference").as[String]   shouldBe "ABC123456"
      }

      "no fields are populated" in {
        val caseReference = CaseReference()

        val json   = Json.toJson(caseReference)
        val parsed = json.validate[CaseReference]

        parsed                                      shouldBe JsSuccess(caseReference)
        (json \ "doYouHaveACaseReference").toOption shouldBe None
        (json \ "whatIsTheCaseReference").toOption  shouldBe None
      }
    }
  }
}
