<!-- PROJECT SHIELDS -->
[![Coverage Status][coveralls-shield]][coveralls-url]
[![JitPack][jitpack-shield]][jitpack-url]
[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- LOGO -->
<br />
<p align="center">
  <a href="https://github.com/D4rK3oN/spring-boot-2-simple-example">
    <img src="images/spring-boot-2.png" alt="logo">
  </a>

  <h3 align="center">Microservices with Spring Boot 2</h3>

  <p align="center">
    A simple example of a microservice
  </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Roadmap](#roadmap)

<!-- ABOUT THE PROJECT -->
## About The Project

There are many great samples of microservices with Spring Boot 2 available on GitHub.
<br />
With the passage of time, I have thought of collecting in a mini-project all those tasks that I usually need in my day to day to accelerate my developments.

<!-- GETTING STARTED -->
## Getting Started

Here are some instructions on how to configure your project locally.
<br />
To get a local copy up and running follow these simple steps.

### Prerequisites

This is a list things that you need to use the software and how to configure them.
* MongoDB Server (optional)
    * We can download the Community Server from their [website](https://www.mongodb.com/download-center/community)
    * A good alternative to installing a MongoDB server is create a MongoDB container in Docker.

* Docker Desktop
    * The software can be download from their [website](https://www.docker.com/products/docker-desktop)
```sh
# Create volumes in docker to save data (optional)
docker volume create mongodb-vol
docker volume create microservices-vol

# Download and run MongoDB container
docker pull mongo
docker run -d -p 27017:27017 -v mongodb-vol:/data/db --name mongodb mongo
```

### Installation

#### Execute with docker
```sh
docker pull d4rk3on/spring-boot-2-simple-example:{version}
docker run -d -p 9080:9080 -e DEV_PATH='/' -e SPRING_PROFILES_ACTIVE='docker' -v microservices-vol:/resources --link mongodb --name spring-boot-example d4rk3on/spring-boot-2-simple-example:{version}
```

#### Execute without docker
1. Clone the repository
```sh
git clone https://github.com/D4rK3oN/spring-boot-2-simple-example.git
```

2. Configure environment variables  
Add DEV_PATH var with the path where you want to save the logs, etc
```xml
<!-- e.g. used in logback.xml -->
<file>${DEV_PATH}/resources/spring-boot-2-simple-example/logs/simple_example.log</file>
```

3. Configure the properties of the application-dev.yml file (if you need it)
```yml
spring.data.mongodb:
  host: localhost
  port: 27017
```

4. Run/Debug the project with the profile "dev"
```text
Program args: --spring.profiles.active=dev
```

<p align="center">
    <img width="600px" src="images/IntelliJ_run_debug_config.png" alt="run config">
</p>

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/D4rK3oN/spring-boot-2-simple-example/issues) for a list of proposed features (and known issues).

<!-- MARKDOWN LINKS & IMAGES : https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[coveralls-shield]: https://coveralls.io/repos/github/D4rK3oN/spring-boot-2-simple-example/badge.svg?branch=master
[coveralls-url]: https://coveralls.io/github/D4rK3oN/spring-boot-2-simple-example?branch=master
[jitpack-shield]: https://jitpack.io/v/D4rK3oN/lib-spring-boot-mvc.svg
[jitpack-url]: https://jitpack.io/#D4rK3oN/lib-spring-boot-mvc
[contributors-shield]: https://img.shields.io/github/contributors/D4rK3oN/spring-boot-2-simple-example.svg?style=flat-square
[contributors-url]: https://github.com/D4rK3oN/spring-boot-2-simple-example/graphs/contributors
[issues-shield]: https://img.shields.io/github/issues/D4rK3oN/spring-boot-2-simple-example.svg?style=flat-square
[issues-url]: https://github.com/D4rK3oN/spring-boot-2-simple-example/issues
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/javier-moreno-alvarez