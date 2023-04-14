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

import models._
import models.disclosure._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import play.api.i18n.Messages
import uk.gov.hmrc.time.{CurrentTaxYear, TaxYear}
import play.twirl.api.HtmlFormat
import scala.math.BigDecimal.RoundingMode

case class OffshoreLiabilitiesViewModel(
  summaryList: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)]
)

object OffshoreLiabilitiesViewModel extends CurrentTaxYear {

  def now = TaxYear.now

  val DELIBERATE_YEARS = 19
  val REASONABLE_EXCUSE_LEGISLATION_START = 2015
  val CARELESS_LEGISLATION_START = 2013
  val YEARS_TO_GO_BACK = 13

  def getEarliestYearByBehaviour(behaviour: Behaviour): Int = {
    val yearsToGoBack = getNumberOfYearsForBehaviour(behaviour)
    current.back(yearsToGoBack+1).startYear
  }

  def getNumberOfYearsForBehaviour(behaviour: Behaviour): Int = behaviour match {
    case Behaviour.ReasonableExcuse => getNumberOfYears(TaxYear(REASONABLE_EXCUSE_LEGISLATION_START))
    case Behaviour.Careless         => getNumberOfYears(TaxYear(CARELESS_LEGISLATION_START))
    case Behaviour.Deliberate       => DELIBERATE_YEARS
  }

  def getNumberOfYears(legislationStartYear: TaxYear): Int = {
    val earliestDate = List(current.back(YEARS_TO_GO_BACK).startYear, legislationStartYear.startYear).max
    current.startYear - earliestDate - 1
  }

  def apply(offshoreLiabilities: OffshoreLiabilities, disclosingAboutThemselves: Boolean, entity: String, offerAmount: Option[BigInt])(implicit messages: Messages): OffshoreLiabilitiesViewModel = {

    val taxYears: Seq[TaxYearWithLiabilities] = offshoreLiabilities.taxYearLiabilities.getOrElse(Map()).values.toSeq
  
    val foreignTaxCredits = offshoreLiabilities.taxYearForeignTaxDeductions.getOrElse(Map())
    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.map(year => (year.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(year, foreignTaxCredits.get(year.taxYear.startYear.toString))))

    OffshoreLiabilitiesViewModel(
      primarySummaryList(offshoreLiabilities, disclosingAboutThemselves, entity), 
      taxYearLists
    )

  }
  
