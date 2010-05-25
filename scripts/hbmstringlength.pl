#!/usr/bin/perl -w

use strict;
use Cwd;
use File::Find;
use File::Copy;
use Getopt::Long;

<<<<<<< HEAD
if($#ARGV+1 < 3){
	die "Usuage: translator.pl umlInputFilePath hmbProcessingDirectory outputPath [verbose=0]\n";
}
my $umlFile = $ARGV[0];
my $hmbDir = $ARGV[1];
my $outputPath = $ARGV[2];
my $verbose = $ARGV[3] or 0;
=======
my $USAGE = <<USAGE_END;
Usage: $0 [options] umlInputFilePath hmbProcessingDirectory

Fixes string fields in hibernate files to math those in the model.

  Options
    --verbose Displays debugging output.

USAGE_END

my $umlFile;
my $hmbDir;
>>>>>>> master
my %umlVarCharMap = ();
my @hmbDirMap = ();
my $verbose = 0;

main();

sub main {
    if (!GetOptions ('verbose'  => \$verbose)) {
        die "ERROR: bad options in command line\n";
    }

    if ($#ARGV+1 < 2) {
        die "$USAGE";
    }


    $umlFile = shift @ARGV;
    $hmbDir = shift @ARGV;

    #Parse the UML file
    #The key is the identifier
    #The value is the size of the varchar field
    open (FH, "<",$umlFile) or die $!;
    while (my $umlLine = <FH>) {
	if ($umlLine =~ m/>(.*) : VARCHAR\((\d+)\)/i) {
            $umlVarCharMap{ $1 } = $2;
	}
    }
    close(FH);

#Saves the %umlVarCharMap hash file
print "Generating VarCharLengths.properies... ".">".$outputPath."VarCharLengths.properties";
open(OUTP, ">".$outputPath."VarCharLengths.properties") or die("Error: cannot open file 'VarCharLengths.properties'\n");
print OUTP "#Found the following: \n\n";
print OUTP "#Identifier = Varchar# \n";
print OUTP "#--------------------------\n";
while ( my ($key, $value) = each(%umlVarCharMap) ) {
	print OUTP "$key = $value\n";
}
print OUTP "#--------------------------\n\n";

    #Prints the %umlVarCharMap hash file
    if ($verbose) {
        print "Found the following: \n\n";
	print "Identifier = Varchar# \n";
	print "--------------------------\n";
	while ( my ($key, $value) = each(%umlVarCharMap) ) {
            print "$key = $value\n";
	}
	print "--------------------------\n\n";
    }

    #Browses the input directory
    #Creates an array @hmbDirMap of all of the files
    #that end with the extension hbm.xml
    find( {wanted=> \&wanted=>, no_chdir => 1}, $hmbDir );

    #Prints the @hmbDirMap array file
    if ($verbose) {
	print "Files found in directory '$hmbDir'\n";
	print "--------------------------\n";
	foreach (@hmbDirMap) {
            print("$_\n");
	}
	print "--------------------------\n\n";
    }

    #Scan through each hbm.xml file in the input directory
    #Look for type="string" column="X", where X is a valid key in umlVarCharMap
    #Replace type="string" with type="VARCHAR(Y)" where Y is the value of the column key
    #Save changes in the same directory with .new appended to the file name
    my $linesChanged = 0;
    foreach (@hmbDirMap) {
	open (FO, ">>","$_.new") or die $!;
	open (FH, "<",$_) or die $!;
	while (my $line = <FH>) {
            if ($line =~ m/<.*type="string".*column="(.*)"\/>/i and not ($line =~ /length="\d+"/i) ) {
                if ($umlVarCharMap{ uc($1) }) {
                    #if the column is found in umlVarCharMap
                    my $s1 = "type=\"string\"";
                    my $s2 = "type=\"string\" length=\"$umlVarCharMap{uc($1)}\"";
                    $line =~ s/$s1/$s2/e;
                    $linesChanged++;
                    if ($verbose) {
                        print("Found line with column '$1' in umlVarCharMap\n");
                        print("\t$line");
                    }

                }
            }
            print FO $line or die $!;
	}
	close(FH);
	close(FO);
    }

    if ($verbose) {
	print("$linesChanged lines changed.\n\n");
    }

    #Remove the original files, Rename the new files
    foreach (@hmbDirMap) {
	unlink("$_") or die $!; #move("$_","$_.old") or die $!;
	move("$_.new","$_") or die $!;
    }
}

sub wanted {
    if ($_ =~/hbm\.xml$/i) {
        push(@hmbDirMap, $_);
    }
}
