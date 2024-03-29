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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import util.WireMockHelper
import java.time.LocalDateTime
import play.api.http.Status._
import connectors.DMSSubmissionConnector
import play.mvc.Http.HeaderNames.{AUTHORIZATION, USER_AGENT}
import models.submission.{SubmissionMetadata, SubmissionRequest, SubmissionResponse}
import java.time.format.DateTimeFormatter

class DMSSubmissionConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with WireMockHelper {

  private lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.dms-submission.port"     -> server.port(),
        "microservice.services.dms-submission.host"     -> "localhost",
        "microservice.services.dms-submission.protocol" -> "http",
        "internal-auth.token"                           -> "authToken",
        "create-internal-auth-token-on-start"           -> false
      )
      .build()

  val expectedCallbackUrl = "http://localhost:15004/digital-disclosure-service/dms-submission/callback"

  private lazy val connector = app.injector.instanceOf[DMSSubmissionConnector]

  "submit" - {

    val url = "/dms-submission/submit"

    val localDate         = LocalDateTime.now()
    val submissionRequest = SubmissionRequest(
      submissionReference = None,
      SubmissionMetadata(
        timeOfReceipt = localDate,
        customerId = "customer Id",
        submissionMark = "mark"
      )
    )

    val pdf = "Some string of data".getBytes

    "must return a successful future when the store responds with ACCEPTED and a SubmissionResponse.Success" in {

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withBody(Json.toJson(SubmissionResponse.Success("id")).toString)
          )
      )

      connector.submit(submissionRequest, pdf).futureValue mustEqual SubmissionResponse.Success("id")
    }

    "must return a successful future when the store responds with ACCEPTED and a SubmissionResponse.Success with the id we passed in" in {

      val submissionRequestWithID = SubmissionRequest(
        submissionReference = Some("our-id-here"),
        SubmissionMetadata(
          timeOfReceipt = localDate,
          customerId = "customer Id",
          submissionMark = "mark"
        )
      )

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("submissionReference").withBody(containing("our-id-here")))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(
            aResponse()
              .withStatus(ACCEPTED)
              .withBody(Json.toJson(SubmissionResponse.Success("our-id-here")).toString)
          )
      )

      connector.submit(submissionRequestWithID, pdf).futureValue mustEqual SubmissionResponse.Success("our-id-here")
    }

    "must return a failed future when the store responds with ACCEPTED and anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(aResponse().withBody("""{"name": "SomeId"}""").withStatus(ACCEPTED))
      )

      val exception = connector.submit(submissionRequest, pdf).failed.futureValue
      exception mustEqual DMSSubmissionConnector.UnexpectedResponseException(202, """{"name": "SomeId"}""")
    }

    "must return a successful future when the store responds with BAD_REQUEST and a successful SubmissionResponse.Failure" in {

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(
            aResponse()
              .withStatus(BAD_REQUEST)
              .withBody(Json.toJson(SubmissionResponse.Failure(Seq("Error 1", "Error 2"))).toString)
          )
      )

      connector.submit(submissionRequest, pdf).futureValue mustEqual SubmissionResponse.Failure(
        Seq("Error 1", "Error 2")
      )
    }

    "must return a failed future when the store responds with BAD_REQUEST and anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(aResponse().withBody("""{"name": "SomeId"}""").withStatus(BAD_REQUEST))
      )

      val exception = connector.submit(submissionRequest, pdf).failed.futureValue
      exception mustEqual DMSSubmissionConnector.UnexpectedResponseException(400, """{"name": "SomeId"}""")
    }

    "must return a failed future when a different response is returned" in {

      server.stubFor(
        post(urlMatching(url))
          .withMultipartRequestBody(aMultipart().withName("callbackUrl").withBody(containing(expectedCallbackUrl)))
          .withMultipartRequestBody(aMultipart().withName("metadata.store").withBody(containing("true")))
          .withMultipartRequestBody(aMultipart().withName("metadata.source").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(
            aMultipart()
              .withName("metadata.timeOfReceipt")
              .withBody(containing(DateTimeFormatter.ISO_DATE_TIME.format(localDate)))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.formId").withBody(containing("DO4SUB")))
          .withMultipartRequestBody(aMultipart().withName("metadata.customerId").withBody(containing("customer Id")))
          .withMultipartRequestBody(aMultipart().withName("metadata.submissionMark").withBody(containing("mark")))
          .withMultipartRequestBody(aMultipart().withName("metadata.casKey").withBody(containing("")))
          .withMultipartRequestBody(
            aMultipart().withName("metadata.classificationType").withBody(containing("EC-CCO-Digital Disclosure Serv"))
          )
          .withMultipartRequestBody(aMultipart().withName("metadata.businessArea").withBody(containing("EC")))
          .withMultipartRequestBody(aMultipart().withName("form").withBody(binaryEqualTo(pdf)))
          .withHeader(AUTHORIZATION, containing("authToken"))
          .withHeader(USER_AGENT, containing("digital-disclosure-service"))
          .willReturn(aResponse().withBody("""{"name": "SomeId"}""").withStatus(NOT_FOUND))
      )

      val exception = connector.submit(submissionRequest, pdf).failed.futureValue
      exception mustEqual DMSSubmissionConnector.UnexpectedResponseException(404, """{"name": "SomeId"}""")
    }

  }

}
