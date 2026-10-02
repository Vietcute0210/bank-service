/**
 * Global App JS Helper with Full Vietnamese Localization & Auth Guards
 */
const App = {
    TOKEN_KEY: 'bank_access_token',
    USER_KEY: 'bank_user_info',

    // Vietnamese translation dictionary
    VI_MESSAGES: {
        'Account not found': 'Không tìm thấy tài khoản',
        'Account already exists': 'Tài khoản đã tồn tại',
        'Account is inactive': 'Tài khoản chưa được kích hoạt',
        'Account is blocked': 'Tài khoản đã bị khóa',
        'Account is closed': 'Tài khoản đã bị đóng',
        'Cannot delete account: Account has linked cards': 'Không thể xóa: Tài khoản đang có thẻ liên kết',
        'Cannot delete account: Account balance must be zero': 'Không thể xóa: Số dư tài khoản phải bằng 0',
        'Invalid ID format: Must be a valid number': 'Định dạng ID không hợp lệ: Phải là số',
        'Card not found': 'Không tìm thấy thẻ ngân hàng',
        'Card already exists': 'Thẻ ngân hàng đã tồn tại',
        'Card is inactive': 'Thẻ chưa được kích hoạt',
        'Card is blocked': 'Thẻ đã bị khóa',
        'Card is expired': 'Thẻ đã hết hạn sử dụng',
        'Insufficient funds': 'Số dư tài khoản không đủ để thực hiện giao dịch',
        'Invalid amount': 'Số tiền không hợp lệ',
        'Invalid account type': 'Loại tài khoản không hợp lệ',
        'Invalid card type': 'Loại thẻ không hợp lệ',
        'Cannot delete card: Card has pending transactions': 'Không thể xóa: Thẻ đang có giao dịch chờ xử lý',
        'Balance not found': 'Không tìm thấy thông tin số dư',
        'Refresh token does not exist': 'Phiên đăng nhập không tồn tại',
        'Refresh token was expired or revoked': 'Phiên đăng nhập đã hết hạn',
        'Full authentication is required to access this resource': 'Vui lòng đăng nhập để truy cập tính năng này',
        'Forbidden: You do not have permission to access this resource': 'Bạn không có quyền truy cập tài nguyên này',
        'Access denied': 'Từ chối quyền truy cập',
        'transaction is not found': 'Không tìm thấy giao dịch',
        'User not found': 'Không tìm thấy người dùng',
        'User level not found': 'Không tìm thấy cấp độ VIP',
        'User level already exists': 'Cấp độ VIP đã tồn tại',
        'Card limit exceeded for current VIP level': 'Đã vượt quá giới hạn số thẻ cho phép của cấp VIP hiện tại',
        'Cannot delete user: has linked accounts/cards': 'Không thể xóa: Người dùng đang có tài khoản hoặc thẻ liên kết',
        'Số tiền phải lớn hơn 1 triệu': 'Số tiền rút phải lớn hơn 1.000.000 VNĐ',
        'Cannot delete user level: It is assigned to one or more users': 'Không thể xóa: Cấp VIP đang được gán cho người dùng',
        'Invalid user level data': 'Dữ liệu cấp VIP không hợp lệ',
        'User already exists': 'Tên người dùng đã tồn tại',
        'Cannot delete current admin user': 'Không thể xóa tài khoản quản trị viên hiện tại',
        'Email already exists': 'Email đã tồn tại trên hệ thống',
        'Phone number already exists': 'Số điện thoại đã tồn tại trên hệ thống',
        'Mã OTP không chính xác hoặc đã hết hạn': 'Mã OTP không chính xác hoặc đã hết hạn',
        'Giao dịch đã được xử lý hoặc đã kết thúc': 'Giao dịch đã được xử lý hoặc đã kết thúc',
        'login successfully': 'Đăng nhập thành công',
        'register successfully': 'Đăng ký thành công',
        'Transfer initiated successfully': 'Khởi tạo chuyển tiền thành công. Vui lòng kiểm tra OTP!',
        'Transfer confirmed successfully': 'Xác thực chuyển tiền thành công!'
    },

    translate(msg) {
        if (!msg) return '';
        if (this.VI_MESSAGES[msg]) return this.VI_MESSAGES[msg];
        // Partial matches
        for (const [en, vi] of Object.entries(this.VI_MESSAGES)) {
            if (msg.includes(en)) {
                return msg.replace(en, vi);
            }
        }
        return msg;
    },

    getToken() {
        return localStorage.getItem(this.TOKEN_KEY);
    },

    setToken(token, user) {
        localStorage.setItem(this.TOKEN_KEY, token);
        if (user) {
            localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        }
    },

    getUser() {
        const raw = localStorage.getItem(this.USER_KEY);
        if (!raw) return null;
        try {
            return JSON.parse(raw);
        } catch (e) {
            return null;
        }
    },

    isAdmin() {
        const u = this.getUser();
        if (!u) return false;
        const role = String(u.role || '').toUpperCase();
        return role === 'ADMIN' || role === 'ROLE_ADMIN';
    },

    clearAuth() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
    },

    logout() {
        this.clearAuth();
        window.location.href = '/login';
    },

    requireAuth() {
        const token = this.getToken();
        if (!token) {
            window.location.href = '/login';
            return false;
        }
        return true;
    },

    requireAdmin() {
        if (!this.requireAuth()) return false;
        if (!this.isAdmin()) {
            alert('Bạn không có quyền truy cập trang quản trị!');
            window.location.href = '/home';
            return false;
        }
        return true;
    },

    async apiFetch(endpoint, options = {}) {
        const token = this.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const res = await fetch(endpoint, config);
            if (res.status === 401 && !endpoint.includes('/api/v1/auth/')) {
                console.warn('401 Unauthorized encountered from:', endpoint);
                this.logout();
                return null;
            }
            const data = await res.json();
            if (data && data.message) {
                data.message = this.translate(data.message);
            }
            return data;
        } catch (err) {
            console.error('API Fetch Error:', err);
            throw err;
        }
    },

    formatMoney(amount) {
        if (amount == null) return '0';
        return Number(amount).toLocaleString('vi-VN');
    },

    formatDate(dateStr) {
        if (!dateStr) return '';
        try {
            const d = new Date(dateStr);
            return d.toLocaleString('vi-VN');
        } catch (e) {
            return dateStr;
        }
    },

    showAlert(containerId, message, type) {
        if (!type) type = 'danger';
        const container = document.getElementById(containerId);
        if (!container) return;
        const translatedMsg = this.translate(message);
        container.innerHTML = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            translatedMsg +
            '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
            '</div>';
    },

    initUserNavbar() {
        const user = this.getUser();
        const navContainer = document.getElementById('userNavContent');
        const navUserSpan = document.getElementById('navUsername');
        if (navUserSpan && user) {
            navUserSpan.textContent = user.username || 'User';
        }

        // If user is Admin, add an "Admin Dashboard" button on user navbar
        if (this.isAdmin() && navContainer) {
            const ul = navContainer.querySelector('ul.navbar-nav');
            if (ul && !document.getElementById('navAdminLink')) {
                const li = document.createElement('li');
                li.className = 'nav-item';
                li.id = 'navAdminLink';
                li.innerHTML = '<a class="nav-link text-warning fw-bold" href="/admin/dashboard"><i class="fa fa-tachometer-alt me-1"></i>Trang Quản Trị</a>';
                ul.insertBefore(li, ul.children[0]);
            }
        }

        const logoutBtn = document.getElementById('btnLogout');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', function(e) {
                e.preventDefault();
                App.logout();
            });
        }
    },

    initAdminHeader() {
        const user = this.getUser();
        const adminNameSpan = document.getElementById('adminNameSpan');
        if (adminNameSpan && user) {
            adminNameSpan.textContent = 'Welcome ' + (user.username || 'admin');
        }
        const adminLoggedSpan = document.getElementById('adminLoggedSpan');
        if (adminLoggedSpan && user) {
            adminLoggedSpan.textContent = user.username || 'admin';
        }

        const topbar = document.querySelector('.admin-topbar .user-info');
        if (topbar && !document.getElementById('btnSwitchToUser')) {
            const btnHome = document.createElement('a');
            btnHome.id = 'btnSwitchToUser';
            btnHome.href = '/home';
            btnHome.className = 'btn btn-sm btn-outline-light me-3';
            btnHome.innerHTML = '<i class="fa fa-home me-1"></i>Giao diện User';
            topbar.insertBefore(btnHome, topbar.firstChild);
        }

        const btnAdminLogout = document.getElementById('btnAdminLogout');
        if (btnAdminLogout) {
            btnAdminLogout.addEventListener('click', function(e) {
                e.preventDefault();
                App.logout();
            });
        }
    },

    // Dynamically inject the Bootstrap modal HTML into the page if not present
    ensureModalEl() {
        var modalEl = document.getElementById('appGlobalModal');
        if (!modalEl) {
            var html = '<div class="modal fade" id="appGlobalModal" tabindex="-1" aria-labelledby="appGlobalModalLabel" aria-hidden="true">' +
                '<div class="modal-dialog modal-dialog-centered">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<h5 class="modal-title" id="appGlobalModalLabel">Title</h5>' +
                '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>' +
                '</div>' +
                '<div class="modal-body" id="appGlobalModalBody">' +
                '<p id="appGlobalModalMessage"></p>' +
                '<div class="mb-3" id="appGlobalModalInputContainer" style="display:none;">' +
                '<label for="appGlobalModalInput" class="form-label" id="appGlobalModalInputLabel"></label>' +
                '<input type="text" class="form-control" id="appGlobalModalInput">' +
                '</div>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-secondary" id="appGlobalModalCancelBtn" data-bs-dismiss="modal">Hủy</button>' +
                '<button type="button" class="btn btn-primary" id="appGlobalModalConfirmBtn">OK</button>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>';
            document.body.insertAdjacentHTML('beforeend', html);
            modalEl = document.getElementById('appGlobalModal');
        }
        return modalEl;
    },

    showModal(title, message) {
        return new Promise(function(resolve) {
            var modalEl = App.ensureModalEl();
            document.getElementById('appGlobalModalLabel').textContent = title;
            document.getElementById('appGlobalModalMessage').textContent = message;
            document.getElementById('appGlobalModalInputContainer').style.display = 'none';
            document.getElementById('appGlobalModalCancelBtn').style.display = 'none';

            var bsModal = new bootstrap.Modal(modalEl);

            var confirmBtn = document.getElementById('appGlobalModalConfirmBtn');

            var onConfirm = function() {
                bsModal.hide();
            };
            var onHidden = function() {
                confirmBtn.removeEventListener('click', onConfirm);
                modalEl.removeEventListener('hidden.bs.modal', onHidden);
                resolve();
            };

            confirmBtn.addEventListener('click', onConfirm);
            modalEl.addEventListener('hidden.bs.modal', onHidden);

            bsModal.show();
        });
    },

    confirmModal(title, message) {
        return new Promise(function(resolve) {
            var modalEl = App.ensureModalEl();
            document.getElementById('appGlobalModalLabel').textContent = title;
            document.getElementById('appGlobalModalMessage').textContent = message;
            document.getElementById('appGlobalModalInputContainer').style.display = 'none';
            document.getElementById('appGlobalModalCancelBtn').style.display = 'inline-block';

            var bsModal = new bootstrap.Modal(modalEl);
            var result = false;

            var confirmBtn = document.getElementById('appGlobalModalConfirmBtn');

            var onConfirm = function() {
                result = true;
                bsModal.hide();
            };
            var onHidden = function() {
                confirmBtn.removeEventListener('click', onConfirm);
                modalEl.removeEventListener('hidden.bs.modal', onHidden);
                resolve(result);
            };

            confirmBtn.addEventListener('click', onConfirm);
            modalEl.addEventListener('hidden.bs.modal', onHidden);

            bsModal.show();
        });
    },

    promptModal(title, label, defaultValue) {
        if (!defaultValue) defaultValue = '';
        return new Promise(function(resolve) {
            var modalEl = App.ensureModalEl();
            document.getElementById('appGlobalModalLabel').textContent = title;
            document.getElementById('appGlobalModalMessage').textContent = '';
            document.getElementById('appGlobalModalInputContainer').style.display = 'block';
            document.getElementById('appGlobalModalInputLabel').textContent = label;

            var inputEl = document.getElementById('appGlobalModalInput');
            inputEl.value = defaultValue;
            document.getElementById('appGlobalModalCancelBtn').style.display = 'inline-block';

            var bsModal = new bootstrap.Modal(modalEl);
            var result = null;

            var confirmBtn = document.getElementById('appGlobalModalConfirmBtn');

            var onConfirm = function() {
                result = inputEl.value;
                bsModal.hide();
            };
            var onHidden = function() {
                confirmBtn.removeEventListener('click', onConfirm);
                modalEl.removeEventListener('hidden.bs.modal', onHidden);
                resolve(result);
            };

            confirmBtn.addEventListener('click', onConfirm);
            modalEl.addEventListener('hidden.bs.modal', onHidden);

            modalEl.addEventListener('shown.bs.modal', function() {
                inputEl.focus();
            }, {once: true});

            bsModal.show();
        });
    }
};
