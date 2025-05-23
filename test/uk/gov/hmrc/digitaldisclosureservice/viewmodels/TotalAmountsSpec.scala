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

package viewmodels

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import models._
import utils.BaseSpec
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import java.time.LocalDate
import scala.math.BigDecimal.RoundingMode

class TotalAmountsSpec extends AnyWordSpec with Matchers with BaseSpec with ScalaCheckPropertyChecks {

  "getPenaltyAmount" should {

    "determine the penalty from a rate and an unpaid amount" in {
      forAll(chooseNum[BigInt](BigInt(0), BigInt(99999999)), chooseNum[BigDecimal](BigDecimal(0), BigDecimal(200))) {
        (amount, rate) =>
          val expectedAmount = ((rate * BigDecimal(amount)) / 100).setScale(2, RoundingMode.DOWN)
          TotalAmounts.getPenaltyAmount(rate, amount) shouldEqual expectedAmount
      }
    }
  }

  "getPeriodTotal" should {

    "determine the penalty from a rate and an unpaid amount" in {
      forAll(
        chooseNum[BigInt](BigInt(0), BigInt(99999999)),
        arbitrary[Int],
        chooseNum[BigInt](BigInt(0), BigInt(99999999))
      ) { (amount, rate, interest) =>
        val penaltyAmount  = ((rate * BigDecimal(amount)) / 100).setScale(2, RoundingMode.DOWN)
        val expectedAmount = penaltyAmount + BigDecimal(amount) + BigDecimal(interest)
        TotalAmounts.getPeriodTotal(rate, amount, interest) shouldEqual expectedAmount
      }
    }

  }

  "+" should {
    "correctly recalculate the amountDueTotal rather than simply adding pre-calculated values" in {

      val totalA = TotalAmounts(
        unpaidTaxTotal = BigInt(100),
        niContributionsTotal = BigInt(50),
        interestTotal = BigInt(30),
        penaltyAmountTotal = BigDecimal(20.50),
        amountDueTotal = BigDecimal(200.50)
      )

      val totalB = TotalAmounts(
        unpaidTaxTotal = BigInt(200),
        niContributionsTotal = BigInt(75),
        interestTotal = BigInt(45),
        penaltyAmountTotal = BigDecimal(30.25),
        amountDueTotal = BigDecimal(350.25)
      )

      val expectedUnpaidTaxTotal       = totalA.unpaidTaxTotal + totalB.unpaidTaxTotal
      val expectedNiContributionsTotal = totalA.niContributionsTotal + totalB.niContributionsTotal
      val expectedInterestTotal        = totalA.interestTotal + totalB.interestTotal
      val expectedPenaltyAmountTotal   = totalA.penaltyAmountTotal + totalB.penaltyAmountTotal

      val expectedAmountDueTotal = BigDecimal(expectedUnpaidTaxTotal + expectedNiContributionsTotal) +
        BigDecimal(expectedInterestTotal) +
        expectedPenaltyAmountTotal

      val result = totalA + totalB

      result.unpaidTaxTotal       shouldEqual expectedUnpaidTaxTotal
      result.niContributionsTotal shouldEqual expectedNiContributionsTotal
      result.interestTotal        shouldEqual expectedInterestTotal
      result.penaltyAmountTotal   shouldEqual expectedPenaltyAmountTotal
      result.amountDueTotal       shouldEqual expectedAmountDueTotal

    }
  }

