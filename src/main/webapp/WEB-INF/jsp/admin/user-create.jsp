<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Create User - Banking</title>
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
                    <a href="/admin/dashboard">Dashboard</a> / <a href="/admin/users">User</a>
                </div>

                <div class="admin-card-box mx-auto" style="max-width: 650px;">
                    <h3 class="fw-bold text-center mb-4">Create New User</h3>
                    <div id="createUserAlert"></div>

                    <form id="createUserForm">
                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label" for="newUsername">Username:</label>
                                <input type="text" class="form-control" id="newUsername" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="newPassword">Password:</label>
                                <input type="password" class="form-control" id="newPassword" required>
                            </div>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label" for="newEmail">Email:</label>
                                <input type="email" class="form-control" id="newEmail" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="newPhone">Phone Number:</label>
                                <input type="text" class="form-control" id="newPhone" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="newFullName">Full Name:</label>
                            <input type="text" class="form-control" id="newFullName" required>
                        </div>

                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label" for="newRole">Role:</label>
                                <select class="form-select" id="newRole">
                                    <option value="USER" selected>USER</option>
                                    <option value="ADMIN">ADMIN</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="newLevel">Level (VIP):</label>
                                <select class="form-select" id="newLevel">
                                </select>
                            </div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-blue">Create User</button>
                            <a href="/admin/users" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            Admin.initUserCreatePage();
        });
    </script>
</body>
</html>
