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

package services

import play.api.i18n.Messages
import java.io.{ByteArrayOutputStream}
import javax.inject.Singleton

import models._

@Singleton
class NotificationPdfService(implicit val messages: Messages) extends PdfGenerationService with PdfGenerationHelper {

  def createPdf(notification: Notification)(implicit messages: Messages): ByteArrayOutputStream = {
    val html = Seq(
      Some(startBoilerplate),
      Some(addMetadata),
      Some(addBackground(notification.background)),
      notification.aboutTheIndividual.map(addAboutTheIndividual),
      notification.aboutTheCompany.map(addAboutTheCompany),
      notification.aboutTheTrust.map(addAboutTheTrust),
      notification.aboutTheLLP.map(addAboutTheLLP),
      Some(addAboutYou(notification.aboutYou, notification.disclosingAboutThemselves)),
      Some(endBoilerplate)
    ).flatten.mkString

    buildPdf(html)
  }

  def addMetadata(implicit messages: Messages): String = {
    s"""
      |<h1 style="padding-top: 1em;">Notification of intent</h1>
      |<h2 style="padding-top: 1em;">Your submission details</h2>
      |
      |<div style="display: block;">
      |
      |<h3 style="margin-bottom: 0em;">Your reference number</h3>
      |<p style="margin-top: 0.3em; padding-left: 0.05em">D61-ABCDE-ABC</p>
      |
      |<h3 style="margin-bottom: 0em;">Your submission was sent on</h3>
      |<p style="margin-top: 0.3em; padding-left: 0.05em">Monday 24th October 2022</p>
      |
      |</div>
      |""".stripMargin
  }

  def addBackground(background: Background)(implicit messages: Messages): String = {
    val baseKey = "notification.background"

    Seq(
      Some(sectionHeader("notification.heading.background")),
      Some(questionAndBooleanAnswer(s"$baseKey.haveYouReceivedALetter", background.haveYouReceivedALetter)),
      background.letterReferenceNumber.map(_ => questionAndAnswer(s"$baseKey.letterReferenceNumber", background.letterReferenceNumber)),
      Some(addDisclosureEntity(background.disclosureEntity)),
      Some(questionAndBooleanAnswer(s"$baseKey.offshoreLiabilities", background.offshoreLiabilities)),
      Some(questionAndBooleanAnswer(s"$baseKey.onshoreLiabilities", background.onshoreLiabilities)),
      Some(sectionEnd)
    ).flatten.mkString
  }

  def addDisclosureEntity(disclosureEntity: Option[DisclosureEntity])(implicit messages: Messages): String = {
    val entityQuestion = questionAndAnswer("notification.background.disclosureEntity", disclosureEntity.map(de => messages(s"notification.background.${de.entity.toString}")))

    disclosureEntity match {
      case Some(de) => entityQuestion + questionAndBooleanAnswer(s"notification.background.areYouThe${de.entity.toString}", de.areYouTheEntity)
      case None => entityQuestion
    }
  }

