#!/bin/bash

# Title: Start scala job for downloading file and spark job for processing data
# Author: Ahmed M Khan
# Date Created: 8/18/22
# Date Modified: 8/18/22
# Description : This program runs two codes:
#		1. scala code to download a file from a URL
#		2. spark job to process data and show top-n frequent URLs and visitors
#######################################################################################

URL=$1
topN=$2
fileName=`basename https://ditotw.space/NASA_access_log_Jul95.gz`
echo $URL
echo $fileName

# Remove the file if it already exists in the data directory
cd $DATA_DIR

if [ -e $fileName ]
then	
	echo 'Removing the existing copy of ' $fileName
	rm $fileName

fi

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
	decompressedFileName=`basename -s .gz $fileName`
	echo 'Decompression completed - resulting file is '$decompressedFileName'.'
fi

cd $SPARK_DIR
spark-submit --class "TopURLsAndVisitors" --master local[4] target/scala-2.12/access-log-processing_2.12-1.0.jar $decompressedFileName $topN

status=$?
[ $status -eq 0 ] && echo "Data processing completed successfully" || "There is a problem with processing, please check."
