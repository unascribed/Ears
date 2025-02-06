#!/bin/bash -e

if [[ "$(uname -s)" =~ ^CYGWIN || "$(uname -s)" =~ ^MINGW || "$(uname -s)" =~ ^MSYS ]]; then
	echo "WARNING: Building Ears on Windows, even under a Linux-like environment, is not supported. Continue at your own peril."
	sleep 3
fi

needShift=
for j in 8 11 17; do
	if [[ "$1" == "--download" && ! -e ".java/$j/bin/java" ]]; then
		needShift=1
		arch=$(uname -m)
		case $arch in
			x86_64) arch=x64 ;;
			i386) arch=x86 ;;
			i686) arch=x86 ;;
			armv7*) arch=arm ;;
			armv8*) arch=aarch64 ;;
			aarch64*) arch=aarch64 ;;
			ppc*le) arch=ppc64le ;;
			ppc*) arch=ppc64 ;;
			s390*) arch=s390x ;;
			riscv*) arch=riscv64 ;;
			*) echo "Unknown architecture; please download Java yourself" && exit 2 ;;
		esac
		mkdir -p .java/tmp
		echo "Downloading Eclipse Temurin $j..."
		curl -L "https://api.adoptium.net/v3/binary/latest/$j/ga/linux/$arch/jdk/hotspot/normal/eclipse?project=jdk" -o .java/tmp/$j.tar.gz || (echo "Failed to download Java $j for $arch" && exit 1)
		rm -rf .java/tmp/$j .java/$j
		mkdir -p .java/tmp/$j
		cd .java/tmp/$j
		echo "Extracting Eclipse Temurin $j..."
		tar xf ../$j.tar.gz || (echo "Failed to extract Java $j" && exit 1)
		mv * ../../$j
		cd ../../..
		rm -rf .java/tmp
	fi
	if [ -d ".java/$j" ]; then
		export JAVA${j}_HOME=$(pwd)/.java/$j
	fi
done

if [ -d ".java/17" ]; then
	export JAVA16_HOME=$JAVA17_HOME
fi

if [ "$needShift" == 1 ]; then
	shift
fi

if [[ -z "$JAVA8_HOME" || -z "$JAVA11_HOME" || -z "$JAVA17_HOME" ]]; then
	echo "Building Ears requires Java 8, Java 11, and Java 17." 1>&2
	echo "Please install them and set the JAVA8_HOME, JAVA11_HOME, and JAVA17_HOME env vars." 1>&2
	echo "You can get all three of these from https://adoptium.net/" 1>&2
	echo "Alternatively, run this script again with --download as the first argument to do it for you. (This will only work on Linux, and will download and execute binaries from adoptium.net.)" 1>&2
	exit 1
fi

if [ -n "$JAVA11_HOME" ]; then
	export JAVA_HOME=$JAVA11_HOME
fi
if [ -z "$JAVA16_HOME" ]; then
	export JAVA16_HOME=$JAVA17_HOME
fi

check_java() {
	v=$(env -u_JAVA_OPTIONS $JAVA_HOME/bin/java -Xint -version 2>&1 | head -n1 |cut -d\" -f2)
	query=$1
	regex=$2
	if [ -n "$3" ]; then
		query="$2 or $3"
		regex="$2|$3"
	fi
	highlighted=$(echo $v |grep -E --color=always "^($regex)" || (echo "Expected JAVA$1_HOME to point to Java $query, but instead it points at Java $v. Stop" 1>&2 && exit 2))
	color=32
	if [ -n "$3" ]; then
		echo $v |grep -q "^$3" >/dev/null && color=33
	fi
	highlighted=$(echo $highlighted |sed "s/31/$color/" |sed 's/1.8/ 8/')
	echo "Java $1: $highlighted"
}

JAVA_HOME=$JAVA8_HOME check_java 8 1.8
check_java 11 11
JAVA_HOME=$JAVA16_HOME check_java 16 16 17
JAVA_HOME=$JAVA17_HOME check_java 17 17
echo "Looks good."
echo

normal="fabric-1.14 fabric-1.16 forge-1.12 forge-1.14 forge-1.15 forge-1.16 fabric-b1.7.3 rift-1.13"
needsJ8="forge-1.6 forge-1.7 forge-1.8 forge-1.9"
needsJ16="fabric-1.17 forge-1.17"
needsJ17="forge-1.18 fabric-1.19 forge-1.19 fabric-1.19.3 forge-1.19.3 fabric-1.19.4 forge-1.19.4 fabric-1.20 fabric-1.20.2 neoforge-1.20.2 stapi-b1.7.3"
# these ones can't be built in parallel (or build so quickly that we shouldn't bother)
special="nfc nsss vanilla-b1.7.3 forge-1.2 forge-1.4 forge-1.5"

buildAll=1
toBuild=" $normal $needsJ8 $needsJ16 $needsJ17 $special "
if [ -n "$1" ]; then
	buildAll=0
	toBuild=" $@ "
fi

for proj in $toBuild; do
	rm -f artifacts/ears-$proj*
done
(
	echo 'Building common...'
	cd common
	#if [ "$buildAll" == "1" ]; then
	#	addn=closure
	#fi
	JAVA_HOME=$JAVA8_HOME TERM=dumb chronic ./gradlew clean build $addn --stacktrace
)
build() {
	for proj in $@; do
		if echo "$toBuild" | grep -qF " $proj "; then
			(
				cd platform-$proj
				rm -f build-ok
				TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok && echo "Built $proj successfully"
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
JAVA_HOME=$JAVA17_HOME build $needsJ17
wait
for proj in $special; do
	if echo "$toBuild" | grep -qF "$proj"; then
		(
			cd platform-$proj
			rm -f build-ok
			TERM=dumb chronic ./gradlew clean build --stacktrace && touch build-ok && echo "Built $proj successfully"
		)
	fi
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
