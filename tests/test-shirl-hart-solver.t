#!/usr/bin/env perl

use strict;
use warnings;
use Test::More tests => 1;

{
    # TEST
    like(
        scalar(
`bash -c "bash run-me-after-make.bash --rowsboardfile <(pi-make-microsoft-freecell-board -t 1024)"`
        ),
        qr#^30\|[^\n]+\n31\|[^\n]+\n32\|[^\n]+\n#ms,
        "output match"
    );
}
