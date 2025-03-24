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

package models.notification

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import models.{AreYouTheEntity, IncomeOrGainSource}

class BackgroundSpec extends AnyWordSpec with Matchers {

  "Background" should {
    "convert to XML correctly with all fields populated" in {
      val background = Background(
        haveYouReceivedALetter = Some(true),
        letterReferenceNumber = Some("ABC123"),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))),
        areYouRepresetingAnOrganisation = Some(false),
        organisationName = Some("Test Org"),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some(false),
        incomeSource = Some(Set(IncomeOrGainSource.Dividends, IncomeOrGainSource.Interest)),
        otherIncomeSource = Some("Other source")
      )

      val xml = background.toXml

      (xml \ "haveYouReceivedALetter").text               shouldBe "true"
      (xml \ "letterReferenceNumber").text                shouldBe "ABC123"
      (xml \ "disclosureEntity" \ "entity").text          shouldBe "Individual"
      (xml \ "disclosureEntity" \ "areYouTheEntity").text shouldBe "yes"
      (xml \ "areYouRepresetingAnOrganisation").text      shouldBe "false"
      (xml \ "organisationName").text                     shouldBe "Test Org"
      (xml \ "offshoreLiabilities").text                  shouldBe "true"
      (xml \ "onshoreLiabilities").text                   shouldBe "false"
      (xml \ "incomeSource" \ "source").map(_.text).toSet shouldBe Set("dividends", "interest")
      (xml \ "otherIncomeSource").text                    shouldBe "Other source"
    }

    "convert to XML correctly with only some fields populated" in {
      val background = Background(
        haveYouReceivedALetter = Some(false),
        disclosureEntity = Some(DisclosureEntity(Company)),
        offshoreLiabilities = Some(true)
      )

      val xml = background.toXml

      (xml \ "haveYouReceivedALetter").text                  shouldBe "false"
      (xml \ "letterReferenceNumber").isEmpty                shouldBe true
      (xml \ "disclosureEntity" \ "entity").text             shouldBe "Company"
      (xml \ "disclosureEntity" \ "areYouTheEntity").isEmpty shouldBe true
      (xml \ "areYouRepresetingAnOrganisation").isEmpty      shouldBe true
      (xml \ "organisationName").isEmpty                     shouldBe true
      (xml \ "offshoreLiabilities").text                     shouldBe "true"
      (xml \ "onshoreLiabilities").isEmpty                   shouldBe true
      (xml \ "incomeSource").isEmpty                         shouldBe true
      (xml \ "otherIncomeSource").isEmpty                    shouldBe true
    }

    "convert to XML correctly with no fields populated" in {
      val background = Background()

      val xml = background.toXml

      (xml \ "haveYouReceivedALetter").isEmpty          shouldBe true
      (xml \ "letterReferenceNumber").isEmpty           shouldBe true
      (xml \ "disclosureEntity").isEmpty                shouldBe true
      (xml \ "areYouRepresetingAnOrganisation").isEmpty shouldBe true
      (xml \ "organisationName").isEmpty                shouldBe true
      (xml \ "offshoreLiabilities").isEmpty             shouldBe true
      (xml \ "onshoreLiabilities").isEmpty              shouldBe true
      (xml \ "incomeSource").isEmpty                    shouldBe true
      (xml \ "otherIncomeSource").isEmpty               shouldBe true
    }

    "convert to XML correctly with all different entity types" in {
      val entityTypes = List(Individual, Estate, Company, LLP, Trust)

      entityTypes.foreach { entityType =>
        val background = Background(
          disclosureEntity = Some(DisclosureEntity(entityType))
        )

        val xml = background.toXml
        (xml \ "disclosureEntity" \ "entity").text shouldBe entityType.toString
      }
    }

    "convert to XML correctly with all income source types" in {
      val allSources = IncomeOrGainSource.values.toSet

      val background = Background(
        incomeSource = Some(allSources)
      )

      val xml            = background.toXml
      val sourcesFromXml = (xml \ "incomeSource" \ "source").map(_.text).toSet

      sourcesFromXml.size shouldBe allSources.size
      allSources.foreach { source =>
        sourcesFromXml should contain(source.toString)
      }
    }
  }
}
