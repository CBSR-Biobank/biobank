#!/bin/sh

MYSQL="mysql -u dummy -pozzy498 biobank2"
$MYSQL -BNe "show tables" |awk {'print "set foreign_key_checks=0; drop table `"$1"`;"}' | $MYSQL
unset MYSQL
