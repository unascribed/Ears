#!/bin/bash -e

normal="fabric-1.8 fabric-1.14 fabric-1.16 forge-1.12 forge-1.14 forge-1.15 forge-1.16 fabric-b1.7.3 rift-1.13"
needsJ8="forge-1.6 forge-1.7 forge-1.8 forge-1.9"
needsJ16="fabric-1.17 forge-1.17"
# these ones can't be built in parallel
special="nfc nsss forge-1.2 forge-1.4 forge-1.5"

toBuild=" $normal $needsJ8 $needsJ16 $special "
if [ -n "$1" ]; then
	toBuild=" $@ "
fi

for proj in $toBuild; do
	rm -f artifacts/ears-$proj*
done
(
	echo 'Building common...'
	cd common
	JAVA_HOME=$JAVA8_HOME TERM=dumb chronic ./gradlew clean build closure --stacktrace
)
build() {
	for proj in $@; do
		if echo "$toBuild" | grep -qF "$proj"; then
			(
				cd platform-$proj
				rm -f build-ok
				TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok
				rm -f build/libs/*-dev.jar
			) &
		fi
	done
}
count=0
for proj in $toBuild; do
	count=$(expr $count + 1)
done
s=s
if [ $count -eq 1 ]; then
	s=
fi
echo "Building $count platform$s..."
build $normal
build $nobodyCares
JAVA_HOME=$JAVA8_HOME build $needsJ8
JAVA_HOME=$JAVA16_HOME build $needsJ16
wait
for proj in $special; do
	(
		cd platform-$proj
		rm -f build-ok
		TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok
	)
done
exit=
for proj in $toBuild; do
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
for proj in $toBuild; do
	cp platform-$proj/build/libs/* artifacts
done
rm -f artifacts/*-dev.jar artifacts/*-sources.jar
