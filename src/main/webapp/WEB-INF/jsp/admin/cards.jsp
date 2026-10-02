<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Manager Card - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">Manager Card</h1>
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <span class="text-secondary">Card</span>
                </div>

                <div class="admin-card-box">
                    <h4 class="fw-bold mb-3">Table Card</h4>

                    <div class="input-group mb-3">
                        <input type="text" class="form-control" id="searchCardInput" placeholder="Search by numberCard">
                        <button class="btn btn-outline-secondary" type="button" onclick="handleSearchCard()">Search</button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>User name:</th>
                                    <th>Card number</th>
                                    <th>Crad type</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="cardsTableBody">
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted">Đang tải danh sách thẻ...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="d-flex justify-content-center mt-3 gap-2">
                        <button class="btn btn-sm btn-outline-secondary">&laquo;</button>
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
            Admin.loadCardsTable();
        });

        function handleSearchCard() {
            const kw = document.getElementById('searchCardInput').value.trim().toLowerCase();
            const rows = document.querySelectorAll('#cardsTableBody tr');
            rows.forEach(r => {
                const text = r.textContent.toLowerCase();
                r.style.display = text.includes(kw) ? '' : 'none';
            });
        }
    </script>
</body>
</html>
