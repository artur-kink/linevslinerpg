package com.jmpmain.lvslrpg;

import android.graphics.Paint;

public class MapGenerator {

	public enum MapTheme{
		Temperate
	}
	
	private static Paint GetTypeColor(Map.TileType type){
		Paint paint = new Paint();
		
		int r =  (int)((Math.random()-0.5f)*20);
		int g =  (int)((Math.random()-0.5f)*20);
		int b =  (int)((Math.random()-0.5f)*20);
		
		if(type == Map.TileType.Ground)
			paint.setARGB(255, 25 + r, 200 + g, 25 + b);
		else if(type == Map.TileType.Water)
			paint.setARGB(255, 25 + r, 25 + g, 200 + b);
		else if(type == Map.TileType.Sand)
			paint.setARGB(255, 200 + r, 200 + g, 20 + b);
		
		return paint;
	}
	
	public static Map GenerateMap(int width, int height, int tileSize){
		Map map = new Map(width, height, tileSize);
		
		MapTheme theme = MapTheme.Temperate;
		CreateGround(map, Map.TileType.Ground);
		
		CreatePatch(map, Map.TileType.Water, 30, 30);
		CreateBorder(map, Map.TileType.Water, Map.TileType.Sand, 3);
		
		return map;
	}
		
	private static void CreateGround(Map map, Map.TileType type){
		
		for(int r = 0; r < map.height; r++){
			for(int c = 0; c < map.width; c++){
				map.lineCanvas.drawRect(c*map.tileSize + 1, r*map.tileSize + 1,
					c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize, GetTypeColor(type));
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
							map.lineCanvas.drawRect(c*map.tileSize + 1, r*map.tileSize + 1,
									c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize, GetTypeColor(borderType));
							
						}
					}
					if(c < map.width-w){
						if(map.getTile(c, r) != target && map.getTile(c+w, r) == target){
							map.setTile(c, r, borderType);
							map.lineCanvas.drawRect(c*map.tileSize + 1, r*map.tileSize + 1,
									c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize, GetTypeColor(borderType));
							
						}
					}
					
					if(r > w){
						if(map.getTile(c, r) != target && map.getTile(c, r-w) == target){
							map.setTile(c, r, borderType);
							map.lineCanvas.drawRect(c*map.tileSize + 1, r*map.tileSize + 1,
									c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize, GetTypeColor(borderType));
							
						}
					}
					
					if(r < map.height-w){
						if(map.getTile(c, r) != target && map.getTile(c, r+w) == target){
							map.setTile(c, r, borderType);
							map.lineCanvas.drawRect(c*map.tileSize + 1, r*map.tileSize + 1,
									c*map.tileSize + map.tileSize, r*map.tileSize + map.tileSize, GetTypeColor(borderType));
							
						}
					}
					
				}
			}
		}
		
	}
	
	private static void CreatePatch(Map map, Map.TileType type, int x, int y){
		recursivePatch(map, type, x, y, 14);
	}
	
	private static void recursivePatch(Map map, Map.TileType type, int x, int y, int depth){
		if(depth <= 0 || x >= map.width || x < 0 || y >= map.height || y < 0)
			return;
		
		map.lineCanvas.drawRect(x*map.tileSize + 1, y*map.tileSize + 1,
				x*map.tileSize + map.tileSize, y*map.tileSize + map.tileSize, GetTypeColor(type));
		
		map.setTile(x, y, type);
		
		if(Math.random() < 0.8)
			recursivePatch(map, type, x-1, y, depth-1);
		if(Math.random() < 0.8)
			recursivePatch(map, type, x+1, y, depth-2);
		if(Math.random() < 0.8)
			recursivePatch(map, type, x, y+1, depth-1);
		if(Math.random() < 0.8)
			recursivePatch(map, type, x, y+1, depth-2);
	}
}
