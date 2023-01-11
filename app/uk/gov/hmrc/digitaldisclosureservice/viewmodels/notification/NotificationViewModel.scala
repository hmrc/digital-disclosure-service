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

package viewmodels

import viewmodels.govuk.SummaryListFluency
import models.notification._
import viewmodels.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import models.YesNoOrUnsure
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

final case class NotificationViewModel(
  metadataList: Option[SummaryList],
  backgroundList: SummaryList,
  aboutTheIndividualList: Option[SummaryList],
  aboutTheCompanyList: Option[SummaryList],
  aboutTheTrustList: Option[SummaryList],
  aboutTheLLPList: Option[SummaryList],
  aboutTheEstateList: Option[SummaryList],
  aboutYouList: SummaryList,
  aboutYouHeading: String = "notification.heading.completing"
)

object NotificationViewModel extends SummaryListFluency {

  val backgroundKey = "notification.background"
  val individualKey = "notification.aboutTheIndividual"
  val aboutYouKey = "notification.aboutYou"
  val estateKey = "notification.aboutTheEstate"

  def apply(notification: Notification)(implicit messages: Messages): NotificationViewModel = {

    NotificationViewModel(
      metadataList(notification.background, notification.metadata),
      backgroundList(notification.background),
      notification.aboutTheIndividual.map(aboutTheIndividualList),
      notification.aboutTheCompany.map(aboutTheCompanyList),
      notification.aboutTheTrust.map(aboutTheTrustList),
      notification.aboutTheLLP.map(aboutTheLLPList),
      notification.aboutTheEstate.map(aboutTheEstateList),
      aboutYouList(notification.aboutYou, notification.disclosingAboutThemselves),
      aboutYouHeading(notification)
    )

  }

  def aboutYouHeading(notification: Notification): String =
    notification.background.disclosureEntity match {
      case Some(DisclosureEntity(Individual, Some(true))) => "notification.heading.aboutYou"
      case _ => "notification.heading.completing"
    }

  def metadataList(background: Background, metadata: Metadata)(implicit messages: Messages): Option[SummaryList] =
    metadata.reference.map{ ref =>
      val referenceKey = if (background.letterReferenceNumber.isDefined) "notification.metadata.caseRef" else "notification.metadata.reference"
      SummaryListViewModel(rows = Seq(
        SummaryListRowViewModel(referenceKey, ValueViewModel(ref)),
        SummaryListRowViewModel("notification.metadata.submissionTime", ValueViewModel(metadata.submissionTime.map(toPrettyDate)))
      ))
    }

  def toPrettyDate(date: LocalDateTime): String = {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mma")
    val formattedDate = date.format(dateFormatter)
    formattedDate.replace("AM", "am").replace("PM","pm")
  }

