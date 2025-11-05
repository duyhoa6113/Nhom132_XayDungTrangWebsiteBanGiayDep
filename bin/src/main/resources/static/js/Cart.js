/**
 * Cart Management JavaScript
 * Quản lý giỏ hàng phía client
 */

const CartManager = {
    // Base URL cho API
    API_URL: '/api/cart',

    /**
     * Khởi tạo
     */
    init() {
        this.bindEvents();
        this.loadCart();
        this.updateCartBadge();
    },

    /**
     * Bind các sự kiện
     */
    bindEvents() {
        // Thêm vào giỏ hàng
        $(document).on('click', '.btn-add-to-cart', (e) => {
            e.preventDefault();
            const variantId = $(e.currentTarget).data('variant-id');
            const soLuong = parseInt($('#quantity-input').val()) || 1;
            this.addToCart(variantId, soLuong);
        });

        // Cập nhật số lượng
        $(document).on('change', '.cart-quantity-input', (e) => {
            const cartItemId = $(e.currentTarget).data('cart-item-id');
            const soLuong = parseInt($(e.currentTarget).val());
            this.updateQuantity(cartItemId, soLuong);
        });

        // Tăng số lượng
        $(document).on('click', '.btn-increase-quantity', (e) => {
            e.preventDefault();
            const input = $(e.currentTarget).siblings('.cart-quantity-input');
            const currentQty = parseInt(input.val());
            const maxQty = parseInt(input.attr('max'));
            if (currentQty < maxQty) {
                input.val(currentQty + 1).trigger('change');
            }
        });

        // Giảm số lượng
        $(document).on('click', '.btn-decrease-quantity', (e) => {
            e.preventDefault();
            const input = $(e.currentTarget).siblings('.cart-quantity-input');
            const currentQty = parseInt(input.val());
            if (currentQty > 1) {
                input.val(currentQty - 1).trigger('change');
            }
        });

        // Xóa sản phẩm
        $(document).on('click', '.btn-remove-item', (e) => {
            e.preventDefault();
            const cartItemId = $(e.currentTarget).data('cart-item-id');
            this.removeItem(cartItemId);
        });

        // Xóa toàn bộ giỏ hàng
        $(document).on('click', '.btn-clear-cart', (e) => {
            e.preventDefault();
            if (confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) {
                this.clearCart();
            }
        });
    },

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    addToCart(variantId, soLuong) {
        $.ajax({
            url: `${this.API_URL}/add`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                variantId: variantId,
                soLuong: soLuong
            }),
            success: (response) => {
                this.showNotification('Đã thêm vào giỏ hàng', 'success');
                this.updateCartBadge();
                // Cập nhật giỏ hàng nếu đang ở trang giỏ hàng
                if (window.location.pathname.includes('cart')) {
                    this.renderCart(response);
                }
            },
            error: (xhr) => {
                const error = xhr.responseJSON?.message || 'Có lỗi xảy ra';
                this.showNotification(error, 'error');
            }
        });
    },

    /**
     * Cập nhật số lượng sản phẩm
     */
    updateQuantity(cartItemId, soLuong) {
        $.ajax({
            url: `${this.API_URL}/items/${cartItemId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                soLuong: soLuong
            }),
            success: (response) => {
                this.renderCart(response);
                this.updateCartBadge();
            },
            error: (xhr) => {
                const error = xhr.responseJSON?.message || 'Có lỗi xảy ra';
                this.showNotification(error, 'error');
                this.loadCart(); // Reload để reset về số lượng cũ
            }
        });
    },

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    removeItem(cartItemId) {
        $.ajax({
            url: `${this.API_URL}/items/${cartItemId}`,
            method: 'DELETE',
            success: (response) => {
                this.showNotification('Đã xóa sản phẩm', 'success');
                this.renderCart(response);
                this.updateCartBadge();
            },
            error: (xhr) => {
                const error = xhr.responseJSON?.message || 'Có lỗi xảy ra';
                this.showNotification(error, 'error');
            }
        });
    },

    /**
     * Xóa toàn bộ giỏ hàng
     */
    clearCart() {
        $.ajax({
            url: `${this.API_URL}/clear`,
            method: 'DELETE',
            success: () => {
                this.showNotification('Đã xóa toàn bộ giỏ hàng', 'success');
                this.loadCart();
                this.updateCartBadge();
            },
            error: (xhr) => {
                const error = xhr.responseJSON?.message || 'Có lỗi xảy ra';
                this.showNotification(error, 'error');
            }
        });
    },

    /**
     * Load giỏ hàng
     */
    loadCart() {
        $.ajax({
            url: this.API_URL,
            method: 'GET',
            success: (response) => {
                this.renderCart(response);
            },
            error: (xhr) => {
                console.error('Error loading cart:', xhr);
            }
        });
    },

    /**
     * Render giỏ hàng
     */
    renderCart(cart) {
        const container = $('#cart-items-container');
        if (!container.length) return;

        if (!cart.items || cart.items.length === 0) {
            container.html(`
                <div class="empty-cart">
                    <i class="fas fa-shopping-cart fa-3x"></i>
                    <h3>Giỏ hàng trống</h3>
                    <p>Hãy thêm sản phẩm vào giỏ hàng</p>
                    <a href="/products" class="btn btn-primary">Tiếp tục mua sắm</a>
                </div>
            `);
            $('#cart-summary').hide();
            return;
        }

        let html = '';
        cart.items.forEach(item => {
            const discount = item.giaGoc ? ((item.giaGoc - item.donGia) / item.giaGoc * 100).toFixed(0) : 0;

            html += `
                <div class="cart-item" data-cart-item-id="${item.gioHangCTId}">
                    <div class="item-image">
                        <img src="${item.hinhAnh || '/img/no-image.png'}" alt="${item.tenSanPham}">
                    </div>
                    <div class="item-details">
                        <h4 class="item-name">${item.tenSanPham}</h4>
                        <p class="item-variant">Màu: ${item.mauSac} | Size: ${item.kichThuoc}</p>
                        <p class="item-sku">SKU: ${item.sku}</p>
                    </div>
                    <div class="item-price">
                        <span class="current-price">${this.formatCurrency(item.donGia)}</span>
                        ${item.giaGoc ? `
                            <span class="original-price">${this.formatCurrency(item.giaGoc)}</span>
                            <span class="discount-badge">-${discount}%</span>
                        ` : ''}
                    </div>
                    <div class="item-quantity">
                        <button class="btn-decrease-quantity">-</button>
                        <input type="number" 
                               class="cart-quantity-input" 
                               data-cart-item-id="${item.gioHangCTId}"
                               value="${item.soLuong}" 
                               min="1" 
                               max="${item.soLuongTon}">
                        <button class="btn-increase-quantity">+</button>
                    </div>
                    <div class="item-total">
                        ${this.formatCurrency(item.thanhTien)}
                    </div>
                    <div class="item-actions">
                        <button class="btn-remove-item" data-cart-item-id="${item.gioHangCTId}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            `;
        });

        container.html(html);

        // Cập nhật tổng kết
        $('#total-items').text(cart.tongSoLuong);
        $('#total-price').text(this.formatCurrency(cart.tongTien));
        $('#total-discount').text(this.formatCurrency(cart.tongTietKiem));
        $('#cart-summary').show();
    },

    /**
     * Cập nhật badge số lượng giỏ hàng
     */
    updateCartBadge() {
        $.ajax({
            url: `${this.API_URL}/count`,
            method: 'GET',
            success: (response) => {
                $('.cart-badge').text(response.count);
                if (response.count > 0) {
                    $('.cart-badge').show();
                } else {
                    $('.cart-badge').hide();
                }
            }
        });
    },

    /**
     * Format tiền tệ
     */
    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    },

    /**
     * Hiển thị thông báo
     */
    showNotification(message, type = 'info') {
        // Sử dụng thư viện notification có sẵn hoặc tự implement
        // Ví dụ với toastr:
        if (typeof toastr !== 'undefined') {
            toastr[type](message);
        } else {
            alert(message);
        }
    }
};

// Khởi tạo khi document ready
$(document).ready(() => {
    CartManager.init();
});