'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let userName = null;
let selectedUserId = null;

window.addEventListener('DOMContentLoaded', (event) => {
	userName = document.getElementById('user').value;
	connect(event);
});

function connect(event) {
	const socket = new SockJS('/ws');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, onConnected, onError);
}

function onConnected() {
	stompClient.subscribe(`/user/${userName}/queue/messages`, onMessageReceived);
	stompClient.subscribe(`/user/public`, onMessageReceived);

	// Register the user
	stompClient.send('/app/user.addUser', {}, JSON.stringify({ userName: userName, status: 'ONLINE' }));

	findAndDisplayUsers().then(() => {
		document.querySelector('#users > .user-item:first-child');
	});
}

async function findAndDisplayUsers() {
	const usersResponse = await fetch('/users');
	let users = await usersResponse.json();
	users = users.filter((user) => user.userName !== userName); // Filter out the current user
	const usersList = document.getElementById('users');
	usersList.innerHTML = '';
	let unseenMessages = {};

	// Fetch all messages for the current user
	const allMessagesResponse = await fetch(`/messages/${userName}`);
	if (!allMessagesResponse.ok) {
		console.error(`Error fetching messages: ${allMessagesResponse.statusText}`);
		return;
	}

	if (allMessagesResponse.status !== 204) {
		const allMessages = await allMessagesResponse.json();
		unseenMessages = allMessages.reduce((acc, message) => {
			if (!message.seen && message.senderId.userName !== userName) {
				const sender = message.senderId.userName;
				acc[sender] = (acc[sender] || 0) + 1;
			}
			return acc;
		}, {});
	}

	// Sort users based on the number of unseen messages, in descending order
	users = users.sort((a, b) => {
		const unseenA = unseenMessages[a.userName] || 0;
		const unseenB = unseenMessages[b.userName] || 0;
		return unseenB - unseenA; // Sort in descending order
	});
	
	console.log(users)

	// Render users list
	users.forEach((user) => {
		appendUserElement(user, usersList);

		// If there are unseen messages for this user, show the number of messages
		if (unseenMessages[user.userName]) {
			const nbrMsg = document.querySelector(`#${CSS.escape(user.userName)} .nbr-msg`);
			nbrMsg.classList.remove('hidden');
			nbrMsg.textContent = unseenMessages[user.userName]; // Show the number of unseen messages
		}

		// Add a separator between users
		if (users.indexOf(user) < users.length - 1) {
			const separator = document.createElement('li');
			separator.classList.add('separator');
			usersList.appendChild(separator);
		}
	});

	// Mark the currently selected user as active (if any)
	if (selectedUserId) {
		document.getElementById(selectedUserId).classList.add('active');
	}
}
function appendUserElement(user, usersList) {
	const listItem = document.createElement('li');
	listItem.classList.add('user-item');
	listItem.id = user.userName; // Set the user ID here

	const usernameSpan = document.createElement('span');
	usernameSpan.textContent = user.userName;

	const receivedMsgs = document.createElement('span');
	receivedMsgs.classList.add('nbr-msg', 'hidden');

	listItem.appendChild(usernameSpan);
	listItem.appendChild(receivedMsgs);

	listItem.addEventListener('click', userItemClick);

	usersList.appendChild(listItem);
}

function userItemClick(event) {
	document.querySelectorAll('.user-item').forEach((item) => {
		item.classList.remove('active');
	});
	messageForm.classList.remove('hidden');

	const clickedUser = event.currentTarget;
	clickedUser.classList.add('active');

	selectedUserId = clickedUser.getAttribute('id');
	fetchAndDisplayUserChat().then();

	markMessagesAsSeen(selectedUserId);

	const nbrMsg = clickedUser.querySelector('.nbr-msg');
	nbrMsg.classList.add('hidden');
	nbrMsg.textContent = '';
}

async function markMessagesAsSeen(userId) {
	await fetch(`/markMessagesAsSeen/${userName}/${userId}`, { method: 'POST' });
	console.log(`Messages marked as seen for user: ${userId}`);
}

function displayMessage(senderId, content) {
	const messageContainer = document.createElement('div');
	messageContainer.classList.add('message');
	if (senderId === userName) {
		messageContainer.classList.add('sender');
	} else {
		messageContainer.classList.add('receiver');
	}
	const message = document.createElement('p');
	message.textContent = content;
	messageContainer.appendChild(message);
	chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
	const userChatResponse = await fetch(`/messages/${userName}/${selectedUserId}`);
	const userChat = await userChatResponse.json();
	chatArea.innerHTML = '';
	userChat.forEach((chat) => {
		displayMessage(chat.senderId.userName, chat.content);
	});
	chatArea.scrollTop = chatArea.scrollHeight;
}

function onError() {
	connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
	connectingElement.style.color = 'red';
}

function sendMessage(event) {
	const messageContent = messageInput.value.trim();
	if (messageContent && stompClient) {
		const chatMessage = {
			senderId: userName,
			recipientId: selectedUserId,
			content: messageInput.value.trim(),
			timestamp: new Date(),
		};
		stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
		displayMessage(userName, messageInput.value.trim());
		messageInput.value = '';
	}
	chatArea.scrollTop = chatArea.scrollHeight;
	event.preventDefault();
}

async function onMessageReceived(payload) {
	const message = JSON.parse(payload.body);

	if (selectedUserId && selectedUserId === message.senderId.userName) {
		displayMessage(message.senderId.userName, message.content);
		chatArea.scrollTop = chatArea.scrollHeight;
	} else {
		// Use CSS.escape to handle special characters in IDs
		const notifiedUser = document.querySelector(`#${CSS.escape(message.senderId.userName)}`);
		if (notifiedUser) {
			const nbrMsg = notifiedUser.querySelector('.nbr-msg');
			if (nbrMsg) {
				nbrMsg.classList.remove('hidden');
				notifiedUser.classList.add('highlight');
				let count = parseInt(nbrMsg.textContent) || 0;
				nbrMsg.textContent = count + 1;
			} else {
				console.warn(`No nbr-msg element found for user '${message.senderId.userName}'`);
			}
		} else {
			console.warn(`User with ID '${message.senderId.userName}' not found in the DOM.`);
		}
	}
	findAndDisplayUsers();
	//window.location.reload();
}

function onLogout() {
	stompClient.send('/app/user.disconnectUser', {}, JSON.stringify({ userName: userName, status: 'OFFLINE' }));
	window.location.reload();
}

messageForm.addEventListener('submit', sendMessage, true);

// Disconnect STOMP on window unload
window.onbeforeunload = function (e) {
	onLogout();
};
