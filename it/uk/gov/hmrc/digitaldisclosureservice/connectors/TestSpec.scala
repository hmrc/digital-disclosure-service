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

// /*
//  * Copyright 2022 HM Revenue & Customs
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *     http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */

// package connectors

// import org.scalatest.concurrent.ScalaFutures
// import org.scalatest.freespec.AnyFreeSpec
// import org.scalatest.matchers.must.Matchers
// import org.scalatestplus.play.guice.GuiceOneAppPerSuite
// import models.notification._
// import java.time.Instant
// import services.DMSSubmissionService
// import play.api.test.{FakeRequest, Injecting}
// import play.api.i18n.{Messages, MessagesApi}
// import scala.concurrent.ExecutionContext.Implicits.global
// import models.submission.SubmissionResponse

// class TestSpec extends AnyFreeSpec with Matchers with ScalaFutures with GuiceOneAppPerSuite with Injecting {

//   "submit" - {

//     "must return a successful future when the store responds with ACCEPTED and a SubmissionResponse.Success" in {

//       implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())
//       val service: DMSSubmissionService = inject[DMSSubmissionService]
//       val notification = Notification("userId", "id", Instant.now(), Metadata(), Background(), AboutYou())
//       val result = service.submitNotification(notification)
//       Thread.sleep(20000)
//       result.futureValue mustEqual SubmissionResponse.Success("id")

//     }
    
//   }

// }