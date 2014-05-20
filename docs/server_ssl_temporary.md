## Temporary SSL Certificate

If you want to use a temporary certificate for testing, run the `/opt/jboss/keystore-gen`
script. You will be prompted for a value for subject alternative names. By default a value like
`IP=x,DNS=y` is used, and y is often `localhost`. The `DNS=y` value is used for hostname
verification, and you should only enter a value here if you can connect to the server using that
DNS. If any DNS value is entered, then it must match the host you are connecting to through
biobank. So, if the client and the server are on the same machine, it is probably fine to have
`CN=localhost` and `IP:<yourip>,DNS:localhost`. However, a client on a different machine will not be
able to connect via `localhost` so, in that case, you should enter values similar to:
`CN=biobank.mydomain.org` and `IP:<ip>,DNS:biobank.mydomain.org` and connect via either IP address
or `biobank.mydomain.org`.


****

[Parent Document](server_installation.md)

[Back to top](../README.md)
