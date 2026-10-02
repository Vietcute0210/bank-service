/**
 * 3-Phase OTP Transfer Flow
 * Step 1: Card Details + Search Recipient Card
 * Step 2: Show Recipient Info + Enter Amount + Initiate Transfer
 * Step 3: Enter OTP + Confirm Transfer
 */
let currentCard = null;
let currentTransactionId = null;
let userAvailableBalance = 0;

async function initTransferPage() {
    if (!App.requireAuth()) return;
    App.initUserNavbar();

    // 1. Load user's cards or specific card
    const urlParams = new URLSearchParams(window.location.search);
    const cardIdParam = urlParams.get('cardId');

    try {
        const res = await App.apiFetch('/api/v1/card');
        if (res && res.code === 1000 && res.data && res.data.length > 0) {
            let selectedCard = res.data[0];
            if (cardIdParam) {
                const found = res.data.find(c => String(c.cardId) === String(cardIdParam));
                if (found) selectedCard = found;
            }
            currentCard = selectedCard;
            renderSenderCard(selectedCard);
            await loadBalance();
        } else {
            await App.showModal('Thông báo', 'Bạn chưa có thẻ ngân hàng nào. Vui lòng tạo thẻ trước!');
            window.location.href = '/account';
        }
    } catch (e) {
        console.error('Error loading cards:', e);
    }
}

async function loadBalance() {
    try {
        if (!currentCard) return;
        const res = await App.apiFetch('/api/v1/card');
        if (res && res.code === 1000 && res.data) {
            const found = res.data.find(c => String(c.cardId) === String(currentCard.cardId));
            if (found) {
                currentCard = found;
            }
        }
        userAvailableBalance = currentCard.availableBalance || 0;
        document.getElementById('senderBalance').textContent = App.formatMoney(userAvailableBalance) + ' VND';
        document.getElementById('senderHoldBalance').textContent = App.formatMoney(currentCard.holdBalance || 0) + ' VND';

        if (userAvailableBalance <= 0) {
            App.showAlert('transferAlert', 'S? du t�i kho?n c?a b?n hi?n b?ng 0. Vui l�ng v�o trang "T�i Kho?n" n?p ti?n tru?c khi chuy?n!', 'warning');
        }
    } catch (e) {
        console.error('Error loading balance:', e);
    }
}
    } catch (e) {
        console.error('Error loading balance:', e);
    }
}

function renderSenderCard(card) {
    document.getElementById('senderCardNumber').textContent = card.cardNumber || '';
    document.getElementById('senderCardType').textContent = card.cardType || 'DEBIT';
}

// Step 1 -> Search Recipient
async function handleSearchRecipient() {
    const searchInput = document.getElementById('searchCardInput');
    const toCardNumber = searchInput.value.trim();
    const alertBox = document.getElementById('transferAlert');
    alertBox.innerHTML = '';

    if (!toCardNumber) {
        App.showAlert('transferAlert', 'Vui lòng nhập số thẻ người nhận!', 'warning');
        return;
    }

    if (currentCard && toCardNumber === currentCard.cardNumber) {
        App.showAlert('transferAlert', 'Không thể chuyển tiền cho chính thẻ này!', 'warning');
        return;
    }

    try {
        document.getElementById('step2Section').style.display = 'block';
        document.getElementById('recipientCardNumberReadonly').value = toCardNumber;
        document.getElementById('recipientNameReadonly').value = 'NGƯỜI NHẬN (' + toCardNumber + ')';
        App.showAlert('transferAlert', 'Đã tìm thấy thông tin người nhận. Vui lòng nhập số tiền!', 'success');
    } catch (e) {
        App.showAlert('transferAlert', 'Lỗi khi tìm kiếm thẻ nhận: ' + e.message, 'danger');
    }
}

// Step 2 -> Initiate Transfer (Hold balance, generate OTP)
async function handleInitiateTransfer() {
    const amountInput = document.getElementById('transferAmountInput');
    const amount = parseFloat(amountInput.value);
    const toCardNumber = document.getElementById('recipientCardNumberReadonly').value;
    const alertBox = document.getElementById('transferAlert');
    alertBox.innerHTML = '';

    if (!amount || amount <= 0) {
        App.showAlert('transferAlert', 'Vui lòng nhập số tiền hợp lệ lớn hơn 0!', 'warning');
        return;
    }

    if (amount > userAvailableBalance) {
        App.showAlert('transferAlert', `Số dư khả dụng (${App.formatMoney(userAvailableBalance)} VND) không đủ để chuyển ${App.formatMoney(amount)} VND! Vui lòng nạp thêm tiền.`, 'danger');
        return;
    }

    const payload = {
        fromCardNumber: currentCard ? currentCard.cardNumber : '',
        toCardNumber: toCardNumber,
        amount: amount,
        description: 'Chuyen tien tu ' + (currentCard ? currentCard.cardNumber : '') + ' den ' + toCardNumber
    };

    try {
        const res = await App.apiFetch('/api/v1/transaction/initiate', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (res && res.code === 1000) {
            currentTransactionId = res.data.transactionId;
            let successMsg = res.data.message || 'Mã OTP đã được gửi đến email của bạn!';
            if (res.data.otp) {
                successMsg += ` (Mã OTP của bạn: ${res.data.otp})`;
            }
            App.showAlert('transferAlert', successMsg, 'info');

            // Show Step 3 (OTP verification)
            document.getElementById('step3Section').style.display = 'block';
            amountInput.disabled = true;
            document.getElementById('btnInitiateTransfer').disabled = true;

            // Pre-fill OTP if present in response
            if (res.data.otp) {
                document.getElementById('otpInput').value = res.data.otp;
            }
        } else {
            const err = res && res.message ? res.message : 'Khởi tạo chuyển tiền thất bại!';
            App.showAlert('transferAlert', err, 'danger');
        }
    } catch (e) {
        App.showAlert('transferAlert', 'Lỗi: ' + e.message, 'danger');
    }
}

// Step 3 -> Confirm Transfer with OTP
async function handleConfirmOtp() {
    const otpInput = document.getElementById('otpInput');
    const otp = otpInput.value.trim();

    if (!otp) {
        App.showAlert('transferAlert', 'Vui lòng nhập mã OTP 6 chữ số!', 'warning');
        return;
    }

    if (!currentTransactionId) {
        App.showAlert('transferAlert', 'Không tìm thấy giao dịch chuyển tiền cần xác thực!', 'danger');
        return;
    }

    const payload = {
        transactionId: currentTransactionId,
        otp: otp
    };

    try {
        const res = await App.apiFetch('/api/v1/transaction/confirm', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (res && res.code === 1000) {
            App.showAlert('transferAlert', '🎉 Chuyển tiền thành công! Đang chuyển về trang Lịch Sử Giao Dịch...', 'success');
            document.getElementById('step3Section').style.display = 'none';
            await loadBalance();
            setTimeout(() => {
                window.location.href = '/history';
            }, 1500);
        } else {
            const msg = res && res.message ? res.message : 'Mã OTP không chính xác hoặc đã hết hạn!';
            App.showAlert('transferAlert', msg, 'danger');
        }
    } catch (e) {
        App.showAlert('transferAlert', 'Lỗi xác nhận OTP: ' + e.message, 'danger');
    }
}

