/*********************************************************************************
  File name: downloadFile.scala
  Title: download file program using a URL
  Author: Ahmed M Khan
  Date Created: 8/16/22
  Date Modified: 8/18/22
  Description : This program downloads a file using a URL provided as an argument.
**********************************************************************************/


import sys.process._
import java.net.{HttpURLConnection, URL}
import java.io.{File,FileReader,FileWriter}
import java.io.FileNotFoundException
import java.io.IOException
import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.util.control._

// This program downloads a file using a URL 
object downloadFile {
  def main(args: Array[String]) {

    // Checking to see if a URL is provided as an argument
    if (args.size == 0 || args.size > 1)
        println("Please specify URL")
    else {
        println("URL provided: " + args(0))
        
        // parsing URL and getting the file name to download
        val urlOfFileToDownload = args(0)
        val index = urlOfFileToDownload.lastIndexOf("/")
        val downloadFileName = urlOfFileToDownload.substring(index+1)
        println("file to download: " + downloadFileName)

        // specifying the download file target path
        val outputFileName = "../../data/" + downloadFileName

        // Instantiating URL object
        val url = new URL(urlOfFileToDownload)

        // Setting up the connection
        val connection = url.openConnection().asInstanceOf[HttpURLConnection]
        connection.setConnectTimeout(5000)
        connection.setReadTimeout(5000)
        connection.connect()
        
        // downloading the file - in case of error, showing the HTTP error response code
        try {
          if (connection.getResponseCode >= 400) {
            println("error downloading the file - HTTP error code: " + connection.getResponseCode)
            connection.disconnect()
        }
          else {
            println("downloading...")
            url #> new File(outputFileName) !!
            
            println("download completed.")
          }
        }
        // catching any possible exceptions
        catch {
         case e: java.io.IOException => println("Had an IOException trying to read that file")
         case e: java.lang.RuntimeException => println("Runtime exception")
         case e: java.io.FileNotFoundException => println("Couldn't find that file.")
         case e: java.net.SocketTimeoutException => println("Connection timeout")
        }
    }
  }
}

