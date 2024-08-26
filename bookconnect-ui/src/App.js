import React from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage/HomePage';
import RegisterationForm from './components/RegisterationForm/RegisterationForm'        
import { AuthProvider } from './context/AuthContent';
import BookPage from './pages/BookPage/BookPage';
import NavBar from './components/NavBar/NavBar'

export default function App(props) {

  /*
    Entry Point....
  */
  return (
    <AuthProvider>
    <Router>
      <NavBar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/home" element={<HomePage />} />     
        <Route path="/book-shelf" element={<BookPage />} />
        <Route path="/get-started" element={<RegisterationForm />} />
      </Routes>
    </Router>
    </AuthProvider>
  )
}
