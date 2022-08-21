# scala-spark-container for access log processing
This project has the following artifacts:
1. Dockerfile - using Ubuntu
2. Scala source code for downloading file (access log)
3. Spark source code for processing the file to find top-N most frequent URLs and visitors
4. Shell script that is the entry point for the container - it takes two arguments: URL and value of N for top-N
5. Unit testing documents covering test cases with screenshots
6. Output of the overall process for top-3 most frequent URLs and visitors

## Assumptions:
1. It is assumed that the file that is downloaded using the URL is a correctly formatted access log file. There is no check performed to see the integrity of the downloaded file structure.  
2. The file contains blanks (empty string) for URL and visitor columns in some lines. When processing data for URL, the dataset was filtered to remove rows with blank values in the URL column. Similarly, when processing for visitors, the blank values in the visitor column were filtered out.  
3. The first column is considered as the visitor column, however, the column contains client machine's host name or IP address.  
4. On each line, the string value after HTTP method is considered as the URL endpoint.  
5. It is assumed we do not need to pay special consideration to HTTP response code values in the data file. There is no filter applied based on HTTP response code 200 when processing the data.  
6. There are 735 duplicate lines in the data file. Deduped has not been applied. It can be either applied using linux commands (sort <filename> | uniq) in the shell script or in the Spark code.

# Environment:
Ubuntu 22.04.1  
Scala code runner version 2.11.12  
Java openjdk version 11.0.16  
Apache Spark version 3.2.1  
version of Scala in Spark distribution is 2.12.15 -- used in build.sbt

# Docker image can be built using the below command:
docker build -t scala-spark-container .

# Run the following command to execute scala and spark jobs:
docker run scala-spark-container https://ditotw.space/NASA_access_log_Jul95.gz 3  
-- where 3 as in top-3  
  
8/19/22: If the above command results in an error saying 'No such file or directory', that could be due to the addition of line breaks in the shell script. The line breaks are added either during git push or git pull operation.The dockerfile has been modified to include the steps to install dos2unix and apply to the shell script for removing extra line breaks.  


# To log on to docker container, override the entry point:
docker run -it --entrypoint /bin/bash scala-spark-container  
### Shell script can be kicked off using the below command:  
start-scala-spark-job.sh https://ditotw.space/NASA_access_log_Jul95.gz 3  
The shell script runs both the Scala code as well as the Spark code. If you want to run individual code, then use the below steps.  

### Scala code can be run as follows:  
cd $SCALA_DIR  
scala downloadFile https://ditotw.space/NASA_access_log_Jul95.gz  

### Decompress the data file:  
cd $DATA_DIR  
gzip -d NASA_access_log_Jul95.gz  

### Spark code can be run as follows:  
cd $SPARK_DIR  
spark-submit --class "TopURLsAndVisitors" --master local[4] target/scala-2.12/access-log-processing_2.12-1.0.jar NASA_access_log_Jul95    

# Resources and references used to work on this project:
Udemy course: Apache Spark with Scala - Hands On with Big Data!  
https://www.udemy.com/course/apache-spark-with-scala-hands-on-with-big-data/  
https://phoenixnap.com/kb/install-spark-on-ubuntu  
https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html  
https://sparkbyexamples.com/spark/spark-submit-command/  
https://spark.apache.org/docs/0.9.2/quick-start.html  
https://medium.com/@raviranjan_iitd/running-a-scala-code-as-a-spark-submit-job-using-sbt-e03ba05b941f  
https://stackoverflow.com/questions/24162478/how-to-download-and-save-a-file-from-the-internet-using-scala  
https://alvinalexander.com/scala/scala-how-to-download-url-contents-to-string-file/  
