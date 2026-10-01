<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Manager User - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">Manager User</h1>
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <span class="text-secondary">User</span>
                </div>

                <div class="admin-card-box">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="fw-bold mb-0">Table User</h4>
                        <a href="/admin/users/create" class="btn btn-blue">Create a user</a>
                    </div>

                    <!-- Search and Level Filter Row -->
                    <div class="row g-2 mb-3">
                        <div class="col-md-8">
                            <div class="input-group">
                                <input type="text" class="form-control" id="searchUserKeyword" placeholder="Search by username">
                                <button class="btn btn-outline-secondary" type="button" onclick="loadFilteredUsers()">Search</button>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <select class="form-select" id="levelFilterSelect" onchange="loadFilteredUsers()">
                                <option value="">-- Tất cả cấp VIP --</option>
                                <option value="VIP1">VIP1</option>
                                <option value="VIP2">VIP2</option>
                                <option value="VIP3">VIP3</option>
                            </select>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>User name</th>
                                    <th>Email</th>
                                    <th>ROLE</th>
                                    <th>Level</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="adminUsersTbody">
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted">Đang tải danh sách người dùng...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="d-flex justify-content-center mt-3 gap-2">
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
            Admin.loadUsersPage();
        });
        function loadFilteredUsers() {
            Admin.loadUsersPage();
        }
    </script>
</body>
</html>
