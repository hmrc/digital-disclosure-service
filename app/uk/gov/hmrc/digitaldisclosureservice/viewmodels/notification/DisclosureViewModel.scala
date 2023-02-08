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
import models.FullDisclosure

final case class DisclosureViewModel(
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

object DisclosureViewModel extends SummaryListFluency with SubmissionViewModel {

  def apply(fullDisclosure: FullDisclosure)(implicit messages: Messages): DisclosureViewModel = {

    DisclosureViewModel(
      metadataList(fullDisclosure.personalDetails.background, fullDisclosure.metadata),
      backgroundList(fullDisclosure.personalDetails.background),
      fullDisclosure.personalDetails.aboutTheIndividual.map(aboutTheIndividualList),
      fullDisclosure.personalDetails.aboutTheCompany.map(aboutTheCompanyList),
      fullDisclosure.personalDetails.aboutTheTrust.map(aboutTheTrustList),
      fullDisclosure.personalDetails.aboutTheLLP.map(aboutTheLLPList),
      fullDisclosure.personalDetails.aboutTheEstate.map(aboutTheEstateList),
      aboutYouList(fullDisclosure.personalDetails.aboutYou, fullDisclosure.disclosingAboutThemselves),
      aboutYouHeading(fullDisclosure.personalDetails, true)
    )

  }

}