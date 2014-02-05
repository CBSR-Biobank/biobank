# Biobank Server Maintenance

This feature allows IT personnel to enable or disable a mode on the server that prevent users from
logging into the server. Enabling this mode is useful when maintenance of the server is about to
start. When the maintenance mode is enabled, users who attempt to log in via the Biobank client will
be informed that the server is under maintenance and to log it at a later time. This feature will
not log out currently logged in users. Logged in users must be contacted individually and be
requested to log off (logged in users can be determined by looking at the logs on the client).

## Requirements

To modify the server's maintenance mode, IT personnel must provide a user name and password that has
already been added to the Biobank application. In addition, the user account must have **Global
Administrator** privileges. Global administrator privileges are users who have access to all sites,
all studies and also have every permission enabled.

## Changing maintenance mode

Changing the mode can done on any computer, connected to the internet, using a Linux shell in a
terminal.  At the project's root directory, use the following commands to query and change the mode:

1. To query the current mode:

    ```bash
    ant server-maintenance-mode-query
	```


2. To change the mode:

    ```bash
    ant server-maintenance-mode-toggle
    ```

Both of these commands request the following paramters:

1. The server's host name. This is the same as the server parameter used on the client's login
   dialog box. The default value for this parameter is `localhost`.

2. The server's port number. The default value is 433.

3. A user name. The user name must belong to a user with **Global Administrator** privileges. If the
   user does not have the correcte privileges the command will show an error message.

4. The user name's password. The password will not be displayed once it is typed.

*Note: the first time one of these commands is run, the SSL certificate may need updating. In this
case please repeat the command. If the SSL certificate require updating the command will display
this text:*

```
[java] SSL certificate updated. Run this tool again to connect to the specified server.
```

## Example usage

```bash
> ant server-maintenance-mode-query

...

server-maintenance-mode-query:
    [input] Enter host name [localhost]
aicml-med.cs.ualberta.ca
    [input] Enter host port [443]

    [input] Enter user name [testuser]

Enter user password []
     [java] Server maintenance mode is disabled
```


****

[Back to top](../README.md)
