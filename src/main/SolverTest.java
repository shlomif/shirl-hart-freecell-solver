/*
 * Copyright 2013 Shirl Hart
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the the Artistic License (2.0). You may obtain a copy
 * of the full license at:
 *
 * http://www.perlfoundation.org/artistic_license_2_0
 *
 * Any use, modification, and distribution of the Standard or Modified
 * Versions is governed by this Artistic License. By using, modifying or
 * distributing the Package, you accept this license. Do not use, modify, or
 * distribute the Package, if you do not accept this license.
 *
 * If your Modified Version has been derived from a Modified Version made by
 * someone other than you, you are nevertheless required to ensure that your
 * Modified Version complies with the requirements of this license.
 *
 * This license does not grant you the right to use any trademark, service
 * mark, tradename, or logo of the Copyright Holder.
 *
 * This license includes the non-exclusive, worldwide, free-of-charge patent
 * license to make, have made, use, offer to sell, sell, import and otherwise
 * transfer the Package with respect to any patent claims licensable by the
 * Copyright Holder that are necessarily infringed by the Package. If you
 * institute patent litigation (including a cross-claim or counterclaim)
 * against any party alleging that the Package constitutes direct or
 * contributory patent infringement, then this Artistic License to you shall
 * terminate on the date that such litigation is filed.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER
 * AND CONTRIBUTORS "AS IS' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES.
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY
 * YOUR LOCAL LAW.  UNLESS REQUIRED BY LAW, NO COPYRIGHT HOLDER OR
 * CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR
 * CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT OF THE USE OF THE PACKAGE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * */
package main;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SolverTest
{

	Tableau tableau;

	@Before public void createTableau() { tableau = new Tableau(); }

	@Test public void newTableau()
	{
		//		Tableau tableau = new Tableau();
		assertArrayEquals(new int[8][21], tableau.tableau);
	}

	@Test public void dealTableau()
	{
		//		Tableau tableau = new Tableau();
		tableau.deal(1);
		int[][] expected = {{0, 75, 77, 114, 84, 115, 70, 118, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		    {0, 66, 93, 125, 85, 74, 120, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0},
		    {0, 105, 121, 73, 122, 116, 72, 98, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0},
		    {0, 91, 117, 76, 108, 106, 124, 102, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0},
		    {0, 69, 65, 123, 100, 104, 86, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0},
		    {0, 103, 92, 113, 81, 82, 67, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0},
		    {0, 87, 109, 97, 68, 107, 88, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0},
		    {0, 101, 99, 83, 119, 71, 90, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0}};
		assertArrayEquals(expected, tableau.tableau);
	}

	@Test public void outOfSequence()
	{
		assertFalse("TC, JC not in sequence",
		    Tableau.inSequence(
			Tableau.fromCard("TC"), Tableau.fromCard("JC")));
	}

	@Test public void inSequence()
	{
		assertTrue("AC, 2H are in sequence",
		    Tableau.inSequence(
			Tableau.fromCard("AC"), Tableau.fromCard("2H")));
	}
	@Test public void rankofblank()
	{
		org.junit.Assert.assertEquals("rank of blank is zero",
		    Tableau.rank(Tableau.fromCard("  ")), 0);
	}
	@Test public void suitofblank()
	{
		org.junit.Assert.assertEquals("suit of blank is 0 [diamond]",
		    Tableau.rank(Tableau.fromCard("  ")), 0);
	}
	@Test public void rankofAC()
	{
		org.junit.Assert.assertEquals(
		    "rank of AC is 1", Tableau.rank(Tableau.fromCard("AC")), 1);
	}
	@Test public void suitofAC()
	{
		org.junit.Assert.assertEquals("suit of AC is 1 [club]",
		    Tableau.rank(Tableau.fromCard("AC")), 1);
	}
	@Test public void blankSequence()
	{
		assertTrue("blank, AC are in sequence",
		    Tableau.inSequence(
			Tableau.fromCard("  "), Tableau.fromCard("AC")));
	}
	@Test public void blankOppositeofAC()
	{
		assertTrue("blank, AC are opposite colors",
		    Tableau.inSequence(
			Tableau.fromCard("  "), Tableau.fromCard("AC")));
	}
	@Test public void blanknotOppositeofAD()
	{
		assertFalse("blank, AD are opposite colors",
		    Tableau.inSequence(
			Tableau.fromCard("  "), Tableau.fromCard("AD")));
	}
}
