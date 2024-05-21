const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const session = require('express-session');
const passport = require('./passportConfig');
const bcrypt = require('bcryptjs');
const authRoutes = require('./router');
const EmployeeModel = require('./models/Employee');

const app = express();

// Middleware
app.use(express.json());
app.use(cors());
app.use(session({
  secret: 'jayanth', // Change this to a real secret
  resave: false,
  saveUninitialized: false,
}));
app.use(passport.initialize());
app.use(passport.session());

app.use('/auth', authRoutes);
mongoose.connect("mongodb://127.0.0.1:27017/employee", {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
.then(() => console.log('MongoDB connected...'))
.catch(err => console.log(err));

app.post('/login', passport.authenticate('local', {
  successRedirect: '/login-success',
  failureRedirect: '/login-failure'
}));

app.get('/login-success', (req, res) => {
  res.json("Success");
});

app.get('/login-failure', (req, res) => {
  res.json("The email or password is incorrect");
});

app.post('/register', async (req, res) => {
  try {
    const {name, email, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const employee = new EmployeeModel({ name,email, password: hashedPassword });
    await employee.save();
    res.status(201).json(employee);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
});




app.listen(3001, () => {
  console.log("Server is running on port 3001");
});
