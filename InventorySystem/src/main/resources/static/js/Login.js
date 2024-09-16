function validateForm() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    if (username.trim() === '' || password.trim() === '') {
        alert('Username and password cannot be empty');
        return false;
    }

    return true;
}

function validateSignUpForm() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    if (username.trim() === '' || password.trim() === '' || confirmPassword.trim() === '') {
        alert('Username and password cannot be empty');
        return false;
    }

    if (!password === confirmPassword) {
        alert('Password and confirm password are not same');
        return false;
    }

    return true;
}

