function validateSignUpForm() {
    var username = document.getElementById('username').value;
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var confirmPassword = document.getElementById('confirmPassword').value;

    // Check for empty fields
    if (username.trim() === '' || email.trim() === '' || password.trim() === '' || confirmPassword.trim() === '') {
        alert('All fields are required.');
        return false;
    }

    // Validate Gmail address
    var emailPattern = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (!emailPattern.test(email)) {
        alert('Please enter a valid Gmail address.');
        return false;
    }

    // Validate password strength
    var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordPattern.test(password)) {
        alert('Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.');
        return false;
    }

    // Check if passwords match
    if (password !== confirmPassword) {
        alert('Password and confirm password do not match.');
        return false;
    }

    return true;
}
