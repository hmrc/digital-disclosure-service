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
import viewmodels.implicits._
import play.api.i18n.{MessagesApi, Messages}
import play.api.test.FakeRequest
import models._
import models.disclosure._
import models.notification._
import utils.BaseSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.time.{TaxYear, CurrentTaxYear}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary.arbitrary

class OffshoreViewModelSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency with ScalaCheckPropertyChecks with CurrentTaxYear {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  def now = TaxYear.now

  val liabilities = TaxYearLiabilities(
    income = BigInt(2000),
    chargeableTransfers = BigInt(2000),
    capitalGains = BigInt(2000),
    unpaidTax = BigInt(2000),
    interest = BigInt(2000),
    penaltyRate = 12,
    penaltyRateReason = "Reason",
    foreignTaxCredit = false
  )

  val completeOffshoreLiabilities = OffshoreLiabilities(
    Some(Set(WhyAreYouMakingThisDisclosure.DidNotNotifyHasExcuse, WhyAreYouMakingThisDisclosure.InaccurateReturnWithCare)), 
    Some(WhatIsYourReasonableExcuse("Some excuse", "Some years")), 
    Some(WhatReasonableCareDidYouTake("Some excuse", "Some years")), 
    Some(WhatIsYourReasonableExcuseForNotFilingReturn("Some excuse", "Some years")), 
    Some(Set(TaxYearStarting(2012))), 
    Some("You have not included the tax year"),
    Some("You have not selected certain tax years"),
    Some("Some liabilities - reasonable"),
    Some("Some liabilities - careless"),
    Some("Some liabilities - deliberate"),
    Some("Missing year details"),
    Some("Missing years details"),
    Some(true),
    Some(Map("2012" -> TaxYearWithLiabilities(TaxYearStarting(2012), liabilities))),
    Some(Map("2012" -> 123)),
    Some(Map()),
    Some(Set(WhereDidTheUndeclaredIncomeOrGainIncluded.Dividends, WhereDidTheUndeclaredIncomeOrGainIncluded.Interest)),
    Some("Other income source"),
    Some(Set(YourLegalInterpretation.AnotherIssue, YourLegalInterpretation.YourDomicileStatus)),
    Some("Some interpretation"),
    Some(HowMuchTaxHasNotBeenIncluded.TenThousandOrLess),
    Some(TheMaximumValueOfAllAssets.Below500k)
  )

