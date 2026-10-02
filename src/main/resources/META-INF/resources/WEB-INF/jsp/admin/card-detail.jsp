<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Card Details - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">Card Details</h1>
                <div class="breadcrumb">
                    <a href="/admin/dashboard">Dashboard</a> / <a href="/admin/cards">Card</a>
                </div>

                <div class="row g-4">
                    <!-- Left: Card Information -->
                    <div class="col-lg-6">
                        <div class="admin-card-box">
                            <h5 class="fw-bold border-bottom pb-2 mb-3">Card Information</h5>
                            <div class="mb-2"><strong>Card ID:</strong> <span id="cardDetailId">---</span></div>
                            <div class="mb-2"><strong>Card Number:</strong> <span id="cardDetailNumber">---</span></div>
                            <div class="mb-2"><strong>Card Type:</strong> <span id="cardDetailType">---</span></div>
                            <div class="mb-2"><strong>Expiry Date:</strong> <span id="cardDetailExpiry">---</span></div>
                            <div class="mb-2"><strong>Status:</strong> <span id="cardDetailStatus">---</span></div>
                            <div class="mb-2"><strong>User ID:</strong> <span id="cardDetailUserId">---</span></div>
                            <div class="mb-2"><strong>User Name:</strong> <span id="cardDetailUserName">---</span></div>
                            <div class="mb-4"><strong>User Email:</strong> <span id="cardDetailUserEmail">---</span></div>

                            <a href="/admin/cards" class="btn btn-blue">Back to User</a>
                        </div>
                    </div>

                    <!-- Right: Balance Information + Deposit/Withdraw -->
                    <div class="col-lg-6">
                        <div class="admin-card-box">
                            <h5 class="fw-bold border-bottom pb-2 mb-3">Balance Information</h5>
                            <div class="mb-2"><strong>Balance ID:</strong> <span id="cardDetailBalanceId">---</span></div>
                            <div class="mb-2"><strong>Available Balance:</strong> <span id="cardDetailAvailableBalance">0</span></div>
                            <div class="mb-2"><strong>Hold Balance:</strong> <span id="cardDetailHoldBalance">0</span></div>
                            <div class="mb-4"><strong>Last Updated:</strong> <span id="cardDetailLastUpdated">---</span></div>

                            <!-- Deposit Section -->
                            <div class="mb-4">
                                <label class="form-label fw-bold" for="depositAmountInput">Deposit Amount:</label>
                                <input type="number" class="form-control mb-2" id="depositAmountInput" placeholder="Nhập số tiền nạp">
                                <button type="button" class="btn btn-green" onclick="handleCardDeposit()">Deposit</button>
                            </div>

                            <!-- Withdraw Section -->
                            <div class="mb-3">
                                <label class="form-label fw-bold" for="withdrawAmountInput">Withdraw Amount:</label>
                                <input type="number" class="form-control mb-2" id="withdrawAmountInput" placeholder="Nhập số tiền rút (lớn hơn 1 triệu)">
                                <button type="button" class="btn btn-red" onclick="handleCardWithdraw()">Withdraw</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        let currentCardId = null;

        document.addEventListener('DOMContentLoaded', () => {
            const pathParts = window.location.pathname.split('/');
            currentCardId = pathParts[pathParts.length - 1];
            Admin.loadCardDetail(currentCardId);
        });

        function handleCardDeposit() {
            Admin.depositToCard(currentCardId);
        }

        function handleCardWithdraw() {
            Admin.withdrawFromCard(currentCardId);
        }
    </script>
</body>
</html>
