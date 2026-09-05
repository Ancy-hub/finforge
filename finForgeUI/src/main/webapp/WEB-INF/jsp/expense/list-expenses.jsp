<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Expenses - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#128683; Expenses</h2>
        <a href="${pageContext.request.contextPath}/expenses?action=add" class="btn btn-primary">
            &#10133; Add Expense
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            Expense <c:out value="${param.success}"/> successfully.
        </div>
    </c:if>

    <%-- ============================================================ --%>
    <%-- Filter card                                                  --%>
    <%-- ============================================================ --%>
    <div class="card filter-card">
        <form method="get" action="${pageContext.request.contextPath}/expenses" class="filter-form">
            <div class="filter-row">
                <div class="filter-field">
                    <label for="fromDate">From</label>
                    <input type="date" id="fromDate" name="fromDate"
                           value="${filter.fromDate}" class="form-control"/>
                </div>
                <div class="filter-field">
                    <label for="toDate">To</label>
                    <input type="date" id="toDate" name="toDate"
                           value="${filter.toDate}" class="form-control"/>
                </div>
                <div class="filter-field">
                    <label for="categoryId">Category</label>
                    <select id="categoryId" name="categoryId" class="form-control">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}"
                                <c:if test="${cat.categoryId == filter.categoryId}">selected</c:if>>
                                ${cat.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filter-actions">
                    <button type="submit" class="btn btn-primary">&#128269; Search</button>
                    <a href="${pageContext.request.contextPath}/expenses" class="btn btn-secondary">&#10006; Clear</a>
                </div>
            </div>
        </form>
    </div>

    <%-- ============================================================ --%>
    <%-- Results card                                                 --%>
    <%-- ============================================================ --%>
    <div class="card">
        <c:if test="${filtered == true}">
            <p class="filter-info">
                Showing <strong>${fn:length(expenses)}</strong> result(s) matching filter.
                <a href="${pageContext.request.contextPath}/expenses">Clear filter</a>
            </p>
        </c:if>
        <c:choose>
            <c:when test="${empty expenses}">
                <div class="empty-state">
                    <span class="empty-icon">&#128683;</span>
                    <c:choose>
                        <c:when test="${filtered == true}">
                            <p>No expenses match the selected filter.</p>
                        </c:when>
                        <c:otherwise>
                            <p>No expenses recorded yet.</p>
                            <a href="${pageContext.request.contextPath}/expenses?action=add" class="btn btn-primary">
                                Add Your First Expense
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <%-- Running total --%>
                <c:set var="grandTotal" value="0"/>
                <c:forEach var="exp" items="${expenses}">
                    <c:set var="grandTotal" value="${grandTotal + exp.amount}"/>
                </c:forEach>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Title</th>
                                <th>Category</th>
                                <th>Date</th>
                                <th class="text-right">Amount (&#8377;)</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="exp" items="${expenses}" varStatus="status">
                                <tr>
                                    <td>${status.count}</td>
                                    <td>
                                        <strong>${exp.title}</strong>
                                        <c:if test="${not empty exp.description}">
                                            <br/><small class="text-muted">${exp.description}</small>
                                        </c:if>
                                    </td>
                                    <td><span class="badge">${exp.categoryName}</span></td>
                                    <td>${exp.expenseDate}</td>
                                    <td class="text-right amount-cell">${exp.amount}</td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/expenses?action=edit&id=${exp.expenseId}"
                                           class="btn btn-sm btn-edit">Edit</a>
                                        <form action="${pageContext.request.contextPath}/expenses"
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this expense?')">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id"     value="${exp.expenseId}"/>
                                            <button type="submit" class="btn btn-sm btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr class="table-total-row">
                                <td colspan="4" class="text-right"><strong>Page Total</strong></td>
                                <td class="text-right amount-cell"><strong>&#8377; <fmt:formatNumber value="${grandTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <%-- Pagination controls (only when not filtering) --%>
                <c:if test="${not empty paged and filtered != true}">
                    <div class="pagination">
                        <c:choose>
                            <c:when test="${paged.hasPreviousPage}">
                                <a href="${pageContext.request.contextPath}/expenses?page=${paged.currentPage - 1}"
                                   class="btn btn-sm btn-secondary">&laquo; Prev</a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-sm btn-secondary" disabled>&laquo; Prev</button>
                            </c:otherwise>
                        </c:choose>

                        <span class="page-info">
                            Page <strong>${paged.currentPage}</strong> of <strong>${paged.totalPages}</strong>
                            &nbsp;&mdash;&nbsp; ${paged.totalItems} record(s)
                        </span>

                        <c:choose>
                            <c:when test="${paged.hasNextPage}">
                                <a href="${pageContext.request.contextPath}/expenses?page=${paged.currentPage + 1}"
                                   class="btn btn-sm btn-secondary">Next &raquo;</a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-sm btn-secondary" disabled>Next &raquo;</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
