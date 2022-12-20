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

package connectors

import akka.actor.ActorSystem
import config.Service
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.libs.ws._
import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace
import models.submission.{SubmissionRequest, SubmissionResponse}
import play.api.mvc.MultipartFormData.{DataPart, FilePart, Part}
import akka.stream.scaladsl.Source

import java.time.format.DateTimeFormatter
import akka.util.ByteString
import play.mvc.Http.HeaderNames.{AUTHORIZATION, USER_AGENT}
import controllers.routes
import config.AppConfig
import uk.gov.hmrc.digitaldisclosureservice.connectors.ConnectorErrorHandler
import utils.Retries

@Singleton
class DMSSubmissionConnectorImpl @Inject() (
  val actorSystem: ActorSystem,
  val wsClient: WSClient,
  configuration: Configuration
)(implicit val ec: ExecutionContext, appConfig: AppConfig)
  extends DMSSubmissionConnector
    with Retries
    with ConnectorErrorHandler {

  private val service: Service = configuration.get[Service]("microservice.services.dms-submission")
  private val clientAuthToken = configuration.get[String]("internal-auth.token")
  private val appName: String = configuration.get[String]("appName")

  private val selfUrl = configuration.get[Service]("microservice.services.self").baseUrl
  private val callbackControllerUrl = routes.SubmissionCallbackController.callback.url
  private val callbackUrl = s"${selfUrl}/digital-disclosure-service${callbackControllerUrl}"

  def submit(submissionRequest: SubmissionRequest, pdf: Array[Byte]): Future[SubmissionResponse] = {

    val multipartFormData = constructMultipartFormData(submissionRequest, pdf)

    retry {
      wsClient.url(s"${service.baseUrl}/dms-submission/submit")
        .withHttpHeaders(AUTHORIZATION -> clientAuthToken, USER_AGENT -> appName)
        .post(multipartFormData)
        .flatMap { response =>
          response.status match {
            case ACCEPTED => handleResponse[SubmissionResponse.Success](response)
            case BAD_REQUEST => handleResponse[SubmissionResponse.Failure](response)
            case _ => Future.failed(DMSSubmissionConnector.UnexpectedResponseException(response.status, response.body))
          }
        }
    }
  }
  
  def handleResponse[A](response: WSResponse)(implicit reads: Reads[A]): Future[A] = {
    response.json.validate[A] match {
      case JsSuccess(a, _) => Future.successful(a)
      case JsError(_) => Future.failed(DMSSubmissionConnector.UnexpectedResponseException(response.status, response.body))
    }
  }

  private def constructMultipartFormData(submissionRequest: SubmissionRequest, pdf: Array[Byte]): Source[Part[Source[ByteString, Any]], Any] =
    Source(Seq(
      DataPart("callbackUrl", callbackUrl),
      DataPart("metadata.store", submissionRequest.metadata.store.toString),
      DataPart("metadata.source", submissionRequest.metadata.formId),
      DataPart("metadata.timeOfReceipt", DateTimeFormatter.ISO_DATE_TIME.format(submissionRequest.metadata.timeOfReceipt)),
      DataPart("metadata.formId", submissionRequest.metadata.formId),
      DataPart("metadata.customerId", submissionRequest.metadata.customerId),
      DataPart("metadata.submissionMark", submissionRequest.metadata.submissionMark),
      DataPart("metadata.casKey", submissionRequest.metadata.casKey),
      DataPart("metadata.classificationType", submissionRequest.metadata.classificationType),
      DataPart("metadata.businessArea", submissionRequest.metadata.businessArea),
      FilePart(
        key = "form",
        filename = "form.pdf",
        contentType = Some("application/pdf"),
        ref = Source.single(ByteString(pdf)),
        fileSize = pdf.size
      )
    ))

}

object DMSSubmissionConnector {

  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from DMS Submission service, status: $status, body: $body"
  }
  
}

@ImplementedBy(classOf[DMSSubmissionConnectorImpl])
trait DMSSubmissionConnector {
  def submit(submissionRequest: SubmissionRequest, pdf: Array[Byte]): Future[SubmissionResponse] 
}