# MySQL server configuration for Biobank

1. Stop the mysql server if it is already running:

    ```bash
	sudo service mysql stop
	```

1. MySQL requires the following setting in `/etc/mysql/my.cnf`: In section `[mysqld]` the following
   line should be added:

    ```
    lower_case_table_names=1
	sql_mode=NO_ENGINE_SUBSTITUTION
    default-storage-engine=InnoDB
    innodb_flush_log_at_trx_commit=1
    innodb_buffer_pool_size=1024M
    innodb_log_file_size=256M
    innodb_log_buffer_size=16M
    innodb_additional_mem_pool_size=8M
    ```

1.  Remove the old server log files `/var/lib/mysql/b_logfile0` and `/var/lib/mysql/b_logfile1`.

1.  Start the mysql server:

    ```bash
	sudo service mysql start
	```

If the MySQL server doesn't start, you can see the errors of your mysql server in `/var/log/mysql/error.log`.

****

[Back to parent document](server_installation.md)

[Back to top](../README.md)
