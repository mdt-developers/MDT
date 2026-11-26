-- Inicjalizacja bazy danych dla aplikacji Java (PostgreSQL / MySQL)
CREATE DATABASE IF NOT EXISTS java_app_db;
USE java_app_db;

-- 1. Tabela użytkowników
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela zadań / zasobów powiązanych z użytkownikiem
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    priority INT DEFAULT 1,
    due_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Indeksy przyspieszające wyszukiwanie (zoptymalizowane pod zapytania JPA/JDBC)
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_tasks_user_status ON tasks(user_id, status);

-- 4. Przykładowe dane testowe (Seed data)
INSERT INTO users (username, email, password_hash, role) VALUES 
('admin', 'admin@example.com', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHeF6.1Z9', 'ADMIN'),
('jan_kowalski', 'jan@example.com', '$2a$10$8K1p/a0dL3.0Z1X9a7b8c9d0e1f2', 'USER');

INSERT INTO tasks (user_id, title, description, status, priority) VALUES 
(1, 'Konfiguracja serwera', 'Wdrożenie aplikacji Java na produkcję', 'COMPLETED', 3),
(2, 'Testy integracyjne', 'Napisanie testów dla warstwy DAO / Repository', 'IN_PROGRESS', 2),
(2, 'Optymalizacja zapytań', 'Sprawdzenie indeksów w bazie danych', 'PENDING', 1);
