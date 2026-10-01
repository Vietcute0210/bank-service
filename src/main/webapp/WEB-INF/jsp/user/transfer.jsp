<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Chuyển khoản - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <jsp:include page="../common/user-navbar.jsp"/>

    <div class="container py-4">
        <h2 class="fw-bold mb-4">Chuyển khoản</h2>

        <div id="transferAlert"></div>

        <div class="row g-4">
            <!-- Left Card: Thông tin thẻ của bạn -->
            <div class="col-md-6">
                <div class="transfer-panel">
                    <div class="transfer-panel-header">
                        Thông tin thẻ của bạn
                    </div>
                    <div class="transfer-panel-body">
                        <div class="mb-3">
                            <span class="fw-bold">Số thẻ:</span> <span id="senderCardNumber">---</span>
                        </div>
                        <div class="mb-3">
                            <span class="fw-bold">Loại thẻ:</span> <span id="senderCardType">DEBIT</span>
                        </div>
                        <div class="mb-3">
                            <span class="fw-bold">Số dư:</span> <span id="senderBalance">0</span>
                        </div>
                        <div class="mb-4">
                            <span class="fw-bold">Đang chờ xử lý:</span> <span id="senderHoldBalance">0</span>
                        </div>

                        <a href="/account" class="btn btn-yellow w-100 py-2">Trở lại</a>
                    </div>
                </div>
            </div>

            <!-- Right Card: Thông tin người nhận & Các bước chuyển tiền -->
            <div class="col-md-6">
                <div class="transfer-panel">
                    <div class="transfer-panel-header">
                        Thông tin người nhận
                    </div>
                    <div class="transfer-panel-body">
                        <!-- Step 1: Search card -->
                        <div class="mb-3">
                            <label class="form-label" for="searchCardInput">Số thẻ:</label>
                            <input type="text" class="form-control mb-2" id="searchCardInput" placeholder="Nhập số thẻ">
                            <button type="button" class="btn btn-gray" onclick="handleSearchRecipient()">Tìm kiếm</button>
                        </div>

                        <!-- Step 2: Show Recipient Details & Amount (Initially Hidden) -->
                        <div id="step2Section" style="display: none;" class="mt-4">
                            <div class="mb-3">
                                <label class="form-label">Số thẻ người nhận:</label>
                                <input type="text" class="form-control input-readonly" id="recipientCardNumberReadonly" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Tên người nhận:</label>
                                <input type="text" class="form-control input-readonly" id="recipientNameReadonly" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="transferAmountInput">Số tiền:</label>
                                <input type="number" class="form-control" id="transferAmountInput" placeholder="Nhập số tiền chuyển">
                            </div>
                            <button type="button" id="btnInitiateTransfer" class="btn btn-blue mb-3" onclick="handleInitiateTransfer()">
                                Xác nhận chuyển khoản
                            </button>
                        </div>

                        <!-- Step 3: Enter OTP (Initially Hidden) -->
                        <div id="step3Section" style="display: none;" class="mt-3 pt-3 border-top">
                            <div class="mb-3">
                                <label class="form-label fw-bold" for="otpInput">Mã OTP:</label>
                                <input type="text" class="form-control" id="otpInput" maxlength="6" placeholder="Nhập mã OTP (6 chữ số)">
                            </div>
                            <button type="button" class="btn btn-green" onclick="handleConfirmOtp()">
                                Xác nhận OTP
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="/js/transfer.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            initTransferPage();
        });
    </script>
</body>
</html>