  "getDirectorLoanTotals" should {

    "total up all values for multiple director loan liabilities" in {
      val liabilities: Seq[DirectorLoanAccountLiabilities] = Seq(
        DirectorLoanAccountLiabilities(
          name = "Director name1",
          periodEnd = LocalDate.of(2022, 8, 23),
          overdrawn = BigInt(10),
          unpaidTax = BigInt(10),
          interest = BigInt(10),
          penaltyRate = 10.25,
          penaltyRateReason = "Some reason"
        ),
        DirectorLoanAccountLiabilities(
          name = "Director name2",
          periodEnd = LocalDate.of(2022, 8, 23),
          overdrawn = BigInt(20),
          unpaidTax = BigInt(20),
          interest = BigInt(20),
          penaltyRate = 20.25,
          penaltyRateReason = "Some reason"
        )
      )
      val expectedTotals                                   = TotalAmounts(
        unpaidTaxTotal = BigInt(30),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(30),
        penaltyAmountTotal = BigDecimal(5.07),
        amountDueTotal = BigDecimal(65.07)
      )
      TotalAmounts.getDirectorLoanTotals(liabilities) shouldEqual expectedTotals
    }

    "return the same values where only one element exists" in {
      val liabilities: Seq[DirectorLoanAccountLiabilities] = Seq(
        DirectorLoanAccountLiabilities(
          name = "Director name1",
          periodEnd = LocalDate.of(2022, 8, 23),
          overdrawn = BigInt(10),
          unpaidTax = BigInt(10),
          interest = BigInt(10),
          penaltyRate = 10.25,
          penaltyRateReason = "Some reason"
        )
      )
      val expectedTotals                                   = TotalAmounts(
        unpaidTaxTotal = BigInt(10),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(10),
        penaltyAmountTotal = BigDecimal(1.02),
        amountDueTotal = BigDecimal(21.02)
      )
      TotalAmounts.getDirectorLoanTotals(liabilities) shouldEqual expectedTotals
    }

    "return zeros for an empty lists" in {
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(0),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(0),
        penaltyAmountTotal = BigDecimal(0),
        amountDueTotal = BigDecimal(0)
      )
      TotalAmounts.getDirectorLoanTotals(Nil) shouldEqual expectedTotals
    }

  }

  "getCorporationTaxTotals" should {

    "total up all values for multiple director loan liabilities" in {
      val liabilities: Seq[CorporationTaxLiability] = Seq(
        CorporationTaxLiability(
          periodEnd = LocalDate.of(2022, 8, 23),
          howMuchIncome = BigInt(10),
          howMuchUnpaid = BigInt(10),
          howMuchInterest = BigInt(10),
          penaltyRate = 10.25,
          penaltyRateReason = "Some reason"
        ),
        CorporationTaxLiability(
          periodEnd = LocalDate.of(2022, 8, 23),
          howMuchIncome = BigInt(20),
          howMuchUnpaid = BigInt(20),
          howMuchInterest = BigInt(20),
          penaltyRate = 20.25,
          penaltyRateReason = "Some reason"
        )
      )
      val expectedTotals                            = TotalAmounts(
        unpaidTaxTotal = BigInt(30),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(30),
        penaltyAmountTotal = BigDecimal(5.07),
        amountDueTotal = BigDecimal(65.07)
      )
      TotalAmounts.getCorporationTaxTotals(liabilities) shouldEqual expectedTotals
    }

    "return the same values where only one element exists" in {
      val liabilities: Seq[CorporationTaxLiability] = Seq(
        CorporationTaxLiability(
          periodEnd = LocalDate.of(2022, 8, 23),
          howMuchIncome = BigInt(10),
          howMuchUnpaid = BigInt(10),
          howMuchInterest = BigInt(10),
          penaltyRate = 10.25,
          penaltyRateReason = "Some reason"
        )
      )
      val expectedTotals                            = TotalAmounts(
        unpaidTaxTotal = BigInt(10),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(10),
        penaltyAmountTotal = BigDecimal(1.02),
        amountDueTotal = BigDecimal(21.02)
      )
      TotalAmounts.getCorporationTaxTotals(liabilities) shouldEqual expectedTotals
    }

    "return zeros for an empty lists" in {
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(0),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(0),
        penaltyAmountTotal = BigDecimal(0),
        amountDueTotal = BigDecimal(0)
      )
      TotalAmounts.getCorporationTaxTotals(Nil) shouldEqual expectedTotals
    }

  }

  "getOffshoreTaxYearTotals" should {
    "total up all values for multiple director loan liabilities" in {
      val liabilities    = Map(
        "2012" -> TaxYearWithLiabilities(
          TaxYearStarting(2012),
          TaxYearLiabilities(
            income = BigInt(10),
            chargeableTransfers = BigInt(10),
            capitalGains = BigInt(10),
            unpaidTax = BigInt(10),
            interest = BigInt(10),
            penaltyRate = 10.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            foreignTaxCredit = false
          )
        ),
        "2011" -> TaxYearWithLiabilities(
          TaxYearStarting(2011),
          TaxYearLiabilities(
            income = BigInt(20),
            chargeableTransfers = BigInt(20),
            capitalGains = BigInt(20),
            unpaidTax = BigInt(20),
            interest = BigInt(20),
            penaltyRate = 20.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            foreignTaxCredit = false
          )
        )
      )
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(30),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(30),
        penaltyAmountTotal = BigDecimal(5.07),
        amountDueTotal = BigDecimal(65.07)
      )
      TotalAmounts.getOffshoreTaxYearTotals(liabilities) shouldEqual expectedTotals
    }

    "return the same values where only one element exists" in {
      val liabilities    = Map(
        "2012" -> TaxYearWithLiabilities(
          TaxYearStarting(2012),
          TaxYearLiabilities(
            income = BigInt(10),
            chargeableTransfers = BigInt(10),
            capitalGains = BigInt(10),
            unpaidTax = BigInt(10),
            interest = BigInt(10),
            penaltyRate = 10.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            foreignTaxCredit = false
          )
        )
      )
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(10),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(10),
        penaltyAmountTotal = BigDecimal(1.02),
        amountDueTotal = BigDecimal(21.02)
      )
      TotalAmounts.getOffshoreTaxYearTotals(liabilities) shouldEqual expectedTotals
    }

    "return zeros for an empty lists" in {
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(0),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(0),
        penaltyAmountTotal = BigDecimal(0),
        amountDueTotal = BigDecimal(0)
      )
      TotalAmounts.getOffshoreTaxYearTotals(Map.empty) shouldEqual expectedTotals
    }
  }

  "getOnshoreTaxYearTotals" should {
    "total up all values for multiple director loan liabilities" in {
      val liabilities    = Map(
        "2012" -> OnshoreTaxYearWithLiabilities(
          OnshoreYearStarting(2012),
          OnshoreTaxYearLiabilities(
            nonBusinessIncome = Some(BigInt(10)),
            businessIncome = Some(BigInt(10)),
            lettingIncome = Some(BigInt(10)),
            gains = Some(BigInt(10)),
            unpaidTax = BigInt(10),
            niContributions = BigInt(10),
            interest = BigInt(10),
            penaltyRate = 10.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            residentialTaxReduction = Some(false)
          )
        ),
        "2011" -> OnshoreTaxYearWithLiabilities(
          OnshoreYearStarting(2012),
          OnshoreTaxYearLiabilities(
            nonBusinessIncome = Some(BigInt(20)),
            businessIncome = Some(BigInt(20)),
            lettingIncome = Some(BigInt(20)),
            gains = Some(BigInt(20)),
            unpaidTax = BigInt(20),
            niContributions = BigInt(20),
            interest = BigInt(20),
            penaltyRate = 20.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            residentialTaxReduction = Some(false)
          )
        )
      )
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(30),
        niContributionsTotal = BigInt(30),
        interestTotal = BigInt(30),
        penaltyAmountTotal = BigDecimal(10.15),
        amountDueTotal = BigDecimal(100.15)
      )
      TotalAmounts.getOnshoreTaxYearTotals(liabilities) shouldEqual expectedTotals
    }

    "return the same values where only one element exists" in {
      val liabilities    = Map(
        "2012" -> OnshoreTaxYearWithLiabilities(
          OnshoreYearStarting(2012),
          OnshoreTaxYearLiabilities(
            nonBusinessIncome = Some(BigInt(10)),
            businessIncome = Some(BigInt(10)),
            lettingIncome = Some(BigInt(10)),
            gains = Some(BigInt(10)),
            unpaidTax = BigInt(10),
            niContributions = BigInt(10),
            interest = BigInt(10),
            penaltyRate = 10.25,
            penaltyRateReason = "Some reason",
            undeclaredIncomeOrGain = Some("Some gain"),
            residentialTaxReduction = Some(false)
          )
        )
      )
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(10),
        niContributionsTotal = BigInt(10),
        interestTotal = BigInt(10),
        penaltyAmountTotal = BigDecimal(2.05),
        amountDueTotal = BigDecimal(32.05)
      )
      TotalAmounts.getOnshoreTaxYearTotals(liabilities) shouldEqual expectedTotals
    }

    "return zeros for an empty lists" in {
      val expectedTotals = TotalAmounts(
        unpaidTaxTotal = BigInt(0),
        niContributionsTotal = BigInt(0),
        interestTotal = BigInt(0),
        penaltyAmountTotal = BigDecimal(0),
        amountDueTotal = BigDecimal(0)
      )
      TotalAmounts.getOnshoreTaxYearTotals(Map.empty) shouldEqual expectedTotals
    }
  }

}
