<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Financial Reports - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#128202; Financial Reports</h2>
        <p>Your complete financial summary</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty report}">

        <!-- Summary Banner -->
        <div class="card-grid mb-4">
            <div class="summary-card income-card">
                <div class="card-icon">&#128181;</div>
                <div class="card-body">
                    <h3>Total Income</h3>
                    <p class="card-amount">&#8377; ${report.totalIncome}</p>
                </div>
            </div>
            <div class="summary-card expense-card">
                <div class="card-icon">&#128683;</div>
                <div class="card-body">
                    <h3>Total Expense</h3>
                    <p class="card-amount">&#8377; ${report.totalExpense}</p>
                </div>
            </div>
            <div class="summary-card savings-card">
                <div class="card-icon">&#127381;</div>
                <div class="card-body">
                    <h3>Net Savings</h3>
                    <p class="card-amount"
                       style="${report.netSavings.signum() < 0 ? 'color:var(--danger)' : ''}">
                        &#8377; ${report.netSavings}
                    </p>
                </div>
            </div>
        </div>

        <!-- Monthly Expense Summary -->
        <div class="card mb-4">
            <div class="card-header">
                <h3>&#128197; Monthly Expense Summary</h3>
            </div>
            <c:choose>
                <c:when test="${empty report.monthlyExpenses}">
                    <p class="text-muted p-3">No expense data available.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Month</th>
                                    <th class="text-right">Total Expense (&#8377;)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${report.monthlyExpenses}">
                                    <tr>
                                        <td>${row.month}</td>
                                        <td class="text-right amount-cell">${row.totalAmount}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Category-wise Expense Summary -->
        <div class="card">
            <div class="card-header">
                <h3>&#127991; Category-wise Expense Summary</h3>
            </div>
            <c:choose>
                <c:when test="${empty report.categoryExpenses}">
                    <p class="text-muted p-3">No category expense data available.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Category</th>
                                    <th class="text-right">Total Expense (&#8377;)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="row" items="${report.categoryExpenses}">
                                    <tr>
                                        <td><span class="badge badge-category">${row.categoryName}</span></td>
                                        <td class="text-right amount-cell">${row.totalAmount}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </c:if>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
