package com.jmpmain.lvslrpg;

import com.jmpmain.lvslrpg.Map.TileType;

import android.graphics.Color;
import android.graphics.Paint;

public class Map {

	public enum TileType{
		Empty,
		Entity,
		Ground,
		Water,
		Sand
	}
	
	private TileType map[][];
	
	/** Map width in tiles. */
	public int width;
	/** Map height in tiles. */
	public int height;
	
	public int tileSize;
	
	/** LineCanvas where line vs line battles are rendered. */
	public LineCanvas lineCanvas;
	
	public Map(int w, int h, int t){
		tileSize = t;

		lineCanvas = new LineCanvas(w, h);
		
		width = w/t;
		height = h/t;
		map = new TileType[height][width];
		for(int r = 0; r < height; r++){
			for(int c = 0; c < width; c++){
				map[r][c] = TileType.Empty;
			}
		}
	}
	
	public TileType getTile(int x, int y){
		return map[y][x];
	}
	
	public void setTile(int x, int y, TileType type) {
		map[y][x] = type;
	}
	
}
