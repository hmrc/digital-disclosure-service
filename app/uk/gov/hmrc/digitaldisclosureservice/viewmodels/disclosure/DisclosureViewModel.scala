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

import viewmodels.govuk.SummaryListFluency
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import play.api.i18n.Messages
import models._
import models.notification._
import models.disclosure._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content._
import viewmodels.implicits._
import play.twirl.api.HtmlFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

final case class DisclosureViewModel(
  metadataList: SummaryList,
  backgroundList: SummaryList,
  aboutTheIndividualList: Option[SummaryList],
  aboutTheCompanyList: Option[SummaryList],
  aboutTheTrustList: Option[SummaryList],
  aboutTheLLPList: Option[SummaryList],
  aboutTheEstateList: Option[SummaryList],
  aboutYouList: SummaryList,
  aboutYouHeading: String = "disclosure.heading.completing",
  offshoreLiabilities: Option[OffshoreLiabilitiesViewModel],
  onshoreLiabilities: Option[OnshoreLiabilitiesViewModel],
  otherLiabilitiesList: SummaryList,
  additionalList: SummaryList,
  totalAmountsList: SummaryList
)

object DisclosureViewModel extends SummaryListFluency with SubmissionViewModel {

  def apply(fullDisclosure: FullDisclosure)(implicit messages: Messages): DisclosureViewModel = {

    val isADisclosure = true

    val entity = fullDisclosure.personalDetails.background.disclosureEntity.map(_.entity.toString).getOrElse("Individual")

    val offshoreLiabilities = {
      val disclosingOffshore = fullDisclosure.personalDetails.background.offshoreLiabilities.getOrElse(false)
      if (disclosingOffshore) Some(OffshoreLiabilitiesViewModel(fullDisclosure.offshoreLiabilities, fullDisclosure.disclosingAboutThemselves, entity, fullDisclosure.offerAmount))
      else None
    }

    val onshoreLiabilities = {
      val disclosingOnshore = fullDisclosure.personalDetails.background.onshoreLiabilities.getOrElse(false)
      if (disclosingOnshore) fullDisclosure.onshoreLiabilities.map(l => OnshoreLiabilitiesViewModel(l, fullDisclosure.disclosingAboutThemselves, entity, fullDisclosure.offerAmount))
      else None
    }

    val totalAmounts = TotalAmounts(fullDisclosure)

    DisclosureViewModel(
      metadataList(fullDisclosure.personalDetails.background, fullDisclosure.metadata, fullDisclosure.caseReference, fullDisclosure.offshoreLiabilities),
      backgroundList(fullDisclosure.personalDetails.background),
      fullDisclosure.personalDetails.aboutTheIndividual.map(aboutTheIndividualList),
      fullDisclosure.personalDetails.aboutTheCompany.map(aboutTheCompanyList),
      fullDisclosure.personalDetails.aboutTheTrust.map(aboutTheTrustList),
      fullDisclosure.personalDetails.aboutTheLLP.map(aboutTheLLPList),
      fullDisclosure.personalDetails.aboutTheEstate.map(aboutTheEstateList),
      aboutYouList(fullDisclosure.personalDetails.aboutYou, fullDisclosure.personalDetails.background, fullDisclosure.disclosingAboutThemselves),
      aboutYouHeading(fullDisclosure.personalDetails, isADisclosure),
      offshoreLiabilities,
      onshoreLiabilities,
      otherLiabilitiesList(fullDisclosure.otherLiabilities, fullDisclosure.disclosingAboutThemselves, entity),
      additionalInformationList(fullDisclosure.reasonForDisclosingNow, fullDisclosure.disclosingAboutThemselves, entity),
      totalAmountsSummaryList(totalAmounts, fullDisclosure.offerAmount)
    )

  }

  def countriesRow(countries: Map[String, CountryOfYourOffshoreLiability])(implicit messages: Messages) = {
    val value = countries.values.map(_.name).mkString(", ")
    row("disclosure.offshore.country", value)
  }

  def metadataList(background: Background, metadata: Metadata, caseReference: CaseReference, offshore: OffshoreLiabilities)(implicit messages: Messages): SummaryList = {
    val rows = Seq(
      metadata.reference.map(ref => row(s"disclosure.metadata.reference", ref)),
      metadata.submissionTime.map(time => row(s"disclosure.metadata.submissionTime", toPrettyDate(time))),
      caseReference.whatIsTheCaseReference.map(caseRef => row("notification.metadata.caseRef", caseRef)),
      background.disclosureEntity.map(de => SummaryListRowViewModel(s"notification.background.areYouThe${de.entity.toString}", ValueViewModel(de.areYouTheEntity))),
      offshore.countryOfYourOffshoreLiability.map(countriesRow)
    ).flatten
    SummaryListViewModel(rows = rows)
  }

