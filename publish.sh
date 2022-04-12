#!/bin/bash -e
if [ -z "$1" -o -z "$2" ]; then
	echo "Need a Curse API key and Modrinth API key to publish."
	exit 2
fi
if [ ! -s "changelog.html" ]; then
	echo "Changelog is empty; refusing to publish."
	exit 1
fi

common="forge-1.2 forge-1.4 forge-1.5 forge-1.6 forge-1.7 forge-1.8 forge-1.9 forge-1.12 fabric-1.14 forge-1.14 forge-1.15 fabric-1.16 forge-1.16 fabric-1.17 forge-1.17 forge-1.18"
curse="rift-1.13 $common"
modrinth="$common fabric-b1.7.3"

cd publish-curseforge
if [ "$1" != "-" ]; then
	for proj in $curse; do
		echo Publishing $proj to CurseForge...
		TERM=dumb chronic ./gradlew -PcurseApiKey=$1 -Ptarget=$proj curseforge
	done
fi
cd ../publish-modrinth
if [ "$2" != "-" ]; then
	for proj in $modrinth; do
		echo Publishing $proj to Modrinth...
		TERM=dumb chronic ./gradlew -PmodrinthApiKey=$2 -Ptarget=$proj modrinth
	done
fi

