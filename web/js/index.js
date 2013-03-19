//This script is paired with index.jsp.
$(document).ready(function () {
    // inserts a keyword in the input field.
    // TO DO: insert the keyword at caret instead of appending.
    $(".keywordsHelper").click(
        function () {
            var value = $(this).attr("id");
            var input = $("input");
            //should implement insertion at caret instead of append
            input.val(input.val() + " " + value);
        }
    );
    //switches view from "search" to about and vice versa
    $(".menuitem").click(
        function () {
            var aboutDiv = $("#about");
            var searchDiv = $("#search");
            var choice = $(this).text();
            if (choice === "About") {
                aboutDiv.css("display", "block");
                searchDiv.css("display", "none");
            } else if (choice === "Search") {
                aboutDiv.css("display", "none");
                searchDiv.css("display",  "block");
            }
        }
    );
});