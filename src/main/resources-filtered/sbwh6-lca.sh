#!/bin/bash


if [ -z "$SBWH6_HOME" ]
then
    SBWH6_HOME=${INSTALL_PATH}
    echo "Default initial installation SBWH6_HOME will be used: $SBWH6_HOME"
else
   echo "SBWH6_HOME defined in the system environment, overriding installation SBWH6_HOME: $SBWH6_HOME"
fi

if [ -z "$SBWH6_CONF" ]
then
    SBWH6_CONF=${INSTALL_PATH}/conf
    echo "Default initial installation conf SBWH6_CONF will be used: $SBWH6_CONF"
else
   echo "SBWH6_CONF defined in the system environment, overriding installation SBWH6_CONF: $SBWH6_CONF"
fi
#to ensure export of all environment variables
set -a
source ${SBWH6_CONF}/sbwh6application.properties
echo "Welcome to $SBWH6_APP-$SBWH6_VERSION"

####
##Program main command
####
echo "Executing ${SBWH6_JRE_HOME}/bin/java -classpath .:${SBWH6_MVN_REPO}/lca-${SBWH6_VERSION}-lca.jar -Dlog4j.configuration=file:${SBWH6_MVN_REPO}/log4j.xml fr.cea.genoscope.sbwh6.lca.LcaClient $@"
${SBWH6_JRE_HOME}/bin/java -classpath .:${SBWH6_MVN_REPO}/lca-${SBWH6_VERSION}-lca.jar -Dlog4j.configuration=file:${SBWH6_MVN_REPO}/lca.log4j.xml fr.cea.genoscope.sbwh6.lca.LcaClient $@

#-Xmx1024m
