
var hash = document.location.hash.split("/");

var webSocket = new WebSocket("ws://" +
    location.hostname +
    ":" +
    location.port +
    "/ws/chat/" +
    hash[1] +
    "/" +
    hash[2]);
