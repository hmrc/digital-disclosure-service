
# Digital Disclosure Service

This protected microservice is part of the Digital Disclosure Service. This service is designed to allow customers to tell HMRC about unpaid tax from previous years for both onshore and offshore liabilities. It is the replacement for the DO4SUB iForm which is the original digital incarnation of the Disclosure Service.

This microservice provides two main bits of functionality, the creation of PDFs which can then either be downloaded by the customer or will contain the data required by the HMRC taskworker, and also it sends the PDF down to the DMS Submission service which is then set up to utilise Object Store to provide the PDF to SDES who can then send it downstream to the DMS for a HMRC taskworker to pick up.

## Running the service


### Start all dependent services
This service depends on multiple other services, including:
- Internal Auth
- DMS Submission

The easiest way to set up required microservices is to use Service Manager and the DDS_ALL profile from service-manager-config repository:

```
sm2 --start DDS_ALL
```

To stop the frontend microservice from running on service manager (e.g. to run your own version locally), you can run:

```
sm2 -stop DIGITAL_DISCLOSURE_SERVICE
```

### Using localhost

To run this frontend microservice locally on the configured port **'15004'**, you can run:

```
sbt run 
```

**NOTE:** Ensure that you are not running the microservice via service manager before starting your service locally (vice versa) or the service will fail to start

### Accessing the service
Endpoints will be available from the host `http://localhost:15004/digital-disclosure-service`.
The easiest way to test the functionality of this microservice is to access the `digital-disclosure-service-frontend` 
microservice which will be available on `http://localhost:15003/digital-disclosure` if you started the above Service Manager profile

More details can be found on the
[DDCY Live Services Credentials sheet](https://docs.google.com/spreadsheets/d/1ecLTROmzZtv97jxM-5LgoujinGxmDoAuZauu2tFoAVU/edit?gid=1186990023#gid=1186990023)
for both staging and local url's or check the Tech Overview section in the
[service summary page ](https://confluence.tools.tax.service.gov.uk/display/ELSY/DDS+Service+Summary)


## Testing the application
This repository contains unit tests for the service. In order to run them, simply execute:
`sbt test`
This repository contains integration tests for the service. In order to run them, simply execute:
`sbt it:test`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

## Monitoring

The following grafana and kibana dashboards are available for this service:

* [Grafana](https://grafana.tools.production.tax.service.gov.uk/d/digital-disclosure-service/digital-disclosure-service?orgId=1&from=now-24h&to=now&timezone=browser&var-ecsServiceName=ecs-ECS&var-ecsServicePrefix=ecs-ECS&refresh=15m)
* [Kibana](https://kibana.tools.production.tax.service.gov.uk/app/dashboards#/view/digital-disclosure-service?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-15m,to:now))


## Other helpful documentation

* [Service Runbook](https://confluence.tools.tax.service.gov.uk/display/ELSY/Digital+Disclosure+Service+%28DDS%29+Runbook)

* [Architecture Links](https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?pageId=857113254)