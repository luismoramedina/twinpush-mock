############
#Not tested#
############

FROM ubuntu
MAINTAINER luismoramedina@gmail.com

RUN apt-get -y update

RUN apt-get -y install maven
RUN apt-get -y install git

EXPOSE 8081

RUN git clone https://github.com/luismoramedina/twinpush-mock.git

RUN cd twinpush-mock
RUN mvn clean install exec:java