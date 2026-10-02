<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lịch Sử Giao Dịch - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <jsp:include page="../common/user-navbar.jsp"/>

    <div class="container py-4">
        <h2 class="fw-bold mb-4">Lịch Sử Giao Dịch</h2>

        <div class="table-responsive bg-white rounded shadow-sm border mb-4">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="py-3 px-4">ID Giao Dịch</th>
                        <th class="py-3 px-4">Ngày Giao Dịch</th>
                        <th class="py-3 px-4">Số Tiền</th>
                        <th class="py-3 px-4">Loại Giao Dịch</th>
                        <th class="py-3 px-4">Trạng Thái</th>
                        <th class="py-3 px-4">Số Thẻ Gửi</th>
                        <th class="py-3 px-4">Số Thẻ Nhận</th>
                    </tr>
                </thead>
                <tbody id="historyTableBody">
                    <tr>
                        <td colspan="7" class="text-center py-4 text-muted">Đang tải lịch sử giao dịch...</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <a href="/home" class="btn btn-blue px-4">Home</a>
    </div>

    <script src="/js/history.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            initHistoryPage();
        });
    </script>
</body>
</html>
