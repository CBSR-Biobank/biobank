# Biobank client

## Requirements

1. The
   [Eclipse Delta Pack](http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/#DeltaPack)
   is required to build the client for the different operating systems. The delta pack version must
   match the version of Eclipse being used. At the moment
   [Eclipse Indigo SR2](http://www.eclipse.org/downloads/packages/release/indigo/sr2) is the latest
   version that can be used.

2. The Microsoft Windows version of Java Rutime Environment (JRE). It can be downloaded from the
   [Java Oracle](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html)
   web site. Once it is installed on a MS Windows computer, the `jre` folder can be copied to the
   Linux computer where the clients are being built.

3. The `java.client.version.num` in `build.properties` has been updated to the correct version
   number.

## Client build instructions

Follow these instructions to build the client on a Linux machine.

1. Use the following command at the project's root directory.

    ```bash
	ant product
    ```

    This creates the following files in `<proj_root>/product/buildDirectory/I.Product`.

    ```bash
	BioBank-_version_-linux.gtk.x86_64.zip
    BioBank-_version_-linux.gtk.x86.zip
    BioBank-_version_-macosx.cocoa.x86_64.zip
    BioBank-_version_-macosx.cocoa.x86.zip
    BioBank-_version_-win32.win32.x86.zip
    ```

    Where `_version_` matches the setting for `java.client.version.num` in `build.properties`.

2. Add the Microsoft Windows JRE to the `win32` zip file using the following commands:

    ```bash
	cd <proj_root>/product/buildDirectory/I.Product
	mkdir BioBank
	cp -r  <ms_windows_jre> BioBank
	zip -r -g BioBank-_version_-win32.win32.x86.zip BioBank/jre
	rm -rf BioBank
    ```

    Where `ms_windows_jre` is the directory containing the MS Windows version of the JRE.

The ZIP files can now be placed where users can download the files.


