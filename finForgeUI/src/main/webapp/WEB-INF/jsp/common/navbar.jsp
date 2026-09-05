<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String currentPage = request.getRequestURI();
    String ctx         = request.getContextPath();
%>
<nav class="sidebar">
    <div class="sidebar-header">
        <span class="logo-icon">&#128200;</span>
        <span class="logo-text">SmartTracker</span>
    </div>

    <ul class="nav-menu">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/dashboard"
               class="nav-link ${pageContext.request.requestURI.contains('dashboard') ? 'active' : ''}">
                <span class="nav-icon">&#127968;</span>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/expenses"
               class="nav-link ${pageContext.request.requestURI.contains('expense') ? 'active' : ''}">
                <span class="nav-icon">&#128683;</span>
                <span>Expenses</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/incomes"
               class="nav-link ${pageContext.request.requestURI.contains('income') ? 'active' : ''}">
                <span class="nav-icon">&#128181;</span>
                <span>Income</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/categories"
               class="nav-link ${pageContext.request.requestURI.contains('categor') ? 'active' : ''}">
                <span class="nav-icon">&#127991;</span>
                <span>Categories</span>
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/reports"
               class="nav-link ${pageContext.request.requestURI.contains('report') ? 'active' : ''}">
                <span class="nav-icon">&#128202;</span>
                <span>Reports</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div class="nav-divider"></div>
        <a href="${pageContext.request.contextPath}/profile"
           class="nav-link ${pageContext.request.requestURI.contains('profile') ? 'active' : ''}">
            <span class="nav-icon">&#128100;</span>
            <span>Profile</span>
        </a>
        <a href="${pageContext.request.contextPath}/change-password"
           class="nav-link ${pageContext.request.requestURI.contains('change-password') ? 'active' : ''}">
            <span class="nav-icon">&#128274;</span>
            <span>Change Password</span>
        </a>
        <a href="${pageContext.request.contextPath}/logout" class="nav-link nav-link-danger"
           onclick="return confirm('Are you sure you want to sign out?')">
            <span class="nav-icon">&#128682;</span>
            <span>Sign Out</span>
        </a>
    </div>
</nav>
