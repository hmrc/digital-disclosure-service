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
import models.Notification

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

    NotificationViewModel(
      metadataList(notification.personalDetails.background, notification.metadata),
      backgroundList(notification.personalDetails.background),
      notification.personalDetails.aboutTheIndividual.map(aboutTheIndividualList),
      notification.personalDetails.aboutTheCompany.map(aboutTheCompanyList),
      notification.personalDetails.aboutTheTrust.map(aboutTheTrustList),
      notification.personalDetails.aboutTheLLP.map(aboutTheLLPList),
      notification.personalDetails.aboutTheEstate.map(aboutTheEstateList),
      aboutYouList(notification.personalDetails.aboutYou, notification.disclosingAboutThemselves),
      aboutYouHeading(notification.personalDetails, false)
    )

  }

}