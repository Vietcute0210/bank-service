/**
 * Comprehensive Admin Module
 */
const Admin = {
    // 1. Dashboard
    async loadDashboard() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        try {
            const res = await App.apiFetch('/api/v1/admin/dashboard');
            if (res && res.code === 1000 && res.data) {
                const d = res.data;
                const totalUsersElem = document.getElementById('statTotalUsers');
                if (totalUsersElem) totalUsersElem.textContent = d.totalUsers;

                const totalCardsElem = document.getElementById('statTotalCards');
                if (totalCardsElem) totalCardsElem.textContent = d.totalCards;

                const totalTxElem = document.getElementById('statTotalTransactions');
                if (totalTxElem) totalTxElem.textContent = d.totalTransactions;

                const totalBalElem = document.getElementById('statTotalBalance');
                if (totalBalElem) totalBalElem.textContent = App.formatMoney(d.totalBalance) + ' VND';
            }
        } catch (e) {
            console.error('Error loading dashboard stats:', e);
        }

        await this.loadUsersTable();
    },

    async loadUsersTable() {
        const tbody = document.getElementById('usersTableBody');
        if (!tbody) return;

        try {
            const res = await App.apiFetch('/api/v1/admin/users');
            if (res && res.code === 1000 && res.data) {
                tbody.innerHTML = res.data.map(u => `
                    <tr>
                        <td>${u.userId}</td>
                        <td>${u.username || ''}</td>
                        <td>${u.email || ''}</td>
                        <td><span class="badge ${u.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'}">${u.role || 'USER'}</span></td>
                        <td>
                            <a href="/admin/users/${u.userId}" class="btn btn-sm btn-green me-1">View</a>
                            <a href="/admin/users/${u.userId}/update" class="btn btn-sm btn-yellow me-1">Update</a>
                            <button onclick="Admin.deleteUser(${u.userId})" class="btn btn-sm btn-red">Delete</button>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            console.error('Error loading users table:', e);
        }
    },

    // 2. Users Management Page
    async loadUsersPage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const level = document.getElementById('levelFilterSelect') ? document.getElementById('levelFilterSelect').value : '';
        const kw = document.getElementById('searchUserKeyword') ? document.getElementById('searchUserKeyword').value.trim().toLowerCase() : '';
        const tbody = document.getElementById('adminUsersTbody');
        if (!tbody) return;

        try {
            let url = '/api/v1/admin/users';
            if (level) url += '?level=' + encodeURIComponent(level);

            const res = await App.apiFetch(url);
            if (res && res.code === 1000 && res.data) {
                let list = res.data;
                if (kw) {
                    list = list.filter(u => (u.username || '').toLowerCase().includes(kw));
                }
                if (list.length === 0) {
                    tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">Không tìm thấy người dùng phù hợp</td></tr>`;
                    return;
                }
                tbody.innerHTML = list.map(u => `
                    <tr>
                        <td>${u.userId}</td>
                        <td><strong>${u.username || ''}</strong></td>
                        <td>${u.email || ''}</td>
                        <td><span class="badge ${u.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'}">${u.role || 'USER'}</span></td>
                        <td><span class="badge bg-secondary">${u.levelName || 'VIP1'}</span></td>
                        <td>
                            <a href="/admin/users/${u.userId}" class="btn btn-sm btn-green me-1">View Card</a>
                            <a href="/admin/users/${u.userId}/update" class="btn btn-sm btn-yellow me-1">Update</a>
                            <button onclick="Admin.deleteUser(${u.userId})" class="btn btn-sm btn-red">Delete</button>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">Lỗi tải dữ liệu: ${e.message}</td></tr>`;
        }
    },

    async deleteUser(userId) {
        if (!await App.confirmModal('Xác nhận', 'Bạn có chắc chắn muốn xóa người dùng ID: ' + userId + '?')) return;

        try {
            const res = await App.apiFetch(`/api/v1/admin/users/${userId}`, { method: 'DELETE' });
            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Xóa người dùng thành công!');
                window.location.reload();
            } else {
                await App.showModal('Thông báo', res.message || 'Không thể xóa người dùng!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi: ' + e.message);
        }
    },

    // 3. User Detail Page
    async loadUserDetailPage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const pathParts = window.location.pathname.split('/');
        const userId = pathParts[pathParts.length - 1];
        const btnCreateCard = document.getElementById('btnCreateCardForUser');
        if (btnCreateCard) btnCreateCard.href = '/admin/cards/create?userId=' + userId;

        try {
            const res = await App.apiFetch(`/api/v1/admin/users/${userId}`);
            if (res && res.code === 1000 && res.data) {
                const u = res.data;
                document.getElementById('uDetailId').textContent = u.userId;
                document.getElementById('uDetailEmail').textContent = u.email || '---';
                document.getElementById('uDetailFullName').textContent = u.fullName || '---';
                document.getElementById('uDetailPhone').textContent = u.phoneNumber || '---';
                document.getElementById('uDetailUsername').textContent = u.username || '---';
                document.getElementById('uDetailRole').textContent = u.role || 'USER';

                const grid = document.getElementById('userCardsGrid');
                if (grid) {
                    if (u.cards && u.cards.length > 0) {
                        const balanceVal = u.balance ? App.formatMoney(u.balance.availableBalance) : '0';
                        grid.innerHTML = u.cards.map(c => `
                            <div class="col-md-6">
                                <div class="p-3 border rounded bg-white shadow-sm">
                                    <h5 class="fw-bold mb-2">Card Number: ${c.cardNumber}</h5>
                                    <div class="text-secondary small mb-1">Expiry Date: ${c.expiryDate || 'N/A'}</div>
                                    <div class="text-secondary small mb-1">Type: <span class="badge bg-secondary">${c.cardType || 'DEBIT'}</span></div>
                                    <div class="text-secondary small mb-1">Status: <span class="badge ${c.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${c.status || 'ACTIVE'}</span></div>
                                    <div class="mb-3">AvailableBalance: <strong>${balanceVal} VND</strong></div>
                                    <a href="/admin/cards/${c.cardId}" class="btn btn-sm btn-secondary">View Details</a>
                                </div>
                            </div>
                        `).join('');
                    } else {
                        grid.innerHTML = `<div class="col-12 text-muted">Người dùng chưa có thẻ nào.</div>`;
                    }
                }
            }
        } catch (e) {
            console.error('Error loading user detail:', e);
        }
    },

    // 4. User Update Page
    async loadUserUpdatePage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const pathParts = window.location.pathname.split('/');
        const userId = pathParts[3];

        try {
            const levelsRes = await App.apiFetch('/api/v1/user-levels');
            if (levelsRes && levelsRes.code === 1000 && levelsRes.data) {
                const levelSelect = document.getElementById('updateLevel');
                if (levelSelect) {
                    levelSelect.innerHTML = levelsRes.data.map(l => `<option value="${l.levelId}">${l.levelName}</option>`).join('');
                }
            }
        } catch(e) {
            console.error(e);
        }

        try {
            const res = await App.apiFetch(`/api/v1/admin/users/${userId}`);
            if (res && res.code === 1000 && res.data) {
                const u = res.data;
                document.getElementById('updateEmail').value = u.email || '';
                document.getElementById('updatePhone').value = u.phoneNumber || '';
                document.getElementById('updateFullName').value = u.fullName || '';
                if (u.role) document.getElementById('updateRole').value = u.role;
                if (u.levelId) document.getElementById('updateLevel').value = u.levelId;
            }
        } catch (e) {
            console.error('Error loading user data:', e);
        }

        const form = document.getElementById('updateUserForm');
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const phoneNumber = document.getElementById('updatePhone').value.trim();
                const fullName = document.getElementById('updateFullName').value.trim();
                const role = document.getElementById('updateRole').value;
                const levelId = parseInt(document.getElementById('updateLevel').value);

                try {
                    const res = await App.apiFetch(`/api/v1/admin/users/${userId}`, {
                        method: 'PUT',
                        body: JSON.stringify({ phoneNumber, fullName, role, levelId })
                    });

                    if (res && res.code === 1000) {
                        App.showAlert('updateUserAlert', 'Cập nhật thành công! Đang chuyển hướng...', 'success');
                        setTimeout(() => {
                            window.location.href = '/admin/users';
                        }, 1200);
                    } else {
                        App.showAlert('updateUserAlert', res.message || 'Cập nhật thất bại!');
                    }
                } catch (err) {
                    App.showAlert('updateUserAlert', 'Lỗi: ' + err.message);
                }
            });
        }
    },

    // 5. User Create Page
    async initUserCreatePage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        try {
            const levelsRes = await App.apiFetch('/api/v1/user-levels');
            if (levelsRes && levelsRes.code === 1000 && levelsRes.data) {
                const levelSelect = document.getElementById('newLevel');
                if (levelSelect) {
                    levelSelect.innerHTML = levelsRes.data.map(l => `<option value="${l.levelId}">${l.levelName}</option>`).join('');
                }
            }
        } catch(e) {
            console.error(e);
        }

        const form = document.getElementById('createUserForm');
        if (!form) return;

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('newUsername').value.trim();
            const password = document.getElementById('newPassword').value;
            const email = document.getElementById('newEmail').value.trim();
            const phoneNumber = document.getElementById('newPhone').value.trim();
            const fullName = document.getElementById('newFullName').value.trim();
            const role = document.getElementById('newRole').value;
            const levelId = parseInt(document.getElementById('newLevel').value);

            try {
                const res = await App.apiFetch('/api/v1/admin/users', {
                    method: 'POST',
                    body: JSON.stringify({ username, password, email, phoneNumber, fullName, role, levelId })
                });

                if (res && res.code === 1000) {
                    App.showAlert('createUserAlert', 'Tạo người dùng thành công! Đang chuyển hướng...', 'success');
                    setTimeout(() => {
                        window.location.href = '/admin/users';
                    }, 1200);
                } else {
                    App.showAlert('createUserAlert', res.message || 'Tạo người dùng thất bại!');
                }
            } catch (err) {
                App.showAlert('createUserAlert', 'Lỗi: ' + err.message);
            }
        });
    },

    // 6. Cards Table
    async loadCardsTable() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const tbody = document.getElementById('cardsTableBody');
        if (!tbody) return;

        try {
            const res = await App.apiFetch('/api/v1/admin/cards');
            if (res && res.code === 1000 && res.data) {
                tbody.innerHTML = res.data.map(c => `
                    <tr>
                        <td>${c.cardId}</td>
                        <td><strong>${c.userName || ''}</strong></td>
                        <td class="font-monospace text-primary">${c.cardNumber || ''}</td>
                        <td><span class="badge bg-secondary">${c.cardType || 'DEBIT'}</span></td>
                        <td><span class="badge ${c.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${c.status || 'ACTIVE'}</span></td>
                        <td>
                            <a href="/admin/cards/${c.cardId}" class="btn btn-sm btn-blue me-1">View</a>
                            <button onclick="Admin.deleteCard(${c.cardId})" class="btn btn-sm btn-red me-1">Delete</button>
                            <button onclick="Admin.toggleCardInactive(${c.cardId})" class="btn btn-sm btn-red">Update INACTIVE</button>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            console.error('Error loading cards:', e);
        }
    },

    async toggleCardInactive(cardId) {
        if (!await App.confirmModal('Xác nhận', 'Chuyển trạng thái thẻ sang INACTIVE?')) return;
        try {
            const res = await App.apiFetch(`/api/v1/admin/cards/${cardId}`, {
                method: 'PUT',
                body: JSON.stringify({ status: 'INACTIVE' })
            });
            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Cập nhật trạng thái thành công!');
                window.location.reload();
            } else {
                await App.showModal('Thông báo', res.message || 'Cập nhật thất bại!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi: ' + e.message);
        }
    },

    async deleteCard(cardId) {
        if (!await App.confirmModal('Xác nhận', 'Xóa thẻ ID: ' + cardId + '?')) return;
        try {
            const res = await App.apiFetch(`/api/v1/admin/cards/${cardId}`, { method: 'DELETE' });
            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Xóa thẻ thành công!');
                window.location.reload();
            } else {
                await App.showModal('Thông báo', res.message || 'Không thể xóa thẻ!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi: ' + e.message);
        }
    },

    // 7. Card Details
    async loadCardDetail(cardId) {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        try {
            const res = await App.apiFetch(`/api/v1/admin/cards/${cardId}`);
            if (res && res.code === 1000 && res.data) {
                const d = res.data;
                document.getElementById('cardDetailId').textContent = d.cardId;
                document.getElementById('cardDetailNumber').textContent = d.cardNumber;
                document.getElementById('cardDetailType').textContent = d.cardType;
                document.getElementById('cardDetailExpiry').textContent = d.expiryDate || '';
                document.getElementById('cardDetailStatus').textContent = d.status;
                document.getElementById('cardDetailUserId').textContent = d.userId || '';
                document.getElementById('cardDetailUserName').textContent = d.userName || '';
                document.getElementById('cardDetailUserEmail').textContent = d.userEmail || '';

                document.getElementById('cardDetailBalanceId').textContent = d.balanceId || '';
                document.getElementById('cardDetailAvailableBalance').textContent = App.formatMoney(d.availableBalance) + ' VND';
                document.getElementById('cardDetailHoldBalance').textContent = App.formatMoney(d.holdBalance || 0) + ' VND';
                document.getElementById('cardDetailLastUpdated').textContent = d.lastUpdated || '';
            }
        } catch (e) {
            console.error('Error loading card detail:', e);
        }
    },

    async depositToCard(cardId) {
        const input = document.getElementById('depositAmountInput');
        const amount = parseFloat(input.value);
        if (!amount || amount <= 0) {
            await App.showModal('Thông báo', 'Vui lòng nhập số tiền nạp hợp lệ!');
            return;
        }

        try {
            const res = await App.apiFetch(`/api/v1/admin/cards/${cardId}/deposit`, {
                method: 'POST',
                body: JSON.stringify({ amount: amount })
            });

            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Nạp tiền vào thẻ thành công!');
                input.value = '';
                this.loadCardDetail(cardId);
            } else {
                await App.showModal('Thông báo', res.message || 'Nạp tiền thất bại!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi nạp tiền: ' + e.message);
        }
    },

    async withdrawFromCard(cardId) {
        const input = document.getElementById('withdrawAmountInput');
        const amount = parseFloat(input.value);
        if (!amount || amount <= 0) {
            await App.showModal('Thông báo', 'Vui lòng nhập số tiền rút hợp lệ!');
            return;
        }

        try {
            const res = await App.apiFetch(`/api/v1/admin/cards/${cardId}/withdraw`, {
                method: 'POST',
                body: JSON.stringify({ amount: amount })
            });

            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Rút tiền từ thẻ thành công!');
                input.value = '';
                this.loadCardDetail(cardId);
            } else {
                await App.showModal('Thông báo', res.message || 'Rút tiền thất bại!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi rút tiền: ' + e.message);
        }
    },

    // 8. Transactions Table
    async loadTransactionsTable(page = 0, size = 10) {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const tbody = document.getElementById('transactionsTableBody');
        if (!tbody) return;

        try {
            const res = await App.apiFetch(`/api/v1/admin/transactions?page=${page}&size=${size}`);
            if (res && res.code === 1000 && res.data) {
                const list = res.data.content || [];
                tbody.innerHTML = list.map(tx => `
                    <tr>
                        <td>${tx.transactionId}</td>
                        <td class="font-monospace">${tx.fromCardNumber || '0'}</td>
                        <td class="font-monospace">${tx.toCardNumber || '0'}</td>
                        <td class="font-monospace fw-bold">${App.formatMoney(tx.amount)} VND</td>
                        <td><span class="badge bg-primary">${tx.transactionType || ''}</span></td>
                        <td><span class="badge ${tx.status === 'SUCCESS' ? 'bg-success' : 'bg-warning text-dark'}">${tx.status || ''}</span></td>
                        <td>
                            ${tx.status === 'PENDING' ? `
                                <button onclick="Admin.updateTxSuccess(${tx.transactionId})" class="btn btn-sm btn-yellow">Update success</button>
                            ` : `
                                <button class="btn btn-sm btn-secondary" disabled>Đã xử lý</button>
                            `}
                        </td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            console.error('Error loading transactions:', e);
        }
    },

    async updateTxSuccess(txId) {
        if (!await App.confirmModal('Xác nhận', 'Đổi trạng thái giao dịch ID ' + txId + ' sang SUCCESS?')) return;
        try {
            const res = await App.apiFetch(`/api/v1/admin/transactions/${txId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ status: 'SUCCESS' })
            });

            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Cập nhật trạng thái giao dịch thành công!');
                window.location.reload();
            } else {
                await App.showModal('Thông báo', res.message || 'Cập nhật thất bại!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi cập nhật giao dịch: ' + e.message);
        }
    },

    // 9. User Levels Page
    async loadUserLevelsPage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const tbody = document.getElementById('userLevelsTbody');
        if (!tbody) return;

        try {
            const res = await App.apiFetch('/api/v1/user-levels');
            if (res && res.code === 1000 && res.data) {
                tbody.innerHTML = res.data.map(lvl => `
                    <tr>
                        <td class="px-4">${lvl.levelId}</td>
                        <td class="px-4 fw-bold text-primary">${lvl.levelName}</td>
                        <td class="px-4">${lvl.cardLimit} thẻ</td>
                        <td class="px-4 font-monospace">${App.formatMoney(lvl.dailyTransferLimit)} VND</td>
                        <td class="px-4">
                            <button onclick="Admin.deleteUserLevel(${lvl.levelId})" class="btn btn-sm btn-red">Delete</button>
                        </td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-danger">Lỗi tải dữ liệu: ${e.message}</td></tr>`;
        }
    },

    async deleteUserLevel(id) {
        if (!await App.confirmModal('Xác nhận', 'Xác nhận xóa cấp độ VIP ID: ' + id + '?')) return;
        try {
            const res = await App.apiFetch(`/api/v1/user-levels/${id}`, { method: 'DELETE' });
            if (res && res.code === 1000) {
                await App.showModal('Thông báo', 'Xóa cấp độ VIP thành công!');
                window.location.reload();
            } else {
                await App.showModal('Thông báo', res.message || 'Không thể xóa cấp độ VIP!');
            }
        } catch (e) {
            await App.showModal('Thông báo', 'Lỗi: ' + e.message);
        }
    },

    // 10. User Level Create Page
    initUserLevelCreatePage() {
        if (!App.requireAdmin()) return;
        App.initAdminHeader();

        const form = document.getElementById('createLevelForm');
        if (!form) return;

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const levelName = document.getElementById('levelName').value.trim();
            const cardLimit = parseInt(document.getElementById('cardLimit').value);
            const dailyTransferLimit = parseFloat(document.getElementById('dailyTransferLimit').value);

            try {
                const res = await App.apiFetch('/api/v1/user-levels', {
                    method: 'POST',
                    body: JSON.stringify({ levelName, cardLimit, dailyTransferLimit })
                });

                if (res && res.code === 1000) {
                    App.showAlert('createLevelAlert', 'Tạo cấp độ VIP thành công! Đang chuyển hướng...', 'success');
                    setTimeout(() => {
                        window.location.href = '/admin/user-levels';
                    }, 1200);
                } else {
                    App.showAlert('createLevelAlert', res.message || 'Tạo cấp độ VIP thất bại!');
                }
            } catch (err) {
                App.showAlert('createLevelAlert', 'Lỗi: ' + err.message);
            }
        });
    }
};
