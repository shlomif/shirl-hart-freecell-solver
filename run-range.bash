set -x
dir="$HOME/shirl-hart-solver"
mkdir -p "$dir"
for deal in `seq 1 100`
do
    fn="$dir/$deal.fc.board"
    if ! test -e "$fn"
    then
        pi-make-microsoft-freecell-board -t "$deal" > "$fn"
        bash run-me-after-make.bash --rowsboardfile "$fn" | tee "$dir/$deal".shirl-hart-solver.sol
    fi
done
