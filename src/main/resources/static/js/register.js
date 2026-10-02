/**
 * Register Controller
 */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const rePassword = document.getElementById('rePassword').value;
        const alertBox = document.getElementById('registerAlert');
        alertBox.innerHTML = '';

        if (password !== rePassword) {
            App.showAlert('registerAlert', 'Mật khẩu nhập lại không khớp!', 'warning');
            return;
        }

        try {
            const res = await fetch('/api/v1/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username,
                    email,
                    password,
                    customerName: username,
                    phoneNumber: '09' + Math.floor(10000000 + Math.random() * 90000000)
                })
            });
            const data = await res.json();

            if (res.ok && data.code === 1000) {
                App.showAlert('registerAlert', 'Đăng ký tài khoản thành công! Đang chuyển hướng sang đăng nhập...', 'success');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 1200);
            } else {
                const msg = data.message ? App.translate(data.message) : 'Đăng ký không thành công!';
                App.showAlert('registerAlert', msg, 'danger');
            }
        } catch (err) {
            App.showAlert('registerAlert', 'Lỗi kết nối máy chủ: ' + err.message, 'danger');
        }
    });
});
