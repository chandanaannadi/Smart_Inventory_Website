import React, { useState } from 'react';
import axios from 'axios';
import './ForgotPassword.css'; // For styling

function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');

  const handleForgotPassword = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:3001/auth/forgot-password', { email });
      setMessage(response.data.message);
    } catch (error) {
      setMessage('Error sending email. Please try again later.');
    }
  };

  return (
    <center> <div className="forgot-password-container">
    <h2>Forgot Password</h2>
    <form onSubmit={handleForgotPassword}>
      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>
      <button type="submit" className="btn">Send Reset Link</button>
    </form>
    {message && <p>{message}</p>}
  </div></center>
   
  );
}

export default ForgotPassword;
