package core;

/*
1. You start the search from the destination-point (Step 0) and not the start-point.
2. You mark the 4 squares over/under and left/right of the current square with "1" (step value) (if its not a wall) as in "one step from the destination", then you add these squares to a node-list.
3. Go through the current node-list and repeat point 2 on each entry, mark the appropriate squares "2" (n+1) and add these to a swap node-list.Squares already marked with a step-value are IGNORED (like a wall).
4. Repeat point 2 & 3 while incresing the step-value for every cycle/point until you have reached the start-point. (or continue and scan the whole maze if desired).
5. Now you have the shortest way to the target, by simply moving to the adjacent square with the lowest number - until you have reached the destination!
*/



public class PathFinder{
	public static int[] nodes = new int[128*128];

	public static boolean createHeuristicMap(byte[] heuristicMap, int occupiedTile0, int occupiedTile1, int occupiedTile2, int occupiedTile3, int destX, int destY){
		
		if(destX == 0)
			destX = 1;
		if(destX == 127)
			destX = 126;
		if(destY == 0)
			destY = 1;
		if(destY == 127)
			destY = 126;
		
		
		int l = 128 * 128;
		for(int i = 0; i < l ; i++)
			heuristicMap[i] = 127;
		boolean[] obstacleMap = MainThread.gridMap.previousObstacleMap;
		
		int destTile = destX + destY*128;
		//mark destination tile with heuristic value 0
		if(destTile < 0)
			destTile = 0;
		if(destTile >= 16384)
			destTile = 16383;
		heuristicMap[destTile] = 0;
		
		int topTile = destTile- 128;
		int botTile = destTile+ 128;
		int leftTile = destTile- 1;
		int rightTile = destTile+ 1;
		
		//check if path is the starting tiles is reached 
		if(topTile == occupiedTile0 || topTile == occupiedTile1 || topTile == occupiedTile2 || topTile == occupiedTile3 ||
		   botTile == occupiedTile0 || botTile == occupiedTile1 || botTile == occupiedTile2 || botTile == occupiedTile3 ||
		   leftTile == occupiedTile0 || leftTile == occupiedTile1 || leftTile == occupiedTile2 || leftTile == occupiedTile3 ||	
		   rightTile == occupiedTile0 || rightTile == occupiedTile1 || rightTile == occupiedTile2 || rightTile == occupiedTile3){
			return true;
		}
		
		
		int nodeCount = 0;
		
		if(obstacleMap[topTile]){
			nodes[nodeCount] = topTile;
			heuristicMap[topTile] = 1;
			nodeCount++;
		}
	
		if(obstacleMap[botTile]){
			nodes[nodeCount] = botTile;
			heuristicMap[botTile] = 1;
			nodeCount++;
		}
	
		if(obstacleMap[leftTile]){
			nodes[nodeCount] = leftTile;
			heuristicMap[leftTile] = 1;
			nodeCount++;
		}

		if(obstacleMap[rightTile]){
			nodes[nodeCount] = rightTile;
			heuristicMap[rightTile] = 1;
			nodeCount++;
		}
		
		int startIndex = 0;
		int endIndex = startIndex + nodeCount;
	
	
		
		for(byte i = 0, distance = 2; i < 126; i++, distance++){  // max depth = 126
			
			
			nodeCount = 0;
			for(int j = startIndex; j < endIndex; j++){
				
				destTile = nodes[j];
				
				
				topTile = destTile- 128;
				botTile = destTile+ 128;
				leftTile = destTile- 1;
				rightTile = destTile+ 1;
				
				//check if path is the starting tiles is reached 
				if(topTile == occupiedTile0 || topTile == occupiedTile1 || topTile == occupiedTile2 || topTile == occupiedTile3 ||
				   botTile == occupiedTile0 || botTile == occupiedTile1 || botTile == occupiedTile2 || botTile == occupiedTile3 ||
				   leftTile == occupiedTile0 || leftTile == occupiedTile1 || leftTile == occupiedTile2 || leftTile == occupiedTile3 ||	
				   rightTile == occupiedTile0 || rightTile == occupiedTile1 || rightTile == occupiedTile2 || rightTile == occupiedTile3){
			
					return true;
					
					
				}
				
				if(heuristicMap[topTile] == 127 && obstacleMap[topTile]){
					nodes[endIndex + nodeCount] = topTile;
					heuristicMap[topTile] = distance;
					nodeCount++;
				}
				
				if(heuristicMap[botTile] == 127 && obstacleMap[botTile]){
					nodes[endIndex + nodeCount] = botTile;
					heuristicMap[botTile] = distance;
					nodeCount++;
				}
				
				if(heuristicMap[leftTile] == 127 && obstacleMap[leftTile]){
					nodes[endIndex + nodeCount] = leftTile;
					heuristicMap[leftTile] = distance;
					nodeCount++;
				}
				
				if(heuristicMap[rightTile] == 127 && obstacleMap[rightTile]){
					nodes[endIndex + nodeCount] = rightTile;
					heuristicMap[rightTile] = distance;
					nodeCount++;
				}
					
				
			}
			startIndex = endIndex;
			endIndex+=nodeCount;
			
		}
		
		return false;
		
	}
	
	
	
}
