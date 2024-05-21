import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import sm from './assets/sm.png';
import './Register.css'; // Assuming you have a CSS file for styling

function Register() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:3001/register', {
        name,
        email,
        password,
      });
      console.log('Registration successful:', response.data);
      navigate('/home');
    } catch (error) {
      console.error('There was an error registering:', error);
    }
  };

  return (
    <div className="register-container">
      <div className="register-left">
        <img src={sm} alt="Smart Inventory Logo" className="logo" />
        <h1>SMART INVENTORY</h1>
      </div>
      <div className="register-right">
        <form className="register-form" onSubmit={handleRegister}>
        <center>
        <img src={sm} alt="Smart Inventory Logo" width={51} height={48}/>
        <h2>Create an account</h2>
        </center>
          
          <br></br>
          <div className="form-group">
            <label htmlFor="name">Name*</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Enter your name"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email*</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password*</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Create a password"
              required
            />
            <small>Must be at least 8 characters.</small>
          </div>
          <button type="submit" className="btn btn-primary">Get started</button>
          <p>
            Already have an account? <a href="/login">Log in</a>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Register;
