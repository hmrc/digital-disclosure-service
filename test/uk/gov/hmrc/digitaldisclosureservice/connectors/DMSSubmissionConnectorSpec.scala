/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.digitaldisclosureservice.connectors

import config.{AppConfig, Service}
import connectors.DMSSubmissionConnectorImpl
import models.submission.{SubmissionMetadata, SubmissionRequest}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Keep, Sink, Source}
import org.apache.pekko.testkit.TestKit
import org.apache.pekko.util.ByteString
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, Part}
import uk.PdfTestUtils

import java.time.LocalDateTime
import scala.concurrent.Future

class DMSSubmissionConnectorSpec
    extends TestKit(ActorSystem("DMSSubmissionConnectorSpec"))
    with AsyncWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with PdfTestUtils {

  override def afterAll(): Unit =
    TestKit.shutdownActorSystem(system)

  val mockAppConfig     = mock[AppConfig]
  val mockConfiguration = mock[Configuration]
  when(mockConfiguration.get[Service]("microservice.services.dms-submission"))
    .thenReturn(Service("gladius", "1237", "http"))
  when(mockConfiguration.get[String]("internal-auth.token")).thenReturn("auth-token")
  when(mockConfiguration.get[String]("appName")).thenReturn("digital-disclosure-service")
  when(mockConfiguration.get[Service]("microservice.services.self")).thenReturn(Service("", "", ""))

  val mockClient = mock[WSClient]

  "constructMultipartFormData" must {
    "have all dataparts" in {
      // Given
      when(mockAppConfig.retryIntervals).thenReturn(Nil)
      val submissionRequest = givenSubmissionRequest()

      val dmsSubmissionConnector =
        new DMSSubmissionConnectorImpl(system, mockClient, mockConfiguration)(system.dispatcher, mockAppConfig)

      // When
      val source: Source[Part[Source[ByteString, Any]], Any] =
        dmsSubmissionConnector.constructMultipartFormData(submissionRequest, givenPdf().byteArray)

      // Then
      val eventuallyMaterialisedSource: Future[Seq[Part[Source[ByteString, Any]]]] =
        source.toMat(Sink.fold(Seq.empty[Part[Source[ByteString, Any]]])(_ :+ _))(Keep.right).run()

      eventuallyMaterialisedSource.map { dataParts =>
        dataParts should contain(DataPart("submissionReference", "sub-ref-123-456-789"))
        dataParts should contain(DataPart("metadata.store", "true"))
        dataParts should contain(DataPart("metadata.source", "DO4SUB"))
        dataParts should contain(DataPart("metadata.timeOfReceipt", "2024-01-01T01:01:20.0000002"))
        dataParts should contain(DataPart("metadata.formId", "DO4SUB"))
        dataParts should contain(DataPart("metadata.customerId", "customer-id-123"))
        dataParts should contain(DataPart("metadata.submissionMark", "submission-mark"))
        dataParts should contain(DataPart("metadata.casKey", ""))
        dataParts should contain(DataPart("metadata.classificationType", "EC-CCO-Digital Disclosure Serv"))
        dataParts should contain(DataPart("metadata.businessArea", "EC"))
      }
    }
  }

  def givenSubmissionRequest(): SubmissionRequest =
    SubmissionRequest(
      Some("sub-ref-123-456-789"),
      SubmissionMetadata(LocalDateTime.of(2024, 1, 1, 1, 1, 20, 200), "customer-id-123", "submission-mark")
    )

  def givenMultiPartFormData(): Source[Part[Source[ByteString, Any]], Any] = {
    val dmsSubmissionConnector =
      new DMSSubmissionConnectorImpl(system, mockClient, mockConfiguration)(system.dispatcher, mockAppConfig)

    dmsSubmissionConnector.constructMultipartFormData(givenSubmissionRequest(), givenPdf().byteArray)
  }
}
