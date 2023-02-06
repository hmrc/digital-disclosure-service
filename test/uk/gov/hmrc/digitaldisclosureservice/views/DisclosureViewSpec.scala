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

class DisclosureViewSpec extends AnyWordSpec with Matchers with BaseSpec {

  implicit protected def htmlBodyOf(html: Html): Document = Jsoup.parse(html.toString())

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  implicit val sut = app.injector.instanceOf[DisclosureView]

  private def createView(disclosure: DisclosureViewModel): Html = sut.render(disclosure, messages)
  
  val viewModel = DisclosureViewModel(FullDisclosure("userId", "id", Instant.now(), Metadata(reference = Some("ref")), CaseReference(), PersonalDetails(Background(), AboutYou()), OffshoreLiabilities(), OtherLiabilities(), ReasonForDisclosingNow()))
  
  "NotificationView" should {

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
      view.select("h2").text() should include(messages("notification.heading.metadata"))
      view.select("h2").text() should include(messages("notification.heading.background"))
      view.select("h2").text() should include(messages("notification.heading.completing"))
    }

  }

  "f" should {
    "render the page" in {
      sut.f(viewModel)(messages) shouldEqual createView(viewModel)
    }
  }


}