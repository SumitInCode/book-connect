import React, { useState, useEffect } from "react";
import Books from "../../components/Books/Books";
import axios from "axios";

export default function HomePage() {
  const [books, setBooks] = useState([]);
  const findAllBookUrl = "/books";

  useEffect(() => {
    async function fetchBooks() {
      try {
        const response = await axios.get(findAllBookUrl);
        setBooks(response.data);
      } catch (error) {
        console.log("Error while fetching books:", error);
      }
    }
    fetchBooks();
  }, []);
  console.log(books);
  return (
    <>
      {books.content != null ? <Books books={books.content} /> : null }
    </>
  );
}
