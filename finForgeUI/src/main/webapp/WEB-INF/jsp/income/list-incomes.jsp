<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Income - Smart FinForge</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body class="app-page">

<jsp:include page="/WEB-INF/jsp/common/navbar.jsp"/>

<div class="main-content">
    <div class="page-header">
        <h2>&#128181; Income</h2>
        <a href="${pageContext.request.contextPath}/incomes?action=add" class="btn btn-primary">
            &#10133; Add Income
        </a>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            Income record <c:out value="${param.success}"/> successfully.
        </div>
    </c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty incomes}">
                <div class="empty-state">
                    <span class="empty-icon">&#128181;</span>
                    <p>No income records found.</p>
                    <a href="${pageContext.request.contextPath}/incomes?action=add" class="btn btn-primary">
                        Add Your First Income
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <%-- Running total --%>
                <c:set var="grandTotal" value="0"/>
                <c:forEach var="inc" items="${incomes}">
                    <c:set var="grandTotal" value="${grandTotal + inc.amount}"/>
                </c:forEach>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Source</th>
                                <th>Date</th>
                                <th class="text-right">Amount (&#8377;)</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="inc" items="${incomes}" varStatus="status">
                                <tr>
                                    <td>${status.count}</td>
                                    <td>${inc.source}</td>
                                    <td>${inc.incomeDate}</td>
                                    <td class="text-right amount-cell income-amount">${inc.amount}</td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/incomes?action=edit&id=${inc.incomeId}"
                                           class="btn btn-sm btn-edit">Edit</a>
                                        <form action="${pageContext.request.contextPath}/incomes"
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Delete this income record?')">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id"     value="${inc.incomeId}"/>
                                            <button type="submit" class="btn btn-sm btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot>
                            <tr class="table-total-row">
                                <td colspan="3" class="text-right"><strong>Page Total</strong></td>
                                <td class="text-right amount-cell income-amount"><strong>&#8377; <fmt:formatNumber value="${grandTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <%-- Pagination controls --%>
                <c:if test="${not empty paged}">
                    <div class="pagination">
                        <c:choose>
                            <c:when test="${paged.hasPreviousPage}">
                                <a href="${pageContext.request.contextPath}/incomes?page=${paged.currentPage - 1}"
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
                                <a href="${pageContext.request.contextPath}/incomes?page=${paged.currentPage + 1}"
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
