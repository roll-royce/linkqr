-- Create database and use it
CREATE DATABASE IF NOT EXISTS linkqr;
USE linkqr;

-- Users Table: stores registration and login details.
CREATE TABLE users (
    id VARCHAR(20) PRIMARY KEY,         -- Alphanumeric user id generated upon registration
    username VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    city VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,       -- Store hashed passwords
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Documents Table: stores information about each processed file (PDF/Epub).
CREATE TABLE documents (
    doc_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(20),
    file_name VARCHAR(255),
    file_type VARCHAR(10),                -- e.g., PDF or Epub
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- QR Codes Table: mapping QR codes to document pages and source content.
CREATE TABLE qrcodes (
    qr_id VARCHAR(20) PRIMARY KEY,
    doc_id INT,
    user_id VARCHAR(20),
    page_no INT,
    source_text VARCHAR(255),
    web_link VARCHAR(255),
    qr_image_path VARCHAR(255),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tags VARCHAR(255),
    FOREIGN KEY (doc_id) REFERENCES documents(doc_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Logs Table: records actions and events in the application.
CREATE TABLE logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(20),
    action VARCHAR(255),
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
