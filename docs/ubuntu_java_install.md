# Installing Java SE 6 on Ubuntu

Oracle’s Java SE 6 is not included in the official Ubuntu repositories due to license problems
created by Oracle. However, it can still be installed on Linux.

1.  Download Sun/Oracle Java SE 6 JDK from here (current version is JDK 6 Update 45):

    ```
	http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html
    ```
    Note: Select the appropriate package for your architecture: x86 or x64.

1.  Make the file executable:

    ```bash
    chmod +x /path/to/file/jdk-6u45-linux-*.bin
    ```

1.  Run Sun/Oracle Java JDK binary

    ```bash
    /path/to/binary/jdk-6u45-linux-*.bin
    ```

    There will be a newly created folder on the same path with the extracted files

1.  Move the folder to a system wide path and give it a more identifiable name.

    ```bash
    mkdir -p /usr/lib/jvm
    mv /path/to/binary/jdk1.6.0_45 /usr/lib/jvm/java-6-oracle
    ```

1.  Add the new version of java, javac and javaws as a system alternative and give it priority 1.

    ```bash
    sudo update-alternatives —install “/usr/bin/java” “java” “/usr/lib/jvm/java-6-oracle/bin/java” 1
    sudo update-alternatives —install “/usr/bin/javac” “javac” “/usr/lib/jvm/java-6-oracle/bin/javac” 1
    sudo update-alternatives —install “/usr/bin/javaws” “javaws” “/usr/lib/jvm/java-6-oracle/bin/javaws” 1
    ```

1.  Select the new alternatives to be used

    ```bash
    sudo update-alternatives —config java
    ```

    Select your created java alternative

    ```bash
    sudo update-alternatives —config javac
    ```

    Select your new created javac alternative

    ```bash
    sudo update-alternatives —config javaws
    ```

    Select your new created javaws alternative

1.  Test your newly added java and javac. The `java -version` command should return.

    ```bash
    java version “1.6.0_45”
    Java™ SE Runtime Environment (build 1.6.0_45-b12)
    Java HotSpot™ Client VM (build 20.5-b03, mixed mode, sharing)
    ```

    and the `javac -version` command should return

    ```bash
    javac 1.6.0_45
    ```

1.  Update system paths. Open `/etc/profile` with you favorite text editor, ie:

    ```bash
    sudo vi /etc/profile
    ```

    Navigate to the end of the file and add these contents:

    ```bash
    JAVA_HOME=/usr/lib/jvm/java-6-oracle
    PATH=$PATH:$HOME/bin:$JAVA_HOME/bin
    export JAVA_HOME
    export JAVA_BIN
    export PATH
    ```

    Reload your system wide PATH /etc/profile with

    ```bash
    . /etc/profile
    ```

****

[Back to parent document](development_environment.md)

[Back to top](../README.md)
