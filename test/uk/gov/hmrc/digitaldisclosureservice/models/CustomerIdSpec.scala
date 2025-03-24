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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class CustomerIdSpec extends AnyWordSpec with Matchers {

  "CustomerId" should {
    "convert to XML correctly" when {
      "it is a NINO" in {
        val nino = NINO("AB123456C")
        val xml = nino.toXml

        xml.headOption.map(_.label) shouldBe Some("nino")
        xml.text shouldBe "AB123456C"
      }

      "it is a CAUTR" in {
        val cautr = CAUTR("1234567890")
        val xml = cautr.toXml

        xml.headOption.map(_.label) shouldBe Some("cautr")
        xml.text shouldBe "1234567890"
      }

      "it is a SAUTR" in {
        val sautr = SAUTR("0987654321")
        val xml = sautr.toXml

        xml.headOption.map(_.label) shouldBe Some("sautr")
        xml.text shouldBe "0987654321"
      }

      "it is an ARN" in {
        val arn = ARN("AARN1234567")
        val xml = arn.toXml

        xml.headOption.map(_.label) shouldBe Some("arn")
        xml.text shouldBe "AARN1234567"
      }

      "it is an ExternalId" in {
        val externalId = ExternalId("EXT123456")
        val xml = externalId.toXml

        xml.headOption.map(_.label) shouldBe Some("externalId")
        xml.text shouldBe "EXT123456"
      }
    }

    "serialize and deserialize correctly" when {
      "it is a NINO" in {
        val nino = NINO("AB123456C")
        val json = Json.toJson(nino)(NINO.format)
        val parsed = json.validate[NINO](NINO.format)

        parsed shouldBe JsSuccess(nino)
        (json \ "id").asOpt[String] shouldBe Some("AB123456C")
      }

      "it is a CAUTR" in {
        val cautr = CAUTR("1234567890")
        val json = Json.toJson(cautr)(CAUTR.format)
        val parsed = json.validate[CAUTR](CAUTR.format)

        parsed shouldBe JsSuccess(cautr)
        (json \ "id").asOpt[String] shouldBe Some("1234567890")
      }

      "it is a SAUTR" in {
        val sautr = SAUTR("0987654321")
        val json = Json.toJson(sautr)(SAUTR.format)
        val parsed = json.validate[SAUTR](SAUTR.format)

        parsed shouldBe JsSuccess(sautr)
        (json \ "id").asOpt[String] shouldBe Some("0987654321")
      }

      "it is an ARN" in {
        val arn = ARN("AARN1234567")
        val json = Json.toJson(arn)(ARN.format)
        val parsed = json.validate[ARN](ARN.format)

        parsed shouldBe JsSuccess(arn)
        (json \ "id").asOpt[String] shouldBe Some("AARN1234567")
      }

      "it is an ExternalId" in {
        val externalId = ExternalId("EXT123456")
        val json = Json.toJson(externalId)(ExternalId.format)
        val parsed = json.validate[ExternalId](ExternalId.format)

        parsed shouldBe JsSuccess(externalId)
        (json \ "id").asOpt[String] shouldBe Some("EXT123456")
      }
    }
  }
}