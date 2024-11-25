function validateSignUpForm() {
    // Retrieve form values
    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();
    const confirmPassword = document.getElementById('confirmPassword').value.trim();

    // Validation messages
    const validationMessages = {
        emptyFields: 'All fields are required.',
        invalidEmail: 'Please enter a valid Gmail address.',
        weakPassword: 'Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character.',
        passwordMismatch: 'Password and confirm password do not match.'
    };

    // Validation patterns
    const emailPattern = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    // Validate fields are not empty
    if (!username || !email || !password || !confirmPassword) {
        alert(validationMessages.emptyFields);
        return false;
    }

    // Validate Gmail address
    if (!emailPattern.test(email)) {
        alert(validationMessages.invalidEmail);
        return false;
    }

    // Validate password strength
    if (!passwordPattern.test(password)) {
        alert(validationMessages.weakPassword);
        return false;
    }

    // Validate password match
    if (password !== confirmPassword) {
        alert(validationMessages.passwordMismatch);
        return false;
    }

    // If all validations pass
    return true;
}
