/**
 * Checkout Page JavaScript - Shopee Style
 * @author Nhóm 132
 */

// Order data
const orderData = {
    products: [
        { id: 1, name: "Nike Air Force 1", price: 2800000, quantity: 1, variant: "Trắng, 42" },
        { id: 2, name: "Adidas Ultraboost 22", price: 3200000, quantity: 2, variant: "Đen, 41" }
    ],
    subtotal: 9200000,
    shipping: 30000,
    voucher: 0,
    total: 9230000
};

/**
 * Format currency
 */
function formatCurrency(number) {
    return new Intl.NumberFormat('vi-VN').format(number);
}

/**
 * Change address
 */
function changeAddress() {
    showNotification('info', 'Chức năng thay đổi địa chỉ đang được phát triển');
}

/**
 * Select voucher
 */
function selectVoucher() {
    showNotification('info', 'Chức năng chọn voucher đang được phát triển');
}

/**
 * Place order
 */
function placeOrder() {
    // Validate payment method
    const paymentMethod = document.querySelector('input[name="payment"]:checked');
    if (!paymentMethod) {
        showNotification('error', 'Vui lòng chọn phương thức thanh toán!');
        return;
    }

    // Show loading
    showLoading('Đang xử lý đơn hàng...');

    // Simulate API call
    setTimeout(() => {
        hideLoading();

        // Show success modal
        showSuccessModal();

        // TODO: Send order to server
        console.log('Order data:', {
            ...orderData,
            paymentMethod: paymentMethod.value
        });
    }, 2000);
}

/**
 * Show loading overlay
 */
function showLoading(text = 'Đang tải...') {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.innerHTML = `
        <div class="loading-spinner"></div>
        <div class="loading-text">${text}</div>
    `;
    document.body.appendChild(overlay);
}

/**
 * Hide loading overlay
 */
function hideLoading() {
    const overlay = document.querySelector('.loading-overlay');
    if (overlay) overlay.remove();
}

/**
 * Show success modal
 */
function showSuccessModal() {
    const modal = document.createElement('div');
    modal.className = 'success-modal';
    modal.innerHTML = `
        <div class="success-content">
            <div class="success-icon">
                <i class="fas fa-check"></i>
            </div>
            <h4 style="font-size: 20px; font-weight: 500; margin-bottom: 10px;">Đặt Hàng Thành Công!</h4>
            <p style="font-size: 14px; color: #888; margin-bottom: 20px;">
                Đơn hàng của bạn đã được đặt thành công.<br>
                Mã đơn hàng: <strong>#DH${Date.now().toString().slice(-8)}</strong>
            </p>
            <button onclick="goToOrders()" 
                    style="background: #ee4d2d; color: white; border: none; padding: 10px 30px; border-radius: 2px; cursor: pointer; font-size: 14px; margin-right: 10px;">
                Xem Đơn Hàng
            </button>
            <button onclick="goToHome()" 
                    style="background: white; color: #333; border: 1px solid #d9d9d9; padding: 10px 30px; border-radius: 2px; cursor: pointer; font-size: 14px;">
                Tiếp Tục Mua Sắm
            </button>
        </div>
    `;
    document.body.appendChild(modal);
}

/**
 * Go to orders page
 */
function goToOrders() {
    window.location.href = '/orders';
}

/**
 * Go to home page
 */
function goToHome() {
    window.location.href = '/Index';
}

/**
 * Show notification
 */
function showNotification(type, message) {
    const existing = document.querySelector('.checkout-notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = 'checkout-notification';

    let bgColor = '#ee4d2d';
    let icon = 'fa-exclamation-circle';

    if (type === 'success') {
        bgColor = '#26aa99';
        icon = 'fa-check-circle';
    } else if (type === 'info') {
        bgColor = '#05a';
        icon = 'fa-info-circle';
    }

    notification.style.cssText = `
        position: fixed;
        top: 80px;
        right: 20px;
        background: ${bgColor};
        color: white;
        padding: 16px 24px;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        z-index: 10000;
        min-width: 300px;
        display: flex;
        align-items: center;
        gap: 12px;
        animation: slideInRight 0.3s ease-out;
        font-size: 14px;
    `;

    notification.innerHTML = `
        <i class="fas ${icon}" style="font-size: 20px;"></i>
        <span>${message}</span>
    `;

    if (!document.getElementById('checkout-notification-styles')) {
        const style = document.createElement('style');
        style.id = 'checkout-notification-styles';
        style.textContent = `
            @keyframes slideInRight {
                from { transform: translateX(400px); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Initialize checkout page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Checkout page loaded');

    // Add click handlers for payment options
    document.querySelectorAll('.payment-option').forEach(option => {
        option.addEventListener('click', function() {
            const radio = this.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = true;
            }
        });
    });

    // Prevent form submission on enter
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && e.target.tagName !== 'BUTTON') {
            e.preventDefault();
        }
    });
});