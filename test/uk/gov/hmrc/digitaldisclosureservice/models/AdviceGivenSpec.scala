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

class AdviceGivenSpec extends AnyWordSpec with Matchers {

  "AdviceGiven" should {
    "convert to XML correctly" in {
      val adviceGiven = AdviceGiven(
        adviceGiven = "Test advice",
        monthYear = MonthYear(1, 2022),
        contactPreference = AdviceContactPreference.Email
      )

      val xml = adviceGiven.toXml

      xml.headOption.map(_.label)      shouldBe Some("adviceGiven")
      (xml \ "advice").text            shouldBe "Test advice"
      (xml \ "monthYear").nonEmpty     shouldBe true
      (xml \ "contactPreference").text shouldBe "email"
    }

    "serialize and deserialize correctly" in {
      val adviceGiven = AdviceGiven(
        adviceGiven = "Test advice",
        monthYear = MonthYear(1, 2022),
        contactPreference = AdviceContactPreference.Email
      )

      val json   = Json.toJson(adviceGiven)
      val parsed = json.validate[AdviceGiven]

      parsed shouldBe JsSuccess(adviceGiven)
    }
  }
}
