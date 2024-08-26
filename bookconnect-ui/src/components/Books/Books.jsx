import React from 'react';
import './Books.css';

export default function Books({ books }) {
    /*
        TODO: 22 character max supported make sure to handle it.
    */
    return (
        <div className="cards">
            {books.map((book, index) => (
                <div className="card" key={index}>
                    <img 
                        src="https://edit.org/images/cat/book-covers-big-2019101610.jpg" 
                        alt="Book Cover" 
                        className="cover-photo" 
                    />
                    <div className="card-content">
                        <h3 className="card-title">{book.title.length > 22 ? `${book.title.slice(0, 19)}...` : book.title}</h3>
                        <p className="card-author">{book.authorName}</p>
                    </div>
                </div>
            ))}
        </div>
    );
}
