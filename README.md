# JavaRTS
Hobby project, trying to create a simple RTS game with somewhat challenging AI using Pure Java.

Some already implemented features:

1. AI does not cheat, its vision are limited by fog of war, and it doesn't have any advantage in resource gathering.
2. AI will send scout unit to find player's bases and player's army composition, it will use this info to adjust its own 
   army composition, and make decoisions on when and where to attack.
3. AI will expand to a different mining base when current mine is running low.
4. During Battle, AI will pick off weakened player units first. 
5. During travelling AI units will wait for each other to avoid being scattered all over the map. 
6. AI will retreat when it lost a significant number of units.

To do list:

1. Send units to partol the outer parameter of the base, so AI can deal with player's sneak attack eariler.
2. When ecnounter a concentrated player static defences, either use long range units to eliminate the threat or find
   another way to attack the Player.
3. Queue up and deploy static defences to protect base when the main AI army is out of position.

Feel free to grab anything you want here, including the source code and image files! 


---------------------------------------
Update - 2018 Sep 05

First up load. More to come.

