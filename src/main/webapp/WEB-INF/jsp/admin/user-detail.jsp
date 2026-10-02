<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Detail User - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">Detail User</h1>
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <a href="/admin/users">User</a>
                </div>

                <div class="row g-4">
                    <!-- Left: User Information -->
                    <div class="col-lg-4">
                        <div class="admin-card-box">
                            <h5 class="fw-bold border-bottom pb-2 mb-3">User Information</h5>
                            <div class="mb-2"><strong>ID:</strong> <span id="uDetailId">---</span></div>
                            <div class="mb-2"><strong>Email:</strong> <span id="uDetailEmail">---</span></div>
                            <div class="mb-2"><strong>Full Name:</strong> <span id="uDetailFullName">---</span></div>
                            <div class="mb-2"><strong>Phone:</strong> <span id="uDetailPhone">---</span></div>
                            <div class="mb-2"><strong>User Name:</strong> <span id="uDetailUsername">---</span></div>
                            <div class="mb-3"><strong>Role:</strong> <span id="uDetailRole">---</span></div>

                            <a href="/admin/users" class="btn btn-blue">Back</a>
                        </div>
                    </div>

                    <!-- Right: User Cards -->
                    <div class="col-lg-8">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h3 class="fw-bold mb-0">User Cards</h3>
                            <a id="btnCreateCardForUser" href="#" class="btn btn-blue">Create Card</a>
                        </div>

                        <div class="row g-3" id="userCardsGrid">
                            <div class="col-12 text-muted">Đang tải thẻ của người dùng...</div>
                        </div>
                    </div>
                </div>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            Admin.loadUserDetailPage();
        });
    </script>
</body>
</html>
