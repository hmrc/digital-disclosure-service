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
import java.time.format.DateTimeFormatter

case class OnshoreLiabilitiesViewModel(
  summaryList: SummaryList,
  taxYearLists: Seq[(Int, SummaryList)],
  corporationTaxLists: Seq[(Int, SummaryList)],
  directorLoanLists: Seq[(Int, SummaryList)],
  lettingPropertyLists: Seq[(Int, SummaryList)]
)

object OnshoreLiabilitiesViewModel extends CurrentTaxYear {

  def now = TaxYear.now

  val downloadDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
  val dmsDateFormatter = DateTimeFormatter.ofPattern("d/MM/YYYY")

  val DELIBERATE_YEARS = 19
  val REASONABLE_EXCUSE_YEARS = 3
  val CARELESS_YEARS = 5 

  def getEarliestYearByBehaviour(behaviour: Behaviour): Int = {
    val yearsToGoBack = getNumberOfYearsForBehaviour(behaviour)
    current.back(yearsToGoBack+1).startYear
  }

  def getNumberOfYearsForBehaviour(behaviour: Behaviour): Int = behaviour match {
    case Behaviour.ReasonableExcuse => REASONABLE_EXCUSE_YEARS
    case Behaviour.Careless         => CARELESS_YEARS
    case Behaviour.Deliberate       => DELIBERATE_YEARS
  }

  def apply(onshoreLiabilities: OnshoreLiabilities, disclosingAboutThemselves: Boolean, entity: String, offerAmount: Option[BigInt], caseflowDateFormat: Boolean)(implicit messages: Messages): OnshoreLiabilitiesViewModel = {

    val taxYears: Seq[OnshoreTaxYearWithLiabilities] = onshoreLiabilities.taxYearLiabilities.getOrElse(Map()).values.toSeq
  
    val lettingDeduction = onshoreLiabilities.lettingDeductions.getOrElse(Map())
    val taxYearLists: Seq[(Int, SummaryList)] = taxYears.map(year => (year.taxYear.startYear, taxYearWithLiabilitiesToSummaryList(year, lettingDeduction.get(year.taxYear.startYear.toString))))

    val corporationTaxLists = onshoreLiabilities.corporationTaxLiabilities.getOrElse(Seq()).zipWithIndex.map{case (ct, i) => (i+1, corporationTaxSummaryList(ct, caseflowDateFormat))}
    val directorLoanLists = onshoreLiabilities.directorLoanAccountLiabilities.getOrElse(Seq()).zipWithIndex.map{case (dl, i) => (i+1, directorLoanSummaryList(dl, caseflowDateFormat))}
    val lettingPropertyLists = onshoreLiabilities.lettingProperties.getOrElse(Seq()).zipWithIndex.map{case (p, i) => (i+1, lettingPropertySummaryList(p, caseflowDateFormat))}

    OnshoreLiabilitiesViewModel(
      primarySummaryList(onshoreLiabilities, disclosingAboutThemselves, entity), 
      taxYearLists, 
      corporationTaxLists,
      directorLoanLists,
      lettingPropertyLists
    )

  }
  
