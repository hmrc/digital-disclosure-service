/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import org.scalatest.wordspec.AnyWordSpec
import java.time.{LocalDate}
import viewmodels.implicits._
import play.api.i18n.{MessagesApi, Messages}
import play.api.test.FakeRequest
import models._
import models.address._
import models.address.Address._
import models.notification._
import utils.BaseSpec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationViewModelSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  val address = Address("line1", None, None, None, None, Country("GBR"))
  val addressString = AddressOps(address).getAddressLines.mkString(", ")
  
  "metadataList" should {
    "return populated values as rows" in {
      val date = LocalDateTime.now()
      val metadata = Metadata(Some("Some reference"), Some(date))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.metadata.reference", ValueViewModel("Some reference")),
        SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel(toPrettyDate(date)))
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
    "return populated values as rows where have you received is yes" in {
      val background = Background (
        haveYouReceivedALetter = Some(true),
        letterReferenceNumber= Some("Some letter reference"),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some (true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.onshore")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return populated values as rows where have you received is no" in {
      val background = Background (
        haveYouReceivedALetter = Some(false),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some (false)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.offshore")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return populated values as rows where both onshore and offshore" in {
      val background = Background (
        haveYouReceivedALetter = Some(false),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some (true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.both")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val background = Background()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Company " in {
      val background = Background (None, None, Some(DisclosureEntity(Company, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Company"))),
        SummaryListRowViewModel("notification.background.areYouTheCompany", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a LLP " in {
      val background = Background (None, None, Some(DisclosureEntity(LLP, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.LLP"))),
        SummaryListRowViewModel("notification.background.areYouTheLLP", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Trust " in {
      val background = Background (None, None, Some(DisclosureEntity(Trust, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Trust"))),
        SummaryListRowViewModel("notification.background.areYouTheTrust", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return organisation information where it's populated" in {
      val background = Background (None, None, Some(DisclosureEntity(Individual, Some(false))), Some(true), Some("Some organisation"))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.organisationName", ValueViewModel("Some organisation")),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return organisation information where it's populated but set to false" in {
      val background = Background (None, None, Some(DisclosureEntity(Individual, Some(false))), Some(false), None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.areYouRepresetingAnOrganisation", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-"))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }
  }

  "aboutTheIndividualList" should {
    "return populated values as rows, hiding Yes rows" in {
      val date = LocalDate.now()
      val aboutTheIndividual = AboutTheIndividual(
        fullName = Some("Some full name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheIndividual.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutTheIndividual.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutTheIndividual.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutTheIndividual.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutTheIndividual.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheIndividual.sautr", ValueViewModel("Some SAUTR")),
        SummaryListRowViewModel("notification.aboutTheIndividual.address", ValueViewModel(addressString))
      ))
      NotificationViewModel.aboutTheIndividualList(aboutTheIndividual) shouldEqual expected
    }

    "return populated values as rows, showing No rows" in {
      val date = LocalDate.now()
      val aboutTheIndividual = AboutTheIndividual(
        fullName = Some("Some full name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.No),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheIndividual.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutTheIndividual.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutTheIndividual.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutTheIndividual.doTheyHaveANino", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForVAT", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForSA", ValueViewModel(messages("service.unsure"))),
        SummaryListRowViewModel("notification.aboutTheIndividual.sautr", ValueViewModel("Some SAUTR")),
        SummaryListRowViewModel("notification.aboutTheIndividual.address", ValueViewModel(addressString))
      ))
      NotificationViewModel.aboutTheIndividualList(aboutTheIndividual) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheIndividual = AboutTheIndividual()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheIndividual.fullName", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.dateOfBirth", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.mainOccupation", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.doTheyHaveANino", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForVAT", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.registeredForSA", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheIndividual.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheIndividualList(aboutTheIndividual) shouldEqual expected
    }
  }

  "aboutTheEstateList" should {
    "return populated values as rows hiding yes rows" in {
      val date = LocalDate.now()
      val aboutTheEstate = AboutTheEstate(
        fullName = Some("Some full name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheEstate.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutTheEstate.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutTheEstate.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutTheEstate.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutTheEstate.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheEstate.sautr", ValueViewModel("Some SAUTR")),
        SummaryListRowViewModel("notification.aboutTheEstate.address", ValueViewModel(addressString))
      ))
      NotificationViewModel.aboutTheEstateList(aboutTheEstate) shouldEqual expected
    }

    "return populated values as rows showing no rows" in {
      val date = LocalDate.now()
      val aboutTheEstate = AboutTheEstate(
        fullName = Some("Some full name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.No),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheEstate.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutTheEstate.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutTheEstate.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutTheEstate.doTheyHaveANino", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutTheEstate.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutTheEstate.registeredForVAT", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutTheEstate.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheEstate.registeredForSA", ValueViewModel(messages("service.unsure"))),
        SummaryListRowViewModel("notification.aboutTheEstate.sautr", ValueViewModel("Some SAUTR")),
        SummaryListRowViewModel("notification.aboutTheEstate.address", ValueViewModel(addressString))
      ))
      NotificationViewModel.aboutTheEstateList(aboutTheEstate) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val aboutTheEstate = AboutTheEstate()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheEstate.fullName", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.dateOfBirth", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.mainOccupation", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.doTheyHaveANino", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.registeredForVAT", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.registeredForSA", ValueViewModel("-")),
        SummaryListRowViewModel("notification.aboutTheEstate.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutTheEstateList(aboutTheEstate) shouldEqual expected
    }
  }

  "aboutYouList" should {
    "return populated values as rows when disclosing as the individual hiding Yes rows" in {
      val date = LocalDate.now()
      val aboutYou = AboutYou(
        fullName = Some("Some full name"),
        telephoneNumber = Some("Some phone number"),
        emailAddress = Some("Some email address"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.Yes),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Yes),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("Some phone number")),
        SummaryListRowViewModel("notification.aboutYou.emailAddress", ValueViewModel("Some email address")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel(addressString)),
        SummaryListRowViewModel("notification.aboutYou.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutYou.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutYou.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutYou.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutYou.sautr", ValueViewModel("Some SAUTR"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, true) shouldEqual expected
    }

    "return populated values as rows when disclosing as the individual, showing No rows" in {
      val date = LocalDate.now()
      val aboutYou = AboutYou(
        fullName = Some("Some full name"),
        telephoneNumber = Some("Some phone number"),
        emailAddress = Some("Some email address"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.No),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("Some phone number")),
        SummaryListRowViewModel("notification.aboutYou.emailAddress", ValueViewModel("Some email address")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel(addressString)),
        SummaryListRowViewModel("notification.aboutYou.dateOfBirth", ValueViewModel(date.toString)),
        SummaryListRowViewModel("notification.aboutYou.mainOccupation", ValueViewModel("Some occupation")),
        SummaryListRowViewModel("notification.aboutYou.doYouHaveANino", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutYou.nino", ValueViewModel("Some nino")),
        SummaryListRowViewModel("notification.aboutYou.registeredForVAT", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.aboutYou.vatRegNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutYou.registeredForSA", ValueViewModel(messages("service.unsure"))),
        SummaryListRowViewModel("notification.aboutYou.sautr", ValueViewModel("Some SAUTR"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, true) shouldEqual expected
    }

    "return populated values as rows when disclosing on behalf of the individual" in {
      val date = LocalDate.now()
      val aboutYou = AboutYou(
        fullName = Some("Some full name"),
        telephoneNumber = Some("Some phone number"),
        emailAddress = Some("Some email address"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some occupation"),
        doYouHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        vatRegNumber = Some("Some reg number"),
        registeredForSA = Some(YesNoOrUnsure.Unsure),
        sautr = Some("Some SAUTR"),
        address = Some(address)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("Some full name")),
        SummaryListRowViewModel("notification.aboutYou.telephoneNumber", ValueViewModel("Some phone number")),
        SummaryListRowViewModel("notification.aboutYou.emailAddress", ValueViewModel("Some email address")),
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel(addressString))
      ))
      NotificationViewModel.aboutYouList(aboutYou, false) shouldEqual expected
    }

    "return unpopulated values as rows with value dash when disclosing as the individual" in {
      val aboutYou = AboutYou()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutYou.fullName", ValueViewModel("-")),
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
        SummaryListRowViewModel("notification.aboutYou.address", ValueViewModel("-"))
      ))
      NotificationViewModel.aboutYouList(aboutYou, false) shouldEqual expected
    }
  }

  "companyList" should {
    "return populated values as rows" in {
      val aboutTheCompany = AboutTheCompany(Some("Some company name"), Some("Some reg number"), Some(address))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheCompany.name", ValueViewModel("Some company name")),
        SummaryListRowViewModel("notification.aboutTheCompany.registrationNumber", ValueViewModel("Some reg number")),
        SummaryListRowViewModel("notification.aboutTheCompany.address", ValueViewModel(addressString))
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
      val aboutTheTrust = AboutTheTrust(Some("Some trust name"), Some(address))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheTrust.name", ValueViewModel("Some trust name")),
        SummaryListRowViewModel("notification.aboutTheTrust.address", ValueViewModel(addressString))
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
      val aboutTheLLP = AboutTheLLP(Some("Some LLP name"), Some(address))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.aboutTheLLP.name", ValueViewModel("Some LLP name")),
        SummaryListRowViewModel("notification.aboutTheLLP.address", ValueViewModel(addressString))
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

  "YesNoOrUnsureToText" should {
    "convert Yes implicitly" in {
      val text: Text = YesNoOrUnsure.Yes
      text shouldEqual Text(messages("service.yes"))
    }

    "convert No implicitly" in {
      val text: Text = YesNoOrUnsure.No
      text shouldEqual Text(messages("service.no"))
    }

    "convert Unsure implicitly" in {
      val text: Text = YesNoOrUnsure.Unsure
      text shouldEqual Text(messages("service.unsure"))
    }
  }

  def toPrettyDate(date: LocalDateTime): String = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:MM:SS")
    date.format(dateFormatter)
  }

}
