import React, { useState, useCallback, useEffect } from 'react';
import axios from 'axios';
import Books from '../../components/Books/Books';
import SearchForm from '../../components/SearchForm/SearchForm';
import './BookPage.css';

export default function BookPage(props) {
    const findAllBooks = "/books";
    const searchBooks = "/books/search";
    const [books, setBooks] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [isDesktop, setIsDesktop] = useState(window.innerWidth >= 1024);
    const [query, setQuery] = useState("");
    const size = isDesktop ? 20 : 10;

    const fetchBooks = useCallback(async (page, query) => {
        setLoading(true);
        try {
            const endpoint = query ? searchBooks : findAllBooks;
            const params = {
                page: page,
                size: size,
                // Add query parameter only if it's present             
                ...(query && { query }) 
            };
            const response = await axios.get(endpoint, { params });
            const { content, last } = response.data;
            setBooks((prevBooks) => [...prevBooks, ...content]);
            setHasMore(!last);
        } 
        catch (err) {   
            setError(err);
        } 
        finally {
            setLoading(false);
        }
    }, [size, searchBooks, findAllBooks]);

    useEffect(() => {
        fetchBooks(page, query);
    }, [page, query, fetchBooks]);

    const loadMoreBooks = () => {
        if (!loading && hasMore) {
            if (isDesktop) {
                // Fetch two pages on desktop
                setPage(prevPage => prevPage + 2); 
            } 
            else {
                // Fetch one page on mobile/tablet
                setPage(prevPage => prevPage + 1);
            }
        }
    };

    const handleSearch = (newQuery) => {
        setQuery(newQuery);
        // Reset page when a new search is initiated
        setPage(0); 
        // Clear current books
        setBooks([]);
    };

    useEffect(() => {
        const handleResize = () => {
            setIsDesktop(window.innerWidth >= 1024);
        };
        
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    if (error)
        return <div>Error loading books: {error.message}</div>;

    return (
        <>
            <SearchForm onSearch={handleSearch} />  
            <Books books={books} />
            {hasMore && !loading && (
                <div className="btn-container">
                    <button className='btn' onClick={loadMoreBooks}>Load More</button>
                </div>
            )}
            {loading && <div>Loading...</div>}
        </>
    );
}
