JAVA_SRCS = \
	src/main/Entry.java \
	src/main/Logger.java \
	src/main/Move.java \
	src/main/Solver.java \
	src/main/SolverTest.java \
	src/main/Stats.java \
	src/main/Tableau.java \

JAVA_BINS = $(patsubst src/%.java,bin/%.class,$(JAVA_SRCS))

all: java_bins

java_bins: $(JAVA_BINS)

$(JAVA_BINS): bin/%.class: src/%.java
	javac -d bin $(JAVA_SRCS)

.PHONY: java_bins

