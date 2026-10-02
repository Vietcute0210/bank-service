<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="admin-sidebar">
    <a href="/admin/dashboard" class="sidebar-brand">
        <span>Banking</span>
        <i class="fa fa-bars text-secondary" style="font-size: 16px;"></i>
    </a>
    
    <div class="sidebar-heading">CORE</div>
    <ul class="nav flex-column mb-auto">
        <li class="nav-item">
            <a class="nav-link" href="/admin/dashboard">
                <i class="fa fa-tachometer-alt"></i>
                <span>Dashboard</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="/admin/users">
                <i class="fa fa-user"></i>
                <span>User</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="/admin/cards">
                <i class="fa fa-credit-card"></i>
                <span>Card</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="/admin/transactions">
                <i class="fa fa-exchange-alt"></i>
                <span>Transaction</span>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="/admin/user-levels">
                <i class="fa fa-layer-group"></i>
                <span>UserLevel</span>
            </a>
        </li>
    </ul>

    <div class="sidebar-footer">
        <div>Logged in as:</div>
        <strong id="adminLoggedSpan">admin</strong>
    </div>
</div>
