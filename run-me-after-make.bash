#! /bin/bash
#
# run.bash
# Copyright (C) 2020 Shlomi Fish <shlomif@cpan.org>
#
# Distributed under terms of the MIT license.
#

CLASSPATH="$CLASSPATH:$PWD/bin" java -d64 -Xmx4g main.Solver --gameno 1
