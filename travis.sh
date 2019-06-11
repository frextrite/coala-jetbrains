#!/bin/bash

set -euox pipefail

function logInformation() {
    echo " "
    echo "==================== $1 ===================="
    echo " "
}

logInformation 'Initializing Travis Script'

logInformation 'Checking Build Mode'

case "$BUILDMODE" in

CI)
    logInformation 'Building Plugin'
    ./gradlew clean buildPlugin check --stacktrace --debug
    ;;

*)
    logInformation 'Unsupported Build Mode'
    exit 1
    ;;

esac
