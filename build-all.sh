#!/bin/bash -e

all="fabric-1.8 fabric-1.14 fabric-1.16 forge-1.12 forge-1.14 forge-1.15 forge-1.16 rift-1.13"
old="forge-1.7 forge-1.8"
new="fabric-1.17"
# these ones can't be built in parallel
special="nfc forge-1.4 forge-1.5"

rm -f artifacts/*
(
	echo 'Building common...'
	cd common
	JAVA_HOME=$JAVA8_HOME TERM=dumb chronic ./gradlew clean build teavm
	# The broken hacked up version of ForgeGradle used by Rift is looking for the common jar here
	cp build/libs/ears-common.jar build/libs/common.jar
	# 1.7 uses an old version of gradle that expects slightly different artifacts
	cd ../forge-1.7
	JAVA_HOME=$JAVA8_HOME TERM=dumb chronic ./gradlew common:build
)
build() {
	for proj in $@; do
		(
			cd $proj
			rm -f build-ok
			# the gradles will race on constantly rebuilding common if not forced not to
			TERM=dumb chronic ./gradlew clean build -x common:build -x common:clean -x common:compileJava -x common:processResources -x common:classes -x common:jar && touch build-ok
			rm -f build/libs/*-dev.jar
		) &
	done
}
echo 'Building everything...'
build $all
JAVA_HOME=$JAVA8_HOME build $old
JAVA_HOME=$JAVA16_HOME build $new
wait
for proj in $special; do
	(
		cd $proj
		rm -f build-ok
		TERM=dumb chronic ./gradlew clean build && touch build-ok
		rm -f build/libs/*-dev.jar
	)
done
exit=
for proj in $all $old $new $special; do
	if [ ! -e "$proj/build-ok" ]; then
		echo "Build failure in $proj."
		exit=y
	fi
	rm -f "$proj/build-ok"
done
if [ "$exit" == "y" ]; then
	echo "Exiting due to build failures."
	exit 1
fi
echo 'All builds completed successfully.'
mkdir -p artifacts
cp */build/libs/* artifacts
rm -f artifacts/*-sources{,-dev}.jar artifacts/common.jar
if [ -n "$1" ]; then
	echo "Proceeding to publish..."
	./publish.sh $1
	echo 'All OK.'
else
	echo "Not publishing as no Curse API key was specified."
fi
