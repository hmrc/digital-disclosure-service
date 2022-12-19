
# Digital Disclosure Service

This protected microservice is part of the Digital Disclosure Service. This service is designed to allow customers to tell HMRC about unpaid tax from previous years for both onshore and offshore liabilities. It is the replacement for the DO4SUB iForm which is the original digital incarnation of the Disclosure Service.

This microservice provides two main bits of functionality, the creation of PDFs which can then either be downloaded by the customer or will contain the data required by the HMRC taskworker, and also it sends the PDF down to the DMS Submission service which is then set up to utilise Object Store to provide the PDF to SDES who can then send it downstream to the DMS for a HMRC taskworker to pick up.

## How to run the service

### Start a MongoDB instance

### Start the microservice
This service is written in Scala and Play, so needs at a JRE to run and a JDK for development. In order to run the application you need to have SBT installed. Then, it is enough to start the service with:
`sbt run`

### Start all dependent services
This service depends on multiple other services, including:
- Internal Auth
- DMS Submission

The easiest way to set up required microservices is to use Service Manager and the DDS_ALL profile from service-manager-config repository:
`sm --start DDS_ALL`

### Accessing the service
Endpoints will be available from the host `http://localhost:15004/digital-disclosure-service`. The easiest way to test the functionality of this microservice is to access the `digital-disclosure-service-frontend` microservice which will be available on `http://localhost:15003/digital-disclosure` if you started the above Service Manager profile

## Testing the application
This repository contains unit tests for the service. In order to run them, simply execute:
`sbt test`
This repository contains integration tests for the service. In order to run them, simply execute:
`sbt it:test`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
