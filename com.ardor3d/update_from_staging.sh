#!/bin/sh

NEWBUILD=0.8-20120331.000142-456
OLDBUILD=0.8-20111229.184150-378

STAGING="../com.ardor3d.staging"

COMPONENTS="\
ardor3d-animation \
ardor3d-awt       \
ardor3d-collada   \
ardor3d-core      \
ardor3d-effects   \
ardor3d-extras    \
ardor3d-jogl      \
ardor3d-lwjgl     \
ardor3d-swt       \
ardor3d-terrain   \
ardor3d-ui        \
"

echo ""
echo "=== Pull jars from ${STAGING} ======="
for COMPONENT in ${COMPONENTS}; do
  echo "-- pull ${COMPONENT}..."
  JAR=${COMPONENT}-${NEWBUILD}.jar
  SRC=${COMPONENT}-${NEWBUILD}-sources.jar
  cp -f ${STAGING}/${JAR} .
  cp -f ${STAGING}/${SRC} .
done 


echo ""
echo "=== Search and Replace =========================="
FILES=".classpath build.properties META-INF/MANIFEST.MF"
for FILE in ${FILES}; do
  COMMAND="sed -i s/${OLDBUILD}/${NEWBUILD}/g ${FILE}"
  echo ${COMMAND}
  ${COMMAND}
done

echo ""
echo "=== Cleanup ====================================="
for COMPONENT in ${COMPONENTS}; do
  echo "-- clean old ${COMPONENT}..."
  JAR=${COMPONENT}-${OLDBUILD}.jar
  SRC=${COMPONENT}-${OLDBUILD}-sources.jar
  rm -f ${JAR}
  rm -f ${SRC}
done 
echo "================================================="

echo ""
echo ""
echo "**************************"
echo "*** REQUIRED FOLLOW UP ***"
echo "**************************"
echo " 1. update Bundle-Version in MANIFEST.MF"
echo " 2. update Export-Package in MANIFEST.MF"
