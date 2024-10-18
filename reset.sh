#!/bin/sh
./bdd.sh < drop_table.sql
./bdd.sh < create_table.sql
./bdd.sh < fill_db.sql
