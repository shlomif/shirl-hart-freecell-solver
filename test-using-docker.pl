#! /usr/bin/env perl

use strict;
use warnings;
use 5.014;
use autodie;

use Docker::CLI::Wrapper::Container v0.0.4 ();
use Term::ANSIColor qw/ colored /;

my $SYS       = "fedora:41";
my $CONTAINER = "shirl_hart_solver_fedora";
my $obj       = Docker::CLI::Wrapper::Container->new(
    { container => $CONTAINER, sys => $SYS, }, );

my @deps = qw(
    beust-jcommander
    guava
    java-1.8.0-openjdk-devel
    javapackages-tools
    junit
);
$obj->clean_up();
$obj->run_docker();
$obj->docker( { cmd => [ 'cp', ".", "${CONTAINER}:source", ] } );
my $script = <<"EOSCRIPTTTTTTT";
set -e -x
sudo dnf -y upgrade --refresh
sudo dnf -y install freecell-solver git make perl-autodie perl-Path-Tiny perl-Test-Harness 'perl(Test::More)' @deps
cd source
. ./CLASSPATH-source-me.sh
make
make test
EOSCRIPTTTTTTT

$obj->exe_bash_code( { code => $script, } );
$obj->clean_up();

print colored( '== Success ==', 'green' );
print "\n";

__END__

=head1 COPYRIGHT & LICENSE

Copyright 2019 by Shlomi Fish

This program is distributed under the MIT / Expat License:
L<http://www.opensource.org/licenses/mit-license.php>

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

=cut
