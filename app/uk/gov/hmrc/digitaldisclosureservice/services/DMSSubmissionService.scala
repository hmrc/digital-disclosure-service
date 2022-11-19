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
import com.google.inject.{Inject, Singleton, ImplementedBy}

import models.notification.Notification
import connectors.DMSSubmissionConnector
import services.NotificationPdfService
import models.submission.{SubmissionMetadata, SubmissionRequest, SubmissionResponse}
import java.time.LocalDateTime
import utils.MarkCalculator
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class DMSSubmissionServiceImpl @Inject()(dmsConnector: DMSSubmissionConnector, pdfService: NotificationPdfService, markCalculator: MarkCalculator) extends DMSSubmissionService {

  def submitNotification(notification: Notification)(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse] = {

    pdfService.createPdf(notification: Notification).flatMap { generatedPdf =>
      val submissionMark = markCalculator.getSfMark(notification.toXml)

      val submissionMetadata = SubmissionMetadata(
        timeOfReceipt = notification.metadata.submissionTime.getOrElse(LocalDateTime.now()),
        numberOfPages = generatedPdf.pageCount,
        customerId = notification.customerId, // NINO or SAUTR/CTUTR/ARN
        submissionMark = submissionMark
      )
      val submissionRequest = SubmissionRequest(
        id = None,
        metadata = submissionMetadata
      )

      dmsConnector.submit(submissionRequest, generatedPdf.byteArray)
    }
  }

}

@ImplementedBy(classOf[DMSSubmissionServiceImpl])
trait DMSSubmissionService {
  def submitNotification(notification: Notification)(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse]
}