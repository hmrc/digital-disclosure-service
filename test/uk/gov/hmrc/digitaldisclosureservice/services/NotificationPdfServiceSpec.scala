/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.OptionValues
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import models._
import models.notification._
import uk.gov.hmrc.digitaldisclosureservice.views.html.NotificationView
import java.time.Instant
import utils.BaseSpec

import java.time.LocalDate

class NotificationPdfServiceSpec extends AnyWordSpecLike
  with Matchers
  with OptionValues
  with BaseSpec
  with NotificationPdfServiceHelper {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  val view = app.injector.instanceOf[NotificationView]
  val testPdfService = new NotificationPdfService(view)

  "PdfGenerationService" should {
    "generate the pdf with background info" when {
      "the user has entered no data" in {
        val notification = Notification("userId", "id", Instant.now(), Metadata(), Background(), AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotIndividualQuestions(parsedText)
        testNotCompanyQuestions(parsedText)
        testNotTrustQuestions(parsedText)
        testNotLLPQuestions(parsedText)
      }

      "the user has entered the full background section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = Some("1234567890"),
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(false)
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        parsedText should include(messages("An individual"))
      }

      "the user has entered the full background section as a company" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(false)
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        parsedText should include(messages("A company"))
      }

      "the user has entered the full background section as a trust" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Trust, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(false)
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        parsedText should include(messages("A trust"))
      }

      "the user has entered the full background section as a limited liability partnership" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(LLP, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        parsedText should include(messages("A limited liability partnership"))
      }
    }


    "generate the pdf with full about you info" when {

      "the user has entered the full background and started the about you section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, AboutYou())
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)
        parsedText should not include(messages("notification.aboutYou.emailAddress"))
        parsedText should not include(messages("notification.aboutYou.nino"))
        parsedText should not include(messages("notification.aboutYou.vatRegNumber"))
        parsedText should not include(messages("notification.aboutYou.sautr"))
      }

      "the user has entered the full about you section with email address" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)
        parsedText should not include(messages("notification.aboutYou.nino"))
        parsedText should not include(messages("notification.aboutYou.vatRegNumber"))
        parsedText should not include(messages("notification.aboutYou.sautr"))

        parsedText should include("Some name")
        parsedText should include("+44 012345678")
        parsedText should include("some.email@address.com")
        parsedText should include("Some occupation")
        parsedText should include(addressString(address("you")))
      }

      "the user has entered the full about you section with nino" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.No),
          nino = Some("Some nino"),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)

        parsedText should include(messages("notification.aboutYou.nino"))
        parsedText should include("Some nino")
      }

      "the user has entered the full about you section with vat" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          vatRegNumber = Some("Some VAT reg number"),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)
        
        parsedText should include(messages("notification.aboutYou.vatRegNumber"))
        parsedText should include("Some VAT reg number")
      }

      "the user has entered the full about you section with SA" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          sautr = Some("Some SAUTR"),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)
        
        parsedText should include(messages("notification.aboutYou.sautr"))
        parsedText should include("Some SAUTR")
      }

      "the user has entered the full about you section with values set to Unsure" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.Unsure),
          registeredForVAT = Some(YesNoOrUnsure.Unsure),
          registeredForSA = Some(YesNoOrUnsure.Unsure),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testFullAboutYouQuestions(parsedText)
        
        parsedText should not include(messages("notification.aboutYou.nino"))
        parsedText should not include(messages("notification.aboutYou.vatRegNumber"))
        parsedText should not include(messages("notification.aboutYou.sautr"))

        parsedText should include("Yes, but I do not know it")
      }

      "the user has entered yes to questions" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          emailAddress = Some("some.email@address.com"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some occupation"),
          doYouHaveANino = Some(YesNoOrUnsure.Yes),
          registeredForVAT = Some(YesNoOrUnsure.Yes),
          registeredForSA = Some(YesNoOrUnsure.Yes),
          address = Some(address("you"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou)
        val parsedText = stripPDFToString(notification)

        parsedText should not include(messages("notification.aboutYou.doYouHaveANino"))
        parsedText should not include(messages("notification.aboutYou.registeredForVAT"))
        parsedText should not include(messages("notification.aboutYou.registeredForSA"))
      }
      
    }  

    "generate the pdf with about the individual info" when {

      "the user has entered the full background and about you sections and started about the individual" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheIndividual = AboutTheIndividual()
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testIndividualQuestions(parsedText)
        testNotCompanyQuestions(parsedText)
        testNotTrustQuestions(parsedText)
        testNotLLPQuestions(parsedText)

        parsedText should not include(messages("notification.aboutTheIndividual.nino"))
        parsedText should not include(messages("notification.aboutTheIndividual.vatRegNumber"))
        parsedText should not include(messages("notification.aboutTheIndividual.sautr"))
      }

      "the user has entered the full about the individual section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheIndividual = AboutTheIndividual(  
          fullName = Some("Some individual's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some individual's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("individual"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testIndividualQuestions(parsedText)

        parsedText should include(messages("Some individual's name"))
        parsedText should include(messages("Some individual's occupation"))
        parsedText should include(addressString(address("individual")))

      }

      "the user has entered the full about the individual section with a NINO" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheIndividual = AboutTheIndividual(  
          fullName = Some("Some individual's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some individual's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          nino = Some("Some individual's nino"),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("individual"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testIndividualQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheIndividual.nino"))
        parsedText should include(messages("Some individual's nino"))
      }

      "the user has entered the full about the individual section with a VAT Reg Number" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheIndividual = AboutTheIndividual(  
          fullName = Some("Some individual's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some individual's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          vatRegNumber = Some("Some individual's VAT number"),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("individual"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testIndividualQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheIndividual.vatRegNumber"))
        parsedText should include(messages("Some individual's VAT number"))
      }

      "the user has entered the full about the individual section with an SAUTR" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheIndividual = AboutTheIndividual(  
          fullName = Some("Some individual's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some individual's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          sautr = Some("Some individual's SAUTR"),
          address = Some(address("individual"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testIndividualQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheIndividual.sautr"))
        parsedText should include(messages("Some individual's SAUTR"))
      }

    }

    "generate the pdf with about company info" when {

      "the user has entered the full background and about you sections and started about the company" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheCompany = AboutTheCompany()
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheCompany = Some(aboutTheCompany))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testCompanyQuestions(parsedText)

        testNotFullAboutYouQuestions(parsedText)
        testNotIndividualQuestions(parsedText)
        testNotTrustQuestions(parsedText)
        testNotLLPQuestions(parsedText)
      }

      "the user has entered the full company section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheCompany = AboutTheCompany(
          name = Some("Some company name"),
          registrationNumber = Some("Some company registration number"),
          address = Some(address("company"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheCompany = Some(aboutTheCompany))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testCompanyQuestions(parsedText)

        parsedText should include(messages("Some company name"))
        parsedText should include(messages("Some company registration number"))
        parsedText should include(messages(addressString(address("you"))))

      }

    }

    "generate the pdf with about trust info" when {

      "the user has entered the full background and about you sections and started about the trust" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheTrust = AboutTheTrust()
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheTrust = Some(aboutTheTrust))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testTrustQuestions(parsedText)

        testNotFullAboutYouQuestions(parsedText)
        testNotIndividualQuestions(parsedText)
        testNotCompanyQuestions(parsedText)
        testNotLLPQuestions(parsedText)
      }

      "the user has entered the full company section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheTrust = AboutTheTrust(
          name = Some("Some trust name"),
          address = Some(address("trust"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheTrust = Some(aboutTheTrust))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testTrustQuestions(parsedText)

        parsedText should include(messages("Some trust name"))
        parsedText should include(messages(addressString(address("trust"))))
      }

    }

    "generate the pdf with about LLP info" when {

      "the user has entered the full background and about you sections and started about the trust" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheLLP = AboutTheLLP()
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheLLP = Some(aboutTheLLP))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testLLPQuestions(parsedText)

        testNotFullAboutYouQuestions(parsedText)
        testNotIndividualQuestions(parsedText)
        testNotCompanyQuestions(parsedText)
        testNotTrustQuestions(parsedText)
      }

      "the user has entered the full company section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Company, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheLLP = AboutTheLLP(
          name = Some("Some LLP name"),
          address = Some(address("LLP"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheLLP = Some(aboutTheLLP))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testLLPQuestions(parsedText)

        parsedText should include(messages("Some LLP name"))
        parsedText should include(messages(addressString(address("LLP"))))
      }

    }

    "generate the pdf with about the estate info" when {

      "the user has entered the full background and about you sections and started about the estate" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Estate, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheEstate = AboutTheEstate()
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheEstate = Some(aboutTheEstate))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testEstateQuestions(parsedText)
        testNotCompanyQuestions(parsedText)
        testNotTrustQuestions(parsedText)
        testNotLLPQuestions(parsedText)

        parsedText should not include(messages("notification.aboutTheEstate.nino"))
        parsedText should not include(messages("notification.aboutTheEstate.vatRegNumber"))
        parsedText should not include(messages("notification.aboutTheEstate.sautr"))
      }

      "the user has entered the full about the estate section" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Estate, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheEstate = AboutTheEstate(  
          fullName = Some("Some estate's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some estate's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("estate"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheEstate = Some(aboutTheEstate))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testEstateQuestions(parsedText)

        parsedText should include(messages("Some estate's name"))
        parsedText should include(messages("Some estate's occupation"))
        parsedText should include(addressString(address("estate")))

      }

      "the user has entered the full about the estate section with a NINO" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Estate, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheEstate = AboutTheEstate(  
          fullName = Some("Some estate's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some estate's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          nino = Some("Some estate's nino"),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("estate"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheEstate = Some(aboutTheEstate))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testEstateQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheEstate.nino"))
        parsedText should include(messages("Some estate's nino"))
      }

      "the user has entered the full about the estate section with a VAT Reg Number" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Estate, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheEstate = AboutTheEstate(  
          fullName = Some("Some estate's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some estate's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          vatRegNumber = Some("Some estate's VAT number"),
          registeredForSA = Some(YesNoOrUnsure.No),
          address = Some(address("estate"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheEstate = Some(aboutTheEstate))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testEstateQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheEstate.vatRegNumber"))
        parsedText should include(messages("Some estate's VAT number"))
      }

      "the user has entered the full about the estate section with an SAUTR" in {
        val background = Background (
          haveYouReceivedALetter = Some(false),
          letterReferenceNumber = None,
          disclosureEntity = Some(DisclosureEntity(Estate, Some(false))),
          offshoreLiabilities = Some(false),
          onshoreLiabilities = Some(true)
        )
        val aboutYou = AboutYou(
          fullName = Some("Some name"),
          telephoneNumber = Some("+44 012345678"),
          address = Some(address("you"))
        )
        val aboutTheEstate = AboutTheEstate(  
          fullName = Some("Some estate's name"),
          dateOfBirth = Some(LocalDate.now()),
          mainOccupation = Some("Some estate's occupation"),
          doTheyHaveANino = Some(YesNoOrUnsure.No),
          registeredForVAT = Some(YesNoOrUnsure.No),
          registeredForSA = Some(YesNoOrUnsure.No),
          sautr = Some("Some estate's SAUTR"),
          address = Some(address("estate"))
        )
        val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, aboutTheEstate = Some(aboutTheEstate))
        val parsedText = stripPDFToString(notification)

        baseNotificationTests(parsedText)
        testNotFullAboutYouQuestions(parsedText)
        testEstateQuestions(parsedText)

        parsedText should include(messages("notification.aboutTheEstate.sautr"))
        parsedText should include(messages("Some estate's SAUTR"))
      }

    }
  }
}