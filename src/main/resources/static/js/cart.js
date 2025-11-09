/**
 * Cart Page JavaScript
 * @author Nhóm 132
 */

// ============================================
// CART DATA (Demo - Thay bằng data từ server)
// ============================================

const CART_STORAGE_KEY = 'nicesport_cart';

// ============================================
// INITIALIZE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('Cart page loaded');
    updateCartTotal();
    updateCartBadge();
});

// ============================================
// QUANTITY CONTROLS
// ============================================

/**
 * Giảm số lượng sản phẩm
 */
function decreaseQuantity(itemId) {
    const input = document.getElementById(itemId);
    if (input && input.value > 1) {
        input.value = parseInt(input.value) - 1;
        const price = getItemPrice(itemId);
        updateItemTotal(itemId, price);
    }
}

/**
 * Tăng số lượng sản phẩm
 */
function increaseQuantity(itemId) {
    const input = document.getElementById(itemId);
    const max = input.getAttribute('max') || 10;
    if (input && input.value < max) {
        input.value = parseInt(input.value) + 1;
        const price = getItemPrice(itemId);
        updateItemTotal(itemId, price);
    }
}

/**
 * Lấy giá sản phẩm
 */
function getItemPrice(itemId) {
    // Demo prices - Thay bằng data thực từ server
    const prices = {
        'item1': 3200000,
        'item2': 4500000,
        'item3': 2800000
    };
    return prices[itemId] || 0;
}

// ============================================
// UPDATE TOTALS
// ============================================

/**
 * Cập nhật tổng tiền từng item
 */
function updateItemTotal(itemId, price) {
    const quantity = parseInt(document.getElementById(itemId).value);
    const total = price * quantity;
    const totalElement = document.getElementById('total-' + itemId);

    if (totalElement) {
        totalElement.textContent = formatCurrency(total);
    }

    updateCartTotal();
}

/**
 * Cập nhật tổng tiền giỏ hàng
 */
function updateCartTotal() {
    let subtotal = 0;

    // Tính tổng tiền từ các items
    const items = ['item1', 'item2', 'item3'];
    items.forEach(itemId => {
        const element = document.getElementById(itemId);
        if (element && element.closest('.cart-item').style.display !== 'none') {
            const quantity = parseInt(element.value);
            const price = getItemPrice(itemId);
            subtotal += price * quantity;
        }
    });

    // Cập nhật hiển thị
    const subtotalElement = document.getElementById('subtotal');
    const totalElement = document.getElementById('total');

    if (subtotalElement) {
        subtotalElement.textContent = formatCurrency(subtotal);
    }

    if (totalElement) {
        totalElement.textContent = formatCurrency(subtotal);
    }

    updateCartBadge();
}

/**
 * Format tiền tệ
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'decimal'
    }).format(amount) + '₫';
}

// ============================================
// CART ACTIONS
// ============================================

/**
 * Xóa sản phẩm khỏi giỏ hàng
 */
function removeItem(itemId) {
    if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        const cartItem = document.getElementById(itemId).closest('.cart-item');

        // Add fade out animation
        cartItem.style.opacity = '0';
        cartItem.style.transform = 'translateX(-20px)';
        cartItem.style.transition = 'all 0.3s ease';

        setTimeout(() => {
            cartItem.style.display = 'none';
            updateCartTotal();
            checkEmptyCart();

            showNotification('success', 'Đã xóa sản phẩm khỏi giỏ hàng');
        }, 300);
    }
}

/**
 * Xóa tất cả sản phẩm
 */
function clearCart() {
    if (confirm('Bạn có chắc muốn xóa tất cả sản phẩm trong giỏ hàng?')) {
        document.querySelectorAll('.cart-item').forEach(item => {
            item.style.opacity = '0';
            item.style.transition = 'all 0.3s ease';
        });

        setTimeout(() => {
            document.querySelectorAll('.cart-item').forEach(item => {
                item.style.display = 'none';
            });
            updateCartTotal();
            checkEmptyCart();

            showNotification('success', 'Đã xóa tất cả sản phẩm');
        }, 300);
    }
}

/**
 * Kiểm tra giỏ hàng có trống không
 */
function checkEmptyCart() {
    const visibleItems = Array.from(document.querySelectorAll('.cart-item'))
        .filter(item => item.style.display !== 'none');

    if (visibleItems.length === 0) {
        // Hide cart section
        const cartSection = document.querySelector('.cart-section');
        if (cartSection) {
            cartSection.classList.add('d-none');
        }

        // Show empty cart message
        const emptyCart = document.querySelector('.empty-cart-section');
        if (emptyCart) {
            emptyCart.classList.remove('d-none');
        }

        // Update badge
        const badge = document.getElementById('cartBadge');
        if (badge) {
            badge.textContent = '0';
        }
    }
}