  def backgroundList(background: Background)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      background.disclosureEntity.map(answer => row("disclosure.background.disclosureEntity", messages(s"notification.background.${answer.entity.toString}"))),
      Some(liabilitiesRow(background, "disclosure"))
    ).flatten
  )

  def otherLiabilitiesList(otherLiabilities: OtherLiabilities, disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = SummaryListViewModel(
    rows = Seq(
      otherLiabilities.issues.map(otherLiabilitiesRow),
      otherLiabilities.other.map(answer => row("disclosure.otherLiabilities.description", answer)),
      otherLiabilities.inheritanceGift.map(answer => row("disclosure.otherLiabilities.transfer", answer)),
      otherLiabilities.taxCreditsReceived.map(answer => taxCreditsReceivedRow(answer, disclosingAboutThemselves, entity))
    ).flatten
  )

  def taxCreditsReceivedRow(received: Boolean, disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = {
    if (disclosingAboutThemselves) SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.you", ValueViewModel(received))
    else if (entity == "Individual") SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.individual", ValueViewModel(received))
    else SummaryListRowViewModel("disclosure.otherLiabilities.taxCredits.person", ValueViewModel(received))
  } 

  def otherLiabilitiesRow(answers: Set[OtherLiabilityIssues])(implicit messages: Messages) = {
    val value = ValueViewModel(
      HtmlContent(
        answers.map {
          answer => HtmlFormat.escape(messages(s"otherLiabilityIssues.$answer")).toString
        }
        .mkString(",<br/><br/>")
      )
    )

    SummaryListRowViewModel(
      key     = "disclosure.otherLiabilities.issues",
      value   = value
    )
  }

  def additionalInformationList(reason: ReasonForDisclosingNow, disclosingAboutThemselves: Boolean, entity: String)(implicit messages: Messages) = SummaryListViewModel(
    rows = Seq(
      reason.reason.map(reasonRow),
      reason.otherReason.map(answer => row("disclosure.additional.otherReason", answer)),
      reason.whyNotBeforeNow.map(answer => row("disclosure.additional.beforeNow", answer)),
      reason.receivedAdvice.map(answer => SummaryListRowViewModel(if (disclosingAboutThemselves) "disclosure.additional.adviceGiven.you" else s"disclosure.additional.adviceGiven.$entity", ValueViewModel(answer))),
      reason.personWhoGaveAdvice.map(answer => row("disclosure.additional.advice.name", answer)),
      reason.adviceOnBehalfOfBusiness.map(answer => SummaryListRowViewModel("disclosure.additional.advice.behalfOf", ValueViewModel(answer))),
      reason.adviceBusinessName.map(answer => row("disclosure.additional.advice.business", answer)),
      reason.personProfession.map(answer => row("disclosure.additional.advice.profession", answer)),
      reason.adviceGiven.map(answer => row("disclosure.additional.advice.given", answer.adviceGiven)),
      reason.adviceGiven.map(answer => dateRow(answer.monthYear)),
      reason.adviceGiven.map(answer => SummaryListRowViewModel("disclosure.additional.advice.discuss",  ValueViewModel(answer.contactPreference != AdviceContactPreference.No))),
      reason.email.map(answer => row("disclosure.additional.advice.email", answer)),
      reason.telephone.map(answer => row("disclosure.additional.advice.telephone", answer))
    ).flatten
  )

  def reasonRow(answers: Set[WhyAreYouMakingADisclosure])(implicit messages: Messages) = {
    val value = ValueViewModel(
      HtmlContent(
        answers.map {
          answer => HtmlFormat.escape(messages(s"whyAreYouMakingADisclosure.$answer")).toString
        }
        .mkString(",<br/><br/>")
      )
    )

    SummaryListRowViewModel(
      key     = "disclosure.additional.reason",
      value   = value
    )
  }

  def dateRow(monthYear: MonthYear)(implicit messages: Messages): SummaryListRow = {

    val FIRST_DAY = 1
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val dateAtStartOfMonth = LocalDate.of(monthYear.year, monthYear.month, FIRST_DAY)

    SummaryListRowViewModel(
      key     = "disclosure.additional.advice.date",
      value   = ValueViewModel(dateAtStartOfMonth.format(dateFormatter))
    )
  }

  def totalAmountsSummaryList(totalAmounts: TotalAmounts, offerAmount: Option[BigInt])(implicit messages: Messages): SummaryList = {

    SummaryListViewModel(
      rows = Seq(
        Some(poundRow("disclosure.totals.tax", s"${totalAmounts.unpaidTaxTotal}")),
        Some(poundRow("disclosure.totals.niContributions", s"${totalAmounts.niContributionsTotal}")),
        Some(poundRow("disclosure.totals.interest", s"${totalAmounts.interestTotal}")),
        Some(poundRow("disclosure.totals.penalty", f"${totalAmounts.penaltyAmountTotal%1.2f}")),
        Some(poundRow("disclosure.totals.amount", f"${totalAmounts.amountDueTotal%1.2f}")),
        offerAmount.map(amount => poundRow("disclosure.totals.offer", s"$amount"))
      ).flatten
    )
  }

  def poundRow(label: String, value: String)(implicit messages: Messages) = {
    SummaryListRowViewModel(
      key     = label,
      value   = ValueViewModel(HtmlContent("£" + HtmlFormat.escape(value).toString))
    )
  }

}