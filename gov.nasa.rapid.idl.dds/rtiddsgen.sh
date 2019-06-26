#!/bin/tcsh

# Generate code from IDL files

setenv NDDSHOME /opt/rti/ndds.4.5d
setenv XALANHOME /Users/dmittman/Development/xalan-j_2_7_1
setenv NDDSJREHOME /Library/Java/Home

find generated-src -name "*.java" -exec rm {} \;
find generated_src -name "*.{cxx,h}" -exec rm {} \;

pushd src-idl

foreach file (`ls *.idl`)
  $NDDSHOME/scripts/rtiddsgen -d ../generated-src -language Java -package gov.nasa.rapid.idl.dds -replace $file
end

foreach file (`ls *.idl`)
  $NDDSHOME/scripts/rtiddsgen -d ../generated_src -replace -namespace -language C++ $file
end

popd

# End
