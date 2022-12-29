# JavaRTS
My hobby project, trying to create a simple RTS game with somewhat challenging AI with only Java. 
Feel free to grab anything you like here, including the source code and image files! 


### How to run the game: ###

For windows users, you can download the compiled game [here](https://github.com/phu004/test/blob/master/test/BattleTank3.zip?raw=true). After unpacking simply click on play.bat to start the game. It comes with its own JRE, so you don't even need to have Java installed.

For non-windows users, you will need to have Java installed. Download the same game package as above, start the game with CLI commands e.g. "java main.java"


### About the AI: ###

- AI does not cheat, its vision is limited by fog of war, and it doesn't have any advantage in resource gathering.
- AI will send scout unit to look for player's bases and figure out player's army composition. It will use this info to adjust its own 
   army composition, and make decisions on when/where to attack (normal/hard difficulty).
- AI will expand to a different mining location when the current one is running low.
- During battle, AI will pick off weakened player units first (hard difficulty). 
- During travelling AI units will wait for each other to avoid being scattered all over the map (normal/hard difficulty). 
- AI will retreat when it lost a significant number of units (normal/hard difficulty).
- Send units to partol the outer parameter of the base, so AI can deal with player's sneak attack eariler (hard difficulty).
- When ecnounter a concentrated player static defences, use long range units to deal with the static defence while keep 
   other units in safe distance (hard difficulty).


### Demo playthrough video: ###

https://www.youtube.com/watch?v=hUJWMpyWdVo

### Some screenshots: ###

![alt text](https://github.com/phu004/test/blob/master/test/rts_screenshot01.png)

![alt text](https://github.com/phu004/test/blob/master/test/rts_screenshot02.png)

![alt text](https://github.com/phu004/test/blob/master/test/rts_screenshot03.png)

![alt text](https://github.com/phu004/test/blob/master/test/rts_screenshot04.png)

