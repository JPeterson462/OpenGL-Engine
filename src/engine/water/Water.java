package engine.water;

import java.util.ArrayList;

public class Water {
	
	private ArrayList<WaterTile> tiles;
	
	public Water(ArrayList<WaterTile> tiles) {
		this.tiles = tiles;
	}
	
	public ArrayList<WaterTile> getTiles() {
		return tiles;
	}

}
