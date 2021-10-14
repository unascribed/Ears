#!/bin/bash -e

all="fabric-1.8 fabric-1.14 fabric-1.16 forge-1.12 forge-1.14 forge-1.15 forge-1.16 fabric-b1.7.3"
old="forge-1.6 forge-1.7 forge-1.8"
new="fabric-1.17 forge-1.17"
nobodyCares="rift-1.13"
# these ones can't be built in parallel
special="nfc forge-1.2 forge-1.4 forge-1.5"

rm -f artifacts/*
(
	echo 'Building common...'
	cd common
	JAVA_HOME=$JAVA8_HOME TERM=dumb chronic ./gradlew clean build closure --stacktrace
)
build() {
	for proj in $@; do
		(
			cd platform-$proj
			rm -f build-ok
			TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok
			rm -f build/libs/*-dev.jar
		) &
	done
}
echo 'Building platforms...'
build $all
build $nobodyCares
JAVA_HOME=$JAVA8_HOME build $old
JAVA_HOME=$JAVA16_HOME build $new
wait
for proj in $special; do
	(
		cd platform-$proj
		rm -f build-ok
		TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok
		rm -f build/libs/*-dev.jar
	)
done
for proj in $nobodyCares; do
	if [ ! -e "platform-$proj/build-ok" ]; then
		echo "Build failure in platform $proj, but nobody cares."
	fi
	rm -f "platform-$proj/build-ok"
done
exit=
for proj in $all $old $new $special; do
	if [ ! -e "platform-$proj/build-ok" ]; then
		echo "Build failure in platform $proj."
		exit=y
	fi
	rm -f "platform-$proj/build-ok"
done
if [ "$exit" == "y" ]; then
	echo "Exiting due to build failures."
	exit 1
fi
echo 'All builds completed successfully.'
mkdir -p artifacts
cp platform-*/build/libs/* artifacts
rm -f artifacts/*-sources{,-dev}.jar
if [ -n "$1" ]; then
	echo "Proceeding to publish..."
	./publish.sh "$@"
	echo 'All OK.'
else
	echo "Not publishing as no Curse API key was specified."
fi
