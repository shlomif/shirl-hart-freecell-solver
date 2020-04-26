#! /bin/bash
#
# ci-before-install.bash
# Copyright (C) 2019 Shlomi Fish <shlomif@cpan.org>
#
# Distributed under terms of the MIT license.
#
set -e -x
cpanm --local-lib=$HOME/perl_modules local::lib
eval "$(perl -I $HOME/perl_modules/lib/perl5 -Mlocal::lib=$HOME/perl_modules)"
cpanm --notest Docker::CLI::Wrapper::Container Path::Tiny
