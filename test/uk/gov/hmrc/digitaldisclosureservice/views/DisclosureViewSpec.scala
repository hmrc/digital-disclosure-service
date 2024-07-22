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
import models.address.{Address, Country}
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

  val pd = PersonalDetails(
    Background(),
    AboutYou(
      fullName = Some("Jessica Carter"),
      address = Some(Address("39 Decant Avenue", Some("Rochdale"), None, None, Some("DE1 5XD"), Country("GB")))
    )
  )

  val fullDisclosure = FullDisclosure(
    "userId",
    "id",
    Instant.now(),
    Metadata(reference = Some("ref")),
    CaseReference(),
    pd,
    None,
    OffshoreLiabilities(),
    OtherLiabilities(),
    ReasonForDisclosingNow(),
    None,
    Some(BigInt.apply(230000))
  )

  val viewModel = DisclosureViewModel(
    fullDisclosure,
    false
  )

  val estate = AboutTheEstate(
    fullName = Some("Spacious Lettings Ltd"),
    address =
      Some(Address("24 Barnes Road", Some("Skegness"), Some("line 3"), Some("line 4"), Some("PE25 2PR"), Country("GB")))
  )

  val trust = AboutTheTrust(
    name = Some("Fidelity European Trust"),
    address =
      Some(Address("30 Gordon Road", Some("London"), Some("line 3"), Some("line 4"), Some("W5 2AH"), Country("GB")))
  )

  val company = AboutTheCompany(
    name = Some("The Chocolate Museum"),
    address = Some(
      Address(
        "Meadowcroft",
        Some("Saves Lane"),
        Some("Askam-In-Furness"),
        Some("line 4"),
        Some("LA16 7DZ"),
        Country("GB")
      )
    )
  )

  val partnership = AboutTheLLP(
    name = Some("Crazy Debt Ltd"),
    address = Some(
      Address(
        "Parklands Barn",
        Some("Godminster Lane"),
        Some("Bruton"),
        Some("line 4"),
        Some("BA10 0ND"),
        Country("GB")
      )
    )
  )

  val individual = AboutTheIndividual(
    fullName = Some("Dan Johns"),
    address = Some(
      Address(
        "Whitcliffe Lodge Cottage",
        Some("Whitcliffe Cottages"),
        Some("Ludlow"),
        Some("line 4"),
        Some("SY8 1PN"),
        Country("GB")
      )
    )
  )

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
        totalAmountsList = SummaryList(rows = Nil),
        fullDisclosure = None
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

    "display the address" when {

      "an estate" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheEstate = Some(estate))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(7)").text()  shouldEqual "24 Barnes Road"
        view.select("main > p:nth-child(8)").text()  shouldEqual "Skegness"
        view.select("main > p:nth-child(9)").text()  shouldEqual "line 3"
        view.select("main > p:nth-child(10)").text() shouldEqual "line 4"
        view.select("main > p:nth-child(11)").text() shouldEqual "PE25 2PR"
      }

      "a trust" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheTrust = Some(trust))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(7)").text()  shouldEqual "30 Gordon Road"
        view.select("main > p:nth-child(8)").text()  shouldEqual "London"
        view.select("main > p:nth-child(9)").text()  shouldEqual "line 3"
        view.select("main > p:nth-child(10)").text() shouldEqual "line 4"
        view.select("main > p:nth-child(11)").text() shouldEqual "W5 2AH"
      }

      "a company" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheCompany = Some(company))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(7)").text()  shouldEqual "Meadowcroft"
        view.select("main > p:nth-child(8)").text()  shouldEqual "Saves Lane"
        view.select("main > p:nth-child(9)").text()  shouldEqual "Askam-In-Furness"
        view.select("main > p:nth-child(10)").text() shouldEqual "line 4"
        view.select("main > p:nth-child(11)").text() shouldEqual "LA16 7DZ"
      }

      "a partnership" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheLLP = Some(partnership))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(7)").text()  shouldEqual "Parklands Barn"
        view.select("main > p:nth-child(8)").text()  shouldEqual "Godminster Lane"
        view.select("main > p:nth-child(9)").text()  shouldEqual "Bruton"
        view.select("main > p:nth-child(10)").text() shouldEqual "line 4"
        view.select("main > p:nth-child(11)").text() shouldEqual "BA10 0ND"
      }

      "an individual" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheIndividual = Some(individual))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(7)").text()  shouldEqual "Whitcliffe Lodge Cottage"
        view.select("main > p:nth-child(8)").text()  shouldEqual "Whitcliffe Cottages"
        view.select("main > p:nth-child(9)").text()  shouldEqual "Ludlow"
        view.select("main > p:nth-child(10)").text() shouldEqual "line 4"
        view.select("main > p:nth-child(11)").text() shouldEqual "SY8 1PN"
      }
    }

    "display the right offer heading" when {

      "an estate" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheEstate = Some(estate))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > h1:nth-child(1)").text() shouldEqual "Your estate's offer"
      }

      "a trust" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheTrust = Some(trust))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > h1:nth-child(1)").text() shouldEqual "Your trust's offer"
      }

      "a company" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheCompany = Some(company))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > h1:nth-child(1)").text() shouldEqual "Your company's offer"
      }

      "a partnership" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheLLP = Some(partnership))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > h1:nth-child(1)").text() shouldEqual "Your partnership's offer"
      }

      "an individual" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheIndividual = Some(individual))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > h1:nth-child(1)").text() shouldEqual "Your offer"
      }
    }

    "display the offered amount" when {
      "an estate" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheEstate = Some(estate))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(13)").text() shouldEqual "Amount: £230000"
      }

      "a trust" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheTrust = Some(trust))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(13)").text() shouldEqual "Amount: £230000"
      }

      "a company" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheCompany = Some(company))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(13)").text() shouldEqual "Amount: £230000"
      }

      "a partnership" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheLLP = Some(partnership))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(13)").text() shouldEqual "Amount: £230000"
      }

      "an individual" in {
        // Given
        val updatedViewModel = viewModel.copy(fullDisclosure =
          Some(fullDisclosure.copy(personalDetails = pd.copy(aboutTheIndividual = Some(individual))))
        )

        // When
        val view = createView(updatedViewModel)

        // Then
        view.select("main > p:nth-child(13)").text() shouldEqual "Amount: £230000"
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
