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

package viewmodels

import viewmodels.govuk.SummaryListFluency
import models.notification._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

final case class NotificationViewModel(
  metadataList: SummaryList,
  backgroundList: SummaryList,
  aboutTheIndividualList: Option[SummaryList],
  aboutTheCompanyList: Option[SummaryList],
  aboutTheTrustList: Option[SummaryList],
  aboutTheLLPList: Option[SummaryList],
  aboutYouList: SummaryList
)

object NotificationViewModel extends SummaryListFluency {

  val backgroundKey = "notification.background"
  val individualKey = "notification.aboutTheIndividual"
  val aboutYouKey = "notification.aboutYou"

  def apply(notification: Notification)(implicit messages: Messages): NotificationViewModel = {

    NotificationViewModel(
      metadataList(notification.metadata),
      backgroundList(notification.background),
      notification.aboutTheIndividual.map(aboutTheIndividualList),
      notification.aboutTheCompany.map(aboutTheCompanyList),
      notification.aboutTheTrust.map(aboutTheTrustList),
      notification.aboutTheLLP.map(aboutTheLLPList),
      aboutYouList(notification.aboutYou, notification.disclosingAboutThemselves)
    )

  }

  def metadataList(metadata: Metadata)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel("notification.metadata.reference", ValueViewModel(metadata.reference)),
      SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel(metadata.submissionTime.map(_.toString)))
    )
  )

  def backgroundList(background: Background)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      Some(SummaryListRowViewModel(s"$backgroundKey.haveYouReceivedALetter", ValueViewModel(background.haveYouReceivedALetter))),
      background.letterReferenceNumber.map(_ => SummaryListRowViewModel(s"$backgroundKey.letterReferenceNumber", ValueViewModel(background.letterReferenceNumber))),
      Some(SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(background.disclosureEntity.map(de => messages(s"notification.background.${de.entity.toString}"))))),
      background.disclosureEntity.map(de => SummaryListRowViewModel(s"notification.background.areYouThe${de.entity.toString}", ValueViewModel(de.areYouTheEntity))),
      background.areYouRepresetingAnOrganisation.map(_ => SummaryListRowViewModel(s"$backgroundKey.areYouRepresetingAnOrganisation", ValueViewModel(background.areYouRepresetingAnOrganisation))),
      background.organisationName.map(_ => SummaryListRowViewModel(s"$backgroundKey.organisationName", ValueViewModel(background.organisationName))),
      Some(SummaryListRowViewModel(s"$backgroundKey.offshoreLiabilities", ValueViewModel(background.offshoreLiabilities))),
      Some(SummaryListRowViewModel(s"$backgroundKey.onshoreLiabilities", ValueViewModel(background.onshoreLiabilities)))
    ).flatten
  )

  def aboutTheIndividualList(aboutTheIndividual: AboutTheIndividual)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      Some(SummaryListRowViewModel(s"$individualKey.fullName", ValueViewModel(aboutTheIndividual.fullName))),
      Some(SummaryListRowViewModel(s"$individualKey.dateOfBirth", ValueViewModel(aboutTheIndividual.dateOfBirth.map(_.toString)))),
      Some(SummaryListRowViewModel(s"$individualKey.mainOccupation", ValueViewModel(aboutTheIndividual.mainOccupation))),
      Some(SummaryListRowViewModel(s"$individualKey.doTheyHaveANino", ValueViewModel(aboutTheIndividual.doTheyHaveANino))),
      aboutTheIndividual.nino.map(_ => SummaryListRowViewModel(s"$individualKey.nino", ValueViewModel(aboutTheIndividual.nino))),
      Some(SummaryListRowViewModel(s"$individualKey.registeredForVAT", ValueViewModel(aboutTheIndividual.registeredForVAT))),
      aboutTheIndividual.vatRegNumber.map(_ => SummaryListRowViewModel(s"$individualKey.vatRegNumber", ValueViewModel(aboutTheIndividual.vatRegNumber))),
      Some(SummaryListRowViewModel(s"$individualKey.registeredForSA", ValueViewModel(aboutTheIndividual.registeredForSA))),
      aboutTheIndividual.sautr.map(_ => SummaryListRowViewModel(s"$individualKey.sautr", ValueViewModel(aboutTheIndividual.sautr))),
      Some(SummaryListRowViewModel(s"$individualKey.address", ValueViewModel(Text(aboutTheIndividual.address.map(_.toSeparatedString).getOrElse("-")))))
    ).flatten
  )

  def aboutTheCompanyList(aboutTheCompany: AboutTheCompany)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel("notification.aboutTheCompany.name", ValueViewModel(aboutTheCompany.name)),
      SummaryListRowViewModel("notification.aboutTheCompany.registrationNumber", ValueViewModel(aboutTheCompany.registrationNumber)),
      SummaryListRowViewModel("notification.aboutTheCompany.address", ValueViewModel(Text(aboutTheCompany.address.map(_.toSeparatedString).getOrElse("-"))))
    )
  )

  def aboutTheTrustList(aboutTheTrust: AboutTheTrust)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel("notification.aboutTheTrust.name", ValueViewModel(aboutTheTrust.name)),
      SummaryListRowViewModel("notification.aboutTheTrust.address", ValueViewModel(Text(aboutTheTrust.address.map(_.toSeparatedString).getOrElse("-"))))
    )
  )
  
  def aboutTheLLPList(aboutTheLLP: AboutTheLLP)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      SummaryListRowViewModel("notification.aboutTheLLP.name", ValueViewModel(aboutTheLLP.name)),
      SummaryListRowViewModel("notification.aboutTheLLP.address", ValueViewModel(Text(aboutTheLLP.address.map(_.toSeparatedString).getOrElse("-"))))
    )
  )

  def aboutYouList(aboutYou: AboutYou, disclosingAboutThemselves: Boolean)(implicit messages: Messages): SummaryList = {

    val commonRows = Seq(
      Some(SummaryListRowViewModel(s"$aboutYouKey.fullName", ValueViewModel(aboutYou.fullName))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.telephoneNumber", ValueViewModel(aboutYou.telephoneNumber))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.doYouHaveAEmailAddress", ValueViewModel(aboutYou.doYouHaveAEmailAddress))),
      aboutYou.emailAddress.map(_ => SummaryListRowViewModel(s"$aboutYouKey.emailAddress", ValueViewModel(aboutYou.emailAddress))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.address", ValueViewModel(Text(aboutYou.address.map(_.toSeparatedString).getOrElse("-")))))
    ).flatten

    lazy val youAreTheIndiviudalRows = Seq(
      Some(SummaryListRowViewModel(s"$aboutYouKey.dateOfBirth", ValueViewModel(aboutYou.dateOfBirth.map(_.toString)))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.mainOccupation", ValueViewModel(aboutYou.mainOccupation))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.doYouHaveANino", ValueViewModel(aboutYou.doYouHaveANino))),
      aboutYou.nino.map(_ => SummaryListRowViewModel(s"$aboutYouKey.nino", ValueViewModel(aboutYou.nino))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.registeredForVAT", ValueViewModel(aboutYou.registeredForVAT))),
      aboutYou.vatRegNumber.map(_ => SummaryListRowViewModel(s"$aboutYouKey.vatRegNumber", ValueViewModel(aboutYou.vatRegNumber))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.registeredForSA", ValueViewModel(aboutYou.registeredForSA))),
      aboutYou.sautr.map(_ => SummaryListRowViewModel(s"$aboutYouKey.sautr", ValueViewModel(aboutYou.sautr)))
    ).flatten

    if (disclosingAboutThemselves) SummaryListViewModel(rows = commonRows ++ youAreTheIndiviudalRows)
    else SummaryListViewModel(rows = commonRows)

  }

}