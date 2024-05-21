import { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

import Register from './Register';
import Login from './Login';
import Home from './Home';
import ForgotPassword from './Forgotpassword'
import ResetPassword from './ResetPassword'

import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <div className="container">
      <BrowserRouter>
        <Routes>
          <Route path='/register' element={<Register />} />
          <Route path='/login' element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/home" element={<Home />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
