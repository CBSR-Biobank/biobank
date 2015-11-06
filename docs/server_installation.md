#Biobank server installation

The server can be deployed on computers running Linux or MS Windows. However, up to now the server
has only been tested and installed on computers running Linux. The Biobank server is distributed as
an Ubuntu package or as a [tar archive](http://en.wikipedia.org/wiki/Tar_(computing)). It should be
installed under a special user account for better security.

The Biobank server runs as an application under JBoss (version 4.0.5 GA). Some precautions, listed
below, should be taken when running the server.

##Download

The most recent version can be downloaded from here:

* [Biobank server v3.10.0 Ubuntu package](http://aicml-med.cs.ualberta.ca/CBSR/Biobank_v3.10.0/biobank-server-3.10.0.deb)

##Requirements

A minimum of 2 GB or RAM memory is required to run the server. Up to now the server has been
installed on stand alone servers and also on
[Virtual Machines](http://en.wikipedia.org/wiki/Virtual_machine).

Prior to deploying a server, the following software packages have to be installed:

1.  Oracle (Sun) Java SE Development Kit 6. Please see the instructions given below on how to
    configure Java on your Ubuntu. For other distributions please refer to it's
    documentation. Please do not use OpenJDK as the application will not work correctly.

1.  MySQL Server version 5.1 or later.

1.  Perl.

1.  Zip.

1.  Unzip.

1.  OpenSSL - This is optional and is only required to create a temporary certificate. If you have
    your own certificate then this package is not requried.


##Installation

1.  Install Java by following the instructions on this page: [Installing Java](java_install.md).

1.  Install the required packages:

    ```bash
	sudo apt-get install perl libterm-readkey-perl zip unzip openssl
	```

1.  For security reasons, it is better to run JBoss as a non root user.  To do this, create a jboss user account:

    ```bash
    sudo useradd --system -d /opt/jboss -s /bin/bash jboss
	```

    Please use `/opt/jboss` as the home directory since the prebuilt package installs the files to
    this directory.

1.  Install the Biobank server package:

    ```bash
	sudo dpkg -i biobank-server-<version>.deb
	```

    where `<version>` matches the version of the file you downloaded.

    After the command completes your `/opt/jboss` directory will be populated with new files. Some
    of these files are scripts that need to be run to configure the server for your
    installation. More details are given below.

1.  Move the JBoss startup script so that the server can be started as a service.

    ```bash
	sudo mv jboss-init.sh /etc/init.d/jboss
    sudo chown root:root /etc/init.d/jboss
    sudo chmod a+rwx /etc/init.d/jboss
	```

1.  Add the server to the init system:

    ```bash
    sudo update-rc.d jboss defaults
    ```

1.  Configure the MySQL server by following the instructions on this page:
    [MySQL configuration](mysql_configuration.md).

1.  Create a database on the MySQL server to be used by the Biobank server and create a user to
    access to the database. Grant all privileges on the biobank database to this user.

    For example, these commands create the database named `biobank`. Start the MySQL command line
    tool with this command:

    ```bash
	mysql -h <hostname> -uroot -p<password> mysql
	```

    and create the database and add a user that can access the database.

    ```bash
    CREATE DATABASE biobank;
	CREATE USER 'XXXXX'@'localhost' IDENTIFIED BY 'ZZZZZ';
    GRANT ALL PRIVILEGES ON biobank.* TO 'XXXXX'@'localhost' WITH GRANT OPTION;
	```

    `XXXXX` and `ZZZZZ` should be replaced with the username and password you would like to use in
    your configuration.

1.  To create an SSL certificate see these instructions:
    [SSL Certificate](server_ssl_certificate.md). To create a temporary certificate for testing see
    here: [Temporary SSL Certificate](server_ssl_temporary.md).

1.  Run the `/opt/jboss/configure` and you will be prompted with values to configure your
    server. You will be asked for the following:

 1. The host name for the MySQL server.

 1. The name of the database to be used by the Biobank server on the MySQL server. In the example
    given above the name of the database was `biobank`.

 1. The user name the Biobank server should use to communicate with the MySQL server.

 1. The password for the user name.

 1. The password for the Jboss Web Console and JMX Console. They will both use the same password.

 1. If the database has not been initialized you will prompted to do so.

1.  Run the `/opt/jboss/chkconfig` script to test your configuration. The script runs a series of
    tests and outputs the results. Ensure all the tests pass. Once all the tests pass you can start
    the server.

1.  Install and configure the Apache Web Server to front JBoss using
    [these instructions](apache_fronting_jboss.md).

Start the biobank server with the command:

```bash
sudo /etc/init.d/jboss start
```

You can test that the server is running by opening the following URL in your browser:

```bash
https://_IP_or_DN_/biobank
```

Where `_IP_or_DN_` is the IP address or domain name for your server. You should see a web page
similar to the one shown below:

![Biobank Server Web Page](images/biobank_server_web_page.jpg?raw=true "Biobank Server Web Page")

You can log into the **JMX Console** and **JBoss Web Console** by opening this URL in your browser:

```bash
https://_IP_or_DN_/
```

and selecting the appropriate link. The user name is **admin** and the password is what was entered
into the `configure` script when it was run.

****

[Back to top](../README.md)
