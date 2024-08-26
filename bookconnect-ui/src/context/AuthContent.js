/*
  AuthenticationContext
*/
import React, { createContext, useState, useContext, useEffect } from "react";
import Cookies from "js-cookie";

const AuthContext = createContext();

// Create a provider component
export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState({
    isAuthenticated: false,
    accessToken: null,
    refreshToken: null,
  });

  // Load initial state from cookies
  useEffect(() => {
    const storedAuthState = Cookies.get("authState");
    if (storedAuthState) {
      setAuthState(JSON.parse(storedAuthState));
    }
  }, []);

  // Update cookies whenever authState changes
  useEffect(() => {
    if (authState.isAuthenticated) {
      Cookies.set("authState", JSON.stringify(authState), {
        expires: 7,
        // Ensures cookie is sent only over HTTPS
        secure: true,
        // Helps prevent CSRF attacks
        sameSite: "Lax",
      });
    }
    else {
      Cookies.remove("authState");
    }
  }, [authState]);

  // Sync auth state across tabs
  useEffect(() => {
    const handleStorageChange = (event) => {
      if (event.key !== "authState") {
        return;
      }

      const newAuthState = JSON.parse(event.newValue);
      if (newAuthState) {
        setAuthState(newAuthState);
      } 
      else {
        setAuthState({
          isAuthenticated: false,
          accessToken: null,
          refreshToken: null,
        });
      }
    };

    window.addEventListener("storage", handleStorageChange);
    return () => window.removeEventListener("storage", handleStorageChange);
  }, []);

  const login = (accessToken, refreshToken) => {
    setAuthState({
      isAuthenticated: true,
      accessToken,
      refreshToken,
    });
  };

  const logout = () => {
    setAuthState({
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,
    });
  };

  return (
    <AuthContext.Provider value={{ authState, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
