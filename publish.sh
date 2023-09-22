#!/bin/bash -e
if [ -z "$1" -o -z "$2" -o -z "$3" ]; then
	echo "Need a Curse API key, Modrinth API key, and mcmod.cn cookie to publish."
	exit 2
fi
if [ ! -s "changelog.html" ]; then
	echo "Changelog is empty; refusing to publish."
	exit 1
fi

CURSE_TOKEN=$1
shift
MODRINTH_TOKEN=$1
shift
MCMODCN_COOKIE=$1
shift

if [ -n "$JAVA11_HOME" ]; then
	export JAVA_HOME=$JAVA11_HOME
fi

if [ -n "$1" ]; then
	common="$@"
	curse="$common"
	modrinth="$common"
	mcmodcn="$common"
else
	common="forge-1.2 forge-1.4 forge-1.5 forge-1.6 forge-1.7 forge-1.8 forge-1.9 forge-1.12 rift-1.13 fabric-1.14 forge-1.14 forge-1.15 fabric-1.16 forge-1.16 fabric-1.17 forge-1.17 forge-1.18 fabric-1.19 forge-1.19 fabric-1.19.3 forge-1.19.3 fabric-1.19.4 forge-1.19.4 fabric-1.20 fabric-1.20.2"
	curse="$common"
	modrinth="fabric-b1.7.3 $common"
	mcmodcn="fabric-b1.7.3 $common"
fi

cd publish-curseforge
if [ "$CURSE_TOKEN" != "-" ]; then
	for proj in $curse; do
		echo Publishing $proj to CurseForge...
		TERM=dumb chronic ./gradlew -PcurseApiKey=$CURSE_TOKEN -Ptarget=$proj curseforge
	done
fi
cd ../publish-modrinth
if [ "$MODRINTH_TOKEN" != "-" ]; then
	for proj in $modrinth; do
		echo Publishing $proj to Modrinth...
		TERM=dumb chronic ./gradlew -PmodrinthApiKey=$MODRINTH_TOKEN -Ptarget=$proj modrinth
	done
fi
cd ..
if [ "$MCMODCN_COOKIE" != "-" ]; then
	export MCMODCN_COOKIE
	classID=3996
	forge=1
	fabric=2
	quilt=11
	rift=3
	other=10
	for proj in $mcmodcn; do
		title=""
		loaders=""
		case $proj in
			fabric-b1.7.3)
				title="Beta 1.7.3"
				loaders="$fabric"
			;;
			forge-1.2)
				title="1.2.5"
				loaders="$other"
			;;
			forge-1.4)
				title="1.4.7"
				loaders="$forge"
			;;
			forge-1.5)
				title="1.5.2"
				loaders="$forge"
			;;
			forge-1.6)
				title="1.6.4"
				loaders="$forge"
			;;
			forge-1.7)
				title="1.7.10"
				loaders="$forge"
			;;
			forge-1.8)
				title="1.8"
				loaders="$forge"
			;;
			forge-1.9)
				title="1.9/10/11"
				loaders="$forge"
			;;
			forge-1.12)
				title="1.12"
				loaders="$forge"
			;;
			rift-1.13)
				title="1.13.2"
				loaders="$rift"
			;;
			fabric-1.14)
				title="1.14"
				loaders="$fabric"
			;;
			forge-1.14)
				title="1.14"
				loaders="$forge"
			;;
			forge-1.15)
				title="1.15"
				loaders="$forge"
			;;
			fabric-1.16)
				title="1.15/16"
				loaders="$fabric"
			;;
			forge-1.16)
				title="1.16"
				loaders="$forge"
			;;
			fabric-1.17)
				title="1.17/18"
				loaders="$fabric,$quilt"
			;;
			forge-1.17)
				title="1.17"
				loaders="$forge"
			;;
			forge-1.18)
				title="1.18"
				loaders="$forge"
			;;
			fabric-1.19)
				title="1.19"
				loaders="$fabric,$quilt"
			;;
			forge-1.19)
				title="1.19"
				loaders="$forge"
			;;
			fabric-1.19.3)
				title="1.19.3"
				loaders="$fabric,$quilt"
			;;
			fabric-1.19.4)
				title="1.19.4"
				loaders="$fabric,$quilt"
			;;
			forge-1.19.3)
				title="1.19.3"
				loaders="$forge"
			;;
			forge-1.19.4)
				title="1.19.4"
				loaders="$forge"
			;;
			fabric-1.20)
				title="1.20"
				loaders="$fabric,$quilt"
			;;
			fabric-1.20.2)
				title="1.20.2"
				loaders="$fabric,$quilt"
			;;
			*)
				echo "Unknown project $proj for mcmod.cn publish"
				exit 2
			;;
		esac
		./mcmodcn-upload.sh $classID "$title" "$loaders" 'client' artifacts/ears-$proj-*.jar
	done
	export -n MCMODCN_COOKIE
fi
