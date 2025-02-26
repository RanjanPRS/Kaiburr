# JAVA BACKEND APP

~ PRIYARANJAN S

This Java backend app was created as a part of the Kaiburr Internship Selection Process and is a task manager API that lets users create, update, delete, and execute shell commands. Each task has a id, name, owner, command, and execution history, stored in MongoDB.

When a task is posted or put, the app runs the command, captures its output, and stores the execution details (start time, end time, and result).

You can interact with it via REST APIs using CURL, Postman, or a frontend.

It also validates commands to block dangerous ones (eg: rm -rf).

### Project Setup

```bash
mvn clean package
mvn spring-boot:run
```
![Spring initialized](SCREENSHOTS/spring-backend.png)
This screenshot shows the Spring Boot application being initialized.

### API Endpoints

Viewing All Tasks on Web

![tasks endpoint](SCREENSHOTS/tasks-endpoint.png)

![search endpoint](SCREENSHOTS/search-endpoint.png)

![tasks endpoint](SCREENSHOTS/specific-task.png)


### API Testing using cURL

POST (Create & Execute a Task)
```bash
curl -X POST http://localhost:8080/tasks \
     -H "Content-Type: application/json" \
     -d '{
           "id": "123",
           "name": "Print Hello",
           "owner": "John Smith",
           "command": "echo Hello World!"
         }'
```
![post](SCREENSHOTS/curl-post.png)

GET All Tasks
```bash
curl -X GET http://localhost:8080/tasks
```
![all tasks](SCREENSHOTS/curl-getTasks.png)

Search Tasks by Name
```bash
curl -X GET "http://localhost:8080/tasks/search?name=Print"
```
![search](SCREENSHOTS/curl-search.png)

Update a Task (PUT)
```bash
curl -X PUT http://localhost:8080/tasks/123 \
     -H "Content-Type: application/json" \
     -d '{
           "id": "123",
           "name": "Updated Task",
           "owner": "John Smith",
           "command": "echo Task Updated!"
         }'
```
![put](SCREENSHOTS/curl-update.png)

Delete a Task
```bash
curl -X DELETE http://localhost:8080/tasks/123
```
![delete](SCREENSHOTS/curl-delete.png)

