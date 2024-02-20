Arifutera
=====================
This Java Spring Boot application provides a REST API to list all GitHub repositories that are not forks for a given user

---
## Features

- Retrieve all non-fork GitHub repositories for a user
- Exception handling for non-existing users and blank usernames
- Testable behaviour with service and controller tests
---

## Requirements
- Java 21 or newer
- Maven for building and running application
---

## Configuration

No additional configuration is required.

To run the application, execute the following commands in the terminal:

---

## Running the Application

```bash
mvn clean install
mvn spring-boot:run
```
The API will be accessible at http://localhost:8080

---

## Usage
The primary endpoint of the API is:
- GET /user/{username}

### Use example:
```
curl -X GET "http://localhost:8080/user/JaktoDziala" -H "accept: application/json"
```
### Successful response example:
```json
[
  {
    "repositoryName": "Arifutera",
    "loginName": "JaktoDziala",
    "branches": [
      {
        "name": "master",
        "commit": {
          "sha": "4ff560e37bf61e9ec27fc9b2560f93bb64a0b932"
        }
      }
    ]
  }
]
```

### Bad request example (username not found):
```json
{
  "message": "Username JaktoDziala could not be found!",
  "status": "NOT_FOUND"
}
```
---

## Application Configuration Properties

This section describes the key configuration properties used by the application. These properties should be set in your `application.properties` file located in the `src/main/resources` directory.

### Cache Configuration

- **`spring.cache.type`**
    - **Default Value**: `caffeine`
    - **Description**: Specifies the cache provider to use. This application uses Caffeine for caching.
    - **Required**: Yes (Search for `@Cacheable` annotation inside project)
    - **Example Value**: `caffeine`

- **`spring.cache.caffeine.spec`**
    - **Default Value**: `expireAfterWrite=120s, maximumSize=50`
    - **Description**: Defines the Caffeine cache specifications. `expireAfterWrite` is the duration after which an entry will be automatically removed from the cache after its last write operation. `maximumSize` is the maximum number of entries the cache can contain.
    - **Required**: Yes
    - **Example Value**: `expireAfterWrite=120s, maximumSize=50`

### GitHub API Configuration

- **`github.api.base-url`**
    - **Default Value**: `https://api.github.com`
    - **Description**: The base URL for the GitHub API. This is used for all requests to GitHub's REST API.
    - **Required**: Yes
    - **Example Value**: `https://api.github.com`

---
# Contact details
name: Jakub Banach

email: jakub.banach72@gmail.com
