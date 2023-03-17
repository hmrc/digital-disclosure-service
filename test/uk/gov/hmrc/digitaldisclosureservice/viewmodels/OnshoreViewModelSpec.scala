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
import java.time.LocalDate
import models.address._
import models.address.Address._

class OnshoreViewModelSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency with ScalaCheckPropertyChecks with CurrentTaxYear {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  def now = TaxYear.now

  val liabilities = OnshoreTaxYearLiabilities(
    nonBusinessIncome = Some(BigInt(2000)),
    businessIncome = Some(BigInt(2000)),
    lettingIncome = Some(BigInt(2000)),
    gains = Some(BigInt(2000)),
    unpaidTax = BigInt(2000),
    niContributions = BigInt(2000),
    interest = BigInt(2000),
    penaltyRate = 12,
    penaltyRateReason = "Reason",
    residentialTaxReduction = Some(true)
  )

  val completeOnshoreLiabilities = OnshoreLiabilities(
    behaviour = Some(Set(WhyAreYouMakingThisOnshoreDisclosure.DidNotNotifyHasExcuse, WhyAreYouMakingThisOnshoreDisclosure.InaccurateReturnWithCare)),
    excuseForNotNotifying = Some(ReasonableExcuseOnshore("Some excuse", "Some years")), 
    reasonableCare = Some(ReasonableCareOnshore("Some excuse", "Some years")), 
    excuseForNotFiling = Some(ReasonableExcuseForNotFilingOnshore("Some excuse", "Some years")), 
    whichYears = Some(Set(OnshoreYearStarting(2012), OnshoreYearStarting(2014))),  
    youHaveNotIncludedTheTaxYear = Some("You have not included the tax year"),
    youHaveNotSelectedCertainTaxYears = Some("You have not selected certain tax years"),
    taxBeforeThreeYears = Some("Some liabilities - reasonable"),
    taxBeforeFiveYears = Some("Some liabilities - careless"),
    taxBeforeNineteenYears = Some("Some liabilities - deliberate"),
    disregardedCDF = Some(true),
    taxYearLiabilities = Some(Map("2012" -> OnshoreTaxYearWithLiabilities(OnshoreYearStarting(2012), liabilities))),
    lettingDeductions = Some(Map("2012" -> BigInt(123))),
    memberOfLandlordAssociations = Some(true),
    landlordAssociations = Some("Some associations"),
    howManyProperties = Some("7")
  )


