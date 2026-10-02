/**
 * Login Controller
 */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const alertBox = document.getElementById('loginAlert');
        alertBox.innerHTML = '';

        try {
            const res = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            const data = await res.json();

            if (res.ok && data.code === 1000 && data.data) {
                const role = String(data.data.role || 'USER').toUpperCase();
                const userInfo = {
                    username: data.data.username || username,
                    role: role,
                    email: data.data.email || '',
                    accountId: data.data.accountId
                };

                App.setToken(data.data.accessToken, userInfo);

                // Check role and redirect
                if (role === 'ADMIN' || role === 'ROLE_ADMIN') {
                    window.location.href = '/admin/dashboard';
                } else {
                    window.location.href = '/home';
                }
            } else {
                const msg = data.message ? App.translate(data.message) : 'Tên đăng nhập hoặc mật khẩu không chính xác!';
                App.showAlert('loginAlert', msg, 'danger');
            }
        } catch (err) {
            App.showAlert('loginAlert', 'Lỗi kết nối máy chủ: ' + err.message, 'danger');
        }
    });
});
