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

package uk.gov.hmrc.digitaldisclosureservice.models

import models.{CarelessPriorTo, DeliberatePriorTo, OffshoreYears, ReasonableExcusePriorTo, TaxYearStarting}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsString, JsSuccess, Json}

class OffshoreYearsSpec extends AnyWordSpec with Matchers {

  "reads" should {
    "convert reasonableExcusePriorTo" in {
      JsString("reasonableExcusePriorTo").validate[OffshoreYears] shouldEqual JsSuccess(ReasonableExcusePriorTo)
    }

    "convert carelessPriorTo" in {
      JsString("carelessPriorTo").validate[OffshoreYears] shouldEqual JsSuccess(CarelessPriorTo)
    }

    "convert deliberatePriorTo" in {
      JsString("deliberatePriorTo").validate[OffshoreYears] shouldEqual JsSuccess(DeliberatePriorTo)
    }

    "convert unknown" in {
      JsString("random").validate[OffshoreYears] shouldEqual JsError("error.invalid")
    }

    "convert year string to TaxYearStarting" in {
      JsString("2020").validate[OffshoreYears] shouldEqual JsSuccess(TaxYearStarting(2020))
    }

    "fail to convert non-numeric string" in {
      JsString("twenty-twenty").validate[OffshoreYears] shouldEqual JsError("error.invalid")
    }
  }

  "writes" should {
    "convert ReasonableExcusePriorTo" in {
      Json
        .toJson[OffshoreYears](ReasonableExcusePriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual ReasonableExcusePriorTo.toString
    }

    "convert CarelessPriorTo" in {
      Json
        .toJson[OffshoreYears](CarelessPriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual CarelessPriorTo.toString
    }

    "convert DeliberatePriorTo" in {
      Json
        .toJson[OffshoreYears](DeliberatePriorTo)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual DeliberatePriorTo.toString
    }

    "convert TaxYearStarting" in {
      val taxYear = TaxYearStarting(2020)
      Json
        .toJson[OffshoreYears](taxYear)(OffshoreYears.writes)
        .asOpt[String]
        .getOrElse("") shouldEqual taxYear.toString
    }
  }

  "toXml" should {
    "generate correct XML for CarelessPriorTo" in {
      val xml = CarelessPriorTo.toXml
      xml.toString shouldEqual "<offshoreYears>carelessPriorTo</offshoreYears>"
    }

    "generate correct XML for DeliberatePriorTo" in {
      val xml = DeliberatePriorTo.toXml
      xml.toString shouldEqual "<offshoreYears>deliberatePriorTo</offshoreYears>"
    }

    "generate correct XML for ReasonableExcusePriorTo" in {
      val xml = ReasonableExcusePriorTo.toXml
      xml.toString shouldEqual "<offshoreYears>reasonableExcusePriorTo</offshoreYears>"
    }

    "generate correct XML for TaxYearStarting" in {
      val taxYear = TaxYearStarting(2020)
      val xml = taxYear.toXml
      xml.toString shouldEqual "<taxYearStarting>2020</taxYearStarting>"
    }

    "generate correct XML for a list of offshore years" in {
      val years = List(
        TaxYearStarting(2020),
        CarelessPriorTo,
        DeliberatePriorTo,
        ReasonableExcusePriorTo
      )

      val xml = OffshoreYears.toXml(years)

      (xml \ "taxYearStarting").text shouldBe "2020"
      (xml \ "offshoreYears").map(_.text).toSet should contain("carelessPriorTo")
      (xml \ "offshoreYears").map(_.text).toSet should contain("deliberatePriorTo")
      (xml \ "offshoreYears").map(_.text).toSet should contain("reasonableExcusePriorTo")
    }
  }

  "OffshoreYears.fromString" should {
    "convert reasonableExcusePriorTo string" in {
      OffshoreYears.fromString("reasonableExcusePriorTo") shouldEqual Some(ReasonableExcusePriorTo)
    }

    "convert carelessPriorTo string" in {
      OffshoreYears.fromString("carelessPriorTo") shouldEqual Some(CarelessPriorTo)
    }

    "convert deliberatePriorTo string" in {
      OffshoreYears.fromString("deliberatePriorTo") shouldEqual Some(DeliberatePriorTo)
    }

    "convert year string" in {
      OffshoreYears.fromString("2020") shouldEqual Some(TaxYearStarting(2020))
    }

    "return None for invalid string" in {
      OffshoreYears.fromString("invalid") shouldEqual None
    }
  }

  "TaxYearStarting" should {
    "compare correctly" in {
      val year2020 = TaxYearStarting(2020)
      val year2021 = TaxYearStarting(2021)

      (year2020 compare year2021) shouldBe 1
      (year2021 compare year2020) shouldBe -1
      (year2020 compare year2020) shouldBe 0
    }

    "find missing years correctly" in {
      val years = List(
        TaxYearStarting(2018),
        TaxYearStarting(2020),
        TaxYearStarting(2022)
      )

      val missingYears = TaxYearStarting.findMissingYears(years)

      missingYears should contain(TaxYearStarting(2019))
      missingYears should contain(TaxYearStarting(2021))
      missingYears.size shouldBe 2
    }

    "return empty list when no years are missing" in {
      val consecutiveYears = List(
        TaxYearStarting(2018),
        TaxYearStarting(2019),
        TaxYearStarting(2020)
      )

      TaxYearStarting.findMissingYears(consecutiveYears) shouldBe empty
    }

    "return empty list for list with fewer than 2 elements" in {
      TaxYearStarting.findMissingYears(List(TaxYearStarting(2020))) shouldBe empty
      TaxYearStarting.findMissingYears(List()) shouldBe empty
    }
  }
}