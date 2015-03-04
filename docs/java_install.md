# Installing Java

Oracle's Java SE is not included in the official Linux repositories due to license problems created
by Oracle. However, it can still be installed on Linux.

1.  Download Sun/Oracle Java JDK from here (current version is JDK 6 Update 45) :

	```
    http://www.oracle.com/technetwork/java/javase/downloads/index.html
	```

    _Note: Select the appropriate package for your architecture: x86 or x64._

1.  Make the file executable

    ```bash
    chmod +x /path/to/file/jdk-6u45-linux-*.bin
	```

1.  Run Sun/Oracle Java JDK binary

    ```bash
    /path/to/binary/jdk-6u45-linux-*.bin
	```

    There will be a newly created folder on the same path with the extracted files

1.  Move the folder to a system wide path and give it a more identifiable name

    ```bash
    mkdir -p /usr/lib/jvm
    cd /path/to/binary/
    mv /path/to/binary/jdk1.6.0_45 /path/to/binary/java-6-oracle
    mv /path/to/binary/java-6-oracle /usr/lib/jvm/
	```

1.  Add the new version of java, javac and javaws as an system alternative and give it priority 1

    ```bash
    sudo update-alternatives --install "/usr/bin/java" "java" "/usr/lib/jvm/java-6-oracle/bin/java" 1
    sudo update-alternatives --install "/usr/bin/javac" "javac" "/usr/lib/jvm/java-6-oracle/bin/javac" 1
    sudo update-alternatives --install "/usr/bin/javaws" "javaws" "/usr/lib/jvm/java-6-oracle/bin/javaws" 1
	```

1.  Select the new alternatives to be used

    ```bash
    sudo update-alternatives --config java
    sudo update-alternatives --config javac
    sudo update-alternatives --config javaws
	```

1.  Test your newly added java and javac. The java -version command should return:

    ```bash
    java version "1.6.0_45"
    Java(TM) SE Runtime Environment (build 1.6.0_45-b12)
    Java HotSpot(TM) Client VM (build 20.5-b03, mixed mode, sharing)
	```

    and the javac -version command should return

    ```bash
    javac 1.6.0_45
	```

1.  Update system paths. Open /etc/profile with you favorite text editor, ie.

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

    Reload your system wide `/etc/profile` with the command:

    ```bash
    . /etc/profile
	```



****

[Back to top](../README.md)
