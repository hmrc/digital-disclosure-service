/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.dfs.utils

import org.scalatestplus.play.PlaySpec
import play.api.Logging
import utils.MarkCalculatorImpl

class MarkCalculatorSpec extends PlaySpec with Logging {

  val sut = new MarkCalculatorImpl

  "mark" must {
    "create mhrc mark" in {
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xfa generator=\"XFA2_4\" APIVersion=\"3.6.13063.0\"?><xdp:xdp xmlns:xdp=\"http://ns.adobe.com/xdp/\" timeStamp=\"2014-03-07T17:12:35Z\" uuid=\"3ceb2fb9-565e-47b3-9c92-19500f382e87\"><xfa:datasets xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\"><xfa:data><form1><ErrorArea><fmErrorMessage><ThereAreXErrors/></fmErrorMessage></ErrorArea><fmContent><fmAboutThisForm><fmIntroduction xfa:dataNode=\"dataGroup\"/></fmAboutThisForm><fmClaimant><ErrorTextArea><ErrorMessageText/></ErrorTextArea><HelpButton xfa:dataNode=\"dataGroup\"/><Answer/><Help xfa:dataNode=\"dataGroup\"/><ErrorTextArea><ErrorMessageText/></ErrorTextArea><HelpButton xfa:dataNode=\"dataGroup\"/><Answer/><Help xfa:dataNode=\"dataGroup\"/></fmClaimant><submit xfa:dataNode=\"dataGroup\"/></fmContent><fmFooter><sfPosn xfa:dataNode=\"dataGroup\"/></fmFooter><fmFormNumber xfa:dataNode=\"dataGroup\"/><fmNotes><Table3><Row1 xfa:dataNode=\"dataGroup\"/><Row2 xfa:dataNode=\"dataGroup\"/><Row3 xfa:dataNode=\"dataGroup\"/><Row4 xfa:dataNode=\"dataGroup\"/><Row5 xfa:dataNode=\"dataGroup\"/><Row6 xfa:dataNode=\"dataGroup\"/><Row7 xfa:dataNode=\"dataGroup\"/><Row8 xfa:dataNode=\"dataGroup\"/><Row9 xfa:dataNode=\"dataGroup\"/><Row10 xfa:dataNode=\"dataGroup\"/><Row11 xfa:dataNode=\"dataGroup\"/><Row12 xfa:dataNode=\"dataGroup\"/><Row13 xfa:dataNode=\"dataGroup\"/><Row13 xfa:dataNode=\"dataGroup\"/></Table3></fmNotes></form1></xfa:data></xfa:datasets></xdp:xdp>"
      val submissionMark = sut.getSfMark(xml.getBytes)
      submissionMark mustBe "vQRlt09HvGQOxycnaEb0Q3riq+U="
    }
  }
}