#!/bin/bash
sed -Ei 's@/\*VERSION\*/.*/\*/VERSION\*/@/*VERSION*/"'$(cat ../version.txt)$(cat ./version-suffix.txt)'"/*/VERSION*/@' $@
