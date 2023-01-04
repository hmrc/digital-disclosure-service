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
import play.twirl.api.Html
import play.api.test.FakeRequest
import uk.gov.hmrc.digitaldisclosureservice.views.html.styles
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import viewmodels.govuk.SummaryListFluency
import org.scalatest.OptionValues
import utils.BaseSpec

class StylesSpec extends AnyWordSpec with Matchers with BaseSpec with SummaryListFluency with OptionValues {

  implicit protected def htmlBodyOf(html: Html): Document = Jsoup.parse(html.toString())

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  implicit val sut = app.injector.instanceOf[styles]

  private def createView(): Html = sut.render()
  
  "styles" should {

    val view = createView()

    "contain styling" in {
      Option(view.select("style")) shouldBe (defined)
    }

  }

  "f" should {
    "render the page" in {
      sut.f() shouldEqual createView()
    }
  }


}
