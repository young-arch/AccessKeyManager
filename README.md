# AccessKey Management System

## Overview
The AccessKey Management System is a web application designed to manage access keys for various users, including School IT personnel and administrators. The system supports user registration, access key creation, password reset, and email notifications.

## Features
- User Registration
- User Authentication
- Role-based Access Control
- Access Key Creation
- Invalidate Access Key
- Find Expired and Active AccessKey
- Password Reset via Email
- Email Notifications
- Thymeleaf-based Frontend

## Technologies Used
- Backend:
  - Java
  - Spring Boot
  - Spring Security
  - Spring Data JPA
  - PostgreSQL
  - JavaMailSender
  - RESTful APIs
  -Spring-boot-starter Test
  -junit - jupiter
- Frontend:
  - Thymeleaf
  - Bootstrap (for frontend styling)

## Getting Started

### Prerequisites
- JDK 11 or higher
- Maven 3.6.3 or higher
- PostgreSQL 12 or higher

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/young-arch/AccessKeyManager.git
   cd AccessKeyManager
   ```

2. **Configure the database:**
   - Ensure PostgreSQL is installed and running.
   - Create a database named `postgres`.
   - Update the `application.properties` file with your PostgreSQL credentials.

3. **Update Email Configuration:**
   - Configure the email settings in `application.properties`:
     ```properties
     spring.mail.host=smtp.gmail.com
     spring.mail.port=587
     spring.mail.username=your-email@gmail.com
     spring.mail.password=your-email-password
     spring.mail.properties.mail.smtp.auth=true
     spring.mail.properties.mail.smtp.starttls.enable=true
     spring.mail.properties.mail.smtp.starttls.required=true
     spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
     ```

4. **Build the application:**
   ```bash
   mvn clean install
   ```

5. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
### ADMIN ACCOUNT DETAILS
 ```
mail: mawulegabriel@gmail.com
password: masters

 ```

### Thymeleaf Frontend
The application uses Thymeleaf templates for the frontend, providing a dynamic and interactive user interface. The following templates are included:
## auth/home
-  `index.html`: Homepage with links to Login and Signup pages. 

### auth/login
- `login.html`: Login page. 

### auth/signup
- `signup.html`: Signup page.

### auth/admin
-`adminPanel.html`: Admin page.

### auth/user
-`userPanel.html`: Signup page.

- `forgot-password.html`: Password reset request page.

- `reset-password.html`: Password reset confirmation page.

These templates are styled using Bootstrap for a modern and responsive design.

#### Homepage
The homepage (`index.html`) provides an overview of the application's purpose and includes buttons to navigate to the login and signup pages.


### API Endpoints

#### User Endpoints
- **Register a new user:**
  ```http
  POST /api/users
  {
      "email": "user@example.com",
      "password": "password",
      "role": "SCHOOL_IT"
  }
  ```

- **Verify your account by email:**
  ```http
  GET /api/users/verify/confirm?token={token}
  ```

- **Initiate password reset:**
  ```http
  POST /api/users/password/resets
  {
      "email": "user@example.com"
  }
  ```

- **Confirm password reset:**
  ```http
  POST /api/users/password/resets/confirms
  {
      "newPassword": "new-password",
      "confirmPassword": "confirm-password"
  }
  ```

#### Access Key Endpoints

- **Create an access key (School IT):**
  ```http
  GET /api/users/createAccessKey?customKeyName=exampleKey
  ```

- **View your accessKeys (School IT):**
  ```http
  GET /api/users/myAccessKeys
  ```

- **Get all access keys generated on the platform (Admin only):**
  ```http
  GET /api/accesskeys/all
  ```

- **Revoke access key (Admin only):**
  ```http
  GET /api/accesskeys/revoke?email={email}
  ```

- **Find active access keys by email (Admin only):**
  ```http
  GET /api/accesskeys/active/email?email={email}
  ```

- **Find all expired access keys (Admin only):**
  ```http
  GET /api/accesskeys/expired
  ```

### Role-Based Access Control
- **Admin**: Can manage all users and access keys.
- **School IT**: Can create access keys.

### Unit Tests
The application includes unit tests to ensure all edge cases are handled properly. Here are the cases tested in the `UserServiceTest` class:

- **createUser**: Test user creation with valid inputs.
- **loginUser**: Test user login with correct credentials.
- **loginUser_withInvalidPassword**: Test user login with incorrect password.
- **confirmVerification**: Test account verification with a valid token.
- **confirmVerification_withExpiredToken**: Test account verification with an expired token.

### ER Diagram
The Entity-Relationship (ER) diagram of the database is included in the project repository under the folder name `ER_Diagram_Database`.

### Using Postman
1. Import the provided Postman collection to access and test all endpoints.
2. Ensure the server is running.
3. Execute requests and verify responses.

### Troubleshooting
- **Mail Server Connection Issues**: Ensure your email configuration is correct and the email server is accessible.
- **Database Connection Issues**: Ensure PostgreSQL is running and the credentials in `application.properties` are correct..

## Contact
For any questions or feedback, please contact gabriel.sakyi@ucc.stu.edu.gh
```
