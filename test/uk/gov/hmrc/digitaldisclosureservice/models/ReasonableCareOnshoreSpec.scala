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

class ReasonableCareOnshoreSpec extends AnyWordSpec with Matchers {

  "ReasonableCareOnshore" should {
    "convert to XML correctly" in {
      val reasonableCare = ReasonableCareOnshore(
        reasonableCare = "abc",
        yearsThisAppliesTo = "123"
      )

      val xml = reasonableCare.toXml

      xml.headOption.map(_.label) shouldBe Some("reasonableCareOnshore")
      (xml \ "reasonableCare").text shouldBe "abc"
      (xml \ "yearsThisAppliesTo").text shouldBe "123"
    }

    "serialize and deserialize correctly" in {
      val reasonableCare = ReasonableCareOnshore(
        reasonableCare = "abc",
        yearsThisAppliesTo = "123"
      )

      val json = Json.toJson(reasonableCare)
      val parsed = json.validate[ReasonableCareOnshore]

      parsed shouldBe JsSuccess(reasonableCare)
      (json \ "reasonableCare").as[String] shouldBe "abc"
      (json \ "yearsThisAppliesTo").as[String] shouldBe "123"
    }
  }
}