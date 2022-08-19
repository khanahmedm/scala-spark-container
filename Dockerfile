# Title: Dockerfile - container for running scala and spark jobs
# Author: Ahmed M Khan
# Date Created: 8/13/22
# Date Modified: 8/18/22
# Description : This dockerfile performs the following steps:
#               1. Uses latest Ubuntu version
#               2. Installs JDK, Scala, and wget
#               3. Downloads apache spark and places the spark install under /opt
#               4. Sets up environment variables
#               5. Copies the scala and spark code to the container
#               Note: The jar file for the spark job was already created using sbt.
#                     This dockerfile does not download sbt components and installs them.
#                     If you want to download sbt, then uncomment the lines in the section 6 below              

# section 1: Use Ubuntu
FROM ubuntu:latest

# section 2: Install JDK, Scala, and wget
RUN apt update
RUN apt install default-jdk -y
RUN apt install scala -y
RUN apt install wget -y

# section 3: Downloads apache spark and places the spark install under /opt
# download and unzip spark distribution
RUN mkdir download && \
    cd download && \
    wget https://archive.apache.org/dist/spark/spark-3.2.1/spark-3.2.1-bin-hadoop3.2.tgz && \
    tar -xvf spark-3.2.1-bin-hadoop3.2.tgz

# set up Apache Spark
RUN cd /opt && \
    mkdir spark && \
    mv /download/spark-3.2.1-bin-hadoop3.2/* ./spark

# section 4: Setting up environment variables
ENV SPARK_HOME /opt/spark
ENV RUN_DIR /app/run
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin:$RUN_DIR
ENV DATA_DIR /app/data
ENV SCALA_DIR /app/scala/download-file
ENV SPARK_DIR /app/spark/access-log-process

# section 5: Copying the scala and spark code to the container and defining the entry point
COPY app /app
RUN cd $RUN_DIR && \
    chmod 755 start-scala-spark-job.sh

ENTRYPOINT ["start-scala-spark-job.sh"]

# section 6: lines for installing sbt
#RUN apt update
#RUN apt install apt-transport-https curl gnupg -yqq
#RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
#RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
#RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
#RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
#RUN apt update
#RUN apt install sbt -y