import React, { useState } from "react";
import "./NavBar.css";
import { Link, useNavigate } from "react-router-dom";
import { RxHamburgerMenu } from "react-icons/rx";
import { useAuth } from "../../context/AuthContent";

export default function NavBar(props) {
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();
  const { authState, logout } = useAuth();
  const { isAuthenticated } = authState;

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  const handleGetStarted = () => {
    navigate("/get-started");
    setMenuOpen(false);
  };

  const handleLogout = () => {
    setMenuOpen(false);
    logout();
    navigate('/get-started')
  };

  return (
    <>
      <nav>
        <div className="logo">BookConnect</div>
        <label htmlFor="menu-btn" className="menu-btn" onClick={toggleMenu}>
          <RxHamburgerMenu size={30} cursor="pointer" />
        </label>
        <ul className={menuOpen ? "show" : ""}>
          <li>
            <Link to="/" onClick={toggleMenu}>
              Home
            </Link>
          </li>
          <li>
            <Link to="/book-shelf" onClick={toggleMenu}>
              BookShelf
            </Link>
          </li>
          <li>
            {isAuthenticated ? (
              <button className="button" onClick={handleLogout}>
                <span>Logout</span>
              </button>
            ) : (
              <button className="button" onClick={handleGetStarted}>
                <span>Get Started</span>
              </button>
            )}
          </li>
        </ul>
      </nav>
    </>
  );
}
