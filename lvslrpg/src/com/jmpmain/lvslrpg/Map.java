package com.jmpmain.lvslrpg;

import java.util.Vector;

import android.graphics.Point;

import com.jmpmain.lvslrpg.entities.Item;

public class Map {

	public enum TileType{
		Empty,
		Ground,
		Water,
		Sand,
		Forest,
		Mountain,
		Hill
	}
	
	/** Tile type array. The map array. */
	private TileType map[][];
	
	private boolean damageMap[][];
	
	/** Map width in tiles. */
	public int width;
	/** Map height in tiles. */
	public int height;
	
	public int tileSize;
	
	public Point city;
	
	public Point playerStart;
	
	public Vector<Point> enemyStarts;
	
	/** LineCanvas where line vs line battles are rendered. */
	public LineCanvas lineCanvas;
	
	public Map(int w, int h, int t){
		tileSize = t;

		lineCanvas = new LineCanvas(w, h);
		
		enemyStarts = new Vector<Point>();
		
		width = w/t;
		height = h/t;
		map = new TileType[height][width];
		damageMap = new boolean[height][width];
		
		for(int r = 0; r < height; r++){
			for(int c = 0; c < width; c++){
				map[r][c] = TileType.Empty;
				damageMap[r][c] = false;
			}
		}
	}
	
	public TileType getTile(int x, int y){
		return map[y][x];
	}
	
	public boolean getDamage(int x, int y){
		return damageMap[y][x];
	}
	
	public void setTile(int x, int y, TileType type) {
		map[y][x] = type;
		if(type == TileType.Water){
			setTileDamage(x, y, true);
		}
	}
	
	public void setTileDamage(int x, int y, boolean damage){
		damageMap[y][x] = damage;
	}
	
}
