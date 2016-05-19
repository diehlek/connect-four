//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");

webSocket.onmessage = function (msg) {
    var data = JSON.parse(msg.data);
    if (data.messageType === "chat") {
        updateChat(data);
    } else if (data.messageType === "board") {
        updateBoard(data.place, data.color);
    } else {
        updateChat(data);
        disableAll();
    }
};


webSocket.onclose = function () { alert("WebSocket connection closed") };

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});


//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(data) {
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
    });
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

//updates the specified location on the board for all users
function updateBoard(place, color) {
    id(place).style.color = color;
}

//Upon win or tie, all the col buttons are disabled.
function disableAll() {
    id("col1").disabled = true;
    id("col2").disabled = true;
    id("col3").disabled = true;
    id("col4").disabled = true;
    id("col5").disabled = true;
    id("col6").disabled = true;
    id("col7").disabled = true;
}

/**
The following functions are used to determine 
which column the user chose to drop a disc down.
That column will then be updated.
**/
function col1Function() {
    webSocket.send(JSON.stringify('col1'));

}

function col2Function() {
    webSocket.send(JSON.stringify('col2'));

}
function col3Function() {
    webSocket.send(JSON.stringify('col3'));

}
function col4Function() {
    webSocket.send(JSON.stringify('col4'));

}
function col5Function() {
    webSocket.send(JSON.stringify('col5'));

}
function col6Function() {
    webSocket.send(JSON.stringify('col6'));

}
function col7Function() {
    webSocket.send(JSON.stringify('col7'));

}