  "penaltyAmount" should {

    "apply the penalty rate to the amount of unpaid tax" in {
      forAll(arbitrary[TaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val expectedAmount = (BigDecimal(penaltyRate) * BigDecimal(unpaidTax)) / 100
        OffshoreLiabilitiesViewModel.penaltyAmount(taxYearWithLiabilities.taxYearLiabilities) shouldEqual expectedAmount
      }
    }

  }

  "yearTotal" should {

    "add the penalty amount to the unpaid tax and the interest" in {
      forAll(arbitrary[TaxYearWithLiabilities]) { taxYearWithLiabilities =>
        val penaltyRate = taxYearWithLiabilities.taxYearLiabilities.penaltyRate
        val unpaidTax = taxYearWithLiabilities.taxYearLiabilities.unpaidTax
        val penaltyAmount: BigDecimal = (BigDecimal(penaltyRate) / 100) * BigDecimal(unpaidTax)
        val interest = taxYearWithLiabilities.taxYearLiabilities.interest
        val expectedAmount: BigDecimal = penaltyAmount + BigDecimal(interest) + BigDecimal(unpaidTax)
        OffshoreLiabilitiesViewModel.yearTotal(taxYearWithLiabilities.taxYearLiabilities) shouldEqual expectedAmount
      }
    }

  }

  "primarySummaryList" should {

    def getYearKey(behaviour: Behaviour) = messages("disclosure.offshore.before", OffshoreLiabilitiesViewModel.getEarliestYearByBehaviour(behaviour).toString)

    "display populated rows when they are the individual" in {

      val expectedBehaviour = messages(s"whyAreYouMakingThisDisclosure.you.didNotNotifyHasExcuse") + "<br/><br/>" + messages(s"whyAreYouMakingThisDisclosure.you.inaccurateReturnWithCare")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.offshore.reason", ValueViewModel(HtmlContent(expectedBehaviour))),
        SummaryListRowViewModel("disclosure.offshore.reasonableExcuse", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableExcuse.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableCare", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableCare.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel("disclosure.offshore.notfileExcuse", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.notfileExcuse.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel(getYearKey(Behaviour.ReasonableExcuse), ValueViewModel(HtmlContent("Some liabilities - reasonable"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Careless), ValueViewModel(HtmlContent("Some liabilities - careless"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Deliberate), ValueViewModel(HtmlContent("Some liabilities - deliberate")))
      ))
      OffshoreLiabilitiesViewModel.primarySummaryList(completeOffshoreLiabilities, true, Individual.toString) shouldEqual expected
    }

    "display populated rows when they are not the individual" in {
      val expectedBehaviour = messages(s"whyAreYouMakingThisDisclosure.individual.didNotNotifyHasExcuse") + "<br/><br/>" + messages(s"whyAreYouMakingThisDisclosure.individual.inaccurateReturnWithCare")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.offshore.reason", ValueViewModel(HtmlContent(expectedBehaviour))),
        SummaryListRowViewModel("disclosure.offshore.reasonableExcuse", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableExcuse.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableCare", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.reasonableCare.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel("disclosure.offshore.notfileExcuse", ValueViewModel(HtmlContent("Some excuse"))),
        SummaryListRowViewModel("disclosure.offshore.notfileExcuse.years", ValueViewModel(HtmlContent("Some years"))),
        SummaryListRowViewModel(getYearKey(Behaviour.ReasonableExcuse), ValueViewModel(HtmlContent("Some liabilities - reasonable"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Careless), ValueViewModel(HtmlContent("Some liabilities - careless"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Deliberate), ValueViewModel(HtmlContent("Some liabilities - deliberate")))
      ))
      OffshoreLiabilitiesViewModel.primarySummaryList(completeOffshoreLiabilities, false, Individual.toString) shouldEqual expected
    }

    "display an empty list when nothing is populated" in {
      val offshoreLiabilities = OffshoreLiabilities()
      val expected = SummaryListViewModel(Seq())
      OffshoreLiabilitiesViewModel.primarySummaryList(offshoreLiabilities, true, Individual.toString) shouldEqual expected
    }

  }

  "legalInterpretationlist" should {

    "display populated rows" in {
      val expectedLegal = messages(s"yourLegalInterpretation.anotherIssue") + ",<br/><br/>" + messages(s"yourLegalInterpretation.yourDomicileStatus")
      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.offshore.legal", ValueViewModel(HtmlContent(expectedLegal))),
        SummaryListRowViewModel("disclosure.offshore.legal.other", ValueViewModel(HtmlContent("Some interpretation"))),
        SummaryListRowViewModel("disclosure.offshore.notInc", ValueViewModel(HtmlContent(messages(s"howMuchTaxHasNotBeenIncluded.tenThousandOrLess")))),
        SummaryListRowViewModel("disclosure.offshore.maxValue", ValueViewModel(HtmlContent(messages(s"theMaximumValueOfAllAssets.below500k"))))
      ))
      OffshoreLiabilitiesViewModel.legalInterpretationlist(completeOffshoreLiabilities) shouldEqual expected
    }

