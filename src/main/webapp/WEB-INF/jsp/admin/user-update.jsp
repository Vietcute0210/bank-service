<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Update User - Banking</title>
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

                <div class="admin-card-box mx-auto" style="max-width: 600px;">
                    <h3 class="fw-bold text-center mb-4">Update User</h3>
                    <div id="updateUserAlert"></div>

                    <form id="updateUserForm">
                        <div class="mb-3">
                            <label class="form-label" for="updateEmail">Email:</label>
                            <input type="email" class="form-control input-readonly" id="updateEmail" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="updatePhone">Phone Number:</label>
                            <input type="text" class="form-control" id="updatePhone" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="updateFullName">Full Name:</label>
                            <input type="text" class="form-control" id="updateFullName" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="updateRole">Role:</label>
                            <select class="form-select" id="updateRole">
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>
                        <div class="mb-4">
                            <label class="form-label" for="updateLevel">Level:</label>
                            <select class="form-select" id="updateLevel">
                            </select>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-blue">Submit</button>
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
            Admin.loadUserUpdatePage();
        });
    </script>
</body>
</html>
