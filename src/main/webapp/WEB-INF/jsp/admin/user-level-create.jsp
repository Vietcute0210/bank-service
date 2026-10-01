<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Create User Level - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">User Level Management</h1>
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <a href="/admin/user-levels">User Level</a>
                </div>

                <div class="admin-card-box mx-auto" style="max-width: 750px;">
                    <h3 class="fw-bold text-center mb-4">Create New User Level</h3>
                    <div id="createLevelAlert"></div>

                    <form id="createLevelForm">
                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label" for="levelName">Level Name:</label>
                                <input type="text" class="form-control" id="levelName" required placeholder="Ví dụ: VIP4">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="cardLimit">Card Limit:</label>
                                <input type="number" class="form-control" id="cardLimit" required min="1" value="5">
                            </div>
                        </div>

                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label" for="dailyTransferLimit">Daily Transfer Limit:</label>
                                <input type="number" class="form-control" id="dailyTransferLimit" required min="0" value="1000000">
                            </div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-blue">Create User Level</button>
                            <a href="/admin/user-levels" class="btn btn-secondary">Cancel</a>
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
            Admin.initUserLevelCreatePage();
        });
    </script>
</body>
</html>
