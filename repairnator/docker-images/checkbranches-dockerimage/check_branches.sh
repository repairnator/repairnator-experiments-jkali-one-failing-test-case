#!/usr/bin/env bash

if [ "$#" -ne 2 ]; then
    echo "Usage: ./check_branches.sh <github repository> <branch name> [--human-patch]"
    exit 2
fi


DOCKER_DEST=/tmp/result.txt
REPO=$1
BRANCH_NAME=$2
HUMAN_PATCH=$3

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

MAVEN_TEST_ARGS="-Denforcer.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true -DskipITs=true -Drat.skip=true -Dlicense.skip=true -Dfindbugs.skip=true -Dgpg.skip=true -Dskip.npm=true -Dskip.gulp=true -Dskip.bower=true"

echo "Treating branch $BRANCH_NAME"
mkdir repo && cd repo && git init
git remote add origin $REPO
git fetch origin $BRANCH_NAME
git checkout $BRANCH_NAME

bugCommitId=`git log --format=format:%H --grep="Bug commit"`
patchCommitId=`git log --format=format:%H --grep="Human patch"`

echo "Checking out the bug commit: $bugCommitId"
git log --format=%B -n 1 $bugCommitId

git checkout -q $bugCommitId

timeout 1800s mvn -q -B test -Dsurefire.printSummary=false $MAVEN_TEST_ARGS

status=$?
if [ "$status" -eq 0 ]; then
    >&2 echo -e "$RED Error while reproducing the bug for branch $BRANCH_NAME $NC (status = $status)"
    echo "$BRANCH_NAME [FAILURE] (bug reproduction - status = $status)" >> $DOCKER_DEST
    exit 2
elif [ "$status" -eq 124 ]; then
    >&2 echo -e "$RED Error while reproducing the bug for branch $BRANCH_NAME $NC"
    echo "$BRANCH_NAME [FAILURE] (bug reproduction timeout)" >> $DOCKER_DEST
    exit 2
fi

if [ $HUMAN_PATCH = "--human-patch" ]; then
    echo "Checking out the patch commit: $patchCommitId"
    git log --format=%B -n 1 $patchCommitId

    git checkout -q $patchCommitId

    timeout 1800s mvn -q -B test -Dsurefire.printSummary=false $MAVEN_TEST_ARGS

    status=$?
    if [ "$status" -eq 124 ]; then
        >&2 echo -e "$RED Error while reproducing the passing build for branch $BRANCH_NAME $NC"
        echo "$BRANCH_NAME [FAILURE] (patch reproduction timeout)" >> $DOCKER_DEST
        exit 2
    elif [ "$status" -ne 0 ]; then
        >&2 echo -e "$RED Error while reproducing the passing build for branch $BRANCH_NAME $NC (status = $status)"
        echo "$BRANCH_NAME [FAILURE] (patch reproduction - status = $status)" >> $DOCKER_DEST
        exit 2
    fi
fi

echo -e "$GREEN Branch $BRANCH_NAME OK $NC"
echo "$BRANCH_NAME [OK]" >> $DOCKER_DEST