# Currency Converter

## How to run
Being a `Spring Boot`-`gradle` project, simply execute `./gradlew clean build bootRun` to download dependencies, build and execute tests and spin up a tomcat server on port `8080`.
Then, open `http://localhost:8080/rate?from=XXX&to=XXX&amount=XXX`.

### Sample response
```
{
	"from": "XXX",
	"to": "XXX",
	"amount": "XXX", //2 digits precision, no matter the request
	"rate": "XXX", //current rate between 'from' and 'to' as per http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml. 4 digits precision
	"result": "XXX" //amount*rate. 2 digit precisions
}
```

## [Lombok](https://projectlombok.org/)
The project makes "heavy" use of `Lombok` to reduce boilerplate code. To open the project in any IDE, consult the instructions [here](https://projectlombok.org/download.html).
