<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Nhập - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <h2>Đăng Nhập</h2>
            <div id="loginAlert"></div>
            <form id="loginForm">
                <div class="mb-3">
                    <label class="form-label" for="username">Tên đăng nhập</label>
                    <input type="text" class="form-control" id="username" required placeholder="Nhập tên đăng nhập">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="password">Mật khẩu</label>
                    <input type="password" class="form-control" id="password" required placeholder="Nhập mật khẩu">
                </div>
                <button type="submit" class="btn btn-auth-primary">Đăng Nhập</button>
            </form>
            <div class="auth-footer-link">
                Chưa có tài khoản? <a href="/register">Đăng ký ngay</a>
            </div>
        </div>
    </div>

    <script src="/js/login.js"></script>
</body>
</html>
