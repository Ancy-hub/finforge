<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Categories - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#127991; Categories</h2>
        <a href="${pageContext.request.contextPath}/categories?action=add" class="btn btn-primary">
            &#10133; Add Category
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            Category <c:out value="${param.success}"/> successfully.
        </div>
    </c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty categories}">
                <div class="empty-state">
                    <span class="empty-icon">&#127991;</span>
                    <p>No categories found.</p>
                    <a href="${pageContext.request.contextPath}/categories?action=add" class="btn btn-primary">
                        Add Your First Category
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Created At</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cat" items="${categories}" varStatus="status">
                                <tr>
                                    <td>${status.count}</td>
                                    <td><span class="badge badge-category">${cat.name}</span></td>
                                    <td>${cat.description}</td>
                                    <td>${cat.createdAt}</td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/categories?action=edit&id=${cat.categoryId}"
                                           class="btn btn-sm btn-edit">Edit</a>
                                        <form action="${pageContext.request.contextPath}/categories"
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this category? Expenses assigned to it may be affected.')">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id"     value="${cat.categoryId}"/>
                                            <button type="submit" class="btn btn-sm btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
