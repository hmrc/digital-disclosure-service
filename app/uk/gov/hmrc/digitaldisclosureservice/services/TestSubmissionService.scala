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

import play.api.i18n.{Lang, LangImplicits, MessagesApi, Messages}
import play.api.Logging
import com.google.inject.{Inject, Singleton, ImplementedBy}
import config.AppConfig
import models.{Metadata, Notification}
import models.notification._
import models.submission.SubmissionResponse
import java.time.{LocalDateTime, Instant}
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class TestSubmissionServiceImpl @Inject()(
  appConfig: AppConfig,
  submissionService: DMSSubmissionService, 
  val messagesApi: MessagesApi
)(implicit ec: ExecutionContext) extends TestSubmissionService with LangImplicits with Logging {

  def submit(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse] = {
    val notification = Notification(
      userId = "TEST",
      submissionId = "TEST",
      lastUpdated = Instant.now(),
      metadata = Metadata(Some("This is a test"), Some(LocalDateTime.now)),
      personalDetails = PersonalDetails(
        background = Background(Some(true), Some("This is a test")),
        aboutYou = AboutYou()
      )
    )

    logger.info("Sending test submission")
    submissionService.submitNotification(notification)
  }

  if (appConfig.sendTestPDFOnStartup) {
    implicit val lang = Lang("en")
    //implicit val messages: Messages = lang2Messages
    submit
  }

}

@ImplementedBy(classOf[TestSubmissionServiceImpl])
trait TestSubmissionService {
  def submit(implicit messages: Messages, ec: ExecutionContext): Future[SubmissionResponse]
}