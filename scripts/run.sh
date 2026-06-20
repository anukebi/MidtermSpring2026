#!/usr/bin/env sh
set -eu

./mvnw -q clean compile exec:java -Dexec.args="$"

