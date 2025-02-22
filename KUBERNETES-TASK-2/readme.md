# Kubernetes Task Manager App- Setup

## Overview
The Task Manager App developed in the first task was containerized and deployed onto a kubernetes cluster, i.e minikube

## Containerization
Tool Used: Docker

The Docker image for the Task Manager application was built and pushed to Docker Hub.

Base Image Used: openjdk:17-jdk-slim

#### Docker Commands Used:

```sh
docker build -t ranjanprs/task-manager-app:latest .
docker push ranjanprs/task-manager-app:latest
docker login
docker push ranjanprs/task-manager-app:latest
```

#### Docker Hub Image Link: https://hub.docker.com/repository/docker/ranjanprs/task-manager-app/general

## Kubernetes Cluster Setup

Minikube v1.33.1

Docker - Container Runtime Interface (CRI)

#### Manifests Used:

mongodb-deployment.yml (MongoDB Pod)

mongodb-pvc.yml (Persistent Volume for MongoDB)

mongodb-svc.yml (MongoDB Service)

deployment.yml (Task Manager App Deployment)

svc.yml (Service for Task Manager App)


#### Commands used:
    
    alias k=kubectl
    k apply -f .
    k get all
    k describe po/<pod-name>
    k get node -o wide
    k delete po/<mongo-pod-name>
    k exec -it <mongo-db-pod> -- mongosh "mongodb://admin:password@mongodb-service:27017/taskdb?authSource=admin" //connect to the mongosh
    db.tasks.find().pretty() // to view if the data still exists even after the pod restarts.
