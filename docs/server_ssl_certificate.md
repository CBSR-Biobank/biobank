# SSL Certificate Installation

Use these instructions to create a keystore file and import an SSL certificate into it.

1.  Create a keystore:

    ```sh
    keytool -genkey -keyalg RSA -keysize 2048 -alias <server_domain_name> -keystore biobank.keystore
    ```

    Replace `<server_domain_name>` with the host name of your server.

    **Use `biobank2` as the password. Use the same password for the keystore.**

    **When prompted for your name, enter the name of the host, not your own name.**

    **If -alias is NOT specified, "mykey" will be used as the default alias.  This can cause a lot
    of grief in the future when attempting to import certificates.**

2.  Generate a CSR:

    ```sh
    keytool -certreq -keyalg RSA -file <csr_file_name> -keystore biobank.keystore
    ```

    Replace `<csr_file_name>` with a file name to be used in the next step.

3.  Submit the file `<csr_file_name>` to the certificate signing authority. E.g.
    [Global Sign](https://system.globalsign.com/bm/public/certificate/poporder.do?domain=7ff3ae40cf752700e377eee8c1545d2796be16ea).

4.  The certificate signing authority will reply with a certificate email.  Download the X509 certificate
    under and save it as `primary.cer`. The intermediate certificate should be saved as
    `inter.cer`. The root certificate should be saved as `root.cer`.

5.  Import the certificates:

    * root:  `keytool -import -trustcacerts -file root.cer -alias root -keystore biobank.keystore`

    * inter:  `keytool -import -trustcacerts -file inter.cer -alias inter -keystore biobank.keystore`

    * cert:  `keytool -import -file reply.cer -alias <server_domain_name> -keystore
      biobank.keystore`

    Replace `<server_domain_name>` with the host name of your server.

    When imported successfully, the message `certificate reply imported` is displayed.

6.  Copy `biobank.keystore` to the JBoss server.

    ```sh
    cp biobank.keystore /opt/jboss/jboss-4.0.5.GA/server/default/conf/biobank.keystore
    ```

****

[Parent Document](server_installation.md)

[Back to top](../README.md)
