$(document).ready(function() {
    var url = "/startGame";

    $.getJSON(url, function(response) {
        var game = response;

        renderBoard(game);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        var errorResponse = jqXHR.responseJSON;

        // Display error details
        console.log("Error:", errorResponse.title);
        console.log("Status:", errorResponse.status);
        console.log("Detail:", errorResponse.detail);

        showErrorMessage(errorResponse.detail);
    });
});


function clearCurrentBoard(){
    $("#gameTable tbody").empty();
    $("#playerOneMancala").text("");
    $("#playerTwoMancala").text("");
    $("#idCurrentPlayer").text("");
    $("#isGameOver").text("");
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
        gameOverText = "The game has ended";
        gameOverClass = "text-danger";
    }
    else{
        gameOverText = "The game is still in play.";
        gameOverClass = "text-primary";
    }

    $("#gameTable tbody").append(topRow, bottomRow);
    $("#playerOneMancala").text(board[0][BIG_PIT_INDEX]);
    $("#playerTwoMancala").text(board[1][BIG_PIT_INDEX]);
    $("#idCurrentPlayer").text(currentPlayer);
    $("#isGameOver").text(gameOverText);
    $("#isGameOver").addClass(gameOverClass);
}

function showErrorMessage(errorMessage){
    if (errorMessage == null){
        errorMessage = "Unknown server error."
    }
    $('#errorModalDetails').text(errorMessage);
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
         showErrorMessage(XMLHttpRequest.responseJSON.detail);
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
        buttonClass = 'btn btn-secondary'
    }
    content += '<button type="button" class=" '+ buttonClass +' " class="text-center"'
    content += 'onclick="pitClicked('+ cellIndex +', '+ playerIndex +', '+ stoneCount +')">';
    content += stoneCount;
    content += '</button>'
    content += '</td>';
    return content;
}
