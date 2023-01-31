/*
 * Copyright 2023 HM Revenue & Customs
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
import models.notification._
import java.time.{LocalDate, ZoneOffset}
import models._
import models.address._

class MarkCalculatorSpec extends PlaySpec with Logging {

  val sut = new MarkCalculatorImpl

  "mark" must {
    "create mhrc mark" in {
      val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xfa generator=\"XFA2_4\" APIVersion=\"3.6.13063.0\"?><xdp:xdp xmlns:xdp=\"http://ns.adobe.com/xdp/\" timeStamp=\"2014-03-07T17:12:35Z\" uuid=\"3ceb2fb9-565e-47b3-9c92-19500f382e87\"><xfa:datasets xmlns:xfa=\"http://www.xfa.org/schema/xfa-data/1.0/\"><xfa:data><form1><ErrorArea><fmErrorMessage><ThereAreXErrors/></fmErrorMessage></ErrorArea><fmContent><fmAboutThisForm><fmIntroduction xfa:dataNode=\"dataGroup\"/></fmAboutThisForm><fmClaimant><ErrorTextArea><ErrorMessageText/></ErrorTextArea><HelpButton xfa:dataNode=\"dataGroup\"/><Answer/><Help xfa:dataNode=\"dataGroup\"/><ErrorTextArea><ErrorMessageText/></ErrorTextArea><HelpButton xfa:dataNode=\"dataGroup\"/><Answer/><Help xfa:dataNode=\"dataGroup\"/></fmClaimant><submit xfa:dataNode=\"dataGroup\"/></fmContent><fmFooter><sfPosn xfa:dataNode=\"dataGroup\"/></fmFooter><fmFormNumber xfa:dataNode=\"dataGroup\"/><fmNotes><Table3><Row1 xfa:dataNode=\"dataGroup\"/><Row2 xfa:dataNode=\"dataGroup\"/><Row3 xfa:dataNode=\"dataGroup\"/><Row4 xfa:dataNode=\"dataGroup\"/><Row5 xfa:dataNode=\"dataGroup\"/><Row6 xfa:dataNode=\"dataGroup\"/><Row7 xfa:dataNode=\"dataGroup\"/><Row8 xfa:dataNode=\"dataGroup\"/><Row9 xfa:dataNode=\"dataGroup\"/><Row10 xfa:dataNode=\"dataGroup\"/><Row11 xfa:dataNode=\"dataGroup\"/><Row12 xfa:dataNode=\"dataGroup\"/><Row13 xfa:dataNode=\"dataGroup\"/><Row13 xfa:dataNode=\"dataGroup\"/></Table3></fmNotes></form1></xfa:data></xfa:datasets></xdp:xdp>"
      val submissionMark = sut.getSfMark(xml)
      submissionMark mustBe "vQRlt09HvGQOxycnaEb0Q3riq+U="
    }

    "create mhrc mark from Notification" in {
      val date = LocalDate.of(2005,11,12)
      val instant = date.atStartOfDay().toInstant(ZoneOffset.UTC)

      val address = Address("line1", None, None, None, None, Country("GBR"))
      val background = Background (
        haveYouReceivedALetter = Some(false),
        letterReferenceNumber = None,
        disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
        offshoreLiabilities = Some(false),
        onshoreLiabilities = Some(true)
      )
      val aboutYou = AboutYou(
        fullName = Some("Some name"),
        telephoneNumber = Some("+44 012345678"),
        address = Some(address)
      )
      val aboutTheIndividual = AboutTheIndividual(  
        fullName = Some("Some individual's name"),
        dateOfBirth = Some(date),
        mainOccupation = Some("Some individual's occupation"),
        doTheyHaveANino = Some(YesNoOrUnsure.Yes),
        nino = Some("Some individual's nino"),
        registeredForVAT = Some(YesNoOrUnsure.No),
        registeredForSA = Some(YesNoOrUnsure.No),
        address = Some(address)
      )
      val notification = Notification("userId", "id", instant, Metadata(), PersonalDetails(background, aboutYou, Some(aboutTheIndividual)))
      val submissionMark = sut.getSfMark(notification.toXml)
      submissionMark mustBe "6+fMlMBTOZ2MLI8eAw0EefAH+Jw="
    }
  }
}