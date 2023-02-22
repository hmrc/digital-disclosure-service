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
import com.google.inject.{Inject, Singleton, ImplementedBy}
import models.Submission
import connectors.DMSSubmissionConnector
import services.SubmissionPdfService
import models.submission.{SubmissionMetadata, SubmissionRequest, SubmissionResponse}
import java.time.LocalDateTime
import utils.MarkCalculator
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class DMSSubmissionServiceImpl @Inject()(dmsConnector: DMSSubmissionConnector, pdfService: SubmissionPdfService, markCalculator: MarkCalculator) extends DMSSubmissionService {

  def submit(submission: Submission)(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse] = {

    val generatedPdf = pdfService.createPdf(submission)
    val submissionMark = markCalculator.getSfMark(submission.toXml)

    val submissionMetadata = SubmissionMetadata(
      timeOfReceipt = submission.metadata.submissionTime.getOrElse(LocalDateTime.now()),
      customerId = submission.customerId.map(_.id).getOrElse(""),
      submissionMark = submissionMark
    )
    val submissionRequest = SubmissionRequest(
      submissionReference = submission.metadata.reference.map(_.replace("-", "")),
      metadata = submissionMetadata
    )

    dmsConnector.submit(submissionRequest, generatedPdf.byteArray)
  
  }

}

@ImplementedBy(classOf[DMSSubmissionServiceImpl])
trait DMSSubmissionService {
  def submit(submission: Submission)(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse]
}