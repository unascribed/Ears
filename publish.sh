#!/bin/bash -e
if [ -z "$1" -o -z "$2" -o -z "$3" ]; then
	echo "Need a Curse API key, Modrinth API key, and mcmod.cn cookie to publish."
	exit 2
fi
if [ ! -s "changelog.html" ]; then
	echo "Changelog is empty; refusing to publish."
	exit 1
fi

common="forge-1.2 forge-1.4 forge-1.5 forge-1.6 forge-1.7 forge-1.8 forge-1.9 forge-1.12 rift-1.13 fabric-1.14 forge-1.14 forge-1.15 fabric-1.16 forge-1.16 fabric-1.17 forge-1.17 forge-1.18 fabric-1.19 forge-1.19"
curse="$common"
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
cd ..
if [ "$3" != "-" ]; then
	export MCMODCN_COOKIE="$3"
	classID=3996
	forge=1
	fabric=2
	quilt=11
	rift=3
	other=10
	./mcmodcn-upload.sh $classID "Beta 1.7.3" $fabric '' artifacts/ears-fabric-b1.7.3-*.jar
	./mcmodcn-upload.sh $classID "1.2.5" $other '' artifacts/ears-forge-1.2-*.jar
	./mcmodcn-upload.sh $classID "1.4.7" $forge '' artifacts/ears-forge-1.4-*.jar
	./mcmodcn-upload.sh $classID "1.5.2" $forge '' artifacts/ears-forge-1.5-*.jar
	./mcmodcn-upload.sh $classID "1.6.4" $forge '' artifacts/ears-forge-1.6-*.jar
	./mcmodcn-upload.sh $classID "1.7.10" $forge '' artifacts/ears-forge-1.7-*.jar
	./mcmodcn-upload.sh $classID "1.8" $forge '' artifacts/ears-forge-1.8-*.jar
	./mcmodcn-upload.sh $classID "1.9/10/11" $forge '' artifacts/ears-forge-1.9-*.jar
	./mcmodcn-upload.sh $classID "1.12" $forge '' artifacts/ears-forge-1.12-*.jar
	./mcmodcn-upload.sh $classID "1.13.2" $rift '' artifacts/ears-rift-1.13-*.jar
	./mcmodcn-upload.sh $classID "1.14" $fabric '' artifacts/ears-fabric-1.14-*.jar
	./mcmodcn-upload.sh $classID "1.14" $forge '' artifacts/ears-forge-1.14-*.jar
	./mcmodcn-upload.sh $classID "1.15" $forge '' artifacts/ears-forge-1.15-*.jar
	./mcmodcn-upload.sh $classID "1.15/16" $fabric '' artifacts/ears-fabric-1.16-*.jar
	./mcmodcn-upload.sh $classID "1.16" $forge '' artifacts/ears-forge-1.16-*.jar
	./mcmodcn-upload.sh $classID "1.17/18" $fabric,$quilt '' artifacts/ears-fabric-1.17-*.jar
	./mcmodcn-upload.sh $classID "1.17" $forge '' artifacts/ears-forge-1.17-*.jar
	./mcmodcn-upload.sh $classID "1.18" $forge '' artifacts/ears-forge-1.18-*.jar
	./mcmodcn-upload.sh $classID "1.19" $fabric,$quilt '' artifacts/ears-fabric-1.19-*.jar
	./mcmodcn-upload.sh $classID "1.19" $forge '' artifacts/ears-forge-1.19-*.jar
fi
