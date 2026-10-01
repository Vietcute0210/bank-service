<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Đăng Ký - MyBank</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <h2>Đăng Ký</h2>
            <div id="registerAlert"></div>
            <form id="registerForm">
                <div class="mb-3">
                    <label class="form-label" for="username">Tên đăng nhập</label>
                    <input type="text" class="form-control" id="username" required placeholder="Nhập tên đăng nhập">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="email">Email</label>
                    <input type="email" class="form-control" id="email" required placeholder="Nhập email">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="password">Mật khẩu</label>
                    <input type="password" class="form-control" id="password" required placeholder="Nhập mật khẩu">
                </div>
                <div class="mb-3">
                    <label class="form-label" for="rePassword">Nhập lại mật khẩu</label>
                    <input type="password" class="form-control" id="rePassword" required placeholder="Nhập lại mật khẩu">
                </div>
                <button type="submit" class="btn btn-auth-success">Đăng Ký</button>
            </form>
            <div class="auth-footer-link">
                Đã có tài khoản? <a href="/login">Đăng nhập ngay</a>
            </div>
        </div>
    </div>

    <script src="/js/register.js"></script>
</body>
</html>
