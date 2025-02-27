
# REACT FRONTEND

This part involves creating a User Interface using **React** for the previously built **Spring Boot** Backend. It **allows users to create, search, and manage tasks** that represent shell commands to be executed. The backend stores task data in **MongoDB**, while the frontend provides an intuitive UI for interaction.

### Install Dependencies & Run the Application


```bash
npm install // to install dependencies
npm start // to start the server
```

### Application Overview

> **Home Page (Task Management Dashboard)**

![front](SCREENSHOTS/frontpage.png)
![front2](SCREENSHOTS/frontpage2.png)

The main dashboard allows users to:

>> Search for tasks by name.

>>Create new tasks with ID, name, owner, and command.

>> View existing tasks and their execution status.

> **New Task Component**

![new](SCREENSHOTS/newtask.png)

This form allows users to create new tasks by providing:
>>Task ID

>>Task Name

>>Owner Name

>>Shell Command to Execute

> **Backend Connectivity**

![status](SCREENSHOTS/connectivity.png)

The frontend communicates with the Spring Boot REST API, sending POST and GET requests.

> **Search Component**

![search](SCREENSHOTS/search.png)

Users can search for tasks by name using this component.

> **Backend data verification**

![backend](SCREENSHOTS/backend.png)

After creating tasks, they are stored in MongoDB and can be retrieved.

### Conclusion

This project showcases a full-stack implementation of a task management application with a React frontend, Spring Boot backend, and MongoDB storage.
