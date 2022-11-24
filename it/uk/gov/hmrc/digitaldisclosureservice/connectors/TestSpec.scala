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

// package connectors

// import org.scalatest.concurrent.ScalaFutures
// import org.scalatest.freespec.AnyFreeSpec
// import org.scalatest.matchers.must.Matchers
// import org.scalatestplus.play.guice.GuiceOneAppPerSuite
// import models.notification._
// import models._
// import models.address._
// import java.time.{LocalDate, Instant}
// import services.DMSSubmissionService
// import play.api.test.{FakeRequest, Injecting}
// import play.api.i18n.{Messages, MessagesApi}
// import scala.concurrent.ExecutionContext.Implicits.global
// import models.submission.SubmissionResponse

// class TestSpec extends AnyFreeSpec with Matchers with ScalaFutures with GuiceOneAppPerSuite with Injecting {

  // "submit" - {

  //   "send a submission" in {

  //     implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
  //     val service: DMSSubmissionService = inject[DMSSubmissionService]
  //           val address = Address("line1", None, None, "line3", "postcode", Country("GBR"))
  //       val background = Background (
  //         haveYouReceivedALetter = Some(false),
  //         letterReferenceNumber = None,
  //         disclosureEntity = Some(DisclosureEntity(Individual, Some(false))),
  //         offshoreLiabilities = Some(false),
  //         onshoreLiabilities = Some(true)
  //       )
  //       val aboutYou = AboutYou(
  //         fullName = Some("Some name"),
  //         telephoneNumber = Some("+44 012345678"),
  //         doYouHaveAEmailAddress = Some(false),
  //         address = Some(address)
  //       )
  //       val aboutTheIndividual = AboutTheIndividual(  
  //         fullName = Some("Some individual's name"),
  //         dateOfBirth = Some(LocalDate.now()),
  //         mainOccupation = Some("Some individual's occupation xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx xxxxxx "),
  //         doTheyHaveANino = Some(YesNoOrUnsure.No),
  //         registeredForVAT = Some(YesNoOrUnsure.Yes),
  //         vatRegNumber = Some("Some individual's VAT number"),
  //         registeredForSA = Some(YesNoOrUnsure.No),
  //         address = Some(address)
  //       )
  //       val notification = Notification("userId", "id", Instant.now(), Metadata(), background, aboutYou, Some(aboutTheIndividual))
  //     val result = service.submitNotification(notification)
  //     Thread.sleep(20000)
  //     result.futureValue mustEqual SubmissionResponse.Success("id")
  //   }
    
  // }

// }