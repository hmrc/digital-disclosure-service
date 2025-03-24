/*
 * Copyright 2025 HM Revenue & Customs
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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class MonthYearSpec extends AnyWordSpec with Matchers {

  "MonthYear" should {
    "convert to XML correctly" in {
      val monthYear = MonthYear(month = 3, year = 2023)
      val xml       = monthYear.toXml

      xml.headOption.map(_.label) shouldBe Some("monthYear")
      (xml \ "month").text        shouldBe "3"
      (xml \ "year").text         shouldBe "2023"
    }

    "serialize and deserialize correctly" in {
      val monthYear = MonthYear(month = 3, year = 2023)
      val json      = Json.toJson(monthYear)
      val parsed    = json.validate[MonthYear]

      parsed                   shouldBe JsSuccess(monthYear)
      (json \ "month").as[Int] shouldBe 3
      (json \ "year").as[Int]  shouldBe 2023
    }
  }
}
