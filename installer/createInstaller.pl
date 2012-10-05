#!/usr/bin/perl -w

#
# For a description of what this script does see documentation at the
# end of this file.
#

use strict;
#use Pod::usage;
use Getopt::Long;
use File::Basename;

sub getVersion {
    my $seven_zip = shift;
    my $export_dir = shift;
    my $version;
    my $line;

    #print "$seven_zip e $export_dir/plugins/biobank_*.jar META-INF/MANIFEST.MF -otmp\n";
    `$seven_zip e $export_dir/plugins/biobank_*.jar META-INF/MANIFEST.MF -otmp`;
    -e "tmp/MANIFEST.MF" or die "failed to extract manifest file";

    open(FH, "tmp/MANIFEST.MF") or die "could not open tmp/MANIFEST.MF";
    while ($line = <FH>) {
        if ($line =~ m/Bundle-Version: (.*)/ ) {
            $version = $1;
            $version =~ s/\s+$//;
            print "Found biobank version: $version\n";
            last;
        }
    }
    close(FH);
    $version ne "" or die "failed to find version number";
    return $version;
}

sub makeNsis {
    my $NSIS_PROGRAM = shift @_;
    my $NSIS_DIR = shift @_;
    my $BIOBANK_PATH = shift @_;
    my $HAS_BUNDLED_JRE = shift @_;
    my $export_dir = shift;
    my $version = shift @_;
    my $INSTALLER_DIR = dirname($BIOBANK_PATH);
    my $line;


    print "Copying the biobank app folder to $BIOBANK_PATH\n";
    mkdir($INSTALLER_DIR, 0777);
    `cp -R $export_dir $BIOBANK_PATH`;
    (-d "$BIOBANK_PATH") or die "could not create biobank directory";

    if ($HAS_BUNDLED_JRE == 1) {
        `cp -R jre $BIOBANK_PATH`;
    }

    my $TMP_NSIS_DIR = $INSTALLER_DIR . "/nsis";
    print "Copying nsis folder: $TMP_NSIS_DIR\n";
    `cp -R $NSIS_DIR $TMP_NSIS_DIR`;
    -d "$TMP_NSIS_DIR" or die "could not create nsis directory";

    open(FH, "$TMP_NSIS_DIR/BioBank.nsi") or die "failed to open $TMP_NSIS_DIR/Biobank.nsi";
    open(FHA, ">$TMP_NSIS_DIR/BiobankTMP.nsi") or die "failed to create $TMP_NSIS_DIR/BiobankTMP.nsi";
    while ($line = <FH>) {
        if ($line =~ m/define VERSION_STR/ ) {
            $line =~ s/".*?"/"$version"/;
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
    (-e "$TMP_NSIS_DIR/BiobankTMP.nsi") or die "could not create customized nsis script";

    print "Compiling nsis script...\n";
	#print "\"$NSIS_PROGRAM\" $TMP_NSIS_DIR/BiobankTMP.nsi\n";
    `\"$NSIS_PROGRAM\" $TMP_NSIS_DIR/BiobankTMP.nsi`;
    (-e "$INSTALLER_DIR/BioBankInstaller-${version}.exe")
        or die "nsis could not create installer: $INSTALLER_DIR/BioBankInstaller-${version}.exe";

    my $INSTALLER_NAME =($HAS_BUNDLED_JRE == 1)
        ? "BioBankInstaller-${version}_with_jre.exe"
            : "BioBankInstaller-${version}.exe";

    print "Moving installer...\n";
    `mv $INSTALLER_DIR/BioBankInstaller-${version}.exe ${INSTALLER_NAME}`;
    -e "${INSTALLER_NAME}" or die "could not move installer";

    print "Successfully created: ${INSTALLER_NAME}\n";
}

my $help;
my $man;

GetOptions ('man' => \$man, 'help|?' => \$help) or pod2usage(2);

pod2usage(1) if $help;
pod2usage(-exitstatus => 0, -verbose => 2) if $man;

my $EXPORT_DIR = "";
my $NSIS_DIR = "";

if ($#ARGV == 1) {
    $EXPORT_DIR = $ARGV[0];
    $EXPORT_DIR =~ s/\/$//;

    $NSIS_DIR = $ARGV[1];
    $NSIS_DIR =~ s/\/$//;
} else {
    print "Usage: createInstaller.pl EXPORT_DIR NSIS_DIR\n";
    exit 0;
}

my $NSIS_PATH;

# fix backslashes
($NSIS_PATH = "$ENV{'PROGRAMFILES'}/nsis") =~ s|\\|\/|;

my $NSIS_PROGRAM = "$NSIS_PATH/makensis";
my $SEVEN_ZIP = "./7zip/7z.exe";

#check for 7zip
(-e "$SEVEN_ZIP") or
    die "7zip not found where expected: $SEVEN_ZIP\n";


# a version of the JRE is required for the bundled version
(-d "jre") or
    die "JRE folder not present in current directory\n";

# remove old tmp directory if it exists	
(-d "tmp" ) and `rm -rf tmp`;

my $VERSION = getVersion($SEVEN_ZIP, $EXPORT_DIR);
my $BIOBANK_FOLDER = "BioBank_v${VERSION}_win32";

#make temp directory
mkdir "tmp";
(-d "tmp") or die "could not create temp directory";

makeNsis($NSIS_PROGRAM, $NSIS_DIR, "tmp/with_jre/$BIOBANK_FOLDER", 1, $EXPORT_DIR, $VERSION);

print "Cleaning up....\n";
`rm -rf tmp`;
(! -d "tmp") or die "temp directory could not be removed";

__END__

=head1 NAME

createInstaller.pl - compiles the exported biobank folder into two executables:

   - BioBankInstaller-<VERSION>_with_jre.exe: has JAVA JRE bundled with app

 Please note that createInstaller.pl expects 7zip directory to be in the same
 location as this script.

 File structure:
  .
  ..
  7zip
       |
       7-zip.dll
       7z.dll
       7z.exe
       7z.sfx
       7zCon.sfx

  jre
       |
       ... ETC

  nsis
       |
       Biobank.nsi
       biobank.ico
       ...ETC

  biobank_exported
       |
       biobank.exe
       biobank.ini
       ..ETC

  createInstaller.pl

 Example:
       ./createInstaller.pl biobank_exported nsis

 This will create:
       BioBankInstaller-$VERSION.exe

=head1 USAGE

createInstaller EXPORT_DIR NSIS_DIR

=cut

