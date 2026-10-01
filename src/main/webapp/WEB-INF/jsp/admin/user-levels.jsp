<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>User Levels List - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h1 class="admin-title mb-0">User Levels List</h1>
                    <a href="/admin/user-levels/create" class="btn btn-blue">Add New User Level</a>
                </div>

                <div class="table-responsive bg-white rounded shadow-sm border mb-4">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-dark">
                            <tr>
                                <th class="py-3 px-4">ID</th>
                                <th class="py-3 px-4">Level Name</th>
                                <th class="py-3 px-4">Card Limit</th>
                                <th class="py-3 px-4">Daily Transfer Limit</th>
                                <th class="py-3 px-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="userLevelsTbody">
                            <tr>
                                <td colspan="5" class="text-center py-4 text-muted">Đang tải danh sách User Level...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <a href="/admin/dashboard" class="btn btn-blue px-4">Back</a>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            Admin.loadUserLevelsPage();
        });
    </script>
</body>
</html>
