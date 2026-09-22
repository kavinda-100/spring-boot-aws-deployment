# Spring Boot AWS Deployment Example

A Spring Boot service packaged as a Docker image and deployed to an existing AWS EC2 instance through GitHub Actions.

## Overview

The deployment pipeline uses Docker Hub as the image registry:

1. A push to `main` builds the application with Java 25.
2. GitHub Actions builds and pushes `<DOCKERHUB_USERNAME>/spring-aws-deploy:latest` to Docker Hub.
3. After that workflow succeeds, a second workflow connects to EC2 over SSH, pulls the image, replaces the existing `spring-aws-deploy` container, and exposes it on port `8080`.

This guide assumes that an EC2 instance is already running, Docker is installed on it, and its security rules permit SSH and HTTP access on port `8080`. Do not commit credentials, Docker Hub tokens, or private keys to this repository.

## Prerequisites

- An AWS EC2 instance with a public IP address or DNS name
- Docker installed on the EC2 instance, with the configured SSH user allowed to run `sudo docker`
- A Docker Hub account and a repository named `spring-aws-deploy`
- A GitHub repository with Actions enabled
- Java 25 and Docker for local development

## Run Locally

1. Clone the repository and build the JAR:

   ```bash
   git clone https://github.com/kavinda-100/spring-boot-aws-deployment.git
   cd spring-aws-deployment
   ./mvnw clean package
   ```

2. Build and run the image:

   ```bash
   docker build -t spring-aws-deploy:local .
   docker run --rm -p 8080:8080 --name spring-aws-deploy spring-aws-deploy:local
   ```

3. In another terminal, verify the service:

   ```bash
   curl http://localhost:8080/
   curl http://localhost:8080/health
   ```

## Configure GitHub Actions Secrets

Open **Repository settings → Secrets and variables → Actions**, select **New repository secret**, and add the following secrets. Use repository secrets; never place their values directly in a workflow file.

| Secret               | Value                                                                                 |
|----------------------|---------------------------------------------------------------------------------------|
| `DOCKERHUB_USERNAME` | Docker Hub username that owns the image repository.                                   |
| `DOCKERHUB_TOKEN`    | Docker Hub access token with permission to push the image.                            |
| `EC2_HOST`           | EC2 public IP address or public DNS name, for example `<EC2_PUBLIC_IP>`.              |
| `EC2_PORT`           | SSH port, normally `22`.                                                              |
| `EC2_USERNAME`       | SSH user for the instance, commonly `ec2-user` on Amazon Linux or `ubuntu` on Ubuntu. |
| `EC2_SSH_KEY`        | Complete contents of the private EC2 `.pem` key file.                                 |

### Add the EC2 Private Key Secret

`EC2_SSH_KEY` must contain the raw private-key text, including its first and final lines:

```text
-----BEGIN PRIVATE KEY-----
...
-----END PRIVATE KEY-----
```

Copy the content of the `.pem` file into the GitHub secret. Do not use the `.pub` file, omit the include terminal prompt text such as `%`. A trailing newline is fine.

The workflow passes this secret to `appleboy/ssh-action` as its SSH private key. If the action reports `ssh.ParsePrivateKey: ssh: no key found`, recreate `EC2_SSH_KEY` from the original `.pem` file and check that the entire multiline key was pasted.

## Connect to EC2 Manually

Keep your downloaded `.pem` file private. SSH will refuse to use it if it is readable by other users.

```bash
chmod 400 <key-name>.pem
ssh -i <key-name>.pem <EC2_USERNAME>@<EC2_PUBLIC_IP>
```

For example, Amazon Linux usually uses `ec2-user`:

```bash
ssh -i spring-boot-aws-deploy-demo.pem ec2-user@<EC2_PUBLIC_IP>
```

On the first connection, SSH asks you to confirm the server fingerprint. Type `yes` only after confirming that the host is your EC2 instance. If you receive `UNPROTECTED PRIVATE KEY FILE`, rerun `chmod 400 <key-name>.pem`. If authentication is denied after that, verify the key pair and the EC2 username; Ubuntu images usually use `ubuntu` instead of `ec2-user`.

## Deploy

1. Confirm the six repository secrets are configured.
2. Commit and push your changes to `main`:

   ```bash
   git push origin main
   ```

3. In the repository's **Actions** tab, wait for **build the docker image and push to docker hub** to complete successfully.
4. The **Deploy to AWS EC2** workflow starts automatically after the image workflow succeeds. It pulls the latest image and replaces the EC2 container.

## Verify the Deployment

Replace `<EC2_PUBLIC_IP>` with your instance's public address:

```bash
curl --fail --silent --show-error http://<EC2_PUBLIC_IP>:8080/
curl --fail --silent --show-error http://<EC2_PUBLIC_IP>:8080/health
```

Both requests should return HTTP `200` JSON. The health endpoint includes a status of `UP` when the service is running.

For server-side checks, connect over SSH and run:

```bash
sudo docker ps --filter name=spring-aws-deploy
sudo docker logs spring-aws-deploy
```

## Troubleshooting

| Symptom                                  | Check                                                                                                                               |
|------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| `ssh.ParsePrivateKey: ssh: no key found` | Recreate `EC2_SSH_KEY` from the complete private `.pem` file. Do not paste a public key, `%`, quotes, or incomplete key boundaries. |
| `UNPROTECTED PRIVATE KEY FILE`           | Restrict local key permissions with `chmod 400 <key-name>.pem`. This affects local SSH only, not the GitHub secret.                 |
| `Permission denied (publickey)`          | Verify `EC2_USERNAME`, the EC2 key pair, and the server's authorized key.                                                           |
| Deployment workflow cannot reach EC2     | Verify `EC2_HOST`, `EC2_PORT`, the instance is running, and the EC2 security group permits SSH from the GitHub Actions runner.      |
| Endpoint does not respond                | Check that port `8080` is permitted by the EC2 security group and inspect `sudo docker logs spring-aws-deploy`.                     |