  def addAboutYou(aboutYou: AboutYou, disclosingAboutThemselves: Boolean)(implicit messages: Messages): String = {
    val baseKey = "notification.aboutYou"

    val commonQuestions = Seq(
      Some(questionAndAnswer(s"$baseKey.fullName", aboutYou.fullName)),
      Some(questionAndAnswer(s"$baseKey.telephoneNumber", aboutYou.telephoneNumber)),
      Some(questionAndBooleanAnswer(s"$baseKey.doYouHaveAEmailAddress", aboutYou.doYouHaveAEmailAddress)),
      aboutYou.emailAddress.map(_ => questionAndAnswer(s"$baseKey.emailAddress", aboutYou.emailAddress)),
      Some(questionAndAnswer(s"$baseKey.address", aboutYou.address)),
    ).flatten

    lazy val youAreTheIndiviudalQuestions = Seq(
      Some(questionAndAnswer(s"$baseKey.dateOfBirth", aboutYou.dateOfBirth.map(_.toString))),
      Some(questionAndAnswer(s"$baseKey.mainOccupation", aboutYou.mainOccupation)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.doYouHaveANino", aboutYou.doYouHaveANino)),
      aboutYou.nino.map(_ => questionAndAnswer(s"$baseKey.nino", aboutYou.nino)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.registeredForVAT", aboutYou.registeredForVAT)),
      aboutYou.vatRegNumber.map(_ => questionAndAnswer(s"$baseKey.vatRegNumber", aboutYou.vatRegNumber)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.registeredForSA", aboutYou.registeredForSA)),
      aboutYou.sautr.map(_ => questionAndAnswer(s"$baseKey.sautr", aboutYou.sautr)),
    ).flatten

    val questions = 
      if (disclosingAboutThemselves) commonQuestions ++ youAreTheIndiviudalQuestions
      else commonQuestions

    (sectionHeader("notification.heading.aboutYou") +: questions :+ sectionEnd).mkString

  }

  def addAboutTheIndividual(aboutTheIndividual: AboutTheIndividual)(implicit messages: Messages): String = {
    val baseKey = "notification.aboutTheIndividual"

    Seq(
      Some(sectionHeader("notification.heading.aboutTheIndividual")),
      Some(questionAndAnswer(s"$baseKey.fullName", aboutTheIndividual.fullName)),
      Some(questionAndAnswer(s"$baseKey.dateOfBirth", aboutTheIndividual.dateOfBirth.map(_.toString))),
      Some(questionAndAnswer(s"$baseKey.mainOccupation", aboutTheIndividual.mainOccupation)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.doYouHaveANino", aboutTheIndividual.doYouHaveANino)),
      aboutTheIndividual.nino.map(_ => questionAndAnswer(s"$baseKey.nino", aboutTheIndividual.nino)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.registeredForVAT", aboutTheIndividual.registeredForVAT)),
      aboutTheIndividual.vatRegNumber.map(_ => questionAndAnswer(s"$baseKey.vatRegNumber", aboutTheIndividual.vatRegNumber)),
      Some(questionAndYesNoOrUnsure(s"$baseKey.registeredForSA", aboutTheIndividual.registeredForSA)),
      aboutTheIndividual.sautr.map(_ => questionAndAnswer(s"$baseKey.sautr", aboutTheIndividual.sautr)),
      Some(questionAndAnswer(s"$baseKey.address", aboutTheIndividual.address)),
      Some(sectionEnd)
    ).flatten.mkString
  }

  def addAboutTheCompany(aboutTheCompany: AboutTheCompany)(implicit messages: Messages): String = 
    Seq(
      sectionHeader("notification.heading.aboutTheCompany"),
      questionAndAnswer("notification.aboutTheCompany.name", aboutTheCompany.name),
      questionAndAnswer("notification.aboutTheCompany.registrationNumber", aboutTheCompany.registrationNumber),
      questionAndAnswer("notification.aboutTheCompany.address", aboutTheCompany.address),
      sectionEnd
    ).mkString

  def addAboutTheTrust(aboutTheTrust: AboutTheTrust)(implicit messages: Messages): String = 
    Seq(
      sectionHeader("notification.heading.aboutTheTrust"),
      questionAndAnswer("notification.aboutTheTrust.name", aboutTheTrust.name),
      questionAndAnswer("notification.aboutTheTrust.address", aboutTheTrust.address),
      sectionEnd
    ).mkString

  def addAboutTheLLP(aboutTheLLP: AboutTheLLP)(implicit messages: Messages): String = 
    Seq(
      sectionHeader("notification.heading.aboutTheLLP"),
      questionAndAnswer("notification.aboutTheLLP.name", aboutTheLLP.name),
      questionAndAnswer("notification.aboutTheLLP.address", aboutTheLLP.address),
      sectionEnd
    ).mkString


}
