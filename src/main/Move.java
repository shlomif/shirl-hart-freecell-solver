package main;

public class Move {
	
	public int srcCol, srcRow, dstCol, dstRow;
	public String origin;
	
	public Move (int srcCol, int srcRow, int dstCol, int dstRow, String origin){
		this.srcCol = srcCol;
		this.srcRow = srcRow;
		this.dstCol = dstCol;
		this.dstRow = dstRow;
		this.origin = origin;
	}
	
	@Override 
	public String toString (){
		return String.format("[%s, %s, %s, %s, %s]", 
			srcCol, srcRow, dstCol, dstRow, origin);
	}
}
