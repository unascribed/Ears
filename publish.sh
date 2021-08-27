#!/bin/bash -e
if [ -z "$1" ]; then
	echo "Need a Curse API key to publish."
	exit 2
fi
if [ ! -s "changelog.html" ]; then
	echo "Changelog is empty; refusing to publish."
	exit 1
fi

all="fabric-1.8 fabric-1.14 fabric-1.16 fabric-1.17 forge-1.2 forge-1.4 forge-1.5 forge-1.7 forge-1.8 forge-1.12 forge-1.14 forge-1.15 forge-1.16 rift-1.13"

cd publish
for proj in $all; do
	echo Publishing $proj...
	TERM=dumb chronic ./gradlew -PcurseApiKey=$1 -Ptarget=$proj curseforge
done

