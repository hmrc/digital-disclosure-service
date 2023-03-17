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
import java.time.{LocalDate, LocalDateTime, Month}
import viewmodels.implicits._
import play.api.i18n.{MessagesApi, Messages}
import play.api.test.FakeRequest
import models._
import models.address._
import models.address.Address._
import models.notification._
import utils.BaseSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class NotificationViewModelSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  val address = Address("line1", None, None, None, None, Country("GBR"))
  val addressString = AddressOps(address).getAddressLines.mkString(", ")
  
  "metadataList" should {
    "return populated values as rows" in {
      val date = LocalDateTime.of(2023, Month.MARCH, 4, 11, 3, 0)
      val metadata = Metadata(Some("Some reference"), Some(date))
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.metadata.reference", ValueViewModel("Some reference")),
        SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel("4 March 2023 11:03am"))
      ))
      NotificationViewModel.metadataList(metadata) shouldEqual Some(expected)
    }

    "return unpopulated values as None" in {
      val metadata = Metadata()
      NotificationViewModel.metadataList(metadata) shouldEqual None
    }

  }

  "backgroundList" should {
    "return populated values as rows where have you received is yes" in {
      val background = Background (
        haveYouReceivedALetter = Some(true),
        letterReferenceNumber= Some("Some letter reference"),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(true))),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some(true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.metadata.caseRef", ValueViewModel(messages("Some letter reference"))),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.onshore"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
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
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.offshore"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
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
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel(messages("notification.background.both"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return unpopulated values as rows with value dash" in {
      val background = Background()
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Company " in {
      val background = Background (None, None, Some(DisclosureEntity(Company, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Company"))),
        SummaryListRowViewModel("notification.background.areYouTheCompany", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a LLP " in {
      val background = Background (None, None, Some(DisclosureEntity(LLP, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.LLP"))),
        SummaryListRowViewModel("notification.background.areYouTheLLP", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Trust " in {
      val background = Background (None, None, Some(DisclosureEntity(Trust, Some(true))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Trust"))),
        SummaryListRowViewModel("notification.background.areYouTheTrust", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
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
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }

    "return organisation information where it's populated but set to false" in {
      val sourceSet: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.Dividends, IncomeOrGainSource.Interest)
      val background = Background (None, None, Some(DisclosureEntity(Individual, Some(false))), Some(false), None, None, None, Some(sourceSet), Some("Other income source"))
      val expectedSource = messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.dividends") + "<br/><br/>" + messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.interest") + "<br/><br/>" + "Other income source"
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("notification.background.haveYouReceivedALetter", ValueViewModel("-")),
        SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("notification.background.areYouTheIndividual", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.areYouRepresetingAnOrganisation", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("notification.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent(expectedSource)))
      ))
      NotificationViewModel.backgroundList(background) shouldEqual expected
    }
  }

}
