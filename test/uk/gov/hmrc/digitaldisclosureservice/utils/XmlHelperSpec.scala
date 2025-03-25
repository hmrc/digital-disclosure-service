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

package uk.gov.hmrc.digitaldisclosureservice.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.xml.{NodeSeq, Text}

class XmlHelperSpec extends AnyWordSpec with Matchers {

  "XmlHelper.extractChildNodes" should {
    "return empty for empty input" in {
      XmlHelper.extractChildNodes(NodeSeq.Empty) shouldBe NodeSeq.Empty
    }

    "extract child nodes from XML" in {
      val xml = <parent><child>value</child></parent>
      XmlHelper.extractChildNodes(xml).text shouldBe "value"
    }

    "return input when not an Elem" in {
      val text = Text("text")
      XmlHelper.extractChildNodes(NodeSeq.fromSeq(Seq(text))) shouldBe NodeSeq.fromSeq(Seq(text))
    }
  }
}
