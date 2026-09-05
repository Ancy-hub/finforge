<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Register - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="auth-page">

<div class="auth-container">
    <div class="auth-card auth-card-wide">
        <div class="auth-logo">
            <span class="logo-icon">&#128200;</span>
            <h1>Smart FinForge</h1>
        </div>

        <h2>Create Account</h2>
        <p class="auth-subtitle">Start tracking your expenses today</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post" novalidate id="registerForm">

            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">First Name <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" class="form-control"
                           placeholder="First name"
                           value="${dto != null ? dto.firstName : ''}" required/>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" class="form-control"
                           placeholder="Last name"
                           value="${dto != null ? dto.lastName : ''}" required/>
                </div>
            </div>

            <div class="form-group">
                <label for="username">Username <span class="required">*</span></label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="Choose a username (3-50 chars, letters/digits/_)"
                       value="${dto != null ? dto.username : ''}" required/>
            </div>

            <div class="form-group">
                <label for="email">Email Address <span class="required">*</span></label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com"
                       value="${dto != null ? dto.email : ''}" required/>
            </div>

            <div class="form-group">
                <label for="phone">Phone (optional)</label>
                <input type="tel" id="phone" name="phone" class="form-control"
                       placeholder="+1 234 567 8900"
                       value="${dto != null ? dto.phone : ''}"/>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="password">Password <span class="required">*</span></label>
                    <div class="password-wrapper">
                        <input type="password" id="password" name="password"
                               class="form-control" placeholder="Min. 8 characters" required/>
                        <button type="button" class="toggle-password" onclick="togglePassword('password')">&#128065;</button>
                    </div>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
                    <div class="password-wrapper">
                        <input type="password" id="confirmPassword" name="confirmPassword"
                               class="form-control" placeholder="Repeat password" required/>
                        <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword')">&#128065;</button>
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Create Account</button>
        </form>

        <p class="auth-link">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login">Sign in</a>
        </p>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
