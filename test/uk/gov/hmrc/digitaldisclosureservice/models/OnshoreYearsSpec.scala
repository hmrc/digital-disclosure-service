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

import models.{OnshoreYears, PriorToFiveYears, PriorToNineteenYears, PriorToThreeYears}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsString, JsSuccess}

class OnshoreYearsSpec extends AnyWordSpec with Matchers {

  "reads" should {
    "convert priorToThreeYears" in {
      JsString("priorToThreeYears").validate[OnshoreYears] shouldEqual JsSuccess(PriorToThreeYears)
    }

    "convert priorToFiveYears" in {
      JsString("priorToFiveYears").validate[OnshoreYears] shouldEqual JsSuccess(PriorToFiveYears)
    }

    "convert priorToNineteenYears" in {
      JsString("priorToNineteenYears").validate[OnshoreYears] shouldEqual JsSuccess(PriorToNineteenYears)
    }

    "convert unknown" in {
      JsString("someRandomValue").validate[OnshoreYears] shouldEqual JsError("error.invalid")
    }
  }
}
