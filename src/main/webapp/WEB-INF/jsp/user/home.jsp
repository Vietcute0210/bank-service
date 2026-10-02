<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang Chủ - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <jsp:include page="../common/user-navbar.jsp"/>

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container">
            <h1>Chào mừng <span id="homeUsername">bạn</span></h1>
            <p>Ngân hàng số - Giải pháp tài chính hiện đại và an toàn</p>
            <a href="/account" class="btn btn-hero">Tìm hiểu thêm</a>
        </div>
    </div>

    <!-- Feature Grid -->
    <div class="container py-5">
        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="icon"><i class="fa fa-dollar-sign"></i></div>
                    <h4>Giao dịch An Toàn</h4>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="icon"><i class="fa fa-chart-line"></i></div>
                    <h4>Tư Vấn Tài Chính</h4>
                </div>
            </div>
            <div class="col-md-4">
                <div class="feature-box">
                    <div class="icon"><i class="fa fa-headset"></i></div>
                    <h4>Hỗ Trợ 24/7</h4>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            if (!App.requireAuth()) return;
            App.initUserNavbar();

            const user = App.getUser();
            if (user && user.username) {
                document.getElementById('homeUsername').textContent = user.username;
            }
        });
    </script>
</body>
</html>
