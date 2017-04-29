package engine.terrain;

public class Terrain {
	
	private TerrainTile[][] tiles;
	
	public Terrain(TerrainTile[][] tiles) {
		this.tiles = tiles;
	}
	
	public TerrainTile[][] getTiles() {
		return tiles;
	}
	
	public float getHeightAt(float x, float z) {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if (tiles[i][j].pointInTile(x, z))
					return tiles[i][j].getHeightAt(x, z);
			}
		}
		return 0;
	}

}
