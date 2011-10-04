#!/usr/bin/perl

################################################################################
#
# This script compiles the exported biobank folder into an executable.
# BioBankInstaller-${VERSION}.exe is created after a successful compilation.
#
# Please note that createInstaller.pl expects 7zip directory to be in the same
# location as this script.
#
# File structure:
#  .
#  ..
#  7zip
#       |
#       7-zip.dll
#       7z.dll
#       7z.exe
#       7z.sfx
#       7zCon.sfx
#
#  nsis
#       |
#       Biobank.nsi
#       biobank.ico
#       ...ETC
#
#  biobank_exported
#       |
#       biobank.exe
#       biobank.ini
#       ..ETC
#
#  createInstaller.pl
#
# Example:
#       ./createInstaller.pl biobank_exported nsis
#
# This will create:
#       BioBankInstaller-$VERSION.exe
#
################################################################################

# fix backslashes
($NSIS_PATH = "$ENV{'PROGRAMFILES'}/nsis") =~ s|\\|\/|;

$NSIS_PROGRAM = "$NSIS_PATH/makensis";

$VERSION = "";
$BIOBANK_FOLDER = "";
$EXPORT_DIR = "";
$DLL_DIR = "";
$NSIS_DIR = "";


if ($#ARGV == 1){
        $EXPORT_DIR = $ARGV[0];
        $EXPORT_DIR =~ s/\/$//;

        $NSIS_DIR = $ARGV[1];
        $NSIS_DIR =~ s/\/$//;
}
else{
        print "Usage: createInstaller.pl EXPORT_DIR NSIS_DIR\n";
        exit 0;
}
print "\n";

#is there a bundled jre?
$HAS_BUNDLED_JRE = 0;
if (-d "$EXPORT_DIR/jre") {
    print "Has bundled jre\n";
	$HAS_BUNDLED_JRE = 1;
}

#make temp directory
if(-d "tmp" ){
        `rm -rf tmp`;
}
mkdir "tmp";

-d "tmp" or die "could not create temp directory";


#get version number
`./7zip/7z.exe e $EXPORT_DIR/plugins/biobank_*.jar META-INF/MANIFEST.MF -otmp`;
-e "tmp/MANIFEST.MF" or die "failed to extract manifest file";

open(FH, "tmp/MANIFEST.MF") or die "could not open tmp/MANIFEST.MF";
while($line = <FH>){
        if($line =~ m/Bundle-Version: (.*)/ ){
                $VERSION = $1;
                $VERSION =~ s/\s+$//;
                $BIOBANK_FOLDER = "BioBank_v${VERSION}_win32";
                print "Found biobank version: $VERSION\n";
                last;
        }
}
close(FH);
$VERSION ne "" or die "failed to find version number";


print "Copying the exported biobank folder...\n";
`cp -R $EXPORT_DIR tmp/$BIOBANK_FOLDER`;
-d "tmp/$BIOBANK_FOLDER" or "could not create biobank directory";

 print "Copying the nsis's...\n";
`cp -R $NSIS_DIR tmp/nsis`;
-d "tmp/nsis" or die "could not create nsis directory";

open(FH, "tmp/nsis/BioBank.nsi") or die "failed to open tmp/nsis/Biobank.nsi";
open(FHA, ">tmp/nsis/BiobankTMP.nsi") or die "failed to create tmp/nsis/BiobankTMP.nsi";
while($line = <FH>){
        if($line =~ m/define VERSION_STR/ ){
                $line =~ s/".*?"/"$VERSION"/;
                print "Modified nsis script line: $line";
        }
		if (($line =~ m/define INSTALL_JAVA/ ) && ($HAS_BUNDLED_JRE == 0)) {
                $line = "!define INSTALL_JAVA";
                print "Defining symbol INSTALL_JAVA\n";
		}
        print FHA $line;
}
close(FHA);
close(FH);
-e "tmp/nsis/BiobankTMP.nsi" or die "could not create customized nsis script";


print "Compiling nsis script...\n";
`\"$NSIS_PROGRAM\" tmp/nsis/BiobankTMP.nsi`;
-e "tmp/BioBankInstaller-${VERSION}.exe" or die "nsis could not create installer";

if ($HAS_BUNDLED_JRE == 1) {
	$INSTALLER_NAME = "BioBankInstaller-${VERSION}_with_jre.exe";
} else {
	$INSTALLER_NAME = "BioBankInstaller-${VERSION}.exe";
}

print "Moving installer...\n";
`mv tmp/BioBankInstaller-${VERSION}.exe ${INSTALLER_NAME}`;
-e "${INSTALLER_NAME}" or die "could not move installer";

print "Cleaning up....\n";
`rm -rf tmp`;
!(-d "tmp") or die "temp directory could not be removed";

print "Successfully created: ${INSTALLER_NAME}\n";
