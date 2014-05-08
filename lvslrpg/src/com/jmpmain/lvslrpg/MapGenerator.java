package com.jmpmain.lvslrpg;

import com.jmpmain.lvslrpg.Map.TileType;

import android.graphics.Paint;
import android.graphics.Point;

public class MapGenerator {

	public enum MapTheme{
		Temperate,
		Desert
	}
	
	/**
	 * Get color of tile based on type.
	 */
	private static Paint GetTypeColor(MapTheme theme, TileType type){
		Paint paint = new Paint();
		
		int r =  (int)((Math.random()-0.5f)*10);
		int g =  (int)((Math.random()-0.5f)*10);
		int b =  (int)((Math.random()-0.5f)*10);
		
		if(theme == MapTheme.Temperate){
			if(type == Map.TileType.Ground)
				paint.setARGB(255, 25 + r, 200 + g, 25 + b);
			else if(type == TileType.Water)
				paint.setARGB(255, 25 + r, 25 + g, 200 + b);
			else if(type == TileType.Sand)
				paint.setARGB(255, 200 + r, 200 + g, 20 + b);
			else if(type == TileType.Mountain)
				paint.setARGB(255, 128 + r, 128 + g, 128 + b);
		}else if(theme == MapTheme.Desert){
			if(type == Map.TileType.Ground)
				paint.setARGB(255, 25 + r, 200 + g, 25 + b);
			else if(type == TileType.Water)
				paint.setARGB(255, 25 + r, 25 + g, 200 + b);
			else if(type == TileType.Sand)
				paint.setARGB(255, 200 + r, 200 + g, 20 + b);
			else if(type == TileType.Mountain)
				paint.setARGB(255, 192 + r, 139 + g, 5 + b);
		}
		
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
		
		if(Math.random() > 0.6)
			theme = MapTheme.Desert;
		
		//Set player start
			map.playerStart = new Point(map.width/2, map.height - 5);
		
		if(theme == MapTheme.Temperate)
			CreateGround(map, TileType.Ground);
		else if(theme == MapTheme.Desert)
			CreateGround(map, TileType.Sand);
			
		//Create mountains
		int numMountains = Math.max(1, (int) (Math.random()*6));
		for(int i = 0; i < numMountains; i++){
			CreatePatch(map, theme, TileType.Mountain, 12, (int)(Math.random()*map.width), (int)(Math.random()*map.height));
		}
		
		//Create lakes
		int numLakes = Math.max(1, (int) (Math.random()*6));
		for(int i = 0; i < numLakes; i++){
			int x = 0;
			int y = 0;
			//Try not to make any lakes around player start.
			do{
				x = (int)(Math.random()*map.width);
				y = (int)(Math.random()*map.height);
			}while(Math.sqrt(Math.pow(x - map.playerStart.x, 2) +  Math.pow(y - map.playerStart.y, 2)) <= 10);
			CreatePatch(map, theme, TileType.Water, 7, x, y);
		}
		
		if(theme == MapTheme.Temperate)
			CreateBorder(map, TileType.Water, TileType.Sand, 3);
		else if(theme == MapTheme.Desert)
			CreateBorder(map, TileType.Water, TileType.Ground, 2);
		
		//Set city location.
		{
			int x = 0;
			int y = 0;
			do{
				x = (int)((map.width*0.9)*Math.random());
				y = (int)(15*Math.random());
			}while(map.getDamage(x, y) == true);
			map.city = new Point(x*map.tileSize, y*map.tileSize);
		}
		
		//Create enemy starting positions.
		for(int i = 0; i <= Math.random()*5; i++){
			//Ensure enemy not spawned in water
			int x = 0;
			int y = 0;
			do{
				x = (int)(map.width*Math.random());
				y = (int)((map.height/2)*Math.random());
			}
			while(map.getDamage(x, y) == true);
			
			map.enemyStarts.add(new Point(x, y));
		}
		
		Paint outline = new Paint();
		outline.setStrokeWidth(0);
		outline.setARGB(10, 0, 0, 0);
		
		//Draw map.
		DrawMap(map, theme);
		
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
				map.setTile(c, r, type);
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
				for(int w = 1; w <= width/2; w++){
					if(c > w && r > w){
						if(map.getTile(c, r) != target && map.getTile(c-w, r-w) == target){
							map.setTile(c, r, borderType);
						}
					}
					if(c > w && r < map.height-w){
						if(map.getTile(c, r) != target && map.getTile(c-w, r+w) == target){
							map.setTile(c, r, borderType);
						}
					}
					
					if(c < map.width-w && r < map.height-w){
						if(map.getTile(c, r) != target && map.getTile(c+w, r+w) == target){
							map.setTile(c, r, borderType);
						}
					}
					if(r > w && c < map.width-w){
						if(map.getTile(c, r) != target && map.getTile(c+w, r-w) == target){
							map.setTile(c, r, borderType);
						}
					}
				}
			}
		}
		
	}
	
	private static void CreatePatch(Map map, MapTheme theme, TileType type, int size, int x, int y){
		recursivePatch(map, theme, type, x, y, size);
	}
	
	private static void recursivePatch(Map map, MapTheme theme, TileType type, int x, int y, int depth){
		if(depth <= 0 || x >= map.width || x < 0 || y >= map.height || y < 0)
			return;
		
		map.lineCanvas.drawRect(x*map.tileSize, y*map.tileSize,
				x*map.tileSize + map.tileSize, y*map.tileSize + map.tileSize, GetTypeColor(theme, type));
		
		map.setTile(x, y, type);
		
		if(Math.random() > 0.5)
			recursivePatch(map, theme, type, x-1, y, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, theme, type, x+1, y, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, theme, type, x, y-1, depth-1);
		if(Math.random() > 0.5)
			recursivePatch(map, theme, type, x, y+1, depth-1);
	}
	
	/**
	 * Draw map tiles based on tile info.
	 */
	private static void DrawMap(Map map, MapTheme theme){
		for(int r = 0; r < map.height; r++){
			for(int c = 0; c < map.width; c++){
				map.lineCanvas.drawRect(c*map.tileSize, r*map.tileSize,
						c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize,
						GetTypeColor(theme, map.getTile(c, r)));
			}
		}
		
	}
}
