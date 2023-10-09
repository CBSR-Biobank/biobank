#!/bin/bash
set -x
set -e

gzip -dc /db_initial.sql.gz | mariadb -uroot -p${MARIADB_ROOT_PASSWORD} ${MARIADB_DATABASE}
