#!/bin/bash

# find the script folder
SCRIPT_DIR=$(dirname "$0")

# go to the parent folder
cd "${SCRIPT_DIR}/.."

# clear old docs
rm -rf docs/javadoc

# generate new docs
find src/main/java/io/github/f4b6a3/ulid/ -name "*.java" | xargs javadoc -d docs/javadoc

