# Todo Service

### Service description

This service provides you with the functionality of maintaining your todo items list. Basically it provides you
following functionalities

* Add todo item. As soon as new item is added/created, it's status turns to *Not Done*.
* Change the *description* of an existing todo item.
* Change the status of an existing item.
    * When the item status is marked as *Done*, also make sure that the datetime is also recorded.
    * Automatically change status of items that are past their due date as *Past Due*.
* Get all todo items with an optional filter based on item status.
* Get details of a specific item.

### Assumptions

* User can add multiple todo items with same *item description*
* It's ok not to write too many unit tests when integration tests have good coverage. So omitting writing too many
  controller layer tests and service layer tests completely.
* Used `defer-datasource-initialization: true` configuration to initialize database at the start of an application
  without any records in the table.
* Used surrogate key for identifying the todo item in the database table.

### Tech stack

* Java 17
* Spring Boot
* Junit 5
* Gradle
* Docker
* Project Lombok
* Spring OpenAPI
* Awaitility

### How to

* Project can be built and test can be run with the following command. The command should be run from the root directory
  of the project.
  ```
  ./gradlew build
  ```
* Here's the command to build the project without running tests.
  ```
  ./gradlew build -x test
  ```
* Application contains the main class called `TodoApplication`. Simply run this class to start the application. Once the
  application start. You can see the swagger documentation at this url `http://localhost:8080/swagger-ui/index.html`.
* Application also contains a dockerfile, so it can also be run as a docker container with the following command. The
  command should be run from the root directory of the project. Make sure that the docker engine is up and running prior
  to running any docker command.
  ```
  docker build -t simplesystem/todo .
  ```
* Once the docker image built, it can be run with the following command. The command should be run from the root
  directory of the project.
  ```
  docker run -p 8080:8080 simplesystem/todo
  ```
  Once the application start. You can see the swagger documentation at this
  url `http://localhost:8080/swagger-ui/index.html`.
