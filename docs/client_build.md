# Biobank client

A computer running Linux to is required to install the software packages listed below. Ubuntu is
recommended.

## Development Environment

Ensure your computer has been set up to build clients by following these
[instructions](development_environment.md).

## Client build instructions

Follow these instructions to build the client on a Linux machine.

1.  Ensure that the `java.client.version.num` in file `build.properties` has the correct version
    number.

1.  Use the following command at the project's root directory.

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

****

[Back to top](../README.md)