  def primarySummaryList(offshoreLiabilities: OffshoreLiabilities, disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = {
    SummaryListViewModel(
      rows = Seq(
        offshoreLiabilities.behaviour.map(answer => whyAreYouMakingThisDisclosure(answer, disclosingAboutThemselves, entity)),
        offshoreLiabilities.excuseForNotNotifying.map(answer => row("disclosure.offshore.reasonableExcuse", answer.excuse)),
        offshoreLiabilities.excuseForNotNotifying.map(answer => row("disclosure.offshore.reasonableExcuse.years", answer.years)),
        offshoreLiabilities.reasonableCare.map(answer => row("disclosure.offshore.reasonableCare", answer.reasonableCare)),
        offshoreLiabilities.reasonableCare.map(answer => row("disclosure.offshore.reasonableCare.years", answer.yearsThisAppliesTo)),
        offshoreLiabilities.excuseForNotFiling.map(answer => row("disclosure.offshore.notfileExcuse", answer.reasonableExcuse)),
        offshoreLiabilities.excuseForNotFiling.map(answer => row("disclosure.offshore.notfileExcuse.years", answer.yearsThisAppliesTo)),
        offshoreLiabilities.disregardedCDF.map(answer => row("disclosure.offshore.cdf", messages("service.yes"))),
        offshoreLiabilities.youHaveNotIncludedTheTaxYear.map(answer => row(messages("disclosure.offshore.notIncluding", getMissingYears(offshoreLiabilities)), answer)),
        offshoreLiabilities.youHaveNotSelectedCertainTaxYears.map(answer => row(messages("disclosure.offshore.notIncluding", getMissingYears(offshoreLiabilities)), answer)),
        offshoreLiabilities.taxBeforeFiveYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString), answer)),
        offshoreLiabilities.taxBeforeSevenYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.Careless).toString), answer)),
        offshoreLiabilities.taxBeforeNineteenYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.Deliberate).toString), answer)),
        offshoreLiabilities.legalInterpretation.map(legalInterpretation),
        offshoreLiabilities.otherInterpretation.map(answer => row("disclosure.offshore.legal.other", answer.toString)),
        offshoreLiabilities.notIncludedDueToInterpretation.map(answer => row("disclosure.offshore.notInc", messages(s"howMuchTaxHasNotBeenIncluded.${answer.toString}"))),
        offshoreLiabilities.maximumValueOfAssets.map(answer => row("disclosure.offshore.maxValue", messages(s"theMaximumValueOfAllAssets.${answer.toString}")))
      ).flatten
    )
  }

  def getMissingYears(offshoreLiabilities: OffshoreLiabilities): String = {
    val yearList = offshoreLiabilities.whichYears.getOrElse(Nil).collect{case TaxYearStarting(y) => TaxYearStarting(y)}.toList
    TaxYearStarting.findMissingYears(yearList).map(_.startYear+1).mkString(", ")
  }

  def whyAreYouMakingThisDisclosure(answers: Set[WhyAreYouMakingThisDisclosure], disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = {
    val value = ValueViewModel(
      HtmlContent(
        answers.map {
          answer => HtmlFormat.escape(messages(if(disclosingAboutThemselves) s"whyAreYouMakingThisDisclosure.you.$answer" else s"whyAreYouMakingThisDisclosure.${entity.toLowerCase}.$answer")).toString
        }
        .mkString("<br/><br/>")
      )
    )

    SummaryListRowViewModel("disclosure.offshore.reason", value)
  }

  def legalInterpretation(answers: Set[YourLegalInterpretation])(implicit messages: Messages) = {
    val value = ValueViewModel(
      HtmlContent(
        answers.map {
          answer => HtmlFormat.escape(messages(s"yourLegalInterpretation.$answer")).toString
        }
        .mkString(",<br/><br/>")
      )
    )

    SummaryListRowViewModel("disclosure.offshore.legal", value)
  }

  def taxYearWithLiabilitiesToSummaryList(yearWithLiabilites: TaxYearWithLiabilities, foreignTaxCredit: Option[BigInt])(implicit messages: Messages): SummaryList = {

    val liabilities = yearWithLiabilites.taxYearLiabilities

    val foreignTaxCreditRow = foreignTaxCredit match {
      case Some(value) => Seq(poundRow("disclosure.offshore.deductions", s"${value}"))
      case _ => Nil
    }

    val penaltyAmount = getPenaltyAmount(liabilities.penaltyRate, liabilities.unpaidTax)
    val yearTotal = getPeriodTotal(liabilities.penaltyRate, liabilities.unpaidTax, liabilities.interest)

    val rows = Seq(
      poundRow("disclosure.offshore.income", s"${liabilities.income}"),
      poundRow("disclosure.offshore.transfers", s"${liabilities.chargeableTransfers}"),
      poundRow("disclosure.offshore.gains", s"${liabilities.capitalGains}"),
      poundRow("disclosure.offshore.tax", s"${liabilities.unpaidTax}"),
      poundRow("disclosure.offshore.interest", s"${liabilities.interest}"),
      row("disclosure.offshore.penaltyRate", s"${liabilities.penaltyRate}%"),
      poundRow("disclosure.offshore.penalty", s"${penaltyAmount}"),
      row("disclosure.offshore.penaltyReason", liabilities.penaltyRateReason)
    ) ++ foreignTaxCreditRow ++ Seq(poundRow("disclosure.offshore.total", f"${yearTotal}%1.2f"))

    SummaryListViewModel(rows)
  }

  def row(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent(HtmlFormat.escape(value).toString))
    )
  }

  def poundRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent("£" + HtmlFormat.escape(value).toString))
    )
  }

  private def getPenaltyAmount(penaltyRate: BigDecimal, unpaidAmount: BigInt): BigDecimal = {
    ((penaltyRate * BigDecimal(unpaidAmount)) /100).setScale(2, RoundingMode.DOWN)
  }
  
  private def getPeriodTotal(penaltyRate: BigDecimal, unpaidAmount: BigInt, interest: BigInt): BigDecimal = {
    BigDecimal(unpaidAmount) + getPenaltyAmount(penaltyRate, unpaidAmount) + BigDecimal(interest)
  }

}

