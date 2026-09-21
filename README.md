# Spring Boot AWS Deployment example

---

## Overview
This is a simple example of how to deploy a Spring Boot application to AWS. This example uses AWS EC2 and docker to deploy this application.

## Prerequisites
- AWS account
- Docker and Docker Hub account
- Java
- Maven

## Technologies Used
- Spring Boot
- Java
- AWS EC2
- Docker

---

### Steps to test the application locally

1. Clone the repository
   ```bash
   git clone https://github.com/kavinda-100/spring-boot-aws-deployment.git
    ```
   
2. Build the application using Maven
   ```bash
   mvn clean package
   ```
   
3. Build the Docker image
   ```bash
   docker build -t spring-boot-aws-deployment:latest .
   ```
   
4. Run the Docker container
   ```bash
   docker run -d -p 8080:8080 --name spring-container spring-boot-aws-deployment:latest
   ```
   
5. Check the container logs to ensure the application is running
   ```bash
   docker logs spring-container
   ```

6. Access the application in your browser at
   ```
   http://localhost:8080
   http://localhost:8080/health
   ```

7. Stop the Docker container when done
   ```bash
   docker stop spring-container
   ```