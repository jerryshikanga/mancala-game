# Mancala Game

## How to Run

#### Requirements
You need to have the following installed:
1. Docker container engine. [Install here](https://docs.docker.com/engine/install/ubuntu/) Engine
2. Git client. [Install here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
3. Java Programming Language. [Install here](https://www.java.com/en/download/help/download_options.html)
4. Web browser e.g. [Google Chrome](https://www.google.com/chrome/)

#### Steps
1. In your terminal, clone the code into your local machine && change into the directory.
```shell
git clone git@github.com:jerryshikanga/mancala-game.git && cd mancala-game
```
2. In your terminal package the application into a jar file. In linux
```shell
./mvnw clean package
```
In windows
```shell
./mvnw.cmd clean package
```
3. In your terminal, Build the docker image and run the container in the background.
```shell
docker-compose up --build -d
```
4. In your browser, visit the application  at [http://localhost:8080](http://localhost:8080)
5. In your terminal you can stream application logs by
```shell
docker logs -f mancala-server
```

## Limitations & Improvements to be made
1. A user cannot switch browsers. This is because we do not have a mechanism for users to login and continue the game from another browser of device.
2. The application uses redis, an in-memory database to store the state of games. This can limit how much the application scales. A good option would be to use a proper database like PostgresSQL.
3. The connection to the application has not been secured. Switch to using https.
4. Improve the test coverage to or closer to 100%.
5. No history of games played by the user. Provide a way to get previous scores and players played against.
6. In the UI we should have visualization of the stones being picked then being dropped into the subsequent pits.
7. Secure the Docker Image and build it in layers.
8. Better naming convention in the API

## Internal Architecture

The application represents the game in the [Game](https://github.com/jerryshikanga/mancala-game/blob/master/src/main/java/com/shikanga/mancala/businesslogic/Game.java) class.
The game class represents the board as a 2*7 matrix. 

For each of the rows, the first 6 indexes are the pits while the last one is the BigPit, also known as the Mancala.
The traversal is done linearly but for the second player representation of his pits in the front end is done in a reversed fashion for ease of visualization.

A sample of a new board(no moves made yet) is below :

```shell
[
    [6, 6, 6, 6, 6, 6, 0],
    [6, 6, 6, 6, 6, 6, 0]
]
```
A pit is therefore represented/accessed as `board[indexOfPlayer][indexOfPit]`

To make a move, we use the following algorithm
1. Validate the input using certain criteria e.g.
2. Get the index and value of the pit . Store the value of the pit in a variable, say initialValue
3. Set the value of the pit to zero.
4. Loop from the pit to the end (big pit) adding one to each pit
   1. At the big pit before adding check if the pit belongs to the player if not then skip
   2. When you encounter a big pit then switch to the other side of the pit if there are more stones
5. Check the pit of the last stone
   1. If the last stone is the players own big pit (i.e. index is 6 and player index is same as playerId) then the player has another chance.
   2. If the last pit was not a big pit but a small pit and it had no stone before then empty the pit and move the stones to the players own big pit.

To check if the game is Over we check the counts of the players pits from index zero to 5. If any of them is zero then the game is over.

#### Backend
The backend is built using the [Spring Framework](https://spring.io).
This exposes a REST API with a few endpoints
1. `/currentGame` - To get the current game. Return type `Game` object. This uses the session id as the unique identifier.
2. `/startGame` - This clears the current game from memory and instantiates a new game. Return type `Game` object.
3. `/makeMove` - This POST endpoint accepts `player` and `pitIndex` via JSON and uses them to modify the state of the board as per the algorithm in the Architecture above.

A sample of the game object is as below
```json
{
    "board": [
        [6, 6, 6, 6, 6, 6, 0],
        [6, 6, 6, 6, 6, 6, 0]
    ],
    "currentPlayer": 0,
    "gameOver": false
}
```

#### Frontend
1. When a new page is loaded (default index) an ajax call is made to the `/currentGame` to retrieve the current state of the game. 
   1. If no game is present then a pop up is shown to the user to ask him/her to start a new game.
2. A new game is started via the `/startGame` API. 
3. The user makes a move using the `/makeMove` API. 
   1. If any error is encountered then we show the user via a pop up.
4. After each makeMove API call we check the status of the game if its ended, a property that is present in the game Object.
   1. If the game is over then we compute and show the user. This is done in the front end to avoid overloading the server.


## Testing
Several types of testing have been done
1. Unit tests. [Sample here](https://github.com/jerryshikanga/mancala-game/blob/master/src/test/java/com/shikanga/mancala/GameTests.java)
2. Integration tests [Sample tests](https://github.com/jerryshikanga/mancala-game/blob/master/src/test/java/com/shikanga/mancala/MancalaApplicationTests.java)
3. UI testing