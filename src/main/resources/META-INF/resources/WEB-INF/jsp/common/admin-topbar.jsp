<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="admin-topbar">
    <div class="d-flex align-items-center gap-3">
        <a href="/admin/dashboard" class="navbar-brand">Banking</a>
        <i class="fa fa-bars text-secondary cursor-pointer"></i>
    </div>
    <div class="user-info">
        <span id="adminNameSpan">Welcome admin</span>
        <div class="dropdown">
            <a href="#" class="text-white text-decoration-none dropdown-toggle" id="adminUserDropdown" data-bs-toggle="dropdown">
                <i class="fa fa-user-circle fa-lg"></i>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow">
                <li><a class="dropdown-item" href="/home">Về trang chủ User</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#" id="btnAdminLogout">Đăng Xuất</a></li>
            </ul>
        </div>
    </div>
</div>
