#!/usr/bin/env perl

use strict;
use warnings;
use Test::More tests => 1;

{
    # TEST
    like(
        scalar(
`bash -c "pi-make-microsoft-freecell-board -t 1024 > 1024.board && bash run-me-after-make.bash --rowsboardfile 1024.board"`
        ),
        qr#^30\|[^\n]+\n31\|[^\n]+\n32\|[^\n]+\n#ms,
        "output match"
    );
}
