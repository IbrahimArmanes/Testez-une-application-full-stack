# Yoga Application - Testing Guide

This repository contains a full-stack Yoga application built with Angular (frontend) and Java Spring Boot (backend). It includes comprehensive testing capabilities for both components.

## Project Structure

The project consists of two main parts located in their respective directories:

*   **`front/`**: Angular frontend application
*   **`back/`**: Java Spring Boot backend application

## Prerequisites

Before you begin, ensure you have the following installed:

*   Node.js (`v14+`) and npm (Node Package Manager)
*   Java (`11+`) JDK (Java Development Kit)
*   Maven (Build automation tool for Java)
*   MySQL database server

## Installation

Follow these steps to set up the project locally:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/Testez-une-application-full-stack.git
    cd Testez-une-application-full-stack
    ```
    *(Replace `your-username` with the actual username or organization)*

2.  **Database Setup:**
    *   Create a MySQL database (e.g., `yoga_app_db`).
    *   Run the provided SQL script to set up the necessary schema and initial data. Replace `your_username` and `your_database` with your MySQL username and the database name you just created.
        ```bash
        mysql -u your_username -p your_database < ressources/sql/script.sql
        ```
    *   **Default Admin Account:**
        *   **Login:** `yoga@studio.com`
        *   **Password:** `test!1234`

3.  **Backend Setup:**
    *   Navigate to the backend directory:
        ```bash
        cd back
        ```
    *   Build the project using Maven:
        ```bash
        mvn clean install
        ```
    *   *(Optional)* Configure your database connection details (URL, username, password) in `back/src/main/resources/application.properties` if they differ from the defaults.

4.  **Frontend Setup:**
    *   Navigate to the frontend directory:
        ```bash
        cd ../front
        # or cd path/to/Testez-une-application-full-stack/front if starting from another location
        ```
    *   Install the necessary Node modules:
        ```bash
        npm install
        ```

## Running the Application

You need to run both the backend and frontend servers.

### Backend

1.  Navigate to the backend directory:
    ```bash
    cd back
    # or cd path/to/Testez-une-application-full-stack/back
    ```
2.  Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```
    The backend API will start and be available at `http://localhost:8080`.

### Frontend

1.  Navigate to the frontend directory:
    ```bash
    cd front
    # or cd path/to/Testez-une-application-full-stack/front
    ```
2.  Start the Angular development server:
    ```bash
    npm run start
    ```
    The frontend application will be available in your browser at `http://localhost:4200`.

## Running Tests

Instructions for running the various tests included in the project.

### Backend Tests (Java/Spring Boot)

1.  Navigate to the backend directory:
    ```bash
    cd back
    # or cd path/to/Testez-une-application-full-stack/back
    ```
2.  Run the tests using Maven. This command will also generate a JaCoCo code coverage report.
    ```bash
    mvn clean test
    ```
3.  The JaCoCo code coverage report can be found at: `back/target/site/jacoco/index.html`. Open this file in your web browser to view the report.

### Frontend Tests (Angular)

#### Unit Tests (Karma/Jasmine)

1.  Navigate to the frontend directory:
    ```bash
    cd front
    # or cd path/to/Testez-une-application-full-stack/front
    ```
2.  To run the unit tests once:
    ```bash
    npm run test
    ```
3.  To run the unit tests in watch mode (automatically re-runs tests when files change):
    ```bash
    npm run test:watch
    ```

#### End-to-End Tests (Cypress)

1.  Navigate to the frontend directory:
    ```bash
    cd front
    # or cd path/to/Testez-une-application-full-stack/front
    ```
2.  Ensure the application (both frontend and backend) is running before executing E2E tests.
3.  To run the Cypress E2E tests (this might open the Cypress Test Runner GUI):
    ```bash
    npm run e2e
    ```
4.  To run E2E tests and generate a coverage report (usually runs headlessly):
    ```bash
    npm run e2e:coverage
    ```
5.  The E2E test coverage report can be found at: `front/coverage/lcov-report/index.html`. Open this file in your web browser to view the report.