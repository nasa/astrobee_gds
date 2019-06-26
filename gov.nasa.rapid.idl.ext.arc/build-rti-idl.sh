#!/bin/bash

if [ "${NDDSHOME}" == "" ]; then
  echo "ERROR: NDDSHOME environment variable must be set before running this script"
  exit
fi

RTIDDSGEN=""
# NOTE: use rtiddsgen instead of rtiddsgen_server because of race conditions when cd'ing
RTIDDSGEN_OPTS="\
${NDDSHOME}/scripts/rtiddsgen \
${NDDSHOME}/bin/rtiddsgen \
"
for RTIDDSGEN_OPT in ${RTIDDSGEN_OPTS}; do
	if [ -x ${RTIDDSGEN_OPT} ]; then
	  export RTIDDSGEN=${RTIDDSGEN_OPT}
	fi
done 

if [ -x ${RTIDDSGEN} ]; then
  echo "==="
  echo "=== using ${RTIDDSGEN}"
  echo "==="
else
  echo "${RTIDDSGEN} is not executable"
  exit
fi

rm -rfv generated-src/*

COREDIR="`pwd`/../gov.nasa.rapid.idl.dds/src-idl"

pushd src-idl

BASEDIR=`pwd`

DIRS="rapidExtArcDds"

for DIR in ${DIRS}; do 

  echo "==="
  echo "=== Generating source in ${DIR}"
  echo "==="
  
  pushd ${DIR}
  
  FILES=`ls *.idl`
  
  OPTS="-language Java \
        -I ${COREDIR}/rapidDds \
        -I ${COREDIR}/rapidExtDds \
        -I ${BASEDIR}/${DIR} \
        -replace \
        -d ${BASEDIR}/../generated-src"
  
  for FILE in ${FILES}; do
    echo "--> Generating code for ${FILE}:"
    ${RTIDDSGEN} ${OPTS} ${FILE}
  done

  popd
  
done

popd