/**
 * Cập nhật số lượng trong badge
 */
function updateCartBadge() {
    const visibleItems = Array.from(document.querySelectorAll('.cart-item'))
        .filter(item => item.style.display !== 'none');

    const badge = document.getElementById('cartBadge');
    if (badge) {
        badge.textContent = visibleItems.length;
    }
}

// ============================================
// DISCOUNT CODE
// ============================================

/**
 * Áp dụng mã giảm giá
 */
function applyDiscount() {
    const codeInput = document.getElementById('discountCode');
    const code = codeInput.value.trim().toUpperCase();

    // Demo discount codes
    const discountCodes = {
        'NICE10': { percent: 0.1, name: '10% off' },
        'NICE20': { percent: 0.2, name: '20% off' },
        'FREESHIP': { percent: 0.05, name: '5% off' },
        'WELCOME': { percent: 0.15, name: '15% off' }
    };

    if (discountCodes[code]) {
        const subtotalText = document.getElementById('subtotal').textContent;
        const subtotal = parseInt(subtotalText.replace(/\D/g, ''));
        const discountPercent = discountCodes[code].percent;
        const discountAmount = subtotal * discountPercent;
        const total = subtotal - discountAmount;

        // Update display
        document.getElementById('discount').textContent = '-' + formatCurrency(discountAmount);
        document.getElementById('total').textContent = formatCurrency(total);

        showNotification('success', `Đã áp dụng mã ${code} (${discountCodes[code].name})`);
        codeInput.value = '';

        // Disable input after applying
        codeInput.disabled = true;
        codeInput.placeholder = `Đã áp dụng: ${code}`;
    } else if (code === '') {
        showNotification('error', 'Vui lòng nhập mã giảm giá');
    } else {
        showNotification('error', 'Mã giảm giá không hợp lệ!');
    }
}

// ============================================
// CHECKOUT
// ============================================

/**
 * Thanh toán
 */
function checkout() {
    const total = document.getElementById('total').textContent;
    const visibleItems = Array.from(document.querySelectorAll('.cart-item'))
        .filter(item => item.style.display !== 'none');

    if (visibleItems.length === 0) {
        showNotification('error', 'Giỏ hàng trống! Vui lòng thêm sản phẩm.');
        return;
    }

    // TODO: Navigate to checkout page
    // window.location.href = '/checkout';

    showNotification('info', `Tổng thanh toán: ${total}\n\nChức năng thanh toán đang được phát triển...`);
}

// ============================================
// NOTIFICATIONS
// ============================================

/**
 * Hiển thị thông báo
 */
function showNotification(type, message) {
    const alertClass = type === 'success' ? 'alert-success' :
        type === 'error' ? 'alert-danger' :
            'alert-info';

    const icon = type === 'success' ? 'check-circle' :
        type === 'error' ? 'exclamation-circle' :
            'info-circle';

    const notification = document.createElement('div');
    notification.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px; max-width: 500px;';

    notification.innerHTML = `
        <i class="fas fa-${icon} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// ============================================
// LOCAL STORAGE (Optional)
// ============================================

/**
 * Lưu giỏ hàng vào localStorage
 */
function saveCartToStorage() {
    const cartData = [];

    document.querySelectorAll('.cart-item').forEach(item => {
        if (item.style.display !== 'none') {
            const itemId = item.querySelector('.quantity-input').id;
            const quantity = item.querySelector('.quantity-input').value;

            cartData.push({
                id: itemId,
                quantity: parseInt(quantity)
            });
        }
    });

    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cartData));
}

/**
 * Load giỏ hàng từ localStorage
 */
function loadCartFromStorage() {
    const cartData = localStorage.getItem(CART_STORAGE_KEY);

    if (cartData) {
        try {
            const items = JSON.parse(cartData);
            // TODO: Restore cart items from storage
            console.log('Loaded cart from storage:', items);
        } catch (e) {
            console.error('Error loading cart from storage:', e);
        }
    }
}

// ============================================
// KEYBOARD SHORTCUTS
// ============================================

document.addEventListener('keydown', function(e) {
    // ESC to close
    if (e.key === 'Escape') {
        const discountInput = document.getElementById('discountCode');
        if (discountInput === document.activeElement) {
            discountInput.blur();
        }
    }

    // CTRL + ENTER to checkout
    if (e.ctrlKey && e.key === 'Enter') {
        checkout();
    }
});

// ============================================
// EXPORT (Optional)
// ============================================

// Export functions for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        decreaseQuantity,
        increaseQuantity,
        removeItem,
        clearCart,
        applyDiscount,
        checkout,
        updateCartTotal
    };
}