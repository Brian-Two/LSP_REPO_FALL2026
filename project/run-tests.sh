#!/usr/bin/env sh
# Compile only the capstone and run its JUnit 5 suite. Run from any directory.
set -eu
REPO_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$REPO_ROOT"
JUNIT_VERSION=1.11.4
JUNIT_SHA256=b016ef6b1c3454d6d7c2c88ce081dabf289699686af6622d6e4e2e1b54b4a2fc
JUNIT_JAR=${JUNIT_JAR:-"$REPO_ROOT/out/project-tools/junit-platform-console-standalone-$JUNIT_VERSION.jar"}
mkdir -p out/project-tools out/project/main out/project/test out/project/reports
if [ ! -f "$JUNIT_JAR" ]; then
    curl -fL "https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar" -o "$JUNIT_JAR"
fi
if command -v sha256sum >/dev/null 2>&1; then
    ACTUAL_SHA256=$(sha256sum "$JUNIT_JAR" | cut -d ' ' -f 1)
else
    ACTUAL_SHA256=$(shasum -a 256 "$JUNIT_JAR" | cut -d ' ' -f 1)
fi
if [ "$ACTUAL_SHA256" != "$JUNIT_SHA256" ]; then
    echo 'JUnit checksum mismatch; remove the cached JAR and retry.' >&2
    exit 1
fi
# Remove stale project classes so deleted tests or classes cannot survive a build.
find out/project/main out/project/test -name '*.class' -delete
find src/org/howard/edu/lsp/project -name '*.java' | sort > out/project/main-sources.txt
find test/org/howard/edu/lsp/project -name '*.java' | sort > out/project/test-sources.txt
javac --release 17 -Xlint:all -Werror -d out/project/main @out/project/main-sources.txt
javac --release 17 -Xlint:all -Werror -cp "$JUNIT_JAR:out/project/main" -d out/project/test @out/project/test-sources.txt
java -jar "$JUNIT_JAR" execute --class-path out/project/main:out/project/test \
    --select-package org.howard.edu.lsp.project --fail-if-no-tests \
    --reports-dir out/project/reports --details summary
