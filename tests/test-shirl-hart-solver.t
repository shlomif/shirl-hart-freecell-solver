#!/usr/bin/env perl

use strict;
use warnings;
use Test::More tests => 2;

{
    foreach my $cmd (
qq#bash run-me-after-make.bash --rowsboardfile <(pi-make-microsoft-freecell-board -t 1024)#,
        qq#bash run-me-after-make.bash --gameno 1024#,
        )
    {
        # TEST*2
        like(
            scalar(`bash -c "$cmd"`),
            qr#^30\|[^\n]+\n31\|[^\n]+\n32\|[^\n]+\n#ms,
            "output match"
        );
    }
}
