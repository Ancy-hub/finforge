<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Edit Category - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#9999; Edit Category</h2>
        <a href="${pageContext.request.contextPath}/categories" class="btn btn-secondary">
            &larr; Back to List
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card form-card">
        <form action="${pageContext.request.contextPath}/categories?action=edit" method="post" novalidate>
            <input type="hidden" name="categoryId" value="${category.categoryId}"/>

            <div class="form-group">
                <label for="name">Category Name <span class="required">*</span></label>
                <input type="text" id="name" name="name" class="form-control"
                       value="${category.name}" required maxlength="50"/>
            </div>

            <div class="form-group">
                <label for="description">Description (optional)</label>
                <textarea id="description" name="description" class="form-control"
                          rows="3" maxlength="200">${category.description}</textarea>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Update Category</button>
                <a href="${pageContext.request.contextPath}/categories" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
