<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Change Password - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>Change Password</h2>
        <p>Update your account password</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <div class="card form-card">
        <div class="card-header">
            <h3>&#128274; Security</h3>
        </div>
        <form action="${pageContext.request.contextPath}/change-password" method="post" novalidate>

            <div class="form-group">
                <label for="currentPassword">Current Password <span class="required">*</span></label>
                <div class="password-wrapper">
                    <input type="password" id="currentPassword" name="currentPassword"
                           class="form-control" required/>
                    <button type="button" class="toggle-password" onclick="togglePassword('currentPassword')">&#128065;</button>
                </div>
            </div>

            <div class="form-group">
                <label for="newPassword">New Password <span class="required">*</span></label>
                <div class="password-wrapper">
                    <input type="password" id="newPassword" name="newPassword"
                           class="form-control" placeholder="Min. 8 characters" required/>
                    <button type="button" class="toggle-password" onclick="togglePassword('newPassword')">&#128065;</button>
                </div>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm New Password <span class="required">*</span></label>
                <div class="password-wrapper">
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           class="form-control" required/>
                    <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword')">&#128065;</button>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Password</button>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
