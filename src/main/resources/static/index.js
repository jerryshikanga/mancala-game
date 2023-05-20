$(document).ready(function() {
    var url = "/currentGame";

    $.getJSON(url, function(response) {
        var game = response;

        renderBoard(game);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        var errorResponse = jqXHR.responseJSON;

        var errorMessage;
        try {
            // Display error details
            console.log("Error:", errorResponse.title);
            console.log("Status:", errorResponse.status);
            console.log("Detail:", errorResponse.detail);
            errorMessage = errorResponse.detail;
        }
        catch(e){
            console.log(e);
            errorMessage = "Unknown server error when getting current game.";
        }

        showErrorMessage(errorMessage);
    });
});


function clearCurrentBoard(){
    $("#gameTable tbody").empty();
    $("#playerOneMancala").text("");
    $("#playerTwoMancala").text("");
    $("#idCurrentPlayer").text("");
    $("#isGameOver").text("");
    $("#isGameOver").removeClass("text-danger");
    $("#isGameOver").removeClass("text-primary");
}

function renderBoard(game){
    var board = game.board;
    var currentPlayer = game.currentPlayer;
    const BIG_PIT_INDEX = 6;
    const NUM_PITS = 6;
    // Add player one pits
    var topRow = $('<tr></tr>');
    var playerIndex = 0;
    for (var i = 0; i < NUM_PITS; i++) {
        var cell = getCellHtml(i, playerIndex, board[playerIndex][i]);
        topRow.append(cell);
    }

    // Add player two pits
    var bottomRow = $('<tr></tr>');
    playerIndex = 1;
    for (var i = NUM_PITS-1; i >= 0; i--) {
        var cell = getCellHtml(i, playerIndex, board[playerIndex][i]);
        bottomRow.append(cell);
    }

    // render is game over
    var gameOverText;
    var gameOverClass;
    if (game.gameOver){
        gameOverText = "The game has ended!";
        gameOverClass = "text-danger";
        handleEndGame(game.board);
    }
    else{
        gameOverText = "The game is still in play.";
        gameOverClass = "text-primary";
    }

    var currentPlayerClass;
    if (currentPlayer == 0){
        currentPlayerClass = 'text-warning';
    }
    else{
        currentPlayerClass = 'text-success';
    }
    var currentPlayerHtml = '<span class="'+ currentPlayerClass +'">'+currentPlayer+'</span>';

    $("#gameTable tbody").append(topRow, bottomRow);
    $("#playerOneMancala").text(board[0][BIG_PIT_INDEX]);
    $("#playerTwoMancala").text(board[1][BIG_PIT_INDEX]);
    $("#idCurrentPlayer").html(currentPlayerHtml);
    $("#isGameOver").text(gameOverText);
    $("#isGameOver").addClass(gameOverClass);
}

function showErrorMessage(errorMessage){
    if (errorMessage == null){
        errorMessage = "Unknown error."
    }
    $('#errorModalDetails').html(errorMessage);
    $('#errorModal').modal('show');
}


function pitClicked(pitIndex, playerIndex, stoneCount){
    console.log("Clicked pit with index : "+pitIndex+ " playerIndex : "+playerIndex);
    var url = "/makeMove";
    var payload = {player: playerIndex, pitIndex: pitIndex};
    $.ajax({
      type: "POST",
      url: url,
      data: JSON.stringify(payload),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(data){
            console.log("Gotten data : "+JSON.stringify(data));
            var game = data;
            clearCurrentBoard();
            renderBoard(game);
      },
      error: function(XMLHttpRequest, textStatus, errorThrown) {
        console.log("Error "+JSON.stringify(XMLHttpRequest));
        var errorMessage;
        try{
            errorMessage = XMLHttpRequest.responseJSON.detail;
        }
        catch (e){
            errorMessage = "Unknown server error when making move."
        }
        showErrorMessage(errorMessage);
      }
    });
}

function getCellHtml(cellIndex, playerIndex, stoneCount){
    var content = '<td>'
    var buttonClass;
    if (playerIndex == 1){
        buttonClass = 'btn btn-success';
    }
    else{
        buttonClass = 'btn btn-warning';
    }
    content += '<button type="button" class=" '+ buttonClass +' " class="text-center"'
    content += 'onclick="pitClicked('+ cellIndex +', '+ playerIndex +', '+ stoneCount +')">';
    content += stoneCount;
    content += '</button>'
    content += '</td>';
    return content;
}


function startNewGame(){
    console.log("Starting new game");
    var url = "/startGame";
    $.ajax({
        type: "GET",
        url: url,
        success: function(data){
            var game = data;
            clearCurrentBoard();
            renderBoard(game);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            console.log("Error "+JSON.stringify(XMLHttpRequest));
            var errorMessage;
            try{
                errorMessage = XMLHttpRequest.responseJSON.detail;
            }
            catch(e){
            console.log(e);
            showErrorMessage("Unknown server error when starting game.")
            }
            showErrorMessage(errorMessage);
        }
    });
}

function findSumOfPlayerScores(board){
    // Find player one scores
    var scores = [0, 0];
    for (var playerIndex=0; playerIndex<scores.length; playerIndex++){
        for (var pitIndex=0; pitIndex<board[playerIndex].length; pitIndex++){
            scores[playerIndex]+=board[playerIndex][pitIndex];
        }
    }
    return scores;
}


function handleEndGame(board){
    var scores = findSumOfPlayerScores(board);
    console.log("Scores: " + scores);
    var endGameMessage = 'The game has ended.</br>';
    endGameMessage += '<span class="text-warning">Player zero has ' + scores[0] + ' stones</span>.</br>';
    endGameMessage += '<span class="text-success">Player one has ' + scores[1] + ' stones</span>.</br>';
    endGameMessage += 'Therefore the winner is: ';
    var winner;
    if (scores[0] > scores[1]) {
        winner = '<span class="text-warning text-lg">Player Zero</span>.</br>';
    } else if (scores[1] > scores[0]) {
        winner = '<span class="text-success text-lg">Player One</span>.</br>';
    } else {
        winner = "No one! It is a draw";
    }

    endGameMessage += winner;
    showErrorMessage(endGameMessage);
}