#!/bin/bash

# This script copies the Biobank files that are required to run the web application to the releveant JBoss folders

cp ../security-logs/jboss/login-config.xml jboss-4.0.5.GA/server/default/conf/
cp ../security-logs/jboss/server.xml jboss-4.0.5.GA/server/default/deploy/jbossweb-tomcat55.sar/server.xml
cp ../security-logs/jboss/login-config.xml jboss-4.0.5.GA/server/default/conf/
cp ../lib/build/mysql-connector-java.jar jboss-4.0.5.GA/server/default/lib/
cp ../security-logs/jboss/upt-ds.xml jboss-4.0.5.GA/server/default/deploy/
cp ../security-logs/jboss/biobank-ds.xml jboss-4.0.5.GA/server/default/deploy
