# OnlineML
RESTful micro services for machine learning based on Spring Boot
# Dependencies
  * `jdk 1.8`
  * `gradle 4.10`
# Usage
If you would like to deploy them on different servers, first clone the submodules by
```
$ git submodule update --init
```
put the services in folder `service` into your machine, and then start them by
```
$ gradle clean bootRun
```
To deploy the services on localhost, please run the following command.
## Windows
```
run.bat
```
## Unix
```
./run.sh
```
