/*********************************************************************************
  File name: downloadFile.scala
  Title: download file program using a URL
  Author: Ahmed M Khan
  Date Created: 8/16/22
  Date Modified: 8/20/22
  Description : This program downloads a file using a URL provided as an argument.
**********************************************************************************/


import sys.process._
import java.net.{HttpURLConnection,URL} 
import java.net.{MalformedURLException,UnknownHostException,SocketTimeoutException}
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
        val targetDirPath = "../../data/"
        //File targetDirPath
        val outputFileName = targetDirPath + downloadFileName
        
        try {
          // Instantiating URL object
          val url = new URL(urlOfFileToDownload)
        
          // Setting up the connection
          val connection = url.openConnection().asInstanceOf[HttpURLConnection]
          connection.setConnectTimeout(5000)
          connection.setReadTimeout(5000)

          val httpResponseCode = connection.getResponseCode
          
          // checking reponse code for errors
          if (httpResponseCode >= 400) {
              println("Error: downloading the file - HTTP error code: " + httpResponseCode)
          }
          // checking if the target directory exists
          else if (! new java.io.File(targetDirPath).exists) {
            println("Error : Target directory path "+ targetDirPath +" does not exist. Please check.")
          }
          // connecting and downloading the file
          else {
            connection.connect()
            println("downloading...")
            url #> new File(outputFileName) !!
          }
        }
        // catching exceptions
        catch {
          case e: java.net.MalformedURLException => println("Error: Malformed URL - please check the URL.")
          case e: java.net.UnknownHostException => println("Error: Unknown host - please check the hostname in the URL")
          case e: java.io.FileNotFoundException => println("Error: Couldn't find that file.")
          case e: java.io.IOException => println("Error: Had an IOException trying to read that file")
          case e: java.lang.RuntimeException => println("Error: Runtime exception")
          case e: java.net.SocketTimeoutException => println("Error: Connection timeout")
        }
    }
  }
}

