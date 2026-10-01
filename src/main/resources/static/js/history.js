/**
 * Transaction History Controller
 */
async function initHistoryPage() {
    if (!App.requireAuth()) return;
    App.initUserNavbar();

    const tbody = document.getElementById('historyTableBody');
    if (!tbody) return;

    try {
        const res = await App.apiFetch('/api/v1/transaction');
        if (res && res.code === 1000 && res.data && res.data.length > 0) {
            tbody.innerHTML = res.data.map(tx => {
                let badgeClass = 'bg-secondary';
                if (tx.status === 'SUCCESS') badgeClass = 'bg-success';
                else if (tx.status === 'PENDING') badgeClass = 'bg-warning text-dark';
                else if (tx.status === 'FAILED') badgeClass = 'bg-danger';

                return `
                    <tr>
                        <td class="px-4 fw-bold">${tx.transactionId}</td>
                        <td class="px-4">${tx.createdAt || ''}</td>
                        <td class="px-4 font-monospace fw-bold">${App.formatMoney(tx.amount)} VND</td>
                        <td class="px-4"><span class="badge bg-primary">${tx.transactionType || ''}</span></td>
                        <td class="px-4"><span class="badge ${badgeClass}">${tx.status || ''}</span></td>
                        <td class="px-4 font-monospace">${tx.fromCardNumber || '0'}</td>
                        <td class="px-4 font-monospace">${tx.toCardNumber || '0'}</td>
                    </tr>
                `;
            }).join('');
        } else {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">Chưa có giao dịch nào được ghi nhận!</td></tr>`;
        }
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-danger">Lỗi tải dữ liệu: ${e.message}</td></tr>`;
    }
}
