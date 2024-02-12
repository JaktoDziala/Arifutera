Atipera interview task
=====================
This Java Spring Boot application provides a REST API to list all GitHub repositories for a given user that are not forks. It uses GitHub with anonymous connection for API methods usage.

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

The application connects to the GitHub API anonymously. No additional configuration is required for this.

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

Use example:
```
curl -X GET "http://localhost:8080/user/JaktoDziala" -H "accept: application/json"
```
**Successful result example:**
```json
[
  {
    "repositoryName": "Atipera",
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

**Bad result example (username not found):**
```json
{
  "message": "Username JaktoDziala could not be found!",
  "status": "NOT_FOUND"
}
```
---

# Contact details
name: Jakub Banach

email: jakub.banach72@gmail.com
