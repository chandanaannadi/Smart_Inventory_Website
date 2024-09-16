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
    userName = document.getElementById('user').value
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

    // register the user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({userName: userName, status: 'ONLINE'})
    );

    findAndDisplayUsers().then(() => {
        document.querySelector('#users > .user-item:first-child')?.click();
    });
}

async function findAndDisplayUsers() {
    const usersResponse = await fetch('/users');
    let users = await usersResponse.json();
    users = users.filter(user => user.userName !== userName);
    const usersList = document.getElementById('users');
    usersList.innerHTML = '';

    users.forEach(user => {
        appendUserElement(user, usersList);
        if (users.indexOf(user) < users.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            usersList.appendChild(separator);
        }
    });

    if (selectedUserId){
        document.getElementById(selectedUserId).classList.add('active');
    }
}

function appendUserElement(user, usersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.userName;

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
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');

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
    userChat.forEach(chat => {
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
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(userName, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

async function onMessageReceived(payload) {
    await findAndDisplayUsers();
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId.userName) {
        displayMessage(message.senderId.userName, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    } else{
        const notifiedUser = document.querySelector(`#${message.senderId.userName}`);
        if (notifiedUser && !notifiedUser.classList.contains('active')) {
            const nbrMsg = notifiedUser.querySelector('.nbr-msg');
            nbrMsg.classList.remove('hidden');
        }
    }
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser",
        {},
        JSON.stringify({userName: userName, status: 'OFFLINE'})
    );
    window.location.reload();
}

messageForm.addEventListener('submit', sendMessage, true);

// Use this to disconnect stom
window.onbeforeunload = function(e) {
    onLogout();
}

