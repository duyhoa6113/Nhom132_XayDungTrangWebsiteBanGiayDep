/* ========================================
   CART PAGE JAVASCRIPT - COMPLETE VERSION
   With multiple checkout methods
   ======================================== */

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updateCartSummary();
    initializeCheckboxes();
});

// Initialize checkboxes state
function initializeCheckboxes() {
    const selectAll = document.getElementById('selectAll');
    const selectAllBottom = document.getElementById('selectAllBottom');

    if (selectAll && selectAllBottom) {
        selectAll.addEventListener('change', function() {
            selectAllBottom.checked = this.checked;
        });
        selectAllBottom.addEventListener('change', function() {
            selectAll.checked = this.checked;
        });
    }
}

// Toggle select all items
function toggleSelectAll(checkbox) {
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    itemCheckboxes.forEach(cb => {
        cb.checked = checkbox.checked;
    });
    updateCartSummary();
}

// Update cart summary
function updateCartSummary() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
    let totalAmount = 0;
    let selectedCount = 0;

    checkedBoxes.forEach(checkbox => {
        const itemId = checkbox.dataset.itemId;
        const qtyInput = document.getElementById(`qty-${itemId}`);
        const price = parseFloat(checkbox.dataset.price);
        const quantity = qtyInput ? parseInt(qtyInput.value) : parseInt(checkbox.dataset.quantity);

        totalAmount += price * quantity;
        selectedCount++;
    });

    // Update UI
    const totalAmountElement = document.getElementById('totalAmount');
    const selectedCountElement = document.getElementById('selectedCount');
    const totalItemsElement = document.getElementById('totalItems');

    if (totalAmountElement) totalAmountElement.textContent = formatNumber(totalAmount);
    if (selectedCountElement) selectedCountElement.textContent = selectedCount;
    if (totalItemsElement) totalItemsElement.textContent = document.querySelectorAll('.item-checkbox').length;

    // Enable/disable checkout button
    const checkoutBtn = document.querySelector('.btn-checkout');
    if (checkoutBtn) checkoutBtn.disabled = selectedCount === 0;

    // Update select all checkbox
    const allCheckboxes = document.querySelectorAll('.item-checkbox');
    const selectAll = document.getElementById('selectAll');
    const selectAllBottom = document.getElementById('selectAllBottom');

    if (selectAll && selectAllBottom && allCheckboxes.length > 0) {
        const allChecked = checkedBoxes.length === allCheckboxes.length;
        selectAll.checked = allChecked;
        selectAllBottom.checked = allChecked;
    }
}

// Increase quantity
function increaseQuantity(itemId) {
    const qtyInput = document.getElementById(`qty-${itemId}`);
    if (!qtyInput) return;

    const currentQty = parseInt(qtyInput.value);
    const maxStock = parseInt(qtyInput.dataset.max) || 999;

    if (currentQty < maxStock) {
        updateQuantity(itemId, currentQty + 1);
    } else {
        showNotification('Đã đạt số lượng tối đa', 'warning');
    }
}

// Decrease quantity
function decreaseQuantity(itemId) {
    const qtyInput = document.getElementById(`qty-${itemId}`);
    if (!qtyInput) return;

    const currentQty = parseInt(qtyInput.value);

    if (currentQty > 1) {
        updateQuantity(itemId, currentQty - 1);
    } else {
        showNotification('Số lượng tối thiểu là 1', 'warning');
    }
}

