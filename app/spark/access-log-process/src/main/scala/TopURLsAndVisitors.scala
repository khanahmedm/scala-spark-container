/*********************************************************************************
  File name: TopURLsAndVisitors.scala
  Title: Find top-n most frequently visited URLs and visitors for each day
  Author: Ahmed M Khan
  Date Created: 8/17/22
  Date Modified: 8/18/22
  Description : This program reads the downloaded file and processes data using
                spark dataframes to find top-n most frequently visited URLs and 
                visitors.
**********************************************************************************/


import org.apache.spark._
import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.WindowSpec
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DateType
import org.apache.spark.sql.functions.dense_rank

// This program processes data using spark and finds top-n most frequent URLs and visitors
object TopURLsAndVisitors {

  // main function
  def main(args: Array[String]) {

    // Checking to see if file name and n-value are provided as arguments
    if (args.size <= 1 || args.size > 2)
        println("Please specify file name and n-value for top-n")
    else {
    

      // retrieve file name from the argument passed
      val fileName = args(0)
      // Setting the log level to print errors only
      Logger.getLogger("org").setLevel(Level.ERROR)

      // Creating a spark session
      val spark = SparkSession
        .builder
        .appName("TopURLsAndVisitors")
        .master("local[*]")
        .getOrCreate()

      // Loading the data file into a dataset
      import spark.implicits._
      val ds = spark.read
        .option("header", "false")
        .option("inferSchema", "false")
        .text("../../data/" + fileName)

      // Creating regular expressions for  extracting data from access log
      val contentSizeExp = "\\s(\\d+)$"
      val statusExp = "\\s(\\d{3})\\s"
      val generalExp = "\"(\\S+)\\s(\\S+)\\s*(\\S*)\""
      val timeExp = "\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} -\\d{4})]"
      val userExp = "(^\\S+\\.[\\S+\\.]+\\S+)\\s"

      // Applying the regular expressions to create structure from the unstructured text
      val logsDF = ds.select(regexp_extract(col("value"), userExp, 1).alias("visitors"),
      regexp_extract(col("value"), timeExp, 1).alias("timestamp"),
      regexp_extract(col("value"), generalExp, 1).alias("method"),
      regexp_extract(col("value"), generalExp, 2).alias("URL"),
      regexp_extract(col("value"), generalExp, 3).alias("protocol"),
      regexp_extract(col("value"), statusExp, 1).cast("Integer").alias("status"),
      regexp_extract(col("value"), contentSizeExp, 1).cast("Integer").alias("content_size"))
      
      // Removing rows where there is no endpoint URL
      val logsNoURLsDF = logsDF.filter("URL != ''")
      
      // Removing rows where there is no visitors
      val logsNoVisitorsDF = logsDF.filter("visitors != ''")

      // Adding a new column for date field for URL dataset - that is a substring of timestamp column
      val logsNoURLsWithDateDF = logsNoURLsDF.withColumn("date", substring(col("timestamp"),1,11))

      // Adding a new column for date field for visitors dataset - that is a substring of timestamp column
      val logsNoVisitorsWithDateDF = logsNoVisitorsDF.withColumn("date", substring(col("timestamp"),1,11))

      // assigning value for 'n' as in top-n rows - the value is passed as an argument 
      val numOfRowsToReturnForEachDay = args(1).toInt 
      // hard-coded value for July, however, it can be made dymanic
      val numOfDaysInMonth = 31

      val numOfRowsToReturnInQueryResult = numOfRowsToReturnForEachDay*numOfDaysInMonth

      // creating a window partitioned by date and count columns
      val overDateCount = Window.partitionBy($"date").orderBy($"count".desc)
      
      /****** Finding top-n URLs *********/
      val countURLsDF = logsNoURLsWithDateDF.groupBy("URL", "date").count()
      
      // applying dense rank to get the ranking for each URL for each day
      val rankedURLsDF = countURLsDF.withColumn("ranking", dense_rank.over(overDateCount)).filter(col("ranking") <= numOfRowsToReturnForEachDay)
      
      // creating the final result set for top-n URLs
      val topURLsDF = rankedURLsDF.select("*").orderBy(col("date").asc, col("count").desc)
      
      // show top-n URLs resulting rows and full column values
      println("********** TOP " + numOfRowsToReturnForEachDay + " URLs for each day **********")
      topURLsDF.show(numOfRowsToReturnInQueryResult,false)

      /****** Finding top-n visitors *******/
      val countVisitorsDF = logsNoVisitorsWithDateDF.groupBy("visitors", "date").count()

      // applying dense rank to get the ranking for each visitor for each day
      val rankedVisitorsDF = countVisitorsDF.withColumn("ranking", dense_rank.over(overDateCount)).filter(col("ranking") <= numOfRowsToReturnForEachDay)

      // creating the final result set for top-n visitors
      val topVisitorsDF = rankedVisitorsDF.select("*").orderBy(col("date").asc, col("count").desc)

      // show top-n visitors resulting rows and full column values
      println("********** TOP " + numOfRowsToReturnForEachDay + " visitors for each day **********")
      topVisitorsDF.show(numOfRowsToReturnInQueryResult,false)

    }
  }

}

