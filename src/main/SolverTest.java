package main;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;



public class SolverTest {
	
	Tableau tableau;

	@Before
	public void createTableau () {
		tableau = new Tableau();
	}
	
	@Test
	public void newTableau () {
//		Tableau tableau = new Tableau();
		assertArrayEquals(new int[8][21], tableau.tableau);
	}

	@Test
	public void dealTableau () {
//		Tableau tableau = new Tableau();
		tableau.deal(1);
		int [][] expected = 
			{{0, 75, 77, 114, 84, 115, 70, 118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 66, 93, 125, 85, 74, 120, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 105, 121, 73, 122, 116, 72, 98, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 91, 117, 76, 108, 106, 124, 102, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 69, 65, 123, 100, 104, 86, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 103, 92, 113, 81, 82, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 87, 109, 97, 68, 107, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 101, 99, 83, 119, 71, 90, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		assertArrayEquals(expected, tableau.tableau);
	}
	
	@Test
	public void outOfSequence () {
		 assertFalse("TC, JC not in sequence", Tableau.inSequence(Tableau.fromCard("TC"),Tableau.fromCard("JC")));
	}

	@Test
	public void inSequence () {
		 assertTrue("AC, 2H are in sequence", Tableau.inSequence(Tableau.fromCard("AC"),Tableau.fromCard("2H")));
	}
	@Test
	public void rankofblank () {
		 org.junit.Assert.assertEquals("rank of blank is zero", Tableau.rank(Tableau.fromCard("  ")), 0);
	}
	@Test
	public void suitofblank () {
		 org.junit.Assert.assertEquals("suit of blank is 0 [diamond]", Tableau.rank(Tableau.fromCard("  ")), 0);
	}
	@Test
	public void rankofAC () {
		 org.junit.Assert.assertEquals("rank of AC is 1", Tableau.rank(Tableau.fromCard("AC")), 1);
	}
	@Test
	public void suitofAC () {
		 org.junit.Assert.assertEquals("suit of AC is 1 [club]", Tableau.rank(Tableau.fromCard("AC")), 1);
	}
	@Test
	public void blankSequence () {
		 assertTrue("blank, AC are in sequence", Tableau.inSequence(Tableau.fromCard("  "),Tableau.fromCard("AC")));
	}
	@Test
	public void blankOppositeofAC () {
		 assertTrue("blank, AC are opposite colors", Tableau.inSequence(Tableau.fromCard("  "),Tableau.fromCard("AC")));
	}
	@Test
	public void blanknotOppositeofAD () {
		 assertFalse("blank, AD are opposite colors", Tableau.inSequence(Tableau.fromCard("  "),Tableau.fromCard("AD")));
	}
}




