CREATE DATABASE marlow_bank_system;
USE marlow_bank_system;

----Accounts table
CREATE TABLE accounts (
       account_id INT AUTO_INCREMENT PRIMARY KEY,
       user_name VARCHAR(100) NOT NULL,
       balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

----Transactions table
CREATE TABLE transactions (
       transaction_id INT AUTO_INCREMENT PRIMARY KEY,
       account_id INT NOT NULL,
       transaction_type ENUM('DEPOSIT', 'WITHDRAW') NOT NULL,
       amount DECIMAL(10,2) NOT NULL,
       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

----Audit logs table (for Kafka-based transaction tracking)
CREATE TABLE audit_logs (
       log_id INT AUTO_INCREMENT PRIMARY KEY,
       account_id INT NOT NULL,
       transaction_id INT NOT NULL,
       action VARCHAR(20) NOT NULL,
       previous_balance DECIMAL(10,2) NOT NULL,
       new_balance DECIMAL(10,2) NOT NULL,
       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (account_id) REFERENCES accounts(account_id),
       FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
);
