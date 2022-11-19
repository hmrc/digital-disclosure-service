/*
 * Copyright 2022 HM Revenue & Customs
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

import models.notification.Notification
import uk.gov.hmrc.digitaldisclosureservice.views.html.NotificationView
import viewmodels.govuk.SummaryListFluency
import viewmodels.NotificationViewModel
import models.PDF
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class NotificationPdfService @Inject()(view: NotificationView) extends PdfGenerationService with SummaryListFluency {

  def createPdf(notification: Notification)(implicit messages: Messages, ec: ExecutionContext): Future[PDF] = {

    val viewModel = NotificationViewModel(notification)

    buildPdf(view(viewModel).toString)
  }

}
