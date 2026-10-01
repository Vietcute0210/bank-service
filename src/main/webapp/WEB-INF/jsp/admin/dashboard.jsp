<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Admin Dashboard - Banking</title>
    <jsp:include page="../common/head.jsp"/>
</head>
<body>
    <div class="admin-wrapper">
        <jsp:include page="../common/admin-sidebar.jsp"/>

        <div class="admin-main">
            <jsp:include page="../common/admin-topbar.jsp"/>

            <div class="admin-content">
                <h1 class="admin-title">Dashboard</h1>
                <div class="breadcrumb text-secondary">Dashboard</div>

                <!-- 4 Stat Cards -->
                <div class="row g-4 mb-4">
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card primary">
                            <div class="stat-card-title">Primary Card (<span id="statTotalUsers">0</span> Users)</div>
                            <a href="/admin/users" class="stat-card-footer">
                                <span>View Details</span>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card warning">
                            <div class="stat-card-title">Warning Card (<span id="statTotalCards">0</span> Cards)</div>
                            <a href="/admin/cards" class="stat-card-footer">
                                <span>View Details</span>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card success">
                            <div class="stat-card-title">Success Card (<span id="statTotalTransactions">0</span> Txs)</div>
                            <a href="/admin/transactions" class="stat-card-footer">
                                <span>View Details</span>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card danger">
                            <div class="stat-card-title">Danger Card (<span id="statTotalBalance">0 VND</span>)</div>
                            <a href="/admin/cards" class="stat-card-footer">
                                <span>View Details</span>
                                <i class="fa fa-angle-right"></i>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Table Card Box -->
                <div class="admin-card-box">
                    <div class="mb-4">
                        <label class="form-label fw-bold" for="tableSelector">Select Table:</label>
                        <select class="form-select w-auto min-w-200" id="tableSelector" onchange="handleTableChange(this.value)">
                            <option value="user" selected>User</option>
                            <option value="card">Card</option>
                            <option value="transaction">Transaction</option>
                        </select>
                    </div>

                    <h3 class="fw-bold text-center mb-4">Table User</h3>

                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <a href="/admin/users/create" class="btn btn-blue">Create a user</a>
                    </div>

                    <div class="input-group mb-3">
                        <input type="text" class="form-control" id="searchUserInput" placeholder="Search by username">
                        <button class="btn btn-outline-secondary" type="button" onclick="handleSearchUser()">Search</button>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>User Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="usersTableBody">
                                <tr>
                                    <td colspan="5" class="text-center py-4 text-muted">Đang tải dữ liệu người dùng...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <jsp:include page="../common/footer.jsp"/>
        </div>
    </div>

    <script src="/js/admin.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            Admin.loadDashboard();
        });

        function handleTableChange(val) {
            if (val === 'card') window.location.href = '/admin/cards';
            else if (val === 'transaction') window.location.href = '/admin/transactions';
        }

        async function handleSearchUser() {
            const kw = document.getElementById('searchUserInput').value.trim().toLowerCase();
            const rows = document.querySelectorAll('#usersTableBody tr');
            rows.forEach(r => {
                const text = r.textContent.toLowerCase();
                r.style.display = text.includes(kw) ? '' : 'none';
            });
        }
    </script>
</body>
</html>
