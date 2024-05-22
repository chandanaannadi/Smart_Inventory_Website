// server/routes/auth.js
const express = require('express');
const router = express.Router();
const nodemailer = require('nodemailer');

router.post('/forgot-password', async (req, res) => {
  const { email } = req.body;
  //console.log(email);
  
  // Configure your SMTP transporter
  const transporter = nodemailer.createTransport({
    service: 'Gmail', // or another service
    auth: {
      user: 'anumulajayanthms@gmail.com',
      pass: 'lfvgcekshzsktlpl',
    },
  });

  const mailOptions = {
    from: 'anumulajayanthms@gmail.com',
    to: email,
    subject: 'Password Reset',
    text: 'Click the link to reset your password: http://localhost:5173/reset-password',
};

  try {
    await transporter.sendMail(mailOptions);
    res.status(200).json({ message: 'Email sent successfully' });
  } catch (error) {
    res.status(500).json({ message: 'Error sending email', error });
  }
});

module.exports = router;