    "display an empty list where nothing's populated" in {
      val offshoreLiabilities = OffshoreLiabilities()
      val expected = SummaryListViewModel(Seq())
      OffshoreLiabilitiesViewModel.legalInterpretationlist(offshoreLiabilities) shouldEqual expected
    }
  }

  "taxYearWithLiabilitiesToSummaryList" should {
    "display all rows" in {
      val taxYearWithLiabilities = TaxYearWithLiabilities(
        TaxYearStarting(2012), 
        TaxYearLiabilities(
          income = BigInt(11111),
          chargeableTransfers = BigInt(22222),
          capitalGains = BigInt(33333),
          unpaidTax = BigInt(44444),
          interest = BigInt(55555),
          penaltyRate = 66,
          penaltyRateReason = "Some reason",
          foreignTaxCredit = true
        )
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.offshore.income", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.offshore.transfers", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.offshore.gains", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.offshore.tax", ValueViewModel(HtmlContent("£44444"))),
        SummaryListRowViewModel("disclosure.offshore.interest", ValueViewModel(HtmlContent("£55555"))),
        SummaryListRowViewModel("disclosure.offshore.penaltyRate", ValueViewModel(HtmlContent("66%"))),
        SummaryListRowViewModel("disclosure.offshore.penalty", ValueViewModel(HtmlContent("£29333.04"))),
        SummaryListRowViewModel("disclosure.offshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.offshore.deductions", ValueViewModel(HtmlContent("£123"))),
        SummaryListRowViewModel("disclosure.offshore.total", ValueViewModel(HtmlContent("£129332.04")))
      ))
      OffshoreLiabilitiesViewModel.taxYearWithLiabilitiesToSummaryList(taxYearWithLiabilities, Some(123)) shouldEqual expected
    }

    "hide foreign tax deductions if it's not populated" in {
      val taxYearWithLiabilities = TaxYearWithLiabilities(
        TaxYearStarting(2012), 
        TaxYearLiabilities(
          income = BigInt(11111),
          chargeableTransfers = BigInt(22222),
          capitalGains = BigInt(33333),
          unpaidTax = BigInt(44444),
          interest = BigInt(55555),
          penaltyRate = 66,
          penaltyRateReason = "Some reason",
          foreignTaxCredit = true
        )
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.offshore.income", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.offshore.transfers", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.offshore.gains", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.offshore.tax", ValueViewModel(HtmlContent("£44444"))),
        SummaryListRowViewModel("disclosure.offshore.interest", ValueViewModel(HtmlContent("£55555"))),
        SummaryListRowViewModel("disclosure.offshore.penaltyRate", ValueViewModel(HtmlContent("66%"))),
        SummaryListRowViewModel("disclosure.offshore.penalty", ValueViewModel(HtmlContent("£29333.04"))),
        SummaryListRowViewModel("disclosure.offshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.offshore.total", ValueViewModel(HtmlContent("£129332.04")))
      ))
      OffshoreLiabilitiesViewModel.taxYearWithLiabilitiesToSummaryList(taxYearWithLiabilities, None) shouldEqual expected
    }
  }

  "totalAmountsSummaryList" should {
    "display all rows" in {
      def taxYearWithLiabilities(int: Int) = TaxYearWithLiabilities(
        TaxYearStarting(2012), 
        TaxYearLiabilities(
          income = BigInt(int),
          chargeableTransfers = BigInt(int),
          capitalGains = BigInt(int),
          unpaidTax = BigInt(int),
          interest = BigInt(int),
          penaltyRate = int,
          penaltyRateReason = "Some reason",
          foreignTaxCredit = false
        )
      )

      val taxYears = Seq(taxYearWithLiabilities(1), taxYearWithLiabilities(2), taxYearWithLiabilities(3))

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.totals.tax", ValueViewModel(HtmlContent("£6"))),
        SummaryListRowViewModel("disclosure.totals.interest", ValueViewModel(HtmlContent("£6"))),
        SummaryListRowViewModel("disclosure.totals.penalty", ValueViewModel(HtmlContent("£0.14"))),
        SummaryListRowViewModel("disclosure.totals.amount", ValueViewModel(HtmlContent("£12.14")))
      ))
      OffshoreLiabilitiesViewModel.totalAmountsSummaryList(taxYears) shouldEqual expected
    }
  }

  implicit lazy val abitraryTaxYearWithLiabilities: Arbitrary[TaxYearWithLiabilities] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
        income <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        chargeableTransfers <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        capitalGains <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        unpaidTax <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        interest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- arbitrary[Int]
        penaltyRateReason <- arbitrary[String]
        foreignTaxCredit <- arbitrary[Boolean]
      } yield {
        val taxYearLiabilities = TaxYearLiabilities(
          income, 
          chargeableTransfers,
          capitalGains,
          unpaidTax,
          interest,
          penaltyRate,
          penaltyRateReason,
          foreignTaxCredit
        )
        TaxYearWithLiabilities(TaxYearStarting(year), taxYearLiabilities)
      }
    }

}
