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

import java.time.LocalDate
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, Json}

class NoLongerBeingLetOutSpec extends AnyWordSpec with Matchers {

  "NoLongerBeingLetOut" should {
    "convert to XML correctly" in {
      val date                = LocalDate.of(2023, 4, 15)
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = date,
        whatHasHappenedToProperty = "abc"
      )

      val xml = noLongerBeingLetOut.toXml

      xml.headOption.map(_.label)              shouldBe Some("noLongerBeingLetOut")
      (xml \ "stopDate").text                  shouldBe "2023-04-15"
      (xml \ "whatHasHappenedToProperty").text shouldBe "abc"
    }

    "serialize and deserialize correctly" in {
      val date                = LocalDate.of(2023, 4, 15)
      val noLongerBeingLetOut = NoLongerBeingLetOut(
        stopDate = date,
        whatHasHappenedToProperty = "abc"
      )

      val json   = Json.toJson(noLongerBeingLetOut)
      val parsed = json.validate[NoLongerBeingLetOut]

      parsed                                          shouldBe JsSuccess(noLongerBeingLetOut)
      (json \ "stopDate").as[String]                  shouldBe "2023-04-15"
      (json \ "whatHasHappenedToProperty").as[String] shouldBe "abc"
    }
  }
}
