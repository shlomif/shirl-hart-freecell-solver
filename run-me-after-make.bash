#! /bin/bash
#
# run.bash
# Copyright (C) 2020 Shlomi Fish <shlomif@cpan.org>
#
# Distributed under terms of the MIT license.
#

export CLASSPATH="$CLASSPATH:$PWD/bin"
cmd='java -Xmx4g main.Solver'

if test -n "$1"
then
    exec $cmd "$@"
else
    exec $cmd --gameno 1
fi
