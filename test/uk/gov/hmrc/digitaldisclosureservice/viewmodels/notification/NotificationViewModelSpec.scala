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

package viewmodels

import org.scalatest.matchers.should.Matchers
import viewmodels.govuk.SummaryListFluency
import org.scalatest.wordspec.AnyWordSpec
import java.util.Date
import viewmodels.implicits._
import play.api.i18n.{MessagesApi, Messages}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import models._

class NotificationViewModelSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite with SummaryListFluency {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  
  "metadataList" should {
    "return populated values as rows" in {
      val date = new Date()
      val metadata = Metadata(Some("Some reference"), Some(date))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.metadata.reference", ValueViewModel("Some reference")),
        SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel(date.toString))
      ))
      NotificationViewModel.metadataList(metadata) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val metadata = Metadata()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.metadata.reference", ValueViewModel("-")),
        SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel("-"))
      ))
      NotificationViewModel.metadataList(metadata) shouldEqual expected
    }
  }

  "backgroundList" should {
    "return populated values as rows" in {
      val background = Background (
        haveYouReceivedALetter = Some(true),
        letterReferenceNumber= Some("Some letter reference"),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some (true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.letterReferenceNumber", ValueViewModel("Some letter reference")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.offshoreLiabilities", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.onshoreLiabilities", ValueViewModel(messages("service.yes")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val background = Background()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.offshoreLiabilities", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.onshoreLiabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Company " in {
      val background = Background (None, None, Some(DisclosureEntity(Company, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Company"))),
        SummaryListRowViewModel("notification.background.areYouTheCompany", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.offshoreLiabilities", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.onshoreLiabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a LLP " in {
      val background = Background (None, None, Some(DisclosureEntity(LLP, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.LLP"))),
        SummaryListRowViewModel("notification.background.areYouTheLLP", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.offshoreLiabilities", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.onshoreLiabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Trust " in {
      val background = Background (None, None, Some(DisclosureEntity(Trust, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Trust"))),
        SummaryListRowViewModel("notification.background.areYouTheTrust", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.offshoreLiabilities", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.onshoreLiabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }
  }

  "aboutTheIndividualList" should {
    "return populated values as rows" in {
      val date = new Date()
      val aboutTheIndividual = AboutTheIndividual(
        fullName = Some("Some full name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some("Some address")
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheIndividual.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutTheIndividual.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutTheIndividual.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutTheIndividual.doYouHaveANino", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForVAT", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForSA", ValueViewModel(messages("service.unsure"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.sautr", ValueViewModel("Some SAUTR")),
        SummaryListRowViewModel("notification.aboutTheIndividual.address", ValueViewModel("Some address"))
      ))
      NotificationViewModel.aboutTheIndividualList(aboutTheIndividual) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheIndividual = AboutTheIndividual()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheIndividual.fullName", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.dateOfBirth", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.mainOccupation", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.doYouHaveANino", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForVAT", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForSA", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheIndividualList(aboutTheIndividual) shouldEqual expected
    }
  }

  "aboutYouList" should {
    "return populated values as rows when disclosing as the individual" in {
      val date = new Date()
      val aboutYou = AboutYou(
        fullName = Some("Some full name"),
        telephoneNumber = Some("Some phone number"),
        doYouHaveAEmailAddress = Some(true),
        emailAddress = Some("Some email address"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some("Some address")
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("Some phone number")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveAEmailAddress", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.aboutYou.emailAddress", ValueViewModel("Some email address")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel("Some address")),
        SummaryListRowViewModel("notification.aboutYou.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutYou.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveANino", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.aboutYou.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutYou.registeredForVAT", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutYou.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutYou.registeredForSA", ValueViewModel(messages("service.unsure"))),
        SummaryListRowViewModel("notification.aboutYou.sautr", ValueViewModel("Some SAUTR"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, true) shouldEqual expected
    }

    "return populated values as rows when disclosing on behalf of the individual" in {
      val date = new Date()
      val aboutYou = AboutYou(
        fullName = Some("Some full name"),
        telephoneNumber = Some("Some phone number"),
        doYouHaveAEmailAddress = Some(true),
        emailAddress = Some("Some email address"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some("Some address")
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("Some phone number")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveAEmailAddress", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.aboutYou.emailAddress", ValueViewModel("Some email address")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel("Some address"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, false) shouldEqual expected
    }

    "return unpopulated values as rows with value dash when disclosing as the individual" in {
      val aboutYou = AboutYou()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveAEmailAddress", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.dateOfBirth", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.mainOccupation", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveANino", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.registeredForVAT", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.registeredForSA", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, true) shouldEqual expected
    }

    "return unpopulated values as rows with value dash when disclosing on behalf of the individual" in {
      val aboutYou = AboutYou()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveAEmailAddress", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, false) shouldEqual expected
    }
  }

  "companyList" should {
    "return populated values as rows" in {
      val aboutTheCompany = AboutTheCompany(Some("Some company name"), Some("Some reg number"), Some("Some address"))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheCompany.name", ValueViewModel("Some company name")),
        SummaryListRowViewModel("notification.aboutTheCompany.registrationNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheCompany.address", ValueViewModel("Some address"))
      ))
      NotificationViewModel.aboutTheCompanyList(aboutTheCompany) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheCompany = AboutTheCompany()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheCompany.name", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheCompany.registrationNumber", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheCompany.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheCompanyList(aboutTheCompany) shouldEqual expected
    }
  }

  "aboutTheTrustList" should {
    "return populated values as rows" in {
      val aboutTheTrust = AboutTheTrust(Some("Some trust name"), Some("Some address"))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheTrust.name", ValueViewModel("Some trust name")),
        SummaryListRowViewModel("notification.aboutTheTrust.address", ValueViewModel("Some address"))
      ))
      NotificationViewModel.aboutTheTrustList(aboutTheTrust) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheTrust = AboutTheTrust()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheTrust.name", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheTrust.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheTrustList(aboutTheTrust) shouldEqual expected
    }
  }

  "aboutTheLLPList" should {
    "return populated values as rows" in {
      val aboutTheLLP = AboutTheLLP(Some("Some LLP name"), Some("Some address"))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheLLP.name", ValueViewModel("Some LLP name")),
        SummaryListRowViewModel("notification.aboutTheLLP.address", ValueViewModel("Some address"))
      ))
      NotificationViewModel.aboutTheLLPList(aboutTheLLP) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheLLP = AboutTheLLP()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheLLP.name", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheLLP.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheLLPList(aboutTheLLP) shouldEqual expected
    }
  }

}
