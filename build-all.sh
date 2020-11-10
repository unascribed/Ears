#!/bin/bash

rm artifacts/*
for proj in fabric-1.14 fabric-1.16 forge-1.8 forge-1.12 forge-1.14 forge-1.15 forge-1.16; do
	(
		cd $proj
		TERM=dumb chronic ./gradlew clean build
	) &
done
wait
mkdir -p artifacts
cp */build/libs/* artifacts
rm artifacts/*-sources{,-dev}.jar
