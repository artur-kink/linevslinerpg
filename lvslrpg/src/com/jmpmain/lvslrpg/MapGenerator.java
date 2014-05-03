package com.jmpmain.lvslrpg;

import com.jmpmain.lvslrpg.Map.TileType;

import android.graphics.Paint;
import android.graphics.Point;

public class MapGenerator {

	public enum MapTheme{
		Temperate
	}
	
	/**
	 * Get color of tile based on type.
	 */
	private static Paint GetTypeColor(Map.TileType type){
		Paint paint = new Paint();
		
		int r =  (int)((Math.random()-0.5f)*10);
		int g =  (int)((Math.random()-0.5f)*10);
		int b =  (int)((Math.random()-0.5f)*10);
		
		if(type == Map.TileType.Ground)
			paint.setARGB(255, 25 + r, 200 + g, 25 + b);
		else if(type == Map.TileType.Water)
			paint.setARGB(255, 25 + r, 25 + g, 200 + b);
		else if(type == Map.TileType.Sand)
			paint.setARGB(255, 200 + r, 200 + g, 20 + b);
		else if(type == Map.TileType.Exit)
			paint.setARGB(255, 255, 0, 0);
		
		return paint;
	}
	
	/**
	 * Generate random map.
	 * @param width Width of map in tiles.
	 * @param height Height of map in tiles.
	 * @param tileSize Size of tiles in map.
	 * @return Generated map.
	 */
	public static Map GenerateMap(int width, int height, int tileSize){
		Map map = new Map(width, height, tileSize);
		
		MapTheme theme = MapTheme.Temperate;
		CreateGround(map, Map.TileType.Ground);
		
		//Set exit point of map.
		map.setTile(map.width/2, 10, TileType.Exit);
		
		for(int i = 0; i < Math.random()*5; i++){
			CreatePatch(map, Map.TileType.Water, (int)(Math.random()*map.width), (int)(Math.random()*map.height));
		}
		CreateBorder(map, Map.TileType.Water, Map.TileType.Sand, 3);
		
		//Set player start
		map.playerStart = new Point(map.width/2, map.height - 5);
		
		//Create enemy starting positions.
		for(int i = 0; i <= Math.random()*5; i++){
			map.enemyStarts.add(new Point((int)(map.width*Math.random()), map.height/2));
		}
		
		Paint outline = new Paint();
		outline.setStrokeWidth(0);
		outline.setARGB(20, 0, 0, 0);
		
		//Draw map.
		DrawMap(map);
		
		//Draw map grid.
		for(int r = 0; r < map.height; r++){
			map.lineCanvas.drawLine(0, r*tileSize, width, r*tileSize, outline);
		}
		for(int c = 0; c < map.width; c++){
			map.lineCanvas.drawLine(c*tileSize, 0, c*tileSize, height, outline);
		}
		
		return map;
	}
	
	/**
	 * Set entire map to specified tile type.
	 * @param type Type to set to.
	 */
	private static void CreateGround(Map map, Map.TileType type){
		
		for(int r = 0; r < map.height; r++){
			for(int c = 0; c < map.width; c++){
				map.setTile(c, r, Map.TileType.Ground);
			}
		}
	}
	
	private static void CreateBorder(Map map, Map.TileType target, Map.TileType borderType, int width){
		
		for(int r = 0; r < map.height; r++){
			for(int c = 0; c < map.width; c++){
				for(int w = 1; w <= width; w++){
					
					if(c > w){
						if(map.getTile(c, r) != target && map.getTile(c-w, r) == target){
							map.setTile(c, r, borderType);
						}
					}
					
					if(c < map.width-w){
						if(map.getTile(c, r) != target && map.getTile(c+w, r) == target){
							map.setTile(c, r, borderType);
						}
					}
					
					if(r > w){
						if(map.getTile(c, r) != target && map.getTile(c, r-w) == target){
							map.setTile(c, r, borderType);
						}
					}
					
					if(r < map.height-w){
						if(map.getTile(c, r) != target && map.getTile(c, r+w) == target){
							map.setTile(c, r, borderType);
						}
					}
					
				}
			}
		}
		
	}
	
	private static void CreatePatch(Map map, Map.TileType type, int x, int y){
		recursivePatch(map, type, x, y, 7);
	}
	
	private static void recursivePatch(Map map, Map.TileType type, int x, int y, int depth){
		if(depth <= 0 || x >= map.width || x < 0 || y >= map.height || y < 0)
			return;
		
		map.lineCanvas.drawRect(x*map.tileSize, y*map.tileSize,
				x*map.tileSize + map.tileSize, y*map.tileSize + map.tileSize, GetTypeColor(type));
		
		map.setTile(x, y, type);
		
		if(Math.random() > 0.5)
			recursivePatch(map, type, x-1, y, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, type, x+1, y, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, type, x, y-1, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, type, x, y+1, depth-1);
	}
	
	/**
	 * Draw map tiles based on tile info.
	 */
	private static void DrawMap(Map map){
		for(int r = 0; r < map.height; r++){
			for(int c = 0; c < map.width; c++){
				map.lineCanvas.drawRect(c*map.tileSize, r*map.tileSize,
						c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize,
						GetTypeColor(map.getTile(c, r)));
			}
		}
		
	}
}
