#!/usr/bin/perl


################################################################################

#This script compiles the exported biobank folder into an executable.
#BioBankInstaller-${VERSION}.exe is created after a successful compilation.

#Please note that createInstaller.pl expects 7zip directory to be in the same location as this script.

#File structure:
# .
# ..
# 7zip
# 	|
#	7-zip.dll
#	7z.dll
#	7z.exe
#	7z.sfx
#	7zCon.sfx
#
# nsis
#	|
#	Biobank.nsi
#	biobank.ico
#	...ETC
#
# biobank2_exported
#	|
#	biobank2.exe
#	biobank2.ini
#	..ETC
#
# scanlibDLL
#	|
#	cv210.dll
#	cxcore210.dll
#	OpenThreadsWin32.dll
#
# createInstaller.pl

#Example: 
#	./createInstaller.pl biobank2_exported scanlibDLL nsis

#This will create:
#	BioBankInstaller-$VERSION.exe

################################################################################

$NSIS_PROGRAM = "c:/Program\\ Files/nsis/makensis";

$VERSION = "";
$BIOBANK_FOLDER = "";
$EXPORT_DIR = "";
$DLL_DIR = "";
$NSIS_DIR = "";



if($#ARGV == 2){	
	$EXPORT_DIR = $ARGV[0];
	$EXPORT_DIR =~ s/\/$//;
	
	$DLL_DIR = $ARGV[1];
	$DLL_DIR =~ s/\/$//;
	
	$NSIS_DIR = $ARGV[2];
	$NSIS_DIR =~ s/\/$//;
}
else{
	print "Usuage: createInstaller.pl exportDir dllDir nsisDir\n";
	exit 0;
}
print "\n";

#make temp directory
if(-d "tmp" ){
	`rm -rf tmp`;
}
mkdir "tmp";

-d "tmp" or die "could not create temp directory";


#get version number
`./7zip/7z.exe e $EXPORT_DIR/plugins/biobank2_*.jar META-INF/MANIFEST.MF -otmp`;
-e "tmp/MANIFEST.MF" or die "failed to extract manifest file";

open(FH, "tmp/MANIFEST.MF") or die "could not open tmp/MANIFEST.MF";
while($line = <FH>){
	if($line =~ m/Bundle-Version: (.*)/ ){
		$VERSION = $1;
		$VERSION =~ s/\s+$//;
		$BIOBANK_FOLDER = "BioBank2_v${VERSION}_win32";
		print "Found biobank version: $VERSION\n";
		last;
	}
}
close(FH);
$VERSION ne "" or die "failed to find version number";


print "Copying the exported biobank folder...\n";
`cp -R $EXPORT_DIR tmp/$BIOBANK_FOLDER`;
-d "tmp/$BIOBANK_FOLDER" or "could not create biobank directory";


print "Copying the dll's...\n";
`cp -f $DLL_DIR/cv210.dll tmp/$BIOBANK_FOLDER/.`;
`cp -f $DLL_DIR/cxcore210.dll tmp/$BIOBANK_FOLDER/.`;
`cp -f $DLL_DIR/OpenThreadsWin32.dll tmp/$BIOBANK_FOLDER/.`;
(-e "tmp/$BIOBANK_FOLDER/cv210.dll" and 
 -e "tmp/$BIOBANK_FOLDER/cxcore210.dll" and 
 -e "tmp/$BIOBANK_FOLDER/OpenThreadsWin32.dll") or die "could not copy dll's";

 print "Copying the nsis's...\n";
`cp -R $NSIS_DIR tmp/nsis`;
-d "tmp/nsis" or die "could not create nsis directory";

open(FH, "tmp/nsis/Biobank.nsi") or die "failed to open tmp/nsis/Biobank.nsi";
open(FHA, ">tmp/nsis/BiobankTMP.nsi") or die "failed to create tmp/nsis/BiobankTMP.nsi";
while($line = <FH>){
	if($line =~ m/define VERSION_STR/ ){
		$line =~ s/".*?"/"$VERSION"/;
		print "Modified nsis script line: $line";
	}
	print FHA $line;
}
close(FHA);
close(FH);
-e "tmp/nsis/BiobankTMP.nsi" or die "could not create customized nsis script";

print "Compiling nsis script...\n";
`$NSIS_PROGRAM tmp/nsis/BiobankTMP.nsi`;
-e "tmp/BioBankInstaller-${VERSION}.exe" or die "nsis could not create installer";

print "Moving installer...\n";
`mv tmp/BioBankInstaller-${VERSION}.exe .`;
-e "BioBankInstaller-${VERSION}.exe" or die "could not move installer";

print "Cleaning up....\n";
`rm -rf tmp`;
!(-d "tmp") or die "temp directory could not be removed";

print "Successfully created: BioBankInstaller-${VERSION}.exe\n";