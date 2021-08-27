#!/bin/bash -e
mkdir -p build/tmp/mcp
cd build/tmp/mcp
unzip -o ../../../mcp62.zip conf/methods.csv
dos2unix conf/methods.csv
patch -N conf/methods.csv ../../../methods.csv.patch
unix2dos conf/methods.csv
zip ../../../mcp62.zip conf/methods.csv
