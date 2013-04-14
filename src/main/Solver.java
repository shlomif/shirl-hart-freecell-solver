//src/main/solver.java
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class Solver {

	@Parameter(names = "--gameno", description = "Microsoft Freecell gameno")
	private int gameno;

	@Parameter(names = "--maxnodes", description = "maxnodes per level retained")
	private int maxnodes = 2000;

	@Parameter(names = "--blocksolve", description = "Generate blocksize solutions")
	private int blocksolve = 1;
	
	@Parameter(names = "--winxp", description = "Solve for Windows XP")
	private boolean winxp = false;
	
	@Parameter(names = "--showall", description = "Debug mode")
	private boolean showall = false;
	
	private Map<String, Entry.Value> position;
	private List<int[]> solutionScores;
	private List<String> solution;
	private List<Entry> nextstack;
	private List<Entry> stack;
	private boolean found;
	private int depth;
	private int cnt;
	private int lvl;

	private final int MAXDEPTH = 55;
	private int MAXSTATS;
	private Stats stats;
	private Logger logger;
	private Map<String, String> set;
	
    // #617 with 5 moves left (Xp invalid!)
	String input = 
		"KS 7C 9S 6S 5D 4C 5H 4S \r\n" + 
		"7D 9C 5C KC 5S 8C KH 7S \r\n" + 
		"TD    QD QH 6D 8H QC    \r\n" + 
		"TH    JC JS    8D JD    \r\n" + 
		"KD             7H TC    \r\n" + 
		"QS             6H 9D    \r\n" + 
		"JH             6C 8S    \r\n" + 
		"TS                      \r\n" + 
		"9H                      \r\n";
	
	public static void main(String[] args) {
//		new Solver().debug(); System.exit(1);
		Solver solver = new Solver();
		new JCommander(solver, args);
		try {
			solver.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() throws Exception {
		if (gameno == 0) {
			System.out.println("Please use options:\n\n" +
				"  java [ -d64 -Xmx6g ] -jar fcsolver.jar " +
				"--gameno <1-1000000> [ --maxnodes <2000-100000> ] "+
				"[ --winxp ] [ --blocksolve <1000-33000> ] [ --showall ]\n\n" +
				"       -d64 is 64bit;\n" +
				"       -Xmx6g is 6G heapsize;\n");
			System.exit(1);
		}
//		load();
//TODO debug #38		
//		for (Map.Entry<String, String> s: set.entrySet()){
//				System.out.println(s.getValue() +","+ s.getKey());
//			}
//		System.out.println(set);
//		System.exit(1);

		while(true){
			position  = new HashMap<String, Entry.Value>();
			solution  = new ArrayList<String>();
			nextstack = new ArrayList<Entry>();
			found     = false;
			depth     = 0;
	
			logger = new Logger();
			logger.log(String.format("--gameno %s --maxnodes %s %s%s%s", gameno, maxnodes,
				winxp ? " --winxp" : " --nowinxp",
				blocksolve > 1 ? " --blocksolve " + blocksolve : "",
				showall ? " --showall" : ""));
			
			// create gameno tableau
			Tableau tableau = new Tableau();
			if (gameno > 0)
				tableau.deal(gameno);
			else
				tableau.fromString(input);

			// store initial tableau entry into position hash
			Entry entre = new Entry(tableau);
			entre.value.score = tableau.heuristic();
			MAXSTATS = entre.value.score.length;
			stats = new Stats(MAXSTATS);
			stats.put(entre.value.score);
			position.put(entre.key, entre.value);
			nextstack.add(entre);
			
			while (depth < MAXDEPTH && !found){
				cnt = 0;
				lvl = nextstack.size();

				int[] loscore = stats.findLoScores();	
				int[] hiscore = stats.findHiScores();	
				int[] midscore = stats.findMidScores(maxnodes);	
				stack = new ArrayList<Entry>();
				
				for (Entry entry : nextstack){
					if (
						entry.value.score[0] > midscore[0]
				&&		entry.value.score[1] > midscore[1]
				//		entry.value.score[2] > midscore[2]
						) continue;
					//TODO needs to use MAXSTATS
					
					// mark all kept entries tree
					stack.add(entry);
					while(true){
						Entry.Value value = position.get(entry.key);
						if (value.level == depth) break;
						value.level = depth;
						if (value.depth == 0) break;
						tableau = new Tableau();
						tableau.fromToken(new Entry(entry.key, value));
						tableau.undo(value.node);
						entry = new Entry(tableau);
					}
				}
				
				// delete all unmarked entries
				int beforesize = position.size();
				Iterator<Map.Entry<String, Entry.Value>> p = position.entrySet().iterator();
				while (p.hasNext()) 
					if (p.next().getValue().level != depth)
						p.remove();
				int aftersize = position.size();
	
				stats = new Stats(MAXSTATS);
				nextstack = new ArrayList<Entry>();
//TODO debug #38			
//				int numberOfKeys = 0;
//				for (Map.Entry<String, String> s: set.entrySet())
//					if (position.containsKey(s.getKey())){
//						numberOfKeys++;
//						System.out.println(s.getValue() +","+ s.getKey());
//					}
//				System.out.println(numberOfKeys);
				
				// generate all possible moves for entries in the stack
				for (Entry entry : stack){
//TODO	debug #38					
// 					if (set.containsKey(entry.key) 
//					&& Integer.parseInt(set.get(entry.key)) == depth 
//						) System.out.println(set.get(entry.key) +","+ entry.key);

					tableau = new Tableau();
					tableau.fromToken(entry);
					search(tableau);
					if (found) break;
				}
				
				logger.log(String.format("d=%2d, l=%8d, s=%3d,%3d,%3d, %3d,%3d,%3d, %3d,%3d,%3d, p=%8d,%8d, cnt=%8d",
					depth, lvl, 
					loscore[0], midscore[0], hiscore[0], 
					loscore[1], midscore[1], hiscore[1],
					loscore[2], midscore[2], hiscore[2], 
					beforesize, aftersize, cnt));
				//TODO needs to use MAXSTATS
				
				if (showall)
					for (TreeMap<Integer, Integer> stat : stats.get())
						logger.log(stat.toString());
				depth++;
			}
			if (solution.size() > 0) {
				StringBuilder sb = new StringBuilder();
				Collections.reverse(solutionScores);
				for (int[] ss : solutionScores)
					sb.append(Arrays.toString(ss));
				logger.log("scores="+ sb.toString());

				Collections.reverse(solution);
				for (String s : solution)
					System.out.println(s);
				System.out.println();

				FileWriter upload = new FileWriter(String.format("upload%03d.txt", gameno / 500), true);
				upload.write(
					String.format("%s,%s,%s,%s,%s\r\n", gameno, depth, maxnodes / 1000, winxp ? "xp" : "w7", 
						Joiner.on('~').join(solution.toArray())	));
				upload.close();
			}
			logger.close();
			if (++gameno % blocksolve == 0) break;
	  }
	}

	public void search (Tableau tableau){
		List<ArrayList<Move>> nodelist = tableau.generateNodelist2(winxp);
		for (ArrayList<Move> node : nodelist){
			for (Move move : node)
				tableau.play(move);
			tableau.autoplay(node);
			Entry entry = new Entry(tableau);
			
			// store unique entries in position hash
			if (!position.containsKey(entry.key)){
				entry.value.depth = depth + 1;
				entry.value.node  = node;
				entry.value.score = tableau.heuristic();
				stats.put(entry.value.score);
				position.put(entry.key, entry.value);
				nextstack.add(entry);
			}
			
			// solution found if all kings on homecells
			if ((tableau.tableau[4][0] & 15) == 13
			&&	(tableau.tableau[5][0] & 15) == 13
			&&	(tableau.tableau[6][0] & 15) == 13
			&&	(tableau.tableau[7][0] & 15) == 13){
				backtrack(entry);
				found = true;
			}
			cnt++;
			tableau.undo(node);
			if (found) break;
		}
	}

	public void backtrack (Entry entry){
		solutionScores = new ArrayList<int[]>();
		while (true) {
			if (entry.value.depth == 0)
				break;
			Tableau tableau = new Tableau();
			tableau.fromToken(entry);
			tableau.undo(entry.value.node);
			solution.add(tableau.notation(entry));
			
			if (showall)
				logger.log("node="+ entry.value.node +"\r\n"+
					Iterables.getLast(solution) +"\r\n"+ tableau);
			
			entry = new Entry(tableau);
			entry.value = position.get(entry.key);
			solutionScores.add(entry.value.score);
			
			if (showall) {
				logger.log("Entry={key='"+ entry.key 
					+ "', token=" + Arrays.toString(entry.value.token) 
					+ ", depth=" + entry.value.depth
					+ ", scores=" + Arrays.toString(entry.value.score) +",");
			}
		}
	}
	
	public void load () {
		set = new TreeMap<String, String>();
		File file = new File("keys.txt");
		try {
			Scanner in = new Scanner(file);
			while (in.hasNext()) {
				String line = in.nextLine();
				set.put(line.substring(2,line.length()), line.substring(0,2));
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void debug () {
		File file = new File("backtrack.txt");
		try {
			Scanner in = new Scanner(file);
			while (in.hasNext()) {
				String line = in.nextLine();
				if (!line.startsWith("'")) continue;
				String[] param = line.split(",");
				param[0] = param[0].substring(1,param[0].length()-1);
				Entry entry = new Entry(param);
				Tableau tableau = new Tableau();
				tableau.fromToken(entry);
				int z[] = new int[8];
				for (int c = 0; c < 8; c++){
					for (int r = 1; r < 21; r++){
						if (tableau.tableau[c][r] == 0)
							break;
						z[c] = r;
					}
				}
				System.out.println(line);
				System.out.println(Arrays.toString(z) +"\n"+ tableau +"\n");
				ArrayList<ArrayList<Move>> nodelist = tableau.generateNodelist2(winxp);
				for (ArrayList<Move> node : nodelist){
					System.out.println(node);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}