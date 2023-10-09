FROM ubuntu:22.04

#COPY sources.list /etc/apt/sources.list

RUN apt-get update \
    &&  apt-get -y -o Dpkg::Options::="--force-confnew" upgrade \
    &&  apt-get -y install \
    perl \
    zip \
    unzip \
    libterm-readkey-perl \
    openssl \
    gettext

ADD jboss-4.0.5.GA /opt/jboss
ADD jdk1.6.0_45 /opt/jdk/jdk1.6.0_45
ADD apache-ant-1.9.0 /opt/apache-ant-1.9.0

ENV PATH="/opt/jdk/jdk1.6.0_45/bin:/opt/apache-ant-1.9.0/bin:${PATH}"

WORKDIR /opt/biobank
#CMD ["/bin/bash"]
