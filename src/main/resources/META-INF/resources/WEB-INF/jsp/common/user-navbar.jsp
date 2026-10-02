<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg user-navbar">
    <div class="container-fluid">
        <a class="navbar-brand" href="/home">MyBank</a>
        <button class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse" data-bs-target="#userNavContent">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-end" id="userNavContent">
            <ul class="navbar-nav align-items-center mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/home">Trang Chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/account">Tài Khoản</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/history">Lịch Sử</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Dịch Vụ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Liên Hệ</a>
                </li>
                <li class="nav-item ms-lg-3">
                    <button id="btnLogout" class="btn btn-logout">Đăng Xuất</button>
                </li>
            </ul>
        </div>
    </div>
</nav>
