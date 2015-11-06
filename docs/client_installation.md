# Biobank client installation

Please follow these instructions to install the Biobank client
software. These instructions apply for the following operating
systems: Microsoft Windows, Apple Mac OSX, and Linux.

## Microsoft Windows

The client software for Microsoft Windows is bundled with the correct
version of the Java Runtime Enviroment (JRE) to make the installation
simpler. If you already have Java installed on your computer, the
Biobank client will not overwrite your current Java installation.

1.  Download and install this file:

    [Microsoft Visual C++ 2010 SP1 Redistributable Package (x86)](http://www.microsoft.com/en-us/download/details.aspx?id=8328)

    You only need to do this once. If you've alredy installed this
    package, you can skip this step.

1.  Create a folder where you want to store the Biobank client
    software. This directory is referred to as the **client folder**
    in the steps below.

    *For example, this can be
    `C:\Users\_userid_\Desktop\BioBank_v3.10.0`, where `_userid_` is
    your Windows user id.* If your user ID is `user1`, then your
    *client folder* folder is at
    `C:\Users\user1\Desktop\BioBank_v3.10.0`.

1.  Download the client from here:

    [Biobank v3.10.0 MS Windows Client](http://aicml-med.cs.ualberta.ca/CBSR/Biobank_v3.10.0/BioBank-3.10.0-win32.win32.x86.zip)

1.  Using Windows Explorer, move the file downloaded in the previous
    step to your *client folder*.

1.  In the Windows Explorer window, right click on the file, and
    select **Extract All**. Do not make any changes to text box
    labelled `Files will be extracted to this folder`. Press the
    `Extract` button,

    After the files have been extracted, your *client folder*
    should contain a folder named `BioBank`.

    *I.e. you will now have the
    `C:\Users\_userid_\Desktop\BioBank_v3.10.0\BioBank` folder.*

1.  If you are using MS Windows in a Virtual Host enviroment, like
    [VM Fusion](http://www.vmware.com/ca/en/products/fusion), you will
    have to edit the file
    `C:\Users\_userid_\BioBank_v3.10.0\BioBank\Biobank.ini`. Once you
    have opened the file in a text editor, remove the string
    `@user.home/` from the line containing `@user.home/biobank2`.

The Biobank client application is now installed and ready to run.

*You can open Windows Explorer and go to the
`C:\Users\_userid_\Desktop\BioBank_v3.10.0\BioBank` folder and
double click on `BioBank.exe` to run the application.*

**_If you are using MS Windows, and wish to install the client in the
`C:\Program Files` folder, you will need administrator rights to
perform these steps._**

## Mac OSX or Linux

Ensure that you use the correct version for your Mac OSX or Linux if
you are running the 64 bit version of the operating system.

1.  Create a directory to hold the contents of the client software.
    This directory is referred to as the **client directory** below.

    *For example, this can be `/opt/BioBank_v3.10.0`.*

1.  Please download the client that matches your computer's operating
    system from here:

    [Biobank v3.10.0](http://aicml-med.cs.ualberta.ca/CBSR/Biobank_v3.10.0/)

1.  Move the file downloaded in the previous step to your *client
    directory* and extract it (the client software file is a ZIP
    archive). After unzipping, your *client directory* should have a
    `BioBank` sub directory.

    *I.e. you will have a directory named `/opt/BioBank_v3.10.0/BioBank`.*

1.  You need to install a Java 6 runtime environment that corresponds
    to their operating system. Please see the following web page for
    download instructions:
    [Java SE Runtime Environment 6 Downloads](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html#jdk-6u45-oth-JPR).

    If you have a different version of the Java JRE installed on your system, you can install the
    Java 6 JRE to the `jre` folder of the installation directory. I.e. on Linux it should be
    installed to `/opt/BioBank/BioBank/jre`.

The Biobank client application is now installed and ready to run.

*You can run the application by typing the following command into the
command line: `/opt/BioBank_v3.10.0/BioBank/BioBank &`.*


## Firewall Information

Please see the following document for details regarding the ports that the client software needs
access to: [Firewall information](firewall_information.md).

****

[Back to top](../README.md)