  def primarySummaryList(onshoreLiabilities: OnshoreLiabilities, disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = {
    SummaryListViewModel(
      rows = Seq(
        onshoreLiabilities.behaviour.map(answer => whyAreYouMakingThisDisclosure(answer, disclosingAboutThemselves, entity)),
        onshoreLiabilities.excuseForNotNotifying.map(answer => row("disclosure.offshore.reasonableExcuse", answer.excuse)),
        onshoreLiabilities.excuseForNotNotifying.map(answer => row("disclosure.offshore.reasonableExcuse.years", answer.years)),
        onshoreLiabilities.reasonableCare.map(answer => row("disclosure.offshore.reasonableCare", answer.reasonableCare)),
        onshoreLiabilities.reasonableCare.map(answer => row("disclosure.offshore.reasonableCare.years", answer.yearsThisAppliesTo)),
        onshoreLiabilities.excuseForNotFiling.map(answer => row("disclosure.offshore.notfileExcuse", answer.reasonableExcuse)),
        onshoreLiabilities.excuseForNotFiling.map(answer => row("disclosure.offshore.notfileExcuse.years", answer.yearsThisAppliesTo)),
        onshoreLiabilities.disregardedCDF.map(answer => row("disclosure.offshore.cdf", messages("service.yes"))),
        onshoreLiabilities.youHaveNotIncludedTheTaxYear.map(answer => row(messages("disclosure.offshore.notIncluding", getMissingYears(onshoreLiabilities)), answer)),
        onshoreLiabilities.youHaveNotSelectedCertainTaxYears.map(answer => row(messages("disclosure.offshore.notIncluding", getMissingYears(onshoreLiabilities)), answer)),
        onshoreLiabilities.taxBeforeThreeYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.ReasonableExcuse).toString), answer)),
        onshoreLiabilities.taxBeforeFiveYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.Careless).toString), answer)),
        onshoreLiabilities.taxBeforeNineteenYears.map(answer => row(messages("disclosure.offshore.before", getEarliestYearByBehaviour(Behaviour.Deliberate).toString), answer)),
        onshoreLiabilities.memberOfLandlordAssociations.map(answer => row(messages("disclosure.property.landlord"), booleanText(answer))),
        onshoreLiabilities.landlordAssociations.map(answer => row(messages("disclosure.property.associationMembership"), answer)),
        onshoreLiabilities.howManyProperties.map(answer => row(messages("disclosure.property.numberOfProperties"), answer.toString))
      ).flatten
    )

  }

  def getMissingYears(onshoreLiabilities: OnshoreLiabilities): String = {
    val yearList = onshoreLiabilities.whichYears.getOrElse(Nil).collect{case OnshoreYearStarting(y) => OnshoreYearStarting(y)}.toList
    OnshoreYearStarting.findMissingYears(yearList).map(_.startYear+1).mkString(", ")
  }


  def whyAreYouMakingThisDisclosure(answers: Set[WhyAreYouMakingThisOnshoreDisclosure], disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = {
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

  def taxYearWithLiabilitiesToSummaryList(yearWithLiabilites: OnshoreTaxYearWithLiabilities, lettingDeduction: Option[BigInt])(implicit messages: Messages): SummaryList = {

    val liabilities = yearWithLiabilites.taxYearLiabilities

    val lettingDeductionRow = lettingDeduction match {
      case Some(value) => Seq(poundRow("disclosure.onshore.deductions", s"${value}"))
      case _ => Nil
    }

    val unpaidAmount = liabilities.niContributions + liabilities.unpaidTax
    val penaltyAmount = getPenaltyAmount(liabilities.penaltyRate, unpaidAmount)
    val yearTotal = getPeriodTotal(liabilities.penaltyRate, unpaidAmount, liabilities.interest)

    val rows = Seq(
      liabilities.businessIncome.map(income => poundRow("disclosure.onshore.businessIncome", s"${income}")),
      liabilities.gains.map(gains => poundRow("disclosure.onshore.gains", s"${gains}")),
      liabilities.lettingIncome.map(income => poundRow("disclosure.onshore.lettingIncome", s"${income}")),
      liabilities.nonBusinessIncome.map(income => poundRow("disclosure.onshore.nonBusinessIncome", s"${income}")),
      Some(poundRow("disclosure.onshore.tax", s"${liabilities.unpaidTax}")),
      Some(poundRow("disclosure.onshore.ni", s"${liabilities.niContributions}")),
      Some(poundRow("disclosure.onshore.interest", s"${liabilities.interest}")),
      Some(row("disclosure.onshore.penaltyRate", s"${liabilities.penaltyRate}%")),
      Some(poundRow("disclosure.onshore.penalty", f"${penaltyAmount}%1.2f")),
      Some(row("disclosure.onshore.penaltyReason", liabilities.penaltyRateReason))
    ).flatten ++ lettingDeductionRow ++ Seq(poundRow("disclosure.onshore.total", f"${yearTotal}%1.2f"))

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

  def corporationTaxSummaryList(liability: CorporationTaxLiability, caseflowDateFormat: Boolean)(implicit messages: Messages): SummaryList = {

    val penaltyAmount = (liability.penaltyRate * BigDecimal(liability.howMuchUnpaid)) /100
    val totalAmount = BigDecimal(liability.howMuchUnpaid) + penaltyAmount + BigDecimal(liability.howMuchInterest)

    val rows = Seq(
      row("disclosure.onshore.accountingPeriod", liability.periodEnd.format(if(caseflowDateFormat) dmsDateFormatter else downloadDateFormatter)),
      poundRow("disclosure.onshore.ct.income", s"${liability.howMuchIncome}"),
      poundRow("disclosure.onshore.corporationTax", s"${liability.howMuchUnpaid}"),
      poundRow("disclosure.onshore.interest", s"${liability.howMuchInterest}"),
      row("disclosure.onshore.penaltyRate", s"${liability.penaltyRate}%"),
      poundRow("disclosure.onshore.penalty", s"$penaltyAmount"),
      row("disclosure.onshore.penaltyReason", liability.penaltyRateReason),
      poundRow("disclosure.onshore.ct.total", s"$totalAmount")
    )

    SummaryListViewModel(rows)
  }

  def directorLoanSummaryList(liability: DirectorLoanAccountLiabilities, caseflowDateFormat: Boolean)(implicit messages: Messages): SummaryList = {
    val penaltyAmount = (liability.penaltyRate * BigDecimal(liability.unpaidTax)) /100
    val totalAmount = BigDecimal(liability.unpaidTax) + penaltyAmount + BigDecimal(liability.interest)

    val rows = Seq(
      row("disclosure.onshore.director.name", s"${liability.name}"),
      row("disclosure.onshore.accountingPeriod", liability.periodEnd.format(if(caseflowDateFormat) dmsDateFormatter else downloadDateFormatter)),
      poundRow("disclosure.onshore.director.overdrawn", s"${liability.overdrawn}"),
      poundRow("disclosure.onshore.tax", s"${liability.unpaidTax}"),
      poundRow("disclosure.onshore.interest", s"${liability.interest}"),
      row("disclosure.onshore.penaltyRate", s"${liability.penaltyRate}%"),
      poundRow("disclosure.onshore.penalty", s"$penaltyAmount"),
      row("disclosure.onshore.penaltyReason", liability.penaltyRateReason),
      poundRow("disclosure.onshore.director.total", s"$totalAmount")
    )

    SummaryListViewModel(rows)
  }

  def lettingPropertySummaryList(property: LettingProperty, caseflowDateFormat: Boolean)(implicit messages: Messages): SummaryList = {

    val typeOfMortgage: Option[String] = property.typeOfMortgage match {
      case Some(TypeOfMortgageDidYouHave.CapitalRepayment) => Some(messages("disclosure.property.mortgageType.capitalRepayment"))
      case Some(TypeOfMortgageDidYouHave.InterestOnly) => Some(messages("disclosure.property.mortgageType.interestOnly"))
      case Some(TypeOfMortgageDidYouHave.Other) => property.otherTypeOfMortgage
      case _ => None
    }

    val rows = Seq(
      property.address.map(address => row("disclosure.property.address", address.toSeparatedString)),
      property.dateFirstLetOut.map(value => row("disclosure.property.firstLet", value.format(if(caseflowDateFormat) dmsDateFormatter else downloadDateFormatter))),
      property.stoppedBeingLetOut.map(bool => row("disclosure.property.stoppedLet", booleanText(bool))),
      property.noLongerBeingLetOut.map(value => row("disclosure.property.stoppedLetDate", value.stopDate.format(if(caseflowDateFormat) dmsDateFormatter else downloadDateFormatter))),
      property.noLongerBeingLetOut.map(value => row("disclosure.property.happenedTo", value.whatHasHappenedToProperty)),
      property.wasFurnished.map(bool => row("disclosure.property.furnished", booleanText(bool))),
      property.fhl.map(bool => row("disclosure.property.fhl", booleanText(bool))),
      property.isJointOwnership.map(bool => row("disclosure.property.joint", booleanText(bool))),
      property.percentageIncomeOnProperty.map(value => row("disclosure.property.share", s"$value%")),
      property.isMortgageOnProperty.map(bool => row("disclosure.property.mortgage", booleanText(bool))),
      typeOfMortgage.map(value => row("disclosure.property.mortgageType", value)),
      property.wasPropertyManagerByAgent.map(bool => row("disclosure.property.lettingAgent", booleanText(bool))),
      property.didTheLettingAgentCollectRentOnYourBehalf.map(bool => row("disclosure.property.collectRent", booleanText(bool)))
    ).flatten

    SummaryListViewModel(rows)

  }

  def booleanText(bool: Boolean)(implicit messages: Messages): String = 
    if (bool) messages("service.yes") else messages("service.no")

  private def getPenaltyAmount(penaltyRate: BigDecimal, unpaidAmount: BigInt): BigDecimal = {
    (penaltyRate * BigDecimal(unpaidAmount)) /100
  }
  
  private def getPeriodTotal(penaltyRate: BigDecimal, unpaidAmount: BigInt, interest: BigInt): BigDecimal = {
    BigDecimal(unpaidAmount) + getPenaltyAmount(penaltyRate, unpaidAmount) + BigDecimal(interest)
  }

}

