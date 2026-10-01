<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Create Card - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <jsp:include page="../common/user-navbar.jsp"/>

    <div class="container py-4">
        <h2 class="fw-bold mb-1">Create Card</h2>
        <nav class="breadcrumb mb-4">
            <a href="/account">Card</a>
        </nav>

        <div class="content-card mx-auto" style="max-width: 800px;">
            <h3 class="text-center fw-bold mb-4">Create New Card</h3>
            <div id="createCardAlert"></div>
            
            <form id="createCardForm">
                <div class="row g-3 mb-3">
                    <div class="col-md-6">
                        <label class="form-label" for="expiryDate">Expiry Date:</label>
                        <input type="date" class="form-control" id="expiryDate" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="cardType">Card Type:</label>
                        <select class="form-select" id="cardType">
                            <option value="DEBIT" selected>DEBIT</option>
                            <option value="CREDIT">CREDIT</option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label" for="cardStatus">Status:</label>
                        <select class="form-select" id="cardStatus">
                            <option value="ACTIVE" selected>ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-blue">Create Card</button>
                    <a href="/account" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            if (!App.requireAuth()) return;
            App.initUserNavbar();

            // Set default expiry date to 3 years from today
            const d = new Date();
            d.setFullYear(d.getFullYear() + 3);
            document.getElementById('expiryDate').value = d.toISOString().split('T')[0];

            document.getElementById('createCardForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                const expiryDate = document.getElementById('expiryDate').value;
                const cardType = document.getElementById('cardType').value;
                const status = document.getElementById('cardStatus').value;

                try {
                    const res = await App.apiFetch('/api/v1/card', {
                        method: 'POST',
                        body: JSON.stringify({ expiryDate, cardType, status })
                    });

                    if (res && res.code === 1000) {
                        App.showAlert('createCardAlert', 'Tạo thẻ thành công! Đang chuyển về trang tài khoản...', 'success');
                        setTimeout(() => {
                            window.location.href = '/account';
                        }, 1200);
                    } else {
                        App.showAlert('createCardAlert', res.message || 'Tạo thẻ thất bại!');
                    }
                } catch (err) {
                    App.showAlert('createCardAlert', 'Lỗi: ' + err.message);
                }
            });
        });
    </script>
</body>
</html>
