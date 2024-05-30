# PII App

This application is a bare-bones functional app designed to display the various layers involved in creating a simple API. It uses Spring Boot 3.3.0 with JWT tokens for API access. Spring Boot was chosen because of my familiarity with Spring and because it is a well-tested and well-supported framework for building APIs. JWT was chosen for its simplicity to implement compared to other more robust solutions, such as OAuth.

The design of the API model was challenging as I tried to balance having an understandable surface API with minimal cruft in the API payloads. I separated the submission POST APIs into three distinct APIs identified by different path names [~/pii/text, ~/pii/image, ~/pii/temporal]. The reason for the separation was to increase the clarity of what fields are required for each payload. For example, the 'text' path only requires parameters pertaining to textual violations (character positions), whereas the 'temporal' path captures items that require timestamps to identify the violation.

The application aims to implement best practice development methods, such as unit tests, code formatters, and CheckStyle. However, it should be noted that some shortcuts have been taken to reduce the effort required for this sample project.

Some other things I had originally planned to develop but didn't get to were:

- Metrics and Tracing
- Docker file
- AWS CDK Build script

## Building assumptions

- Java 17 or higher

## Building

- Build war

```
 ~/humanai-pii/> ./mvnw install
```

- Running war

```
 ~/humanai-pii/> java -jar ./target/api-0.0.1-SNAPSHOT.jar
```

## Running the App

### Swagger docs are accessible from

http://localhost:8080/swagger-ui/index.html#/

[Postman V2 collection of the following API's ](./docs/pii-api.postman_collection.json)

### Using the API

- create an apiUser or adminUser token

```
API_TOKEN=$(curl -v -X 'POST' 'http://localhost:8080/public/login?username=apiUser&password=adsfasdf' -H 'accept: */*' -d '' | jq -r '.token')
ADMIN_TOKEN=$(curl -v -X 'POST' 'http://localhost:8080/public/login?username=adminUser&password=adsfasdf' -H 'accept: */*' -d '' | jq -r '.token')
```

- Create a submission

```
curl -v -H "Authorization: Bearer $API_TOKEN" -H "Content-Type: application/json" -d @./text.json  http://localhost:8080/pii/text

curl -v -H "Authorization: Bearer $API_TOKEN" -H "Content-Type: application/json" -d @./image.json  http://localhost:8080/pii/image

curl -v -H "Authorization: Bearer $API_TOKEN" -H "Content-Type: application/json" -d @./temporal.json  http://localhost:8080/pii/temporal
```

- Delete a submission

```
curl -v -X DELETE -H "Authorization: Bearer $API_TOKEN" -H "Content-Type: application/json"  "http://localhost:8080/pii?submissionId=3"

```

- Admin update the state of a submission

```
curl -v --request PATCH  -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json"  "http://localhost:8080/pii/updateState?submissionId=3&submissionState=PROCESSING"
```

- Admin list all submissions by type

```
curl -v -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json"  http://localhost:8080/pii/getSubmissions?CorpusType=IMAGE
```
