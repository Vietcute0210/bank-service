<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thông tin Tài khoản - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <jsp:include page="../common/user-navbar.jsp"/>

    <div class="container py-4">
        <h2 class="mb-4 fw-bold">Thông tin Tài khoản và Chuyển khoản</h2>

        <div class="content-card">
            <!-- Account Info -->
            <div class="row align-items-center mb-3">
                <div class="col-md-7">
                    <h4 class="fw-bold mb-3">Thông tin Tài khoản</h4>
                    <div class="info-line"><strong>Họ và tên:</strong> <span id="accFullName">---</span></div>
                    <div class="info-line"><strong>Email:</strong> <span id="accEmail">---</span></div>
                    <div class="info-line"><strong>Tên người dùng:</strong> <span id="accUsername">---</span></div>
                    <div class="info-line"><strong>Số điện thoại:</strong> <span id="accPhone">---</span></div>
                    <div class="info-line"><strong>Vai trò:</strong> <span id="accRole">---</span></div>
                </div>

                <!-- Account Balance & Deposit / Withdraw section -->
                <div class="col-md-5">
                    <div class="p-3 bg-light rounded border">
                        <h5 class="fw-bold mb-3"><i class="fa fa-wallet text-primary me-2"></i>Số dư tài khoản</h5>
                        <div class="mb-2">
                            <span class="text-secondary">Khả dụng:</span>
                            <span id="accountAvailableBalance" class="fs-4 fw-bold text-success ms-2">0 VND</span>
                        </div>
                        <div class="mb-3">
                            <span class="text-secondary">Tạm giữ (Hold):</span>
                            <span id="accountHoldBalance" class="fw-bold text-warning ms-2">0 VND</span>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-green flex-grow-1" onclick="handleUserDeposit()">
                                <i class="fa fa-plus-circle me-1"></i> Nạp tiền
                            </button>
                            <button type="button" class="btn btn-red flex-grow-1" onclick="handleUserWithdraw()">
                                <i class="fa fa-minus-circle me-1"></i> Rút tiền
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <hr class="my-4">

            <!-- Card List Section -->
            <div class="d-flex align-items-center justify-content-between mb-3">
                <h4 class="fw-bold mb-0">Danh sách thẻ của bạn</h4>
                <a href="/create-card" class="btn btn-yellow">Creat card</a>
            </div>

            <div id="cardsListContainer">
                <div class="text-secondary py-3">Đang tải danh sách thẻ...</div>
            </div>
        </div>
    </div>

    <script src="/js/account.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            initAccountPage();
        });
    </script>
</body>
</html>
