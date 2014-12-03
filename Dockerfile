############
#Not tested#
############

FROM ubuntu
MAINTAINER luismoramedina@gmail.com

RUN apt-get -y update

RUN apt-get install -y openjdk-7-jdk
RUN apt-get -y install maven
RUN apt-get -y install git

ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
EXPOSE 8081

RUN git clone https://github.com/luismoramedina/twinpush-mock.git

WORKDIR /twinpush-mock

CMD "mvn clean install exec:java"