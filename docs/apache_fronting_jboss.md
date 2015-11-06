# Apache fronting JBoss

These instructions allow for the Apache Web server to front the Web
Application running under JBoss.

## Installation instructions

1.  *Install Apache*: in Ubuntu execute the following command on the
    command line:

    ```bash
    sudo apt-get update
    sudo apt-get install apache2
    ```

1.  *Install mod_jk*: To install @mod_jk@ in Ubuntu execute the
    following command on the command line:

    ```bash
    sudo apt-get install libapache2-mod-jk
    ```

    This wil also enable the module.

1.  Ensure the following settings are enabled in
    `/etc/apache2/mods-enabled/jk.conf`:

    ```bash
    JkWorkersFile /etc/libapache2-mod-jk/workers.properties
    JkLogFile /var/log/apache2/mod_jk.log
    JkLogLevel info
    JkOptions +RejectUnsafeURI
    JkStripSession On
    ```

1.  *Configure the workers properties file*: Edit
    `/etc/apache2/mods-enabled/jk.conf` and Set `workers.tomcat_home`
    to point to the Biobank Jboss directory.

    ```bash
    workers.tomcat_home=/data/jboss/jboss-4.0.5.GA
    ```

    Set `workers.java_home` to point to your Java JRE.

    ```bash
    workers.java_home=/data/java-6-oracle
    ```

1. *Enable SSL connections on Apache server*:

    ```bash
    sudo a2ensite default-ssl.conf
    ```

    Enable the `ssl` module on Apache:

    ```bash
    a2enmod ssl
    ```

1. *Set server name*: Edit the SSL configuration file:

    ```bash
    ServerName __your_server_name_here__
    ServerAlias __your_alias_here__
    ```

    And add the following line in the `<VirtualHost>` section:

    ```bash
    JkMount /biobank* ajp13_worker
    ```

1. *Optional*: create a self signed certificate. Use
   [these instructions](https://www.digitalocean.com/community/tutorials/how-to-create-a-ssl-certificate-on-apache-for-ubuntu-12-04).

1.  *Restart*: restart the appache server and start the Biobank JBoss
    server:

    ```bash
    sudo service apache2 reload
    /etc/init.d/jboss start
    ```

For more SSL certificate info see `/usr/share/doc/apache2/README.Debian.gz`.
