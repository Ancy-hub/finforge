<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Add Expense - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#10133; Add Expense</h2>
        <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">
            &larr; Back to List
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card form-card">
        <form action="${pageContext.request.contextPath}/expenses?action=add" method="post" novalidate>

            <div class="form-group">
                <label for="title">Title <span class="required">*</span></label>
                <input type="text" id="title" name="title" class="form-control"
                       placeholder="e.g. Grocery shopping"
                       value="${dto != null ? dto.title : ''}" required maxlength="100"/>
            </div>

            <div class="form-group">
                <label for="description">Description (optional)</label>
                <textarea id="description" name="description" class="form-control"
                          rows="3" placeholder="Additional details..."
                          maxlength="500">${dto != null ? dto.description : ''}</textarea>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="amount">Amount (&#8377;) <span class="required">*</span></label>
                    <input type="number" id="amount" name="amount" class="form-control"
                           placeholder="0.00" step="0.01" min="0.01"
                           value="${dto != null ? dto.amount : ''}" required/>
                </div>
                <div class="form-group">
                    <label for="expenseDate">Expense Date <span class="required">*</span></label>
                    <input type="date" id="expenseDate" name="expenseDate" class="form-control"
                           value="${dto != null ? dto.expenseDate : ''}" required/>
                </div>
            </div>

            <div class="form-group">
                <label for="categoryId">Category <span class="required">*</span></label>
                <select id="categoryId" name="categoryId" class="form-control" required>
                    <option value="">-- Select Category --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}"
                            ${dto != null && dto.categoryId == cat.categoryId.toString() ? 'selected' : ''}>
                            ${cat.name}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Save Expense</button>
                <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
