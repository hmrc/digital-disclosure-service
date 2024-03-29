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

package views

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.i18n.{Messages, MessagesApi}
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
  implicit val sut                = app.injector.instanceOf[DisclosureView]

  private val lang                                              = "en"
  private def createView(disclosure: DisclosureViewModel): Html = sut.render(disclosure, lang, messages)

  "DisclosureView" when {

    "neither onshore or offshore are populated" should {

      val viewModel = DisclosureViewModel(
        FullDisclosure(
          "userId",
          "id",
          Instant.now(),
          Metadata(reference = Some("ref")),
          CaseReference(),
          PersonalDetails(Background(), AboutYou()),
          None,
          OffshoreLiabilities(),
          OtherLiabilities(),
          ReasonForDisclosingNow()
        ),
        false
      )

      val view = createView(viewModel)

      "display the service name" in {
        view.select("title").text() should include(messages("service.name"))
      }

      "display the heading" in {
        view.select("h1").text() should include(messages("disclosure.h1"))
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
        metadataList = SummaryList(rows = Nil),
        backgroundList = SummaryList(rows = Nil),
        aboutTheIndividualList = Some(SummaryList(rows = Nil)),
        aboutTheCompanyList = None,
        aboutTheTrustList = None,
        aboutTheLLPList = None,
        aboutTheEstateList = None,
        aboutYouList = SummaryList(rows = Nil),
        aboutYouHeading = "disclosure.heading.completing",
        offshoreLiabilities = Some(
          OffshoreLiabilitiesViewModel(
            summaryList = SummaryList(rows = Nil),
            taxYearLists = Seq((2012, SummaryList(rows = Nil)))
          )
        ),
        onshoreLiabilities = Some(
          OnshoreLiabilitiesViewModel(
            summaryList = SummaryList(rows = Nil),
            taxYearLists = Seq((2012, SummaryList(rows = Nil))),
            corporationTaxLists = Seq((1, SummaryList(rows = Nil))),
            directorLoanLists = Seq((1, SummaryList(rows = Nil))),
            lettingPropertyLists = Seq((1, SummaryList(rows = Nil)))
          )
        ),
        otherLiabilitiesList = SummaryList(rows = Nil),
        additionalList = SummaryList(rows = Nil),
        totalAmountsList = SummaryList(rows = Nil)
      )
      val view      = createView(viewModel)

      "display the service name" in {
        view.select("title").text() should include(messages("service.name"))
      }

      "display the heading" in {
        view.select("h1").text() should include(messages("disclosure.h1"))
      }

      "display the section headings" in {
        view.select("h2").text() should include(messages("disclosure.heading.metadata"))
        view.select("h2").text() should include(messages("notification.heading.background"))
        view.select("h2").text() should include(messages("disclosure.heading.aboutTheIndividual"))
        view.select("h2").text() should include(messages("disclosure.heading.completing"))
        view.select("h2").text() should include(messages("disclosure.onshore.heading"))
        view.select("h2").text() should include(messages("disclosure.onshore.year", "2013"))
        view.select("h2").text() should include(
          messages("disclosure.onshore.director.heading", "1").replace("&#39;", "'")
        )
        view.select("h2").text() should include(messages("disclosure.onshore.ct.heading", "1"))
        view.select("h2").text() should include(messages("disclosure.offshore.heading"))
        view.select("h2").text() should include(messages("disclosure.offshore.year", "2013"))
        view.select("h2").text() should include(messages("disclosure.totals.heading"))
        view.select("h2").text() should include(messages("disclosure.onshore.letting.heading", "1"))
        view.select("h2").text() should include(messages("disclosure.otherLiabilities.heading"))
        view.select("h2").text() should include(messages("disclosure.additional.heading"))
      }

    }

  }

  "f" should {
    "render the page" in {
      val viewModel = DisclosureViewModel(
        FullDisclosure(
          "userId",
          "id",
          Instant.now(),
          Metadata(reference = Some("ref")),
          CaseReference(),
          PersonalDetails(Background(), AboutYou()),
          None,
          OffshoreLiabilities(),
          OtherLiabilities(),
          ReasonForDisclosingNow()
        ),
        false
      )
      sut.f(viewModel, lang)(messages) shouldEqual createView(viewModel)
    }
  }

}
