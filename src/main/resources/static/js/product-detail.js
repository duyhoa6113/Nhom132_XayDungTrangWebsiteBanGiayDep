/**
 * Product Detail Page JavaScript
 * @author Nhóm 132
 */

// ============================================
// IMAGE GALLERY
// ============================================

/**
 * Thay đổi hình ảnh chính khi click vào thumbnail
 */
function changeImage(src) {
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
        mainImage.src = src;

        // Add fade animation
        mainImage.style.opacity = '0';
        setTimeout(() => {
            mainImage.style.opacity = '1';
        }, 100);
    }
}

// ============================================
// COLOR SELECTION
// ============================================

/**
 * Chọn màu sắc
 */
function selectColor(element) {
    // Remove active class from all color options
    document.querySelectorAll('.color-option').forEach(btn => {
        btn.classList.remove('active');
    });

    // Add active class to selected option
    element.classList.add('active');

    const colorName = element.getAttribute('data-color');
    console.log('Selected color:', colorName);

    // TODO: Filter size options based on selected color
    // filterSizesByColor(colorName);
}

// ============================================
// SIZE SELECTION
// ============================================

/**
 * Chọn kích thước
 */
function selectSize(element) {
    // Remove active class from all size options
    document.querySelectorAll('.size-option').forEach(btn => {
        btn.classList.remove('active');
    });

    // Add active class to selected option
    element.classList.add('active');

    const size = element.getAttribute('data-size');
    const variantId = element.getAttribute('data-variant-id');
    console.log('Selected size:', size, 'Variant ID:', variantId);
}

// ============================================
// QUANTITY CONTROLS
// ============================================

/**
 * Giảm số lượng
 */
function decreaseQuantity() {
    const input = document.getElementById('quantity');
    if (input && input.value > 1) {
        input.value = parseInt(input.value) - 1;
    }
}

/**
 * Tăng số lượng
 */
function increaseQuantity() {
    const input = document.getElementById('quantity');
    const max = input.getAttribute('max') || 10;
    if (input && input.value < max) {
        input.value = parseInt(input.value) + 1;
    }
}

/**
 * Validate quantity input
 */
document.addEventListener('DOMContentLoaded', function() {
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        quantityInput.addEventListener('change', function() {
            const min = parseInt(this.getAttribute('min')) || 1;
            const max = parseInt(this.getAttribute('max')) || 10;
            let value = parseInt(this.value);

            if (isNaN(value) || value < min) {
                this.value = min;
            } else if (value > max) {
                this.value = max;
            }
        });
    }
});

// ============================================
// ADD TO CART
// ============================================

/**
 * Thêm sản phẩm vào giỏ hàng
 */
function addToCart() {
    // Get selected size
    const selectedSize = document.querySelector('.size-option.active');

    if (!selectedSize) {
        showNotification('error', 'Vui lòng chọn kích thước!');
        // Scroll to size selection
        document.querySelector('.size-options').scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
        return;
    }

    // Get quantity
    const quantity = parseInt(document.getElementById('quantity').value) || 1;

    // Get variant ID
    const variantId = selectedSize.getAttribute('data-variant-id');

    // Get product info
    const productName = document.querySelector('.product-name').textContent;
    const size = selectedSize.getAttribute('data-size');

    // Show loading
    const btn = event.target;
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang thêm...';

    // Simulate API call (replace with actual API call)
    setTimeout(() => {
        // Success
        btn.disabled = false;
        btn.innerHTML = originalText;

        showNotification('success', `Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);

        // Update cart badge
        updateCartBadge();

        console.log('Added to cart:', {
            variantId,
            productName,
            size,
            quantity
        });

        // TODO: Call actual API
        // fetch('/api/cart/add', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify({ variantId, quantity })
        // });

    }, 1000);
}

// ============================================
// NOTIFICATION
// ============================================

/**
 * Hiển thị thông báo
 */
function showNotification(type, message) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';

    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
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
// CART BADGE
// ============================================

/**
 * Cập nhật số lượng giỏ hàng
 */
function updateCartBadge() {
    const cartBadge = document.querySelector('.cart-badge');
    if (cartBadge) {
        const currentCount = parseInt(cartBadge.textContent) || 0;
        cartBadge.textContent = currentCount + 1;

        // Add animation
        cartBadge.style.transform = 'scale(1.3)';
        setTimeout(() => {
            cartBadge.style.transform = 'scale(1)';
        }, 200);
    }
}

// ============================================
// IMAGE ZOOM (Optional)
// ============================================

/**
 * Zoom hình ảnh khi hover (optional feature)
 */
function initImageZoom() {
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
        mainImage.addEventListener('mousemove', function(e) {
            const rect = this.getBoundingClientRect();
            const x = ((e.clientX - rect.left) / rect.width) * 100;
            const y = ((e.clientY - rect.top) / rect.height) * 100;

            this.style.transformOrigin = `${x}% ${y}%`;
            this.style.transform = 'scale(1.5)';
        });

        mainImage.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('Product detail page loaded');

    // Optional: Enable image zoom
    // initImageZoom();

    // Auto-select first size option if only one available
    const sizeOptions = document.querySelectorAll('.size-option');
    if (sizeOptions.length === 1) {
        sizeOptions[0].click();
    }

    // Auto-select first color option if only one available
    const colorOptions = document.querySelectorAll('.color-option');
    if (colorOptions.length === 1) {
        colorOptions[0].click();
    }
});

// ============================================
// SMOOTH SCROLL
// ============================================

/**
 * Smooth scroll to element
 */
function smoothScrollTo(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}