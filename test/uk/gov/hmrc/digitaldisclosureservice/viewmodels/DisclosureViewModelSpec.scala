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
import org.scalatest.wordspec.AnyWordSpec
import java.time.{LocalDateTime, Month}
import viewmodels.implicits._
import play.api.i18n.{MessagesApi, Messages}
import play.api.test.FakeRequest
import models._
import models.disclosure._
import models.address._
import models.address.Address._
import models.notification._
import utils.BaseSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class DisclosureViewModelSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  val address = Address("line1", None, None, None, None, Country("GBR"))
  val addressString = AddressOps(address).getAddressLines.mkString(", ")
  
  "metadataList" should {
    "return populated values as rows" in {
      val date = LocalDateTime.of(2023, Month.MARCH, 4, 11, 3, 0)
      val metadata = Metadata(Some("Some reference"), Some(date))

      val disclosureEntity = DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))
      val background = Background(disclosureEntity = Some(disclosureEntity))

      val caseReference = CaseReference(Some(true), Some("Case ref"))

      val countryOfYourOffshoreLiability = Map("GBR" -> CountryOfYourOffshoreLiability("GBR", "United Kingdom"), "FRA" -> CountryOfYourOffshoreLiability("FRA", "France"))
      val offshoreLiabilities = OffshoreLiabilities(countryOfYourOffshoreLiability = Some(countryOfYourOffshoreLiability))

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.metadata.reference", ValueViewModel("Some reference")),
        SummaryListRowViewModel("disclosure.metadata.submissionTime", ValueViewModel("04 March 2023 11:03am")),
        SummaryListRowViewModel("notification.metadata.caseRef", ValueViewModel("Case ref")),
        SummaryListRowViewModel("areYouTheEntity.Individual.heading", ValueViewModel(messages("areYouTheEntity.Individual.yes"))),
        SummaryListRowViewModel("disclosure.offshore.country", ValueViewModel("United Kingdom, France"))
      ))
      DisclosureViewModel.metadataList(background, metadata, caseReference, offshoreLiabilities, false) shouldEqual expected
    }

    "return unpopulated values as empty" in {
      val metadata = Metadata()
      DisclosureViewModel.metadataList(Background(), metadata, CaseReference(), OffshoreLiabilities(), false) shouldEqual SummaryListViewModel(rows = Nil)
    }

  }

  "backgroundList" should {
    "return populated values as rows where have you received is yes" in {
      val background = Background (
        haveYouReceivedALetter = Some(true),
        letterReferenceNumber= Some("Some letter reference"),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some (true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel(messages("notification.background.onshore"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

    "return populated values as rows where have you received is no" in {
      val background = Background (
        haveYouReceivedALetter = Some(false),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some (false)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel(messages("notification.background.offshore"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

    "return populated values as rows where both onshore and offshore" in {
      val background = Background (
        haveYouReceivedALetter = Some(false),
        disclosureEntity = Some(DisclosureEntity(Individual, Some(AreYouTheEntity.YesIAm))),
        offshoreLiabilities = Some(true),
        onshoreLiabilities = Some (true)
      )
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.Individual"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel(messages("notification.background.both"))),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Company " in {
      val background = Background (None, None, Some(DisclosureEntity(Company, Some(AreYouTheEntity.YesIAm))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.Company"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a LLP " in {
      val background = Background (None, None, Some(DisclosureEntity(LLP, Some(AreYouTheEntity.YesIAm))), None, None)
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.LLP"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent("")))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

    "return disclosure Entity row with information for a Trust " in {
      val sourceSet: Set[IncomeOrGainSource] = Set(IncomeOrGainSource.Dividends, IncomeOrGainSource.Interest)
      val background = Background (None, None, Some(DisclosureEntity(Trust, Some(AreYouTheEntity.YesIAm))), None, None, None, None, Some(sourceSet), Some("Other income source"))
      val expectedSource = messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.dividends") + "<br/>" + messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.interest") + "<br/>" + "Other income source"
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.background.disclosureEntity", ValueViewModel(messages("notification.background.Trust"))),
        SummaryListRowViewModel("disclosure.background.liabilities", ValueViewModel("-")),
        SummaryListRowViewModel("disclosure.offshore.incomeFrom", ValueViewModel(HtmlContent(expectedSource)))
      ))
      DisclosureViewModel.backgroundList(background) shouldEqual expected
    }

  }

  "otherLiabilitiesList" should {

    "display populated rows where you're the individual" in {
      val otherLiabilities = OtherLiabilities(
        issues = Some(Set(OtherLiabilityIssues.EmployerLiabilities, OtherLiabilityIssues.VatIssues)),
        inheritanceGift = Some("Some gift"),
        other = Some("Some other liability"),
        taxCreditsReceived = Some(true)
      ) 
      val expectedIssues = messages(s"otherLiabilityIssues.employerLiabilities") + ",<br/>" + messages(s"otherLiabilityIssues.vatIssues")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.otherLiabilities.issues", ValueViewModel(HtmlContent(expectedIssues))),
        SummaryListRowViewModel("disclosure.otherLiabilities.description", ValueViewModel("Some other liability")),
        SummaryListRowViewModel("disclosure.otherLiabilities.transfer", ValueViewModel("Some gift")),
        SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.you", ValueViewModel(messages("service.yes")))
      ))
      DisclosureViewModel.otherLiabilitiesList(otherLiabilities, true, "individual") shouldEqual expected
    }

    "display populated rows where you're not the individual" in {
      val otherLiabilities = OtherLiabilities(
        issues = Some(Set(OtherLiabilityIssues.EmployerLiabilities, OtherLiabilityIssues.VatIssues)),
        inheritanceGift = Some("Some gift"),
        other = Some("Some other liability"),
        taxCreditsReceived = Some(false)
      ) 
      val expectedIssues = messages(s"otherLiabilityIssues.employerLiabilities") + ",<br/>" + messages(s"otherLiabilityIssues.vatIssues")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.otherLiabilities.issues", ValueViewModel(HtmlContent(expectedIssues))),
        SummaryListRowViewModel("disclosure.otherLiabilities.description", ValueViewModel("Some other liability")),
        SummaryListRowViewModel("disclosure.otherLiabilities.transfer", ValueViewModel("Some gift")),
        SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.individual", ValueViewModel(messages("service.no")))
      ))
      DisclosureViewModel.otherLiabilitiesList(otherLiabilities, false, "Individual") shouldEqual expected
    }

    "display populated rows where it's an estate" in {
      val otherLiabilities = OtherLiabilities(
        issues = Some(Set(OtherLiabilityIssues.EmployerLiabilities, OtherLiabilityIssues.VatIssues)),
        inheritanceGift = Some("Some gift"),
        other = Some("Some other liability"),
        taxCreditsReceived = Some(false)
      ) 
      val expectedIssues = messages(s"otherLiabilityIssues.employerLiabilities") + ",<br/>" + messages(s"otherLiabilityIssues.vatIssues")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.otherLiabilities.issues", ValueViewModel(HtmlContent(expectedIssues))),
        SummaryListRowViewModel("disclosure.otherLiabilities.description", ValueViewModel("Some other liability")),
        SummaryListRowViewModel("disclosure.otherLiabilities.transfer", ValueViewModel("Some gift")),
        SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.person", ValueViewModel(messages("service.no")))
      ))
      DisclosureViewModel.otherLiabilitiesList(otherLiabilities, false, "Estate") shouldEqual expected
    }

    "display an empty list where nothing is populated" in {
      val otherLiabilities = OtherLiabilities() 
      val expected = SummaryListViewModel(Seq())
      DisclosureViewModel.otherLiabilitiesList(otherLiabilities, true, "Individual") shouldEqual expected
    }

  }

  "additionalList" should {

    "display populated rows" in {
      val reason = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.LetterFromHMRC)),
        otherReason = Some("Some other reason"),
        whyNotBeforeNow = Some("Reason why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("Some advice giver"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("Some org"),
        personProfession = Some("Some profession"),
        adviceGiven= Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.Email)),
        whichEmail = None,
        whichPhone = None,
        email = Some("Some email"),
        telephone = Some("Phone number")
      )
      val expectedReason = messages(s"whyAreYouMakingADisclosure.govUkGuidance") + ",<br/>" + messages(s"whyAreYouMakingADisclosure.letterFromHMRC")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.additional.reason", ValueViewModel(HtmlContent(expectedReason))),
        SummaryListRowViewModel("disclosure.additional.otherReason", ValueViewModel("Some other reason")),
        SummaryListRowViewModel("disclosure.additional.beforeNow", ValueViewModel("Reason why not before now")),
        SummaryListRowViewModel("disclosure.additional.adviceGiven.you", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("disclosure.additional.advice.name", ValueViewModel("Some advice giver")),
        SummaryListRowViewModel("disclosure.additional.advice.behalfOf", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("disclosure.additional.advice.business", ValueViewModel("Some org")),
        SummaryListRowViewModel("disclosure.additional.advice.profession", ValueViewModel("Some profession")),
        SummaryListRowViewModel("disclosure.additional.advice.given", ValueViewModel("Some advice")),
        SummaryListRowViewModel("disclosure.additional.advice.date", ValueViewModel("December 2012")),
        SummaryListRowViewModel("disclosure.additional.advice.discuss", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("disclosure.additional.advice.email", ValueViewModel("Some email")),
        SummaryListRowViewModel("disclosure.additional.advice.telephone", ValueViewModel("Phone number"))
      ))
      DisclosureViewModel.additionalInformationList(reason, true, "individual") shouldEqual expected
    }

    "display populated rows when not the individual" in {
      val reason = ReasonForDisclosingNow(
        reason = Some(Set(WhyAreYouMakingADisclosure.GovUkGuidance, WhyAreYouMakingADisclosure.LetterFromHMRC)),
        otherReason = Some("Some other reason"),
        whyNotBeforeNow = Some("Reason why not before now"),
        receivedAdvice = Some(true),
        personWhoGaveAdvice = Some("Some advice giver"),
        adviceOnBehalfOfBusiness = Some(false),
        adviceBusinessName = Some("Some org"),
        personProfession = Some("Some profession"),
        adviceGiven= Some(AdviceGiven("Some advice", MonthYear(12, 2012), AdviceContactPreference.No)),
        whichEmail = None,
        whichPhone = None,
        email = Some("Some email"),
        telephone = Some("Phone number")
      )
      val expectedReason = messages(s"whyAreYouMakingADisclosure.govUkGuidance") + ",<br/>" + messages(s"whyAreYouMakingADisclosure.letterFromHMRC")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.additional.reason", ValueViewModel(HtmlContent(expectedReason))),
        SummaryListRowViewModel("disclosure.additional.otherReason", ValueViewModel("Some other reason")),
        SummaryListRowViewModel("disclosure.additional.beforeNow", ValueViewModel("Reason why not before now")),
        SummaryListRowViewModel("disclosure.additional.adviceGiven.Individual", ValueViewModel(messages("service.yes"))),
        SummaryListRowViewModel("disclosure.additional.advice.name", ValueViewModel("Some advice giver")),
        SummaryListRowViewModel("disclosure.additional.advice.behalfOf", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("disclosure.additional.advice.business", ValueViewModel("Some org")),
        SummaryListRowViewModel("disclosure.additional.advice.profession", ValueViewModel("Some profession")),
        SummaryListRowViewModel("disclosure.additional.advice.given", ValueViewModel("Some advice")),
        SummaryListRowViewModel("disclosure.additional.advice.date", ValueViewModel("December 2012")),
        SummaryListRowViewModel("disclosure.additional.advice.discuss", ValueViewModel(messages("service.no"))),
        SummaryListRowViewModel("disclosure.additional.advice.email", ValueViewModel("Some email")),
        SummaryListRowViewModel("disclosure.additional.advice.telephone", ValueViewModel("Phone number"))
      ))
      DisclosureViewModel.additionalInformationList(reason, false, Individual.toString) shouldEqual expected
    }

    "display an empty list when nothing is populated" in {
      val reason = ReasonForDisclosingNow()
      val expected = SummaryListViewModel(Seq())
      DisclosureViewModel.additionalInformationList(reason, false, Individual.toString) shouldEqual expected
    }

  }

}
