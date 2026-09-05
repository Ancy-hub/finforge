<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String contextPath = request.getContextPath();
    String currentUri  = request.getRequestURI();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Dashboard - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<!-- Sidebar -->
<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>Dashboard</h2>
        <p>Welcome back, <strong>${sessionScope.loggedInUser.firstName}!</strong></p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- Summary Cards -->
    <div class="card-grid">
        <div class="summary-card income-card">
            <div class="card-icon">&#128181;</div>
            <div class="card-body">
                <h3>Total Income</h3>
                <p class="card-amount">&#8377; <c:out value="${report.totalIncome}"/></p>
            </div>
        </div>

        <div class="summary-card expense-card">
            <div class="card-icon">&#128683;</div>
            <div class="card-body">
                <h3>Total Expenses</h3>
                <p class="card-amount">&#8377; <c:out value="${report.totalExpense}"/></p>
            </div>
        </div>

        <div class="summary-card savings-card">
            <div class="card-icon">&#127381;</div>
            <div class="card-body">
                <h3>Net Savings</h3>
                <p class="card-amount ${report.netSavings < 0 ? 'negative' : ''}">
                    &#8377; <c:out value="${report.netSavings}"/>
                </p>
            </div>
        </div>
    </div>

    <!-- Quick Links -->
    <div class="quick-links">
        <h3>Quick Actions</h3>
        <div class="quick-link-grid">
            <a href="${pageContext.request.contextPath}/expenses?action=add" class="quick-link-card">
                <span>&#10133;</span> Add Expense
            </a>
            <a href="${pageContext.request.contextPath}/incomes?action=add" class="quick-link-card">
                <span>&#10133;</span> Add Income
            </a>
            <a href="${pageContext.request.contextPath}/reports" class="quick-link-card">
                <span>&#128202;</span> View Reports
            </a>
            <a href="${pageContext.request.contextPath}/categories" class="quick-link-card">
                <span>&#127991;</span> Categories
            </a>
        </div>
    </div>

    <!-- Category-wise expense mini table -->
    <c:if test="${not empty report.categoryExpenses}">
        <div class="card mt-4">
            <div class="card-header">
                <h3>Category-wise Expense Summary</h3>
            </div>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Category</th>
                        <th class="text-right">Amount (&#8377;)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${report.categoryExpenses}">
                        <tr>
                            <td>${cat.categoryName}</td>
                            <td class="text-right">${cat.totalAmount}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