  "primarySummaryList" should {

    def getYearKey(behaviour: Behaviour) = messages("disclosure.offshore.before", OnshoreLiabilitiesViewModel.getEarliestYearByBehaviour(behaviour).toString)

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
        SummaryListRowViewModel("disclosure.offshore.cdf", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel(messages("disclosure.offshore.notIncluding", "2014"), ValueViewModel(HtmlContent("You have not included the tax year"))),
        SummaryListRowViewModel(messages("disclosure.offshore.notIncluding", "2014"), ValueViewModel(HtmlContent("You have not selected certain tax years"))),
        SummaryListRowViewModel(getYearKey(Behaviour.ReasonableExcuse), ValueViewModel(HtmlContent("Some liabilities - reasonable"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Careless), ValueViewModel(HtmlContent("Some liabilities - careless"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Deliberate), ValueViewModel(HtmlContent("Some liabilities - deliberate"))),
        SummaryListRowViewModel("disclosure.property.landlord", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.associationMembership", ValueViewModel(HtmlContent("Some associations"))),
        SummaryListRowViewModel("disclosure.property.numberOfProperties", ValueViewModel(HtmlContent("7")))
      ))
      OnshoreLiabilitiesViewModel.primarySummaryList(completeOnshoreLiabilities, true, Individual.toString) shouldEqual expected
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
        SummaryListRowViewModel("disclosure.offshore.cdf", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel(messages("disclosure.offshore.notIncluding", "2014"), ValueViewModel(HtmlContent("You have not included the tax year"))),
        SummaryListRowViewModel(messages("disclosure.offshore.notIncluding", "2014"), ValueViewModel(HtmlContent("You have not selected certain tax years"))),
        SummaryListRowViewModel(getYearKey(Behaviour.ReasonableExcuse), ValueViewModel(HtmlContent("Some liabilities - reasonable"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Careless), ValueViewModel(HtmlContent("Some liabilities - careless"))),
        SummaryListRowViewModel(getYearKey(Behaviour.Deliberate), ValueViewModel(HtmlContent("Some liabilities - deliberate"))),
        SummaryListRowViewModel("disclosure.property.landlord", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.associationMembership", ValueViewModel(HtmlContent("Some associations"))),
        SummaryListRowViewModel("disclosure.property.numberOfProperties", ValueViewModel(HtmlContent("7")))
      ))
      OnshoreLiabilitiesViewModel.primarySummaryList(completeOnshoreLiabilities, false, Individual.toString) shouldEqual expected
    }

    "display an empty list when nothing is populated" in {
      val offshoreLiabilities = OnshoreLiabilities()
      val expected = SummaryListViewModel(Seq())
      OnshoreLiabilitiesViewModel.primarySummaryList(offshoreLiabilities, true, Individual.toString) shouldEqual expected
    }

  }

  "taxYearWithLiabilitiesToSummaryList" should {
    "return zeros where the list is empty" in {
      val taxYearWithLiabilities = OnshoreTaxYearWithLiabilities(
        OnshoreYearStarting(2012), 
        OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(11111)),
          businessIncome = Some(BigInt(22222)),
          lettingIncome = Some(BigInt(33333)),
          gains = Some(BigInt(44444)),
          unpaidTax = BigInt(55555),
          niContributions = BigInt(66666),
          interest = BigInt(77777),
          penaltyRate = 88,
          penaltyRateReason = "Some reason",
          residentialTaxReduction = Some(true)
        )
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.onshore.businessIncome", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.onshore.gains", ValueViewModel(HtmlContent("£44444"))),
        SummaryListRowViewModel("disclosure.onshore.lettingIncome", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.onshore.nonBusinessIncome", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.onshore.tax", ValueViewModel(HtmlContent("£55555"))),
        SummaryListRowViewModel("disclosure.onshore.ni", ValueViewModel(HtmlContent("£66666"))),
        SummaryListRowViewModel("disclosure.onshore.interest", ValueViewModel(HtmlContent("£77777"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyRate", ValueViewModel(HtmlContent("88%"))),
        SummaryListRowViewModel("disclosure.onshore.penalty", ValueViewModel(HtmlContent("£107554.48"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.onshore.deductions", ValueViewModel(HtmlContent("£123"))),
        SummaryListRowViewModel("disclosure.onshore.total", ValueViewModel(HtmlContent("£307552.48")))
      ))
      OnshoreLiabilitiesViewModel.taxYearWithLiabilitiesToSummaryList(taxYearWithLiabilities, Some(123)) shouldEqual expected
    }

    "hide foreign tax deductions if it's not populated" in {
      val taxYearWithLiabilities = OnshoreTaxYearWithLiabilities(
        OnshoreYearStarting(2012), 
        OnshoreTaxYearLiabilities(
          nonBusinessIncome = Some(BigInt(11111)),
          businessIncome = Some(BigInt(22222)),
          lettingIncome = Some(BigInt(33333)),
          gains = Some(BigInt(44444)),
          unpaidTax = BigInt(55555),
          niContributions = BigInt(66666),
          interest = BigInt(77777),
          penaltyRate = 88,
          penaltyRateReason = "Some reason",
          residentialTaxReduction = Some(false)
        )
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.onshore.businessIncome", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.onshore.gains", ValueViewModel(HtmlContent("£44444"))),
        SummaryListRowViewModel("disclosure.onshore.lettingIncome", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.onshore.nonBusinessIncome", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.onshore.tax", ValueViewModel(HtmlContent("£55555"))),
        SummaryListRowViewModel("disclosure.onshore.ni", ValueViewModel(HtmlContent("£66666"))),
        SummaryListRowViewModel("disclosure.onshore.interest", ValueViewModel(HtmlContent("£77777"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyRate", ValueViewModel(HtmlContent("88%"))),
        SummaryListRowViewModel("disclosure.onshore.penalty", ValueViewModel(HtmlContent("£107554.48"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.onshore.total", ValueViewModel(HtmlContent("£307552.48")))
      ))
      OnshoreLiabilitiesViewModel.taxYearWithLiabilitiesToSummaryList(taxYearWithLiabilities, None) shouldEqual expected
    }
  }

  implicit lazy val abitraryOnshoreTaxYearWithLiabilities: Arbitrary[OnshoreTaxYearWithLiabilities] =
    Arbitrary {
      for {
        year <- Gen.choose(2002, 2032)
        businessIncome <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        lettingIncome <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        nonBusinessIncome <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        capitalGains <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        unpaidTax <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        niContributions <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        interest <- Gen.choose(BigInt(1), BigInt("9999999999999999999"))
        penaltyRate <- arbitrary[Int]
        penaltyRateReason <- arbitrary[String]
        residentialTaxReduction <- arbitrary[Boolean]
      } yield {
        val taxYearLiabilities = OnshoreTaxYearLiabilities(
          Some(nonBusinessIncome), 
          Some(businessIncome),
          Some(lettingIncome),
          Some(capitalGains),
          unpaidTax,
          niContributions,
          interest,
          penaltyRate,
          penaltyRateReason,
          Some(residentialTaxReduction)
        )
        OnshoreTaxYearWithLiabilities(OnshoreYearStarting(year), taxYearLiabilities)
      }
    }

  "corporationTaxSummaryList" should {
    "display all rows" in {
      val corporationTaxLiability = CorporationTaxLiability(
        periodEnd = LocalDate.of(2022, 8, 23),
        howMuchIncome = BigInt(11111),
        howMuchUnpaid = BigInt(22222),
        howMuchInterest = BigInt(33333),
        penaltyRate = 44,
        penaltyRateReason = "Some reason"
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.onshore.accountingPeriod", ValueViewModel(HtmlContent("23 August 2022"))),
        SummaryListRowViewModel("disclosure.onshore.ct.income", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.onshore.corporationTax", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.onshore.interest", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyRate", ValueViewModel(HtmlContent("44%"))),
        SummaryListRowViewModel("disclosure.onshore.penalty", ValueViewModel(HtmlContent("£9777.68"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.onshore.ct.total", ValueViewModel(HtmlContent("£65332.68")))
      ))
      OnshoreLiabilitiesViewModel.corporationTaxSummaryList(corporationTaxLiability) shouldEqual expected
    }
  }

  "directorLoanSummaryList" should {
    "display all rows" in {
      val directorLoanAccountLiabilities = DirectorLoanAccountLiabilities(
        name = "Director name",
        periodEnd = LocalDate.of(2022, 8, 23),
        overdrawn = BigInt(11111),
        unpaidTax = BigInt(22222),
        interest = BigInt(33333),
        penaltyRate = 44,
        penaltyRateReason = "Some reason"
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.onshore.director.name", ValueViewModel(HtmlContent("Director name"))),
        SummaryListRowViewModel("disclosure.onshore.accountingPeriod", ValueViewModel(HtmlContent("23 August 2022"))),
        SummaryListRowViewModel("disclosure.onshore.director.overdrawn", ValueViewModel(HtmlContent("£11111"))),
        SummaryListRowViewModel("disclosure.onshore.tax", ValueViewModel(HtmlContent("£22222"))),
        SummaryListRowViewModel("disclosure.onshore.interest", ValueViewModel(HtmlContent("£33333"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyRate", ValueViewModel(HtmlContent("44%"))),
        SummaryListRowViewModel("disclosure.onshore.penalty", ValueViewModel(HtmlContent("£9777.68"))),
        SummaryListRowViewModel("disclosure.onshore.penaltyReason", ValueViewModel(HtmlContent("Some reason"))),
        SummaryListRowViewModel("disclosure.onshore.director.total", ValueViewModel(HtmlContent("£65332.68")))
      ))
      OnshoreLiabilitiesViewModel.directorLoanSummaryList(directorLoanAccountLiabilities) shouldEqual expected
    }
  }

  "lettingPropertySummaryList" should {
    "display all rows" in {
      val address = Address("line1", None, None, None, None, Country("GBR"))
      val addressString = AddressOps(address).getAddressLines.mkString(", ")
      val property = LettingProperty(
        address = Some(address),
        dateFirstLetOut = Some(LocalDate.of(2021,1,1)),
        stoppedBeingLetOut = Some(true),
        noLongerBeingLetOut = Some(NoLongerBeingLetOut(LocalDate.of(2022,1,1), "Something happened")),
        fhl = Some(true),
        isJointOwnership = Some(false),
        isMortgageOnProperty = Some(true),
        percentageIncomeOnProperty = Some(12),
        wasFurnished = Some(false),
        typeOfMortgage = Some(TypeOfMortgageDidYouHave.Other),
        otherTypeOfMortgage = Some("Some other type of mortgage"),
        wasPropertyManagerByAgent = Some(true),
        didTheLettingAgentCollectRentOnYourBehalf = Some(false)
      )

      val expected = SummaryListViewModel(Seq(
        SummaryListRowViewModel("disclosure.property.address", ValueViewModel(HtmlContent(addressString))),
        SummaryListRowViewModel("disclosure.property.firstLet", ValueViewModel(HtmlContent("1 January 2021"))),
        SummaryListRowViewModel("disclosure.property.stoppedLet", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.stoppedLetDate", ValueViewModel(HtmlContent("1 January 2022"))),
        SummaryListRowViewModel("disclosure.property.happenedTo", ValueViewModel(HtmlContent("Something happened"))),
        SummaryListRowViewModel("disclosure.property.furnished", ValueViewModel(HtmlContent(messages("service.no")))),
        SummaryListRowViewModel("disclosure.property.fhl", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.joint", ValueViewModel(HtmlContent(messages("service.no")))),
        SummaryListRowViewModel("disclosure.property.share", ValueViewModel(HtmlContent("12%"))),
        SummaryListRowViewModel("disclosure.property.mortgage", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.mortgageType", ValueViewModel(HtmlContent("Some other type of mortgage"))),
        SummaryListRowViewModel("disclosure.property.lettingAgent", ValueViewModel(HtmlContent(messages("service.yes")))),
        SummaryListRowViewModel("disclosure.property.collectRent", ValueViewModel(HtmlContent(messages("service.no"))))
      ))

      OnshoreLiabilitiesViewModel.lettingPropertySummaryList(property) shouldEqual expected
    }
  }

}