# scala-spark-container for access log processing
This project has the following artifacts:
1. Dockerfile - using Ubuntu
2. Scala source code for downloading file (access log)
3. Spark source code for processing the file to find top-N most frequent URLs and visitors
4. Shell script that is the entry point for the conatiner - it takes two arguments: URL and value of N for top-N
5. Unit testing document - contains screenshots
6. Output of the overall process for top-3 most frequent URLs and visitors

# Docker image can be built using the below command:
docker build -t scala-spark-container .

# Run the following command to execute scala and spark jobs:
docker run scala-spark-container https://ditotw.space/NASA_access_log_Jul95.gz 3  
-- where 3 as in top-3

# To log on to docker container, override the entry point:
docker run -it --entrypoint /bin/bash scala-spark-container

# Resources and references used to work on this project:
Udemy course: Apache Spark with Scala - Hands On with Big Data!  
https://www.udemy.com/course/apache-spark-with-scala-hands-on-with-big-data/  
https://phoenixnap.com/kb/install-spark-on-ubuntu  
https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html  
https://sparkbyexamples.com/spark/spark-submit-command/  
https://spark.apache.org/docs/0.9.2/quick-start.html  
https://medium.com/@raviranjan_iitd/running-a-scala-code-as-a-spark-submit-job-using-sbt-e03ba05b941f  
