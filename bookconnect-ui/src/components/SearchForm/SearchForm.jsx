import React, { useState } from "react";
import "./SearchForm.css";

export default function SearchForm({ onSearch }) {
  const [query, setQuery] = useState("");
  const [isSearched, setIsSearched] = useState(false);

  const handleChange = (event) => {
    setQuery(event.target.value);
    /*
      As of now i am still learning react so i am using workaround for clear search technique
    */
    if (isSearched && event.target.value === "") {
      setIsSearched(false);
      onSearch("");
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setIsSearched(true);
    onSearch(query);
  };

  return (
    <form className="search-form" onSubmit={handleSubmit}>
      <div className="search-box">
        <input
          type="text"
          placeholder="Search..."
          value={query}
          onChange={handleChange}
        />
        <button className="search-button" type="submit">
          <span>Search</span>
        </button>
      </div>
    </form>
  );
}
