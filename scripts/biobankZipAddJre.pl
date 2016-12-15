#!/usr/bin/perl

use warnings;
use strict;
use v5.10;
use Getopt::Long;
use Data::Dumper qw(Dumper);
use File::Copy;

=head1 NAME

biobankZipAddJre.pl - This script adds the Java 1.6 JRE folder to one or more Biobank installation
ZIP files.

=head1 USAGE

biobankZipAddJre.pl [OPTIONS]

=head1 OPTIONS

  -p DIR      The path to the directory holding the JRE files.
  -h          Help text.
=cut

my $havePodUsage = eval { require Pod::Usage };
no strict 'refs';

my $ZIP = "/usr/bin/zip";
my $UNZIP = "/usr/bin/unzip";
my $jrePath = ".";
my $man = 0;
my $help = 0;

use constant {
    WIN32           => "win32",
    LINUX64         => "linux64",
    CLI             => "cli",
    WIN32_JRE_ZIP   => "jre.win32.x86.zip",
    LINUX64_JRE_ZIP => "jre.linux.x86_64.zip"
};

my $parseCmdLine = GetOptions ("path=s" => \$jrePath,
                               "man"    => \$man,
                               "help|?" => \$help)
or die "Bad command line arguments\n";

if ($havePodUsage) {
    Pod::Usage->import();
    $parseCmdLine or pod2usage(2);
    pod2usage(1) if $help;
    pod2usage(-exitstatus => 0, -verbose => 2) if $man;
}

sub addJre {
    my ($biobankZipFile, $biobankJreZipFile, $javaJreZip) = (@_);

    if (-e "$javaJreZip") {
        copy($biobankZipFile, $biobankJreZipFile);
        system("rm -rf ./BioBank");
        say "uncompressing $javaJreZip";
        system("$UNZIP -d BioBank $javaJreZip > /dev/null");
        say "compressing $biobankJreZipFile";
        system("$ZIP -r -g $biobankJreZipFile BioBank/jre/ > /dev/null");
        system("rm -rf ./BioBank");
    } else {
        die "ERROR: Could not find JRE zip file: $javaJreZip\n";
    }
}

sub addWin32Jre {
    my $zipFile = shift(@_);
    (my $jreZipFile = $zipFile) =~ s/.zip$/_with_jre.zip/;
    addJre $zipFile, $jreZipFile, "$jrePath/@{[ WIN32_JRE_ZIP ]}"
}

sub addLinux64Jre {
    my $zipFile = shift(@_);
    (my $jreZipFile = $zipFile) =~ s/.zip$/_with_jre.zip/;
    addJre $zipFile, $jreZipFile, "$jrePath/@{[ LINUX64_JRE_ZIP ]}"
}

sub addJreToLinuxCli {
    my ($biobankCliZipFile, $javaJreZip) = (@_);
    (my $biobankLinuxCliZipFile = $biobankCliZipFile) =~ s/.zip$/_linux_with_jre.zip/;

    if (-e "$javaJreZip") {
        copy($biobankCliZipFile, $biobankLinuxCliZipFile);
        system("rm -rf ./biobank-cli");
        say "uncompressing $javaJreZip";
        system("$UNZIP -d biobank-cli $javaJreZip > /dev/null");
        say "compressing $biobankLinuxCliZipFile";
        system("$ZIP -r -g $biobankLinuxCliZipFile biobank-cli/jre/ > /dev/null");
        system("rm -rf ./biobank-cli");
    } else {
        die "ERROR: Could not find JRE zip file: $javaJreZip\n";
    }
}

sub findBiobankFilesInDir {
    my $dir = shift(@_);
    my %result = ();

    opendir(my $dh, ".") || die "can't open current directory for reading: $!";
    while (readdir $dh) {
        if ($_ =~ /^Biobank.*win32.x86.zip$/) {
            $result{WIN32} = $_;
        }
        if ($_ =~ /^Biobank.*linux.gtk.x86_64.zip$/) {
            $result{LINUX64} = $_;
        }
        if ($_ =~ /^BiobankCli.*zip$/ && $_ !~ /with_jre/) {
            $result{CLI} = $_;
        }
    }
    close $dh;
    %result;
}

my %files = findBiobankFilesInDir(".");

if (exists($files{WIN32})) {
    addWin32Jre $files{WIN32}
}

if (exists($files{WIN32})) {
    addLinux64Jre $files{LINUX64}
}

if (exists($files{CLI})) {
    addJreToLinuxCli $files{CLI}, "$jrePath/@{[ LINUX64_JRE_ZIP ]}"
}
