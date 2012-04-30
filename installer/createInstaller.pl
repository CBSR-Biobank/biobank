#!/usr/bin/perl

################################################################################
#
# This script compiles the exported biobank folder into two executables:
#   - BioBankInstaller-${VERSION}.exe: installs JAVA JRE if not present on computer
#   - BioBankInstaller-${VERSION}_with_jre.exe: has JAVA JRE bundled with app
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
#  jre
#       |
#       ... ETC
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

use File::Basename;

# fix backslashes
($NSIS_PATH = "$ENV{'PROGRAMFILES'}/nsis") =~ s|\\|\/|;

$NSIS_PROGRAM = "$NSIS_PATH/makensis";

$VERSION = "";
$EXPORT_DIR = "";
$DLL_DIR = "";
$NSIS_DIR = "";
$SEVEN_ZIP = "./7zip/7z.exe";


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

#check for 7zip
if (! -e "$SEVEN_ZIP") {
	die "7zip not found where expected: $SEVEN_ZIP\n";
}

# a version of the JRE is required for the bundled version
if (! -d "jre") {
	die "JRE folder not present in current directory\n";
}

#make temp directory
if (-d "tmp" ){
	`rm -rf tmp`;
}

mkdir "tmp";
-d "tmp" or die "could not create temp directory";

$VERSION = getVersion();
$BIOBANK_FOLDER = "BioBank_v${VERSION}_win32";

makeNsis("tmp/no_jre/$BIOBANK_FOLDER", 0);
makeNsis("tmp/with_jre/$BIOBANK_FOLDER", 1);

print "Cleaning up....\n";
`rm -rf tmp`;
!(-d "tmp") or die "temp directory could not be removed";

sub getVersion() {
	my $VERSION;
	
	`$SEVEN_ZIP e $EXPORT_DIR/plugins/biobank_*.jar META-INF/MANIFEST.MF -otmp`;
	-e "tmp/MANIFEST.MF" or die "failed to extract manifest file";

	open(FH, "tmp/MANIFEST.MF") or die "could not open tmp/MANIFEST.MF";
	while($line = <FH>){
        if($line =~ m/Bundle-Version: (.*)/ ){
                $VERSION = $1;
                $VERSION =~ s/\s+$//;
                print "Found biobank version: $VERSION\n";
                last;
        }
	}
	close(FH);
	$VERSION ne "" or die "failed to find version number";
	return $VERSION;
}

sub makeNsis() {
	my $BIOBANK_PATH = shift(@_);
	my $HAS_BUNDLED_JRE = shift(@_);
	my $INSTALLER_DIR = dirname($BIOBANK_PATH);
	
	print "Copying the biobank app folder to $BIOBANK_PATH\n";
	mkdir($INSTALLER_DIR, 0777);
	`cp -R $EXPORT_DIR $BIOBANK_PATH`;
	-d "$BIOBANK_PATH" or "could not create biobank directory";
	
	if ($HAS_BUNDLED_JRE == 1) {
		`cp -R jre $BIOBANK_PATH`;
	}

	my $TMP_NSIS_DIR = $INSTALLER_DIR . "/nsis";
	print "Copying nsis folder: $TMP_NSIS_DIR\n";
	`cp -R $NSIS_DIR $TMP_NSIS_DIR`;
	-d "$TMP_NSIS_DIR" or die "could not create nsis directory";
	
	open(FH, "$TMP_NSIS_DIR/BioBank.nsi") or die "failed to open $TMP_NSIS_DIR/Biobank.nsi";
	open(FHA, ">$TMP_NSIS_DIR/BiobankTMP.nsi") or die "failed to create $TMP_NSIS_DIR/BiobankTMP.nsi";
	while($line = <FH>){
        if($line =~ m/define VERSION_STR/ ){
            $line =~ s/".*?"/"$VERSION"/;
            print "Modified nsis script line: $line";
        }
		if (($line =~ m/define INSTALL_JAVA/ ) && ($HAS_BUNDLED_JRE == 0)) {
            $line = "!define INSTALL_JAVA\n";
            print "Defining symbol INSTALL_JAVA\n";
		}
        print FHA $line;
	}
	close(FHA);
	close(FH);
	-e "$TMP_NSIS_DIR/BiobankTMP.nsi" or die "could not create customized nsis script";

	print "Compiling nsis script...\n";
	`\"$NSIS_PROGRAM\" $TMP_NSIS_DIR/BiobankTMP.nsi`;
	-e "$INSTALLER_DIR/BioBankInstaller-${VERSION}.exe" 
		or die "nsis could not create installer: $INSTALLER_DIR/BioBankInstaller-${VERSION}.exe";

	if ($HAS_BUNDLED_JRE == 1) {
		$INSTALLER_NAME = "BioBankInstaller-${VERSION}_with_jre.exe";
	} else {
		$INSTALLER_NAME = "BioBankInstaller-${VERSION}.exe";
	}

	print "Moving installer...\n";
	`mv $INSTALLER_DIR/BioBankInstaller-${VERSION}.exe ${INSTALLER_NAME}`;
	-e "${INSTALLER_NAME}" or die "could not move installer";

	print "Successfully created: ${INSTALLER_NAME}\n";
}
