<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Transactions - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <span class="text-secondary">Transactions</span>
                </div>

                <div class="admin-card-box">
                    <h3 class="fw-bold mb-4 text-start">Table Transactions</h3>

                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>FromCardNumber</th>
                                    <th>ToCardNumber</th>
                                    <th>Amount</th>
                                    <th>Transaction Type</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="transactionsTableBody">
                                <tr>
                                    <td colspan="7" class="text-center py-4 text-muted">Đang tải danh sách giao dịch...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="d-flex justify-content-center mt-4 gap-2">
                        <button class="btn btn-sm btn-outline-secondary">&laquo;</button>
                        <button class="btn btn-sm btn-primary">1</button>
                        <button class="btn btn-sm btn-outline-secondary">&raquo;</button>
                    </div>
                </div>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            Admin.loadTransactionsTable(0, 10);
        });
    </script>
</body>
</html>
