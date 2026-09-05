<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Error - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="auth-page">

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-logo">
            <span class="logo-icon" style="font-size:3rem;">&#9888;</span>
        </div>
        <h2>Oops! Something went wrong</h2>

        <%
            Integer statusCode  = (Integer)  request.getAttribute("jakarta.servlet.error.status_code");
            String  errorMsg    = (String)   request.getAttribute("jakarta.servlet.error.message");
            Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        %>

        <c:if test="${not empty requestScope['jakarta.servlet.error.status_code']}">
            <p class="text-muted">HTTP Status: <%= statusCode != null ? statusCode : "Unknown" %></p>
        </c:if>

        <p class="text-muted">
            <%= (errorMsg != null && !errorMsg.isEmpty()) ? errorMsg : "An unexpected error occurred." %>
        </p>

        <div class="form-actions" style="justify-content:center;">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                Go to Dashboard
            </a>
            <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
