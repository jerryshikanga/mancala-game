$(document).ready(function() {
    var url = "/startGame";

    $.getJSON(url, function(response) {
        var board = response.board;
        var currentPlayer = response.currentPlayer;

        // Add player one pits
        var topRow = $("<tr></tr>");
        for (var i = 0; i < 6; i++) {
            var cell = $("<td></td>").text(board[0][i]);
            topRow.append(cell);
        }

        // Add player two pits
        var bottomRow = $("<tr></tr>");
        for (var i = 5; i >= 0; i--) {
            var cell = $("<td></td>").text(board[1][i]);
            bottomRow.append(cell);
        }

        $("#gameTable tbody").append(topRow, bottomRow);
        $("#playerOneMancala").text(board[0][6]);
        $("#playerTwoMancala").text(board[1][6]);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        var errorResponse = jqXHR.responseJSON;

        // Display error details
        console.log("Error:", errorResponse.title);
        console.log("Status:", errorResponse.status);
        console.log("Detail:", errorResponse.detail);
    });
});