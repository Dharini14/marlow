**Marlow Banking Application - Scala, MySQL, Kafka, Docker**

**Overview**

This is a Scala-based banking application with an API that allows users to deposit and withdraw money from an ATM while ensuring:

1. No overdrafts (balance cannot go below 0).
2. Concurrency support (multiple users accessing the same account).
3. Transaction auditing using Kafka and a separate audit logging system.
4. Logging using Logback.
5. Dockerized setup for easy deployment.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Tech Stack**

1. [x] Scala(Business logic & API Layer)
2. [x] Play Framework (REST API)
3. [x] Slick (Database access)
4. [x] MySQL (Relational database)
5. [x] Kafka (Event-driven transaction auditing)
6. [x] Docker (Containerized deployment)
7. [x] Logback (Logging)

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Setup Instructions**
1. _Prerequisites_
   1.[x] Make sure you have the following installed:
      1.[x] Docker & Docker Compose
      2.[x] Scala & sbt
      3.[x] curl or Postman (For API Testing)
      

2. _Database Setup_



The MySQL database is automatically created via Docker. However, if you want to set it manually:

   1. Create Database & Tables
      1.[x] Run the following SQL script:


         `CREATE DATABASE bank_system;
         USE bank_system;`

         `CREATE TABLE accounts (
         account_id INT AUTO_INCREMENT PRIMARY KEY,
         balance DECIMAL(10,2) NOT NULL DEFAULT 0.00
         );`

         `CREATE TABLE transactions (
         transaction_id INT AUTO_INCREMENT PRIMARY KEY,
         account_id INT,
         transaction_type ENUM('DEPOSIT', 'WITHDRAW'),
         amount DECIMAL(10,2),
         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         FOREIGN KEY (account_id) REFERENCES accounts(account_id)
         );`

         `CREATE TABLE audit_logs (
         log_id INT AUTO_INCREMENT PRIMARY KEY,
         account_id INT,
         action ENUM('DEPOSIT', 'WITHDRAW'),
         previous_balance DECIMAL(10,2),
         new_balance DECIMAL(10,2),
         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
         );`
3. _Running the Application_ (Dockerized Setup)

    The easiest way to start everything is using Docker Compose.
   1. Build the Application JAR
                                                                                                           
           `sbt assembly`
        1.[x] This generates target/scala-2.13/bank-app.jar
   2. Start Services
       
          `docker-compose up-d`
        1.[x] This will start
        2.[x] MySQL database
        3.[x] Kafka & Zookeeper
        4.[x] Scala API Service (bank-api)
   3. Verify Everything is Running
          
          `docker ps`
       1.[x] You should see containers for MySQL, Kafka, Zookeeper, and bank-api.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
**API Endpoints & Testing**

1. Deposit Money 
   
    `curl -X POST "http://localhost:8080/api/deposit?accountId=1&amount=100"`


2. Deposits $100 into Account 1.

 
3. Withdraw Money

   `curl -X POST "http://localhost:8080/api/withdraw?accountId=1&amount=50"`


4.  Withdraw $50 from Account1. 


5. Check Balance

   `curl -X GET "http://localhost:8080/api/balance/1"`


6. Returns the current balance of Account1.


7. Joint Account Validation 

    This application supports joint accounts, meaning multiple users can access and withdraw from the same account. **Concurrency control** ensures that withdrawals are processed in the correct order, and the balance never falls below $0.
    1.[x] Database-level Locking (SELECT ... FOR UPDATE)

       1. Before processing a withdrawal, the system **locks the account row** in the database to prevent race conditions.
       2. This ensures that no two transactions update the same balance simultaneously.
    2.[x] Transaction Handling 
       1. If the balance is sufficient, the withdrawal succeeds.
       2. If the balance is insuffiecient, the transaction is rejected.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Kafka Transaction Logging**

Every deposit/withdrawal is published to Kafka and then stored in an audit log table.

1.[x] Check Kafka Messages

    `docker exec -it kafka /bin/kafka-console-consumer --bootstrap-server kafka:9092 --topic transaction_topic --from-beginning`

2.[x] Check Audit Logs (MySQL)                                                      
 `docker exec -it mysql_db mysql -u root -p`

3.[x] Then run:

   `USE bank_system;
SELECT * FROM audit_logs;`

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Logs & Debugging**

1.[x] View API Logs

   `cat logs/bank_app.log`

2.[x] Logs all transactions and errors.

3.[x] Stop Services
                                                                                                                             
    `docker-compose down`

4.[x] Stops and removes all containers.

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Future Improvements**
2. [x] Kubernetes Deployment
3. [x] Monitoring (Grafana + Prometheus)
4. [x] More security features (Authentication, Rate limiting)

   