// Update quantity via API
function updateQuantity(itemId, newQuantity) {
    showLoading(true);

    const requestBody = {
        cartItemId: parseInt(itemId),
        soLuong: parseInt(newQuantity)
    };

    fetch('/cart/update', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Không thể cập nhật số lượng');
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                const qtyInput = document.getElementById(`qty-${itemId}`);
                if (qtyInput) qtyInput.value = newQuantity;

                const checkbox = document.querySelector(`.item-checkbox[data-item-id="${itemId}"]`);
                if (checkbox) {
                    const price = parseFloat(checkbox.dataset.price);
                    const total = price * newQuantity;
                    checkbox.dataset.quantity = newQuantity;

                    const totalSpan = document.querySelector(`.item-total[data-item-id="${itemId}"] span`);
                    if (totalSpan) totalSpan.textContent = formatNumber(total);
                }

                updateCartSummary();
                showNotification('Đã cập nhật số lượng', 'success');
            } else {
                throw new Error(data.message || 'Không thể cập nhật số lượng');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message, 'error');

            const qtyInput = document.getElementById(`qty-${itemId}`);
            if (qtyInput) {
                const checkbox = document.querySelector(`.item-checkbox[data-item-id="${itemId}"]`);
                if (checkbox) qtyInput.value = checkbox.dataset.quantity;
            }
        })
        .finally(() => {
            showLoading(false);
        });
}

