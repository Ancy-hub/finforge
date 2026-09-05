<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Add Income - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#10133; Add Income</h2>
        <a href="${pageContext.request.contextPath}/incomes" class="btn btn-secondary">
            &larr; Back to List
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card form-card">
        <form action="${pageContext.request.contextPath}/incomes?action=add" method="post" novalidate>

            <div class="form-group">
                <label for="source">Income Source <span class="required">*</span></label>
                <input type="text" id="source" name="source" class="form-control"
                       placeholder="e.g. Salary, Freelance, Dividend"
                       value="${dto != null ? dto.source : ''}" required maxlength="100"/>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="amount">Amount (&#8377;) <span class="required">*</span></label>
                    <input type="number" id="amount" name="amount" class="form-control"
                           placeholder="0.00" step="0.01" min="0.01"
                           value="${dto != null ? dto.amount : ''}" required/>
                </div>
                <div class="form-group">
                    <label for="incomeDate">Income Date <span class="required">*</span></label>
                    <input type="date" id="incomeDate" name="incomeDate" class="form-control"
                           value="${dto != null ? dto.incomeDate : ''}" required/>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Income</button>
                <a href="${pageContext.request.contextPath}/incomes" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
