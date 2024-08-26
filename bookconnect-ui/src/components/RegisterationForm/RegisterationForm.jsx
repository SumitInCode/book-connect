import React, { useState } from "react";
import "./RegisterationForm.css";
import logImage from "../../assets/log.svg";
import registerImage from "../../assets/register.svg";
import { useAuth } from "../../context/AuthContent";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const API_URLS = {
  login: "/auth/authenticate",
  register: "/auth/register",
};

export default function RegisterationForm() {
  const navigate = useNavigate();
  const [loginFailed, setLoginFailed] = useState(false);
  const [registerFailed, setRegisterFailed] = useState(false);
  const [isSignUp, setIsSignUp] = useState(false);
  const [loading, setLoading] = useState(false); 
  const { login } = useAuth();

  const handleSignUpClick = () => {
    setIsSignUp(true);
  };

  const handleSignInClick = () => {
    setIsSignUp(false);
  };

  const handleSignInSubmit = async (event) => {
    event.preventDefault();
    setLoading(true); // Start loading

    const email = event.target.email.value;
    const password = event.target.password.value;

    try {
      const response = await axios.post(API_URLS.login, { email, password });
      console.log(response);
      if (response.status === 200) {
        const { accesToken, refreshToken } = response.data;
        login(accesToken, refreshToken);
        navigate("/home");
      } 
      else {
        alert(`Login Failed: Unexpected status code ${response.status}`);
      }
    } 
    catch (error) {
      setLoginFailed(true);
    } 
    finally {
      setLoading(false);
    }
  };

  const handleSignUpSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    const firstName = event.target.firstName.value;
    const lastName = event.target.lastName.value;
    const email = event.target.email.value;
    const password = event.target.password.value;

    try {
      await axios.post(API_URLS.register, {
        firstName,
        lastName,
        email,
        password,
      });
      handleSignInClick();

    } 
    catch (error) {
      setRegisterFailed(true);
    } 
    finally {
      // Stop loading
      setLoading(false);
    }
  };

  return (
    <>
      <div className={`reg-container ${isSignUp ? "sign-up-mode" : ""}`}>
        <div className="forms-reg-container">
          <div className="signin-signup">
            <form
              onSubmit={handleSignInSubmit}
              className={`sign-in-form ${isSignUp ? "hidden" : ""}`}
            >
              <h2 className="title">Sign in</h2>
              <div className="input-field">
                <i className="fas fa-user"></i>
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  required
                />
              </div>
              <div className="input-field">
                <i className="fas fa-lock"></i>
                <input
                  type="password"
                  name="password"
                  placeholder="Password"
                  required
                />
              </div>
              {loginFailed ? (<p style={{ color: 'red' }}>Login Failed: Invalid credentials</p>) : null}
              <input
                type="submit"
                value="Login"
                className="btn solid"
                // Disable button while loading
                disabled={loading} 
              />
              
            </form>
            <form
              onSubmit={handleSignUpSubmit}
              className={`sign-up-form ${isSignUp ? "" : "hidden"}`}
            >
              <h2 className="title">Sign up</h2>
              <div className="input-field">
                <i className="fas fa-user"></i>
                <input
                  type="text"
                  name="firstName"
                  placeholder="First Name"
                  required
                />
              </div>
              <div className="input-field">
                <i className="fas fa-user"></i>
                <input
                  type="text"
                  name="lastName"
                  placeholder="Last Name"
                  required
                />
              </div>
              <div className="input-field">
                <i className="fas fa-envelope"></i>
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  required
                />
              </div>
              <div className="input-field">
                <i className="fas fa-lock"></i>
                <input
                  type="password"
                  name="password"
                  placeholder="Password"
                  required
                />
              </div>
              {registerFailed ? (<p style={{ color: 'red' }}>Register Failed</p>) : null}
              <input
                type="submit"
                className="btn"
                value="Sign up"
                // Disable button while loading
                disabled={loading}
              />
            </form>
          </div>
        </div>

        <div className="panels-reg-container">
          <div className="panel left-panel">
            <div className="content">
              <h3>New here?</h3>
              <p>
                Register to BookConnect and Start sharing and reading Book with
                us.
              </p>
              <button
                className="btn transparent"
                onClick={handleSignUpClick}
                // Disable button while loading
                disabled={loading} 
              >
                Sign up       
              </button>
            </div>
            <img src={logImage} className="image" alt="Log in illustration" />
          </div>
          <div className="panel right-panel">
            <div className="content">
              <h3>Already a user ?</h3>
              <p>
                Login to BookConnect and start reading book and sharing book
              </p>
              <button
                className="btn transparent"
                onClick={handleSignInClick}
                // Disable button while loading
                disabled={loading} 
              >
                Sign in
              </button>
              
            </div>
            <img
              src={registerImage}
              className="image"
              alt="Register illustration"
            />
          </div>
        </div>
      </div>
    </>
  );
}
