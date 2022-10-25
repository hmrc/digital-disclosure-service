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
import models._
import scala.io.Source

trait PdfGenerationHelper { 
  
  implicit val messages: Messages

  def startBoilerplate(implicit messages: Messages) =
    s"""
        |<!DOCTYPE html>
        |<html lang="${messages.lang.code}">
        |<head>
        |<title>Digital </title>
        |<meta name="Lang" content="${messages.lang.code}"/>
        |<meta name="subject" content="Test subject"/>
        |<meta name="about" content="HMRC PDFA Document"/>
        |<style>
        |  body {margin: 0; font-family: 'arial'; font-size: 16px;}
        |  header {margin: 0; font-family: 'arial'; font-size: 16px;}
        |  h1 {font-size: 36pt; letter-spacing: 0.04em;}
        |  h2 {font-size: 24pt;}
        |  h3 {font-size: 1em;}
        |</style>
        |</head>
        |<body>
        |${pdfHeader}
        |""".stripMargin

  val endBoilerplate = "</body></html>"

  def sectionHeader(messageKey: String)(implicit messages: Messages): String = {
    s"""
      |<h2 style="padding-top: 1em;">${messages(messageKey)}</h2>
      |
      |<div style="display: block;">
      |""".stripMargin
  }

  def questionAndAnswer(messageKey: String, answer: Option[String])(implicit messages: Messages): String = {
    s"""
      |<h3 style="margin-bottom: 0em;">${messages(messageKey)}</h3>
      |<p style="margin-top: 0.3em; padding-left: 0.05em">${answer.getOrElse("")}</p>
      |""".stripMargin
  }

  def questionAndBooleanAnswer(messageKey: String, answer: Option[Boolean])(implicit messages: Messages): String = {
    s"""
      |<h3 style="margin-bottom: 0em;">${messages(messageKey)}</h3>
      |<p style="margin-top: 0.3em; padding-left: 0.05em">${answer.map(booleanToText).getOrElse("")}</p>
      |""".stripMargin
  }

  def questionAndYesNoOrUnsure(messageKey: String, answer: Option[YesNoOrUnsure])(implicit messages: Messages): String = {
    s"""
      |<h3 style="margin-bottom: 0em;">${messages(messageKey)}</h3>
      |<p style="margin-top: 0.3em; padding-left: 0.05em">${answer.map(YesNoOrUnsureToText).getOrElse("")}</p>
      |""".stripMargin
  }

  def sectionEnd: String = 
    s"""
      |
      |</div>
      |""".stripMargin

  def pdfHeader(implicit messages: Messages): String = {
    val crownIcon = Source.fromFile(getClass.getResource("/resources/crown.svg").toURI)
    val startHtml = s"""<div style="padding-bottom: 0.3em; margin-top: -1em;">"""
    val endHtml =
      s"""<p style="padding-left: 0.5em; display: inline-block; font-size: 16pt; vertical-align: middle;">${messages("service.name")}</p>
            </div><hr/>"""
    val headingHtml = startHtml ++ crownIcon.getLines().mkString ++ endHtml
    crownIcon.close()
    headingHtml
  }

  def booleanToText(boolean: Boolean)(implicit messages: Messages): String = {
    if (boolean) messages("service.yes")
    else messages("service.no")
  }

  def YesNoOrUnsureToText(yesNoOrUnsure: YesNoOrUnsure)(implicit messages: Messages): String = {
    yesNoOrUnsure match {
      case YesNoOrUnsure.Yes => messages("service.yes")
      case YesNoOrUnsure.No => messages("service.no")
      case YesNoOrUnsure.Unsure => messages("service.unsure")
    }
  }


}