  def backgroundList(background: Background)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      displayWhenNo(s"$backgroundKey.haveYouReceivedALetter", background.haveYouReceivedALetter),
      Some(SummaryListRowViewModel("notification.background.disclosureEntity", ValueViewModel(background.disclosureEntity.map(de => messages(s"notification.background.${de.entity.toString}"))))),
      background.disclosureEntity.map(de => SummaryListRowViewModel(s"notification.background.areYouThe${de.entity.toString}", ValueViewModel(de.areYouTheEntity))),
      background.areYouRepresetingAnOrganisation.flatMap(areYou => displayWhenNo(s"$backgroundKey.areYouRepresetingAnOrganisation", areYou)),
      background.organisationName.map(_ => SummaryListRowViewModel(s"$backgroundKey.organisationName", ValueViewModel(background.organisationName))),
      Some(liabilitiesRow(background))
    ).flatten
  )

  def liabilitiesRow(background: Background)(implicit messages: Messages): SummaryListRow = {
    val rowValue = (background.offshoreLiabilities, background.onshoreLiabilities) match{
      case (Some(true), Some(true)) => messages("notification.background.both")
      case (Some(true), _)          => messages("notification.background.offshore")
      case (_,         Some(true))  => messages("notification.background.onshore")
      case (_,         _)           => "-"
    }
    SummaryListRowViewModel(s"$backgroundKey.liabilities", ValueViewModel(rowValue))
  }


  def aboutTheIndividualList(aboutTheIndividual: AboutTheIndividual)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      Some(SummaryListRowViewModel(s"$individualKey.fullName", ValueViewModel(aboutTheIndividual.fullName))),
      Some(SummaryListRowViewModel(s"$individualKey.dateOfBirth", ValueViewModel(aboutTheIndividual.dateOfBirth.map(_.toString)))),
      Some(SummaryListRowViewModel(s"$individualKey.mainOccupation", ValueViewModel(aboutTheIndividual.mainOccupation))),
      displayWhenNotYes(s"$individualKey.doTheyHaveANino", aboutTheIndividual.doTheyHaveANino),
      aboutTheIndividual.nino.map(_ => SummaryListRowViewModel(s"$individualKey.nino", ValueViewModel(aboutTheIndividual.nino))),
      displayWhenNotYes(s"$individualKey.registeredForVAT", aboutTheIndividual.registeredForVAT),
      aboutTheIndividual.vatRegNumber.map(_ => SummaryListRowViewModel(s"$individualKey.vatRegNumber", ValueViewModel(aboutTheIndividual.vatRegNumber))),
      displayWhenNotYes(s"$individualKey.registeredForSA", aboutTheIndividual.registeredForSA),
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

  def aboutTheEstateList(aboutTheEstate: AboutTheEstate)(implicit messages: Messages): SummaryList = SummaryListViewModel(
    rows = Seq(
      Some(SummaryListRowViewModel(s"$estateKey.fullName", ValueViewModel(aboutTheEstate.fullName))),
      Some(SummaryListRowViewModel(s"$estateKey.dateOfBirth", ValueViewModel(aboutTheEstate.dateOfBirth.map(_.toString)))),
      Some(SummaryListRowViewModel(s"$estateKey.mainOccupation", ValueViewModel(aboutTheEstate.mainOccupation))),
      displayWhenNotYes(s"$estateKey.doTheyHaveANino", aboutTheEstate.doTheyHaveANino),
      aboutTheEstate.nino.map(_ => SummaryListRowViewModel(s"$estateKey.nino", ValueViewModel(aboutTheEstate.nino))),
      displayWhenNotYes(s"$estateKey.registeredForVAT", aboutTheEstate.registeredForVAT),
      aboutTheEstate.vatRegNumber.map(_ => SummaryListRowViewModel(s"$estateKey.vatRegNumber", ValueViewModel(aboutTheEstate.vatRegNumber))),
      displayWhenNotYes(s"$estateKey.registeredForSA", aboutTheEstate.registeredForSA),
      aboutTheEstate.sautr.map(_ => SummaryListRowViewModel(s"$estateKey.sautr", ValueViewModel(aboutTheEstate.sautr))),
      Some(SummaryListRowViewModel(s"$estateKey.address", ValueViewModel(Text(aboutTheEstate.address.map(_.toSeparatedString).getOrElse("-")))))
    ).flatten
  )

  def aboutYouList(aboutYou: AboutYou, disclosingAboutThemselves: Boolean)(implicit messages: Messages): SummaryList = {

    val commonRows = Seq(
      Some(SummaryListRowViewModel(s"$aboutYouKey.fullName", ValueViewModel(aboutYou.fullName))),
      aboutYou.telephoneNumber.map(_ => SummaryListRowViewModel(s"$aboutYouKey.telephoneNumber", ValueViewModel(aboutYou.telephoneNumber))),
      aboutYou.emailAddress.map(_ => SummaryListRowViewModel(s"$aboutYouKey.emailAddress", ValueViewModel(aboutYou.emailAddress))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.address", ValueViewModel(Text(aboutYou.address.map(_.toSeparatedString).getOrElse("-")))))
    ).flatten

    lazy val youAreTheIndividualRows = Seq(
      Some(SummaryListRowViewModel(s"$aboutYouKey.dateOfBirth", ValueViewModel(aboutYou.dateOfBirth.map(_.toString)))),
      Some(SummaryListRowViewModel(s"$aboutYouKey.mainOccupation", ValueViewModel(aboutYou.mainOccupation))),
      displayWhenNotYes(s"$aboutYouKey.doYouHaveANino", aboutYou.doYouHaveANino),
      aboutYou.nino.map(_ => SummaryListRowViewModel(s"$aboutYouKey.nino", ValueViewModel(aboutYou.nino))),
      displayWhenNotYes(s"$aboutYouKey.registeredForVAT", aboutYou.registeredForVAT),
      aboutYou.vatRegNumber.map(_ => SummaryListRowViewModel(s"$aboutYouKey.vatRegNumber", ValueViewModel(aboutYou.vatRegNumber))),
      displayWhenNotYes(s"$aboutYouKey.registeredForSA", aboutYou.registeredForSA),
      aboutYou.sautr.map(_ => SummaryListRowViewModel(s"$aboutYouKey.sautr", ValueViewModel(aboutYou.sautr)))
    ).flatten

    if (disclosingAboutThemselves) SummaryListViewModel(rows = commonRows ++ youAreTheIndividualRows)
    else SummaryListViewModel(rows = commonRows)

  }

  def displayWhenNo(key: String, boolean: Option[Boolean])(implicit messages: Messages): Option[SummaryListRow] = 
    boolean match {
      case Some(true) => None
      case _ => Some(SummaryListRowViewModel(key, ValueViewModel(boolean)))
    }

  def displayWhenNo(key: String, boolean: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    if (boolean) None else Some(SummaryListRowViewModel(key, ValueViewModel(boolean)))

  def displayWhenNotYes(key: String, yesNoOrUnsure: Option[YesNoOrUnsure])(implicit messages: Messages): Option[SummaryListRow] = 
    yesNoOrUnsure match {
      case Some(YesNoOrUnsure.Yes) => None
      case _ => Some(SummaryListRowViewModel(key, ValueViewModel(yesNoOrUnsure)))
    }

}