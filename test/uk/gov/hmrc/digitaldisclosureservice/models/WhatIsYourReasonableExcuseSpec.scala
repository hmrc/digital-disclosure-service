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

class WhatIsYourReasonableExcuseSpec extends AnyWordSpec with Matchers {

  "WhatIsYourReasonableExcuse" should {
    "convert to XML correctly" in {
      val excuse = WhatIsYourReasonableExcuse(
        excuse = "Bereavement",
        years = "2021-2022"
      )

      val xml = excuse.toXml

      xml.headOption.map(_.label) shouldBe Some("whatIsYourReasonableExcuse")
      (xml \ "excuse").text       shouldBe "Bereavement"
      (xml \ "years").text        shouldBe "2021-2022"
    }

    "serialize and deserialize correctly" in {
      val excuse = WhatIsYourReasonableExcuse(
        excuse = "Bereavement",
        years = "2021-2022"
      )

      val json   = Json.toJson(excuse)
      val parsed = json.validate[WhatIsYourReasonableExcuse]

      parsed                       shouldBe JsSuccess(excuse)
      (json \ "excuse").as[String] shouldBe "Bereavement"
      (json \ "years").as[String]  shouldBe "2021-2022"
    }
  }
}
