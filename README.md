# JCoinche 
French card game Client/Server in Java
(realisation time = 1week | group of 2 people) 

---
---

##### Coinche is version of the French card game, Belote

The Jcoinche project is compose of two project:
* Jcoinche-Server.
* JCoinche-Client.
Allowing to playing Coinche from your home.

## To use the Jcoinche project : 
* Go to the project root.
* Compile the project.
> To do so use the command line: mvn package.
* Launch the Server.
> To do so use the command line: java -jar /target/Jcoinche-server.jar
> Folow the instruction prompted by the server.
* You can then launch the client.
> To do so use the command line: java -jar target/Jcoinche-client.jar
> Folow the instruction prompted by the client (port and ip of the server).

## More about the program

It is possible that when you launch the client the minimal number of player to launch a Coinche game is not complet.
In this case you will be place in queue list to wait other player to connect.
The minimal number of player for a Coinche game is 4.

## HELP

* Command: HELP (in game, allow you to see all the commands of the game).

## Credits
 Erwan GUIOMAR and Tom LENORMAND.

## License
    Copyright © 2000 Benjamin Guariglia <benjamin.guariglia@epitech.eu>
    This work is free. You can redistribute it and/or modify it under the
    terms of the Do What The Fuck You Want To Public License, Version 2,
    as published by Sam Hocevar. See the COPYING file for more details.
