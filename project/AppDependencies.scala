import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.12.0"
  
  private val pdfboxVersion = "2.0.26"
  private val openHtmlVersion = "1.0.10"
  private val silencerVersion = "1.7.12"

  val compile = Seq(
    "uk.gov.hmrc"              %%  "bootstrap-backend-play-28"     % bootstrapVersion,
    "org.apache.pdfbox"        %   "pdfbox"                        % pdfboxVersion,
    "org.apache.pdfbox"        %   "xmpbox"                        % pdfboxVersion,
    "org.apache.xmlgraphics"   %   "batik-transcoder"              % "1.14",
    "org.apache.xmlgraphics"   %   "batik-codec"                   % "1.14",
    "com.openhtmltopdf"        %   "openhtmltopdf-core"            % openHtmlVersion,
    "com.openhtmltopdf"        %   "openhtmltopdf-pdfbox"          % openHtmlVersion,
    "com.openhtmltopdf"        %   "openhtmltopdf-svg-support"     % openHtmlVersion,
    "uk.gov.hmrc"              %%  "play-frontend-hmrc"            % "6.2.0-play-28",
    "org.typelevel"            %%  "cats-core"                     % "2.8.0",
    "com.github.pathikrit"     %%  "better-files"                  % "3.9.1",
    "commons-codec"            %   "commons-codec"                 % "1.14",
    "com.thoughtworks.xstream" %   "xstream"                       % "1.4.16",
    "uk.gov.hmrc.mongo"        %% "hmrc-mongo-play-28"             % "0.74.0",
    "uk.gov.hmrc"              %% "internal-auth-client-play-28"   % "1.3.0",
    "uk.gov.hmrc"              %% "tax-year"                       % "3.0.0",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
  )

  val test = Seq(
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.62.2"                    % "test, it",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion            % "test, it",
    "org.scalatestplus"       %% "mockito-4-6"                % "3.2.14.0"                  % "test, it",
    "com.github.tomakehurst"  %  "wiremock-standalone"        % "2.27.2"                    % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"                     % "test, it",
    "org.scalamock"           %% "scalamock"                  % "5.1.0"                     % "test, it",
    "org.scalatestplus"       %% "scalacheck-1-15"            % "3.2.10.0"                  % "test, it",
    "org.scalacheck"          %% "scalacheck"                 % "1.15.4"                    % "test, it"
  )
}
