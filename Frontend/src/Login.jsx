import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import sm from './assets/sm.png';
import './Login.css'; // Assuming you have a CSS file for styling

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:3001/login', {
        email,
        password,
        rememberMe,
      });
      console.log(response.data)
      if(response.data == "The email or password is incorrect"){
        navigate('/Login');
      }else{
        console.log('Login successful:', response.data);
        navigate('/home');
      }
      
    } catch (error) {
      console.error('There was an error logging in:', error);
    }
  };

  return (
    <div className="login-container">
      <div className="login-left">
        <img src={sm} alt="Smart Inventory Logo" className="logo" />
        <h1>SMART INVENTORY</h1>
      </div>
      <div className="login-right">
        <form className="login-form" onSubmit={handleLogin}>
            <center><img src={sm} alt="Smart Inventory Logo" width={51} height={48}/>
          <h2>Log in to your account</h2>
          <p>Welcome back! Please enter your details.</p>
          </center>
          <p>
            Don't have an account? <a href="/register">Sign up</a>
          </p>
          <div className="form-group">
            <label htmlFor="email">Email</label>
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
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
            />
          </div>
            <span>Do you forgot your password ?</span>
            <a href="/forgot-password" className="forgot-password"> Click Here</a>
            <br></br>
            <br></br>
         
          <button type="submit" className="btn btn-primary">Sign in</button>
        </form>
      </div>
    </div>
  );
}

export default Login;
