const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const bcrypt = require('bcryptjs');
const EmployeeModel = require('./models/Employee');

passport.use(new LocalStrategy({ usernameField: 'email' }, async (email, password, done) => {
  try {
    const user = await EmployeeModel.findOne({ email: email });
    if (!user) {
      return done(null, false, { message: 'No user with that email' });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (isMatch) {
      return done(null, user);
    } else {
      return done(null, false, { message: 'Password incorrect' });
    }
  } catch (err) {
    return done(err);
  }
}));

passport.serializeUser((user, done) => {
  done(null, user.id);
});

passport.deserializeUser((id, done) => {
  EmployeeModel.findById(id, (err, user) => {
    done(err, user);
  });
});

module.exports = passport;