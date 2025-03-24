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

import models.address.{Address, Country}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}
import java.time.LocalDate

class LettingPropertySpec extends AnyWordSpec with Matchers {

  "LettingProperty" should {
    "convert to XML correctly with all fields populated" in {
      val date = LocalDate.of(2020, 1, 1)
      val stopDate = LocalDate.of(2022, 1, 1)
      val address = Address(
        line1 = "1 Test Street",
        line2 = Some("Test Area"),
        line3 = Some("Test Town"),
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = stopDate,
        whatHasHappenedToProperty = "Sold"
      )

      val lettingProperty = LettingProperty(
        address = Some(address),
        dateFirstLetOut = Some(date),
        stoppedBeingLetOut = Some(true),
        noLongerBeingLetOut = Some(noLongerBeingLetOut),
        fhl = Some(true),
        isJointOwnership = Some(false),
        isMortgageOnProperty = Some(true),
        percentageIncomeOnProperty = Some(75),
        wasFurnished = Some(true),
        typeOfMortgage = Some(TypeOfMortgageDidYouHave.InterestOnly),
        otherTypeOfMortgage = None,
        wasPropertyManagerByAgent = Some(false),
        didTheLettingAgentCollectRentOnYourBehalf = Some(false)
      )

      val xml = lettingProperty.toXml

      xml.headOption.map(_.label) shouldBe Some("lettingProperty")
      (xml \ "address").nonEmpty shouldBe true
      (xml \ "dateFirstLetOut").text shouldBe "2020-01-01"
      (xml \ "stoppedBeingLetOut").text shouldBe "true"
      (xml \ "noLongerBeingLetOut").nonEmpty shouldBe true
      (xml \ "fhl").text shouldBe "true"
      (xml \ "isJointOwnership").text shouldBe "false"
      (xml \ "isMortgageOnProperty").text shouldBe "true"
      (xml \ "percentageIncomeOnProperty").text shouldBe "75"
      (xml \ "wasFurnished").text shouldBe "true"
      (xml \ "typeOfMortgage").text shouldBe "interestOnly"
      (xml \ "wasPropertyManagerByAgent").text shouldBe "false"
      (xml \ "didTheLettingAgentCollectRentOnYourBehalf").text shouldBe "false"
    }

    "convert to XML correctly with minimal fields" in {
      val lettingProperty = LettingProperty()

      val xml = lettingProperty.toXml

      xml.headOption.map(_.label) shouldBe Some("lettingProperty")
      (xml \ "address").isEmpty shouldBe true
      (xml \ "dateFirstLetOut").isEmpty shouldBe true
      (xml \ "stoppedBeingLetOut").isEmpty shouldBe true
      (xml \ "noLongerBeingLetOut").isEmpty shouldBe true
      (xml \ "fhl").isEmpty shouldBe true
      (xml \ "isJointOwnership").isEmpty shouldBe true
      (xml \ "isMortgageOnProperty").isEmpty shouldBe true
      (xml \ "percentageIncomeOnProperty").isEmpty shouldBe true
      (xml \ "wasFurnished").isEmpty shouldBe true
      (xml \ "typeOfMortgage").isEmpty shouldBe true
      (xml \ "otherTypeOfMortgage").isEmpty shouldBe true
      (xml \ "wasPropertyManagerByAgent").isEmpty shouldBe true
      (xml \ "didTheLettingAgentCollectRentOnYourBehalf").isEmpty shouldBe true
    }

    "serialize and deserialize correctly with all fields populated" in {
      val date = LocalDate.of(2020, 1, 1)
      val stopDate = LocalDate.of(2022, 1, 1)
      val address = Address(
        line1 = "1 Test Street",
        line2 = Some("Test Area"),
        line3 = Some("Test Town"),
        line4 = None,
        postcode = Some("TE1 1ST"),
        country = Country("GB")
      )
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = stopDate,
        whatHasHappenedToProperty = "Sold"
      )

      val lettingProperty = LettingProperty(
        address = Some(address),
        dateFirstLetOut = Some(date),
        stoppedBeingLetOut = Some(true),
        noLongerBeingLetOut = Some(noLongerBeingLetOut),
        fhl = Some(true),
        isJointOwnership = Some(false),
        isMortgageOnProperty = Some(true),
        percentageIncomeOnProperty = Some(75),
        wasFurnished = Some(true),
        typeOfMortgage = Some(TypeOfMortgageDidYouHave.InterestOnly),
        otherTypeOfMortgage = None,
        wasPropertyManagerByAgent = Some(false),
        didTheLettingAgentCollectRentOnYourBehalf = Some(false)
      )

      val json = Json.toJson(lettingProperty)
      val parsed = json.validate[LettingProperty]

      parsed shouldBe JsSuccess(lettingProperty)
    }

    "serialize and deserialize correctly with minimal fields" in {
      val lettingProperty = LettingProperty()

      val json = Json.toJson(lettingProperty)
      val parsed = json.validate[LettingProperty]

      parsed shouldBe JsSuccess(lettingProperty)
    }
  }

  "NoLongerBeingLetOut" should {
    "convert to XML correctly" in {
      val stopDate = LocalDate.of(2022, 1, 1)
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = stopDate,
        whatHasHappenedToProperty = "Sold"
      )

      val xml = noLongerBeingLetOut.toXml

      xml.headOption.map(_.label) shouldBe Some("noLongerBeingLetOut")
      (xml \ "stopDate").text shouldBe "2022-01-01"
      (xml \ "whatHasHappenedToProperty").text shouldBe "Sold"
    }

    "serialize and deserialize correctly" in {
      val stopDate = LocalDate.of(2022, 1, 1)
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = stopDate,
        whatHasHappenedToProperty = "Sold"
      )

      val json = Json.toJson(noLongerBeingLetOut)
      val parsed = json.validate[NoLongerBeingLetOut]

      parsed shouldBe JsSuccess(noLongerBeingLetOut)
    }
  }
}