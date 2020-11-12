#!/bin/bash
cur=$(cat version.txt)
read -er -i $cur -p 'New version: ' new
if [ "$new" == "$cur" ]; then
	exit 0
fi
echo $new > version.txt
echo | tee */version-suffix.txt