// Delete single item
function deleteItem(itemId) {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }

    showLoading(true);

    fetch(`/cart/delete/${itemId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Không thể xóa sản phẩm');
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                const cartItem = document.querySelector(`.cart-item[data-item-id="${itemId}"]`);
                if (cartItem) {
                    cartItem.style.animation = 'slideOutRight 0.3s ease-in';
                    setTimeout(() => {
                        cartItem.remove();

                        updateCartBadgeFromServer();

                        const remainingItems = document.querySelectorAll('.cart-item');
                        if (remainingItems.length === 0) {
                            showEmptyCart();
                        } else {
                            updateCartSummary();
                        }
                    }, 300);
                }

                showNotification('Đã xóa sản phẩm khỏi giỏ hàng', 'success');
            } else {
                throw new Error(data.message || 'Không thể xóa sản phẩm');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message, 'error');
        })
        .finally(() => {
            showLoading(false);
        });
}

// Delete selected items
function deleteSelected() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (checkedBoxes.length === 0) {
        showNotification('Vui lòng chọn sản phẩm cần xóa', 'warning');
        return;
    }

    if (!confirm(`Bạn có chắc muốn xóa ${checkedBoxes.length} sản phẩm đã chọn?`)) {
        return;
    }

    showLoading(true);

    const itemIds = Array.from(checkedBoxes).map(cb => parseInt(cb.dataset.itemId));

    fetch('/cart/delete-multiple', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(itemIds)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Không thể xóa sản phẩm');
                });
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                checkedBoxes.forEach((checkbox, index) => {
                    const itemId = checkbox.dataset.itemId;
                    const cartItem = document.querySelector(`.cart-item[data-item-id="${itemId}"]`);
                    if (cartItem) {
                        setTimeout(() => {
                            cartItem.style.animation = 'slideOutRight 0.3s ease-in';
                            setTimeout(() => {
                                cartItem.remove();

                                if (index === checkedBoxes.length - 1) {
                                    updateCartBadgeFromServer();

                                    const remainingItems = document.querySelectorAll('.cart-item');
                                    if (remainingItems.length === 0) {
                                        showEmptyCart();
                                    } else {
                                        updateCartSummary();
                                    }
                                }
                            }, 300);
                        }, index * 100);
                    }
                });

                showNotification(`Đã xóa ${itemIds.length} sản phẩm`, 'success');
            } else {
                throw new Error(data.message || 'Không thể xóa sản phẩm');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message, 'error');
        })
        .finally(() => {
            setTimeout(() => {
                showLoading(false);
            }, checkedBoxes.length * 100 + 300);
        });
}

/**
 * CHECKOUT FUNCTION
 * Choose ONE of these methods based on your backend implementation
 */

// METHOD 1: Using GET with query parameters (Recommended for viewing checkout page)
function checkout() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (checkedBoxes.length === 0) {
        showNotification('Vui lòng chọn sản phẩm để thanh toán', 'warning');
        return;
    }

    // Get selected item IDs
    const selectedItems = Array.from(checkedBoxes).map(cb => parseInt(cb.dataset.itemId));

    // Build query string
    const itemsParam = selectedItems.join(',');

    // Redirect to checkout page with items as query parameter
    window.location.href = `/checkout?items=${itemsParam}`;
}

// METHOD 2: Using POST form submission
function checkoutWithPost() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (checkedBoxes.length === 0) {
        showNotification('Vui lòng chọn sản phẩm để thanh toán', 'warning');
        return;
    }

    const selectedItems = Array.from(checkedBoxes).map(cb => parseInt(cb.dataset.itemId));

    // Create and submit form
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/checkout';

    // Add each item ID as a separate parameter
    selectedItems.forEach(itemId => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'cartItemIds';
        input.value = itemId;
        form.appendChild(input);
    });

    document.body.appendChild(form);
    form.submit();
}

// METHOD 3: Using AJAX to create checkout session
function checkoutWithAjax() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');

    if (checkedBoxes.length === 0) {
        showNotification('Vui lòng chọn sản phẩm để thanh toán', 'warning');
        return;
    }

    const selectedItems = Array.from(checkedBoxes).map(cb => parseInt(cb.dataset.itemId));

    showLoading(true);

    fetch('/api/checkout/create-session', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ cartItemIds: selectedItems })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Redirect to checkout page
                window.location.href = `/checkout?sessionId=${data.sessionId}`;
            } else {
                throw new Error(data.message || 'Không thể tạo phiên thanh toán');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification(error.message, 'error');
        })
        .finally(() => {
            showLoading(false);
        });
}

// Show empty cart message
function showEmptyCart() {
    const cartItems = document.querySelector('.cart-items');
    if (cartItems) {
        cartItems.innerHTML = `
            <div class="empty-cart-message">
                <div class="bg-white text-center py-5" style="border-radius: 2px;">
                    <img src="https://deo.shopeemobile.com/shopee/shopee-pcmall-live-sg/cart/9bdd8040b334d31946f49e36beaf32db.png"
                         alt="Empty Cart"
                         style="width: 100px; opacity: 0.5; margin-bottom: 20px;">
                    <p style="font-size: 14px; color: #888;">Giỏ hàng của bạn còn trống</p>
                    <a href="/Index" class="btn btn-primary mt-3" style="background: #ee4d2d; border: none; padding: 10px 40px; border-radius: 2px; font-size: 14px;">
                        MUA NGAY
                    </a>
                </div>
            </div>
        `;
    }

    const cartSummary = document.querySelector('.cart-summary');
    if (cartSummary) cartSummary.style.display = 'none';

    const cartHeader = document.querySelector('.cart-header');
    if (cartHeader) cartHeader.style.display = 'none';

    const cartBadge = document.querySelector('.cart-badge-main');
    if (cartBadge) cartBadge.style.display = 'none';
}

// Update cart badge from server
function updateCartBadgeFromServer() {
    fetch('/cart/count')
        .then(response => response.json())
        .then(data => {
            const cartBadge = document.querySelector('.cart-badge-main');
            if (cartBadge) {
                const count = data.cartCount || 0;
                if (count > 0) {
                    cartBadge.textContent = count;
                    cartBadge.style.display = 'flex';
                } else {
                    cartBadge.style.display = 'none';
                }
            }
        })
        .catch(error => {
            console.error('Error updating cart badge:', error);
        });
}

// Format number
function formatNumber(number) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(number));
}

// Show loading
function showLoading(show) {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = show ? 'flex' : 'none';
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas ${getNotificationIcon(type)} me-2"></i>
            <span>${message}</span>
        </div>
    `;

    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                position: fixed;
                top: 80px;
                right: 20px;
                z-index: 9999;
                padding: 12px 20px;
                border-radius: 4px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                animation: slideInRight 0.3s ease-out;
                min-width: 250px;
            }
            .notification-success { background: #52c41a; color: white; }
            .notification-error { background: #ff4d4f; color: white; }
            .notification-warning { background: #faad14; color: white; }
            .notification-info { background: #1890ff; color: white; }
            @keyframes slideInRight {
                from { transform: translateX(400px); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOutRight {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(400px); opacity: 0; }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-in';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Get notification icon
function getNotificationIcon(type) {
    switch(type) {
        case 'success': return 'fa-check-circle';
        case 'error': return 'fa-times-circle';
        case 'warning': return 'fa-exclamation-circle';
        default: return 'fa-info-circle';
    }
}