#JBoss configuration

##JMX-Console and administration console security

When a JBoss server is launched, a JMX-console and an administration web console are part of the
deployed applications. By default, these can be accessed without passwords.

This is ok for development but the JMX console can be used to issue various commands, shut down the
server, and many other things which should not be available in a production environment. The server
administration console can be used to deploy or undeploy applications and other important
actions. Neither should be visible to web visitors. Therefore, they should be secured in a
production environment.

The consoles are ordinary web applications, so they can be secured in the same way any other Tomcat
deployed web application is secured.

1.  Edit `server/default/deploy/management/console-mgr.sar/web-console.war/WEB-INF/web.xml` and
    `server/default/deploy/jmx-console.war/WEB-INF/web.xml`: uncomment the security-constraint.

1.  Edit `server/default/deploy/management/console-mgr.sar/web-console.war/WEB-INF/jboss-web.xml` and
    `server/default/deploy/jmx-console.war/WEB-INF/jboss-web.xml`: uncomment the security-domain
    section.

1.  Set passwords for the JMX console:

 1. Edit `server/default/conf/props/jmx-console-users.properties` and add `username=password` pairs.

 1. Edit `jmx-console-roles.properties` to assign the necessary roles to those users. In particular,
 the JBossAdmin role is the role to edit for the JMX console.

1.  Set passwords for the administration web console:

 1. Edit `server/default/deploy/management/console-mgr.sar/web-console.war/WEB-INF/classes/web-console-users.properties`.

 1. Edit `web-console-roles.properties` in the same way to secure the web console.

Accessing https://localhost:8443/jmx-console/ and https://localhost:8443/web-console/ should now
ask for identification.

##Use IP tables to route traffic on port 443 to 8443

1.  Assumes empty iptables, check with `sudo iptables-save`.

1.  Edit /etc/init.d/iptables and add the following at the end of the file:

    ```
    # Redirect HTTPS traffic to port 844
    iptables -t nat -A OUTPUT -p tcp --dport 443 -j REDIRECT --to-ports 8443
    iptables -t nat -A OUTPUT -p tcp --dport 443 -j REDIRECT --to-ports 8443
    iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-ports 8443
    ```

1.  You can add a destination address by adding `-d xxx.xxx.xxx.xxxx` to the lines given
    above. `xxx.xxx.xxx.xxx` would be the IP address of the server.

1.  Restart computer to test.

##Disable the deployment scanner

The default configuration enables the deployment scanner. JBoss scans the deploy directory to load or
unload archives. This is useful in development, but it is using CPU to do that. In production
systems, it should be disabled (the jmx-console instead can be used for deployment).

To disable the deployment scanner, open `<jboss_folder>/server/default/conf/jboss-service.xml` and replace

```
<attribute name="ScanEnabled">true/attribute>
```

with

```
<attribute name="ScanEnabled">false</attribute>
```

##Modify Permanent Generation space maximum size

To avoid having JBoss run out of memory (`java.lang.OutOfMemoryError: PermGen space`) do the
following:

1.  Edit `<jboss folder>/bin/run.conf` file.

1.  Find the line

    ```
    JAVA_OPTS="-Xms128m -Xmx512m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000"
    ```
1.  Add `-XX:MaxPermSize=128m`:

    ```
    JAVA_OPTS="-Xms128m -Xmx512m -XX:MaxPermSize=128m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000"
    ```

1. Restart JBoss.

1.  To check if the modification is enabled:

 1. Go to https://localhost:8443/web-console/
 1. Open the tree to `System / JMX MBeans / jboss.system / jboss.system:type=ServerInfo`
 1. In the right hand side of the page find the first occurrence of `java.lang.String listMemoryPools()`
 1. Press the `Invoke` button.
 1. A new page is displayed with the Permgen info on the bottom

##HOWTO Setup JBoss AS to Run as a Service

Assumes `$JBOSS_HOME` is where JBoss AS is installed to and that user account `jboss` has already
been created.

1.  Create symbolic link from `/usr/local/jboss` to `$JBOSS_HOME`.

    ```bash
    $ ln -s $JBOSS_HOME /usr/local/jboss
    ```

1.  Change user and group ownership to jboss.

    ```bash
    $ chown jboss:jboss /usr/local/jboss
    $ chown -R jboss:jboss $JBOSS_HOME
    ```

1.  Get JBoss AS init script.

    ```bash
    $ wget http://chiralsoftware.com/linux-system-administration/jboss-deployment/jboss-init.sh
    ```

1.  Move script to `/etc/init.d`, change ownership, and change permissions.

    ```bash
    $ mv jboss-init.sh /etc/init.d/jboss
    $ chown root:root /etc/init.d/jboss
    $ chmod a+rwx /etc/init.d/jboss
    ```

1.  Add JBoss AS to init system

    ```bash
    $ update-rc.d jboss defaults
    ```

1.  Test

    ```bash
    $ /etc/init.d/jboss start
    $ /etc/init.d/jboss stop
    ```

##Timezone setting

Add the following to the `JAVA_OPTS` variable `-Duser.timezone=GMT` to `$JBOSS_HOME/bin/run.sh`.

****

[Back to parent document](development_environment.md)

[Back to top](../README.md)
