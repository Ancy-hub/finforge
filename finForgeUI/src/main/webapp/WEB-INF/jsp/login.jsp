<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="auth-page">

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-logo">
            <span class="logo-icon">&#128200;</span>
            <h1>Smart FinForge</h1>
        </div>

        <h2>Welcome Back</h2>
        <p class="auth-subtitle">Sign in to manage your finances</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty param.timeout}">
            <div class="alert alert-warning">Your session has expired. Please sign in again.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" novalidate>
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username"
                       class="form-control" placeholder="Enter your username"
                       value="${username}" required autofocus/>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <div class="password-wrapper">
                    <input type="password" id="password" name="password"
                           class="form-control" placeholder="Enter your password" required/>
                    <button type="button" class="toggle-password" onclick="togglePassword('password')">&#128065;</button>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign In</button>
        </form>

        <p class="auth-link">
            Don&rsquo;t have an account?
            <a href="${pageContext.request.contextPath}/register">Register here</a>
        </p>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
