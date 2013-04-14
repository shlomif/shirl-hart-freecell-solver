freecell-solver
===============

A solver for Freecell written in Java

Please use the following options:

  java [ -d64 -Xmx6g ] -jar fcsolver.jar --gameno \<1-1000000\> [ --maxnodes \<2000-200000\> ] [ --winxp ]

 	-d64 is 64bit;
 	-Xmx6g is 6G heapsize;
	--winxp solves specifically for Windows XP (but also works in Vista, 7);
        ( default is --nowinxp (may not work in XP) )
	--maxnodes 2000 is default
        ; 20000 needs -d64 -Xmx4g
        ; 200000 needs -d64 -Xmx14g

For example:

java -jar fcsolver.jar --gameno 4003

java -d64 -Xmx4g  -jar fcsolver.jar --gameno 4003 --maxnodes 20000

java -d64 -Xmx14g -jar fcsolver.jar --gameno 4003 --maxnodes 200000 --winxp

Note. This is a straight-forward conversion of my Perl program
Freecell-App-0.03 on the CPAN ( https://metacpan.org/release/Freecell-App ).
The Java version runs 70 times faster than the Perl one!

