/**
 * Account page controller & data binder
 */
let currentBalance = 0;
let currentHoldBalance = 0;

async function initAccountPage() {
    if (!App.requireAuth()) return;
    App.initUserNavbar();

    await loadUserProfile();
    await loadUserBalance();
    await loadUserCards();
}

async function loadUserProfile() {
    const user = App.getUser();
    try {
        const res = await App.apiFetch('/api/v1/account/my-profile');
        if (res && res.code === 1000 && res.data) {
            const acc = res.data;
            document.getElementById('accFullName').textContent = acc.customerName || (user ? user.username : '---');
            document.getElementById('accEmail').textContent = acc.email || '---';
            document.getElementById('accUsername').textContent = user ? user.username : (acc.customerName || '---');
            document.getElementById('accPhone').textContent = acc.phoneNumber || '---';
            document.getElementById('accRole').textContent = (user && user.role) ? user.role : 'CUSTOMER';
        } else if (user) {
            document.getElementById('accFullName').textContent = user.username || '---';
            document.getElementById('accEmail').textContent = user.email || '---';
            document.getElementById('accUsername').textContent = user.username || '---';
            document.getElementById('accRole').textContent = user.role || 'CUSTOMER';
        }
    } catch (e) {
        console.error('Lỗi tải thông tin tài khoản:', e);
    }
}

async function loadUserBalance() {
    try {
        const balRes = await App.apiFetch('/api/v1/balance');
        if (balRes && balRes.code === 1000 && balRes.data) {
            currentBalance = balRes.data.availableBalance || 0;
            currentHoldBalance = balRes.data.holdBalance || 0;
            const balElem = document.getElementById('accountAvailableBalance');
            if (balElem) balElem.textContent = App.formatMoney(currentBalance) + ' VND';
            const holdElem = document.getElementById('accountHoldBalance');
            if (holdElem) holdElem.textContent = App.formatMoney(currentHoldBalance) + ' VND';
        }
    } catch (e) {
        console.error('Lỗi tải số dư:', e);
    }
}

async function loadUserCards() {
    const container = document.getElementById('cardsListContainer');
    if (!container) return;

    try {
        const cardRes = await App.apiFetch('/api/v1/card');
        if (cardRes && cardRes.code === 1000 && cardRes.data && cardRes.data.length > 0) {
            container.innerHTML = cardRes.data.map(c => `
                <div class="card-item-box mb-3">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <div>
                            <div><strong>Số thẻ:</strong> <span class="font-monospace text-primary fw-bold">${c.cardNumber}</span> - <strong>Loại thẻ:</strong> <span class="badge bg-secondary">${c.cardType || 'DEBIT'}</span> - <strong>Trạng thái:</strong> <span class="badge ${c.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${c.status || 'ACTIVE'}</span></div>
                            <div class="mt-1"><strong>Số dư khả dụng:</strong> ${App.formatMoney(c.availableBalance)} VND | <strong>Đang chờ xử lý:</strong> ${App.formatMoney(c.holdBalance)} VND</div>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-sm btn-green" onclick="handleUserDeposit('${c.cardId}', '${c.cardNumber}')">Nạp tiền</button>
                            <button type="button" class="btn btn-sm btn-red" onclick="handleUserWithdraw('${c.cardId}', '${c.cardNumber}')">Rút tiền</button>
                            <a href="/transfer?cardId=${c.cardId}" class="btn btn-sm btn-blue">Chuyển khoản</a>
                        </div>
                    </div>
                </div>
            `).join('');
        } else {
            container.innerHTML = `
                <div class="alert alert-info">
                    Bạn chưa có thẻ ngân hàng nào. Nhấn nút <strong>"Creat card"</strong> ở trên để tạo thẻ mới!
                </div>
            `;
        }
    } catch (e) {
        container.innerHTML = `<div class="text-danger py-2">Lỗi tải danh sách thẻ: ${e.message}</div>`;
    }
}

// User Deposit (Nạp tiền vào tài khoản)
async function handleUserDeposit(cardId, cardNumber) {
    const raw = await App.promptModal('Nạp tiền vào thẻ', `Nhập số tiền muốn nạp vào thẻ ${cardNumber} (VND):`, '500000');
    if (!raw) return;
    const amount = parseFloat(raw);
    if (isNaN(amount) || amount <= 0) {
        await App.showModal('Lỗi', 'Số tiền nạp không hợp lệ!');
        return;
    }

    try {
        const res = await App.apiFetch(`/api/v1/balance/add?cardId=${cardId}&money=${amount}`, { method: 'POST' });
        if (res && res.code === 1000) {
            await App.showModal('Thành công', `Nạp thành công ${App.formatMoney(amount)} VND vào thẻ ${cardNumber}!`);
            await loadUserBalance();
            await loadUserCards();
        } else {
            await App.showModal('Lỗi', res.message || 'Nạp tiền thất bại!');
        }
    } catch (e) {
        await App.showModal('Lỗi', 'Lỗi nạp tiền: ' + e.message);
    }
}

// User Withdraw (Rút tiền từ tài khoản)
async function handleUserWithdraw(cardId, cardNumber) {
    const raw = await App.promptModal('Rút tiền từ thẻ', `Nhập số tiền muốn rút từ thẻ ${cardNumber} (VND):`, '100000');
    if (!raw) return;
    const amount = parseFloat(raw);
    if (isNaN(amount) || amount <= 0) {
        await App.showModal('Lỗi', 'Số tiền rút không hợp lệ!');
        return;
    }

    try {
        const res = await App.apiFetch(`/api/v1/balance/sub?cardId=${cardId}&money=${amount}`, { method: 'POST' });
        if (res && res.code === 1000) {
            await App.showModal('Thành công', `Rút thành công ${App.formatMoney(amount)} VND từ thẻ ${cardNumber}!`);
            await loadUserBalance();
            await loadUserCards();
        } else {
            await App.showModal('Lỗi', res.message || 'Rút tiền thất bại!');
        }
    } catch (e) {
        await App.showModal('Lỗi', 'Lỗi rút tiền: ' + e.message);
    }
}
