#!/usr/bin/env sh
set -eu

./mvnw -q clean compile spring-boot:run -Dspring-boot.run.arguments="$*"

