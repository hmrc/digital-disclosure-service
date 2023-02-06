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

package services

import play.api.i18n.Messages
import javax.inject.{Singleton, Inject}

import models.{Submission, Notification, FullDisclosure}
import uk.gov.hmrc.digitaldisclosureservice.views.html.{NotificationView, DisclosureView}
import viewmodels.govuk.SummaryListFluency
import viewmodels.{DisclosureViewModel, NotificationViewModel}
import models.PDF

@Singleton
class SubmissionPdfService @Inject()(notificationView: NotificationView, diclosureView: DisclosureView) extends PdfGenerationService with SummaryListFluency {

  def createPdf(submission: Submission)(implicit messages: Messages): PDF = {

    val html = submission match {
      case notification: Notification => 
        val viewModel = NotificationViewModel(notification)
        notificationView(viewModel).toString
      case disclosure: FullDisclosure =>
        val viewModel = DisclosureViewModel(disclosure)
        diclosureView(viewModel).toString
    }
    
    buildPdf(html)
  }

}