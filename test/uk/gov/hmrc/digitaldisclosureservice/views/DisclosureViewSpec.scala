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

package views

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.i18n.{MessagesApi, Messages}
import models._
import models.notification._
import models.disclosure._
import viewmodels._
import play.twirl.api.Html
import play.api.test.FakeRequest
import uk.gov.hmrc.digitaldisclosureservice.views.html.DisclosureView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Instant
import utils.BaseSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class DisclosureViewSpec extends AnyWordSpec with Matchers with BaseSpec {

  implicit protected def htmlBodyOf(html: Html): Document = Jsoup.parse(html.toString())

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  implicit val sut = app.injector.instanceOf[DisclosureView]

  private def createView(disclosure: DisclosureViewModel): Html = sut.render(disclosure, messages)
  
  "DisclosureView" when {

    "neither onshore or offshore are populated" should {

      val viewModel = DisclosureViewModel(FullDisclosure("userId", "id", Instant.now(), Metadata(reference = Some("ref")), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow()))
  
      val view = createView(viewModel)

      "display the service name" in {
        view.select("title").text() should include(messages("service.name"))
      }

      "display the heading" in {
        view.select("h1").text() should include(messages("disclosure.h1"))
      }

      "display the beta banner" in {
        view.select("strong").text() should include("beta")
      }

      "display the section headings" in {
        view.select("h2").text() should include(messages("disclosure.heading.metadata"))
        view.select("h2").text() should include(messages("notification.heading.background"))
        view.select("h2").text() should include(messages("disclosure.heading.completing"))
        view.select("h2").text() should include(messages("disclosure.otherLiabilities.heading"))
        view.select("h2").text() should include(messages("disclosure.additional.heading"))
      }

    }

    "offshore is populated" should {
    
      val viewModel = DisclosureViewModel(
        metadataList = SummaryList(rows=Nil),
        backgroundList = SummaryList(rows=Nil),
        aboutTheIndividualList = Some(SummaryList(rows=Nil)),
        aboutTheCompanyList = None,
        aboutTheTrustList = None,
        aboutTheLLPList = None,
        aboutTheEstateList = None,
        aboutYouList = SummaryList(rows=Nil),
        aboutYouHeading = "disclosure.heading.completing",
        offshoreLiabilities = Some(OffshoreLiabilitiesViewModel(
          summaryList = SummaryList(rows=Nil),
          legalInterpretationlist = SummaryList(rows=Nil),
          taxYearLists = Seq((2012, SummaryList(rows=Nil))),
          totalAmountsList = SummaryList(rows=Nil),
          liabilitiesTotal = 0
        )),
        otherLiabilitiesList = SummaryList(rows=Nil),
        additionalList = SummaryList(rows=Nil)
      )
      val view = createView(viewModel)

      "display the service name" in {
        view.select("title").text() should include(messages("service.name"))
      }

      "display the heading" in {
        view.select("h1").text() should include(messages("disclosure.h1"))
      }

      "display the beta banner" in {
        view.select("strong").text() should include("beta")
      }

      "display the section headings" in {
        view.select("h2").text() should include(messages("disclosure.heading.metadata"))
        view.select("h2").text() should include(messages("notification.heading.background"))
        view.select("h2").text() should include(messages("disclosure.heading.aboutTheIndividual"))
        view.select("h2").text() should include(messages("disclosure.heading.completing"))
        view.select("h2").text() should include(messages("disclosure.offshore.heading"))
        view.select("h2").text() should include(messages("disclosure.offshore.year", "2013"))
        view.select("h2").text() should include(messages("disclosure.otherLiabilities.heading"))
        view.select("h2").text() should include(messages("disclosure.additional.heading"))
      }

    }
    
  }

  "f" should {
    "render the page" in {      
      val viewModel = DisclosureViewModel(FullDisclosure("userId", "id", Instant.now(), Metadata(reference = Some("ref")), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow()))
      sut.f(viewModel)(messages) shouldEqual createView(viewModel)
    }
  }

}
