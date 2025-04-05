#!/bin/bash
for f in *; do
	if [ -d "$f" ]; then
	(
		cd "$f"
		if [ -e gradlew ]; then
			# has to be hard-links, otherwise gradle tries to run in the root dir
			rm gradlew{,.bat} gradle/wrapper/gradle-wrapper.jar
			ln ../gradlew
			ln ../gradlew.bat
			cd gradle/wrapper
			ln ../../../gradle/wrapper/gradle-wrapper.jar
		fi
	)
	fi
done
