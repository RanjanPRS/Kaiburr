# Kubernetes Deployment - Task 2

This part involves containerizing the Task Manager App and deploying it to Kubernetes. The application runs inside a Minikube cluster and uses a BusyBox pod to execute tasks dynamically. MongoDB is deployed with Persistent Volumes (PV) to ensure data consistency.

### Cluster Setup

The **Kubernetes cluster** was set up on my local machine using **Minikube v1.33.1**.
The **Container Runtime Interface (CRI)** used is **Docker**.

```bash
alias k=kubectl
minikube status
```
![status](SCREENSHOTS/minikube-status.png)

### Containerizing the Application

**Tool Used: Docker**

The Task Manager API was packaged into a Docker image.

**Build & Push Docker Image**
```bash
docker build -t myrepo/taskmanager:latest .
docker push myrepo/taskmanager:latest
```
![build](SCREENSHOTS/dockerbuild-cmd.png)
![img](SCREENSHOTS/dockerimg-hub.png)

### Kubernetes Manifests

The following Kubernetes objects were used:

- Deployment: Runs the Task Manager API

- Service: Exposes the API (Type: NodePort)

- MongoDB Deployment & Service: Runs MongoDB and exposes it internally.

- Persistent Volume: Ensures MongoDB data persists.

```bash
k get all
```
![l](SCREENSHOTS/manifest-list.png)
![apply](SCREENSHOTS/get-all.png)

### API Testing (cURL)

Ping API

```bash
curl -X GET http://<MINIKUBE-IP>:<PORT>/tasks/ping
```
![ping](SCREENSHOTS/ping.png)

POST a Task

```bash
curl -X POST http://<MINIKUBE-IP>:<PORT>/tasks \
     -H "Content-Type: application/json" \
     -d '{
           "id": "123",
           "name": "K8s Task",
           "owner": "Priyaranjan",
           "command": "echo Kubernetes Running!"
         }'
```
![postt](SCREENSHOTS/post-check.png)

### BusyBox Pod Execution

A BusyBox pod is dynamically created to execute each task instead of running commands locally.

**Task Execution Process:**
- A BusyBox pod is created dynamically when a task is executed.

- The task command runs inside the BusyBox pod

- The logs from the BusyBox pod are retrieved and stored in MongoDB.


```bash
k get event --sort-by=.metadata.creationTimestamp | tail -n 6
k logs <podname>
```
![BusyBox](SCREENSHOTS/busybox-creation.png)
![BusyBox2](SCREENSHOTS/busybox-creation2.png)

### MongoDB Data Persistence (Persistent Volume - PV)

A Persistent Volume (PV) is used to store MongoDB data even after the pod is deleted.

**Testing Process:**

> Delete the MongoDB Pod.
```bash
k describe pvc/<mongo-pvc>
k delete po/<mongo-pod-name>
```
The ReplicaSet ensures that the pod is recreated successfully

> Check if the data still exists in MongoDB

```bash
k get pod
k exec -it <mongo-db-pod> -- mongosh "mongodb://admin:password@mongodb-service:27017/taskdb?authSource=admin" //mongosh
db.tasks.find().pretty() // to view if the data still exists even after the pod restarts.
```
![pvc](SCREENSHOTS/mongo-pvc.png)
![pvc2](SCREENSHOTS/mongo-pvc2.png)
![pvc3](SCREENSHOTS/mongo-pvc3.png)

### Conclusion:

> The Task Manager app was successfully containerized and deployed on Minikube.
> BusyBox pods were used to dynamically execute tasks.
> MongoDB data persisted even after pod deletion using Persistent Volumes.
