#!/bin/bash

# Title: Start scala job for downloading file and spark job for processing data
# Author: Ahmed M Khan
# Date Created: 8/18/22
# Date Modified: 8/20/22
# Description : This script runs two programs:
#		1. Scala program to download a file from a URL
#		2. Spark program to process data and show top-n frequent URLs and visitors
#######################################################################################

URL=$1
topN=$2

# Check number of arguments passed to the script
if [ "$#" != "2" ]; then
    echo "Error: Please provide URL and N value as arguments."
	echo "Exiting the program."
	exit 1
fi

re='^[0-9]+$'
if ! [[ $topN =~ $re ]] ; then
   echo "Error: 2nd argument is not a number."
   echo "Exiting the program."
   exit 1
fi


fileName=`basename https://ditotw.space/NASA_access_log_Jul95.gz`

# Check if the data directory does not exist
if [ ! -d $DATA_DIR ] 
then
    echo "Error: The data directory " $DATA_DIR " does not exist." 
	echo "Exiting the program."
    exit 1
fi

# Remove the file if it already exists in the data directory
cd $DATA_DIR

if [ -e $fileName ]
then	
	echo 'Removing the existing copy of ' $fileName
	rm $fileName

fi

# Download the file using scala program and decompress the file using gzip command
cd $SCALA_DIR
scala downloadFile $URL
cd $DATA_DIR
if [ ! -e $fileName ]	
then
	echo 'There is a problem downloading the file. Please check.'
	exit 255
else
	echo 'decompressing the file...'
	gzip -d -f $fileName
	# retrieve the uncompressed file name that is used by spark program
	decompressedFileName=`basename -s .gz $fileName`
	echo 'Decompression completed - resulting file is '$decompressedFileName'.'
fi

# Process data using spark program
cd $SPARK_DIR
spark-submit --class "TopURLsAndVisitors" --master local[4] target/scala-2.12/access-log-processing_2.12-1.0.jar $decompressedFileName $topN

# check if the spark job exited with any issues by looking at the exit code
status=$?
[ $status -eq 0 ] && echo "Data processing completed successfully" || "There is a problem with processing, please check."
