/**
 * 
 */
'use strict';

//var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
// var usernameForm = document.querySelector('#usernameForm');
var chatButton = document.querySelector('#chatButton');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var closeButton = document.querySelector('#closeButton');

var stompClient = null;
var username = null;
var socket = null;

var colors = [ '#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107',
		'#ff85af', '#FF9800', '#39bbb0' ];

function connect(event) {
	chatButton.style.display = "none";
	chatPage.classList.remove('hidden');

	if (username && null==socket) {
		socket = new SockJS('/ws');
		stompClient = Stomp.over(socket);

		stompClient.connect({}, onConnected, onError);
	}
	event.preventDefault();
}

function onConnected() {
	// Subscribe to the Public Topic
	stompClient.subscribe('/topic/public', onMessageReceived);

	// Tell your username to the server
	stompClient.send("/app/chat.addUser."+event_id, {}, JSON.stringify({
		sender : username,
		type : 'JOIN'
	}))

	connectingElement.classList.add('hidden');
}

function onError(error) {
	connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
	connectingElement.style.color = 'red';
}

function sendMessage(event) {
	var messageContent = messageInput.value.trim();
	if (messageContent && stompClient) {
		var chatMessage = {
			sender : username,
			content : messageInput.value,
			type : 'CHAT'
		};
		stompClient.send("/app/chat.sendMessage."+event_id, {}, JSON
				.stringify(chatMessage));
		messageInput.value = '';
	}
	event.preventDefault();
}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);

	var messageElement = document.createElement('li');

	if (message.type === 'JOIN') {
		messageElement.classList.add('event-message');
		message.content = (message.sender==username?'You':message.sender) + ' joined!';
	} else if (message.type === 'LEAVE') {
		messageElement.classList.add('event-message');
		message.content = message.sender + ' left!';
	} else {
		messageElement.classList.add('chat-message');

		var avatarElement = document.createElement('i');
		var avatarText = document.createTextNode(message.sender[0]);
		avatarElement.appendChild(avatarText);
		avatarElement.style['background-color'] = getAvatarColor(message.sender);

		messageElement.appendChild(avatarElement);

		var usernameElement = document.createElement('span');
		var usernameText = document.createTextNode(message.sender);
		usernameElement.appendChild(usernameText);
		messageElement.appendChild(usernameElement);
	}

	var textElement = document.createElement('p');
	var messageText = document.createTextNode(message.content);
	textElement.appendChild(messageText);

	messageElement.appendChild(textElement);

	messageArea.appendChild(messageElement);
	messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
	var hash = 0;
	for (var i = 0; i < messageSender.length; i++) {
		hash = 31 * hash + messageSender.charCodeAt(i);
	}
	var index = Math.abs(hash % colors.length);
	return colors[index];
}

function closeForm() {
	chatPage.classList.add('hidden');
	chatButton.style.display = "block";
}
// usernameForm.addEventListener('submit', connect, true)
chatButton.addEventListener('click', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
closeButton.addEventListener('click', closeForm, true)

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: '/getuser',
        success: function (data) {
        	if(null == data || data == ""){
        		username = 'Guest';
        	} else
            username=data.name;
        },
        error: function (e) {
        	username='Guest';
        }

    });
})