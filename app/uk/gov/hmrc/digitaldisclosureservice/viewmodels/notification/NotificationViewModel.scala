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
import viewmodels.implicits._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

final case class NotificationViewModel(
  metadataList: Option[SummaryList],
  backgroundList: SummaryList,
  aboutTheIndividualList: Option[SummaryList],
  aboutTheCompanyList: Option[SummaryList],
  aboutTheTrustList: Option[SummaryList],
  aboutTheLLPList: Option[SummaryList],
  aboutTheEstateList: Option[SummaryList],
  aboutYouList: SummaryList,
  aboutYouHeading: String = "notification.heading.completing"
)

object NotificationViewModel extends SummaryListFluency with SubmissionViewModel {

  def apply(notification: Notification)(implicit messages: Messages): NotificationViewModel = {

    val isADisclosure = false

    NotificationViewModel(
      metadataList(notification.metadata),
      backgroundList(notification.personalDetails.background),
      notification.personalDetails.aboutTheIndividual.map(aboutTheIndividualList),
      notification.personalDetails.aboutTheCompany.map(aboutTheCompanyList),
      notification.personalDetails.aboutTheTrust.map(aboutTheTrustList),
      notification.personalDetails.aboutTheLLP.map(aboutTheLLPList),
      notification.personalDetails.aboutTheEstate.map(aboutTheEstateList),
      aboutYouList(notification.personalDetails.aboutYou, notification.personalDetails.background, notification.disclosingAboutThemselves),
      aboutYouHeading(notification.personalDetails, isADisclosure)
    )

  }

  def metadataList(metadata: Metadata)(implicit messages: Messages): Option[SummaryList] = {
    metadata.reference.map{ ref =>
      SummaryListViewModel(rows = Seq(
        SummaryListRowViewModel(s"notification.metadata.reference", ValueViewModel(ref)),
        SummaryListRowViewModel(s"notification.metadata.submissionTime", ValueViewModel(metadata.submissionTime.map(toPrettyDate)))
      ))
    }
  }

  def backgroundList(background: Background)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      displayWhenNo(s"$backgroundKey.haveYouReceivedALetter", background.haveYouReceivedALetter),
      background.letterReferenceNumber.map(_ => SummaryListRowViewModel("notification.metadata.caseRef", ValueViewModel(background.letterReferenceNumber))),
      Some(SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(background.disclosureEntity.map(de => messages(s"notification.background.${de.entity.toString}"))))),
      background.disclosureEntity.map(de => SummaryListRowViewModel(s"notification.background.areYouThe${de.entity.toString}", ValueViewModel(de.areYouTheEntity))),
      background.areYouRepresetingAnOrganisation.flatMap(areYou => displayWhenNo(s"$backgroundKey.areYouRepresetingAnOrganisation", areYou)),
      background.organisationName.map(_ => SummaryListRowViewModel(s"$backgroundKey.organisationName", ValueViewModel(background.organisationName))),
      Some(liabilitiesRow(background)),
      Some(incomeFromRow(background)),
    ).flatten
  )

  def incomeFromRow(background: Background)(implicit messages: Messages) = {

    val sources = background.incomeSource.getOrElse(Nil).map(answer => messages(s"whereDidTheUndeclaredIncomeOrGainIncluded.$answer")).toList
    val otherSource = background.otherIncomeSource.toList

    val answers = sources ::: otherSource

    val value = ValueViewModel(
      HtmlContent(
        answers.map {
          answer => HtmlFormat.escape(answer).toString
        }
        .mkString("<br/><br/>")
      )
    )

    SummaryListRowViewModel("disclosure.offshore.incomeFrom", value)
  }


}