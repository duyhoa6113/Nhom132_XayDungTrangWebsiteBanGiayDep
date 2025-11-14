// checkout.js - Xử lý thanh toán với hiệu ứng Shopee-style

// Biến toàn cục
let selectedAddressId = null;
let selectedPaymentMethod = 'COD';
let subtotal = 0;
let shippingFee = 30000;
let discount = 0;

// Base URL cho API
const API_BASE = '/user/checkout/api';

// Khởi tạo khi trang load
document.addEventListener('DOMContentLoaded', function() {
    // Lấy giá trị từ hidden inputs
    const addressInput = document.getElementById('selectedAddress');
    const paymentInput = document.getElementById('selectedPayment');

    if (addressInput && addressInput.value) {
        selectedAddressId = addressInput.value;
    }

    if (paymentInput && paymentInput.value) {
        selectedPaymentMethod = paymentInput.value;
    }

    // Tính toán ban đầu
    calculateTotals();

    // Reset modal khi đóng
    const addAddressModal = document.getElementById('addAddressModal');
    if (addAddressModal) {
        addAddressModal.addEventListener('hidden.bs.modal', function () {
            document.getElementById('addAddressForm').reset();
            // Reset nút save về trạng thái thêm mới
            const saveBtn = document.querySelector('#addAddressModal .btn-primary');
            if (saveBtn) {
                saveBtn.setAttribute('onclick', 'saveAddress(false, null)');
            }
        });
    }
});

/**
 * Chọn địa chỉ giao hàng
 */
function selectAddress(element) {
    // Bỏ active của tất cả các địa chỉ
    document.querySelectorAll('.address-item').forEach(item => {
        item.classList.remove('active');
    });

    // Thêm active cho địa chỉ được chọn
    element.classList.add('active');

    // Lưu ID địa chỉ được chọn
    selectedAddressId = element.dataset.addressId;

    // Hiển thị toast thông báo
    showSuccessToast('Đã chọn địa chỉ giao hàng');

    console.log('Đã chọn địa chỉ:', selectedAddressId);
}

/**
 * Hiển thị modal thêm địa chỉ mới
 */
function showAddAddressModal() {
    const modal = new bootstrap.Modal(document.getElementById('addAddressModal'));

    // Reset form
    document.getElementById('addAddressForm').reset();

    // Reset tiêu đề modal
    document.querySelector('#addAddressModal .modal-title').textContent = 'Địa Chỉ Mới';

    // Reset nút save
    const saveBtn = document.querySelector('#addAddressModal .btn-primary');
    if (saveBtn) {
        saveBtn.setAttribute('onclick', 'saveAddress(false, null)');
    }

    modal.show();
}

/**
 * Chỉnh sửa địa chỉ
 */
function editAddress(event, addressId) {
    event.stopPropagation();

    showLoading();

    // Fetch thông tin địa chỉ từ server
    fetch(`/api/dia-chi/${addressId}`)
        .then(response => {
            if (!response.ok) throw new Error('Không thể tải thông tin địa chỉ');
            return response.json();
        })
        .then(data => {
            hideLoading();

            // Điền dữ liệu vào form
            document.getElementById('hoTenNhan').value = data.hoTenNhan || '';
            document.getElementById('sdtNhan').value = data.sdtNhan || '';
            document.getElementById('tinhTP').value = data.tinhTP || '';
            document.getElementById('quanHuyen').value = data.quanHuyen || '';
            document.getElementById('phuongXa').value = data.phuongXa || '';
            document.getElementById('diaChi').value = data.diaChi || '';
            document.getElementById('macDinh').checked = data.macDinh || false;

            // Cập nhật tiêu đề modal
            document.querySelector('#addAddressModal .modal-title').textContent = 'Cập Nhật Địa Chỉ';

            // Cập nhật nút save
            const saveBtn = document.querySelector('#addAddressModal .btn-primary');
            if (saveBtn) {
                saveBtn.setAttribute('onclick', `saveAddress(true, ${addressId})`);
            }

            // Hiển thị modal
            const modal = new bootstrap.Modal(document.getElementById('addAddressModal'));
            modal.show();
        })
        .catch(error => {
            hideLoading();
            showErrorToast(error.message || 'Không thể tải thông tin địa chỉ');
        });
}

/**
 * Lưu địa chỉ (Thêm mới hoặc Cập nhật) - SHOPEE STYLE
 */
function saveAddress(isEdit = false, addressId = null) {
    const form = document.getElementById('addAddressForm');

    // Validate form
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Lấy KH ID từ hidden input
    const khachHangId = document.getElementById("khachHangId")?.value;

    if (!khachHangId) {
        showErrorToast('Không tìm thấy thông tin khách hàng');
        return;
    }

    // Lấy dữ liệu từ form
    const data = {
        diaChiId: addressId,
        hoTenNhan: document.getElementById("hoTenNhan").value.trim(),
        sdtNhan: document.getElementById("sdtNhan").value.trim(),
        tinhTP: document.getElementById("tinhTP").value.trim(),
        quanHuyen: document.getElementById("quanHuyen").value.trim(),
        phuongXa: document.getElementById("phuongXa").value.trim(),
        diaChi: document.getElementById("diaChi").value.trim(),
        macDinh: document.getElementById("macDinh").checked
    };

    // Validate số điện thoại
    const phoneRegex = /^[0-9]{10,11}$/;
    if (!phoneRegex.test(data.sdtNhan)) {
        showErrorToast('Số điện thoại không hợp lệ (10-11 chữ số)');
        return;
    }

    const url = isEdit
        ? `/api/dia-chi/update/${khachHangId}`
        : `/api/dia-chi/add/${khachHangId}`;

    const method = isEdit ? "PUT" : "POST";

    // Hiển thị loading
    showLoading();

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
        .then(res => {
            if (!res.ok) throw new Error("Không thể lưu địa chỉ");
            return res.json();
        })
        .then(result => {
            hideLoading();

            // Đóng modal với hiệu ứng
            const modal = bootstrap.Modal.getInstance(document.getElementById('addAddressModal'));
            if (modal) {
                modal.hide();
            }

            // Hiển thị animation thành công SHOPEE STYLE
            showSuccessAnimation(() => {
                // Reload trang sau khi animation kết thúc
                location.reload();
            });
        })
        .catch(err => {
            hideLoading();
            showErrorToast(err.message || 'Có lỗi xảy ra, vui lòng thử lại');
        });
}

/**
 * Chọn phương thức thanh toán
 */
function selectPaymentMethod(element) {
    // Bỏ active của tất cả các phương thức
    document.querySelectorAll('.payment-method').forEach(item => {
        item.classList.remove('active');
    });

    // Thêm active cho phương thức được chọn
    element.classList.add('active');

    // Lưu phương thức được chọn
    selectedPaymentMethod = element.dataset.method;

    // Hiển thị toast thông báo
    showSuccessToast('Đã chọn phương thức thanh toán');

    console.log('Đã chọn phương thức thanh toán:', selectedPaymentMethod);
}

/**
 * Cập nhật phí vận chuyển
 */
function updateShippingFee() {
    const shippingMethod = document.querySelector('input[name="shippingMethod"]:checked');

    if (shippingMethod) {
        if (shippingMethod.value === 'standard') {
            shippingFee = 30000;
        } else if (shippingMethod.value === 'economy') {
            shippingFee = 20000;
        }

        // Animation số tiền thay đổi
        const shippingFeeElement = document.getElementById('shippingFee');
        if (shippingFeeElement) {
            animateNumber(shippingFeeElement, shippingFee);
        }

        calculateTotals();
    }
}

/**
 * Tính toán tổng tiền
 */
function calculateTotals() {
    // Lấy tổng tiền hàng từ trang
    const subtotalElement = document.getElementById('subtotal');
    if (subtotalElement) {
        const subtotalText = subtotalElement.textContent.replace(/[₫,.]/g, '');
        subtotal = parseFloat(subtotalText) || 0;
    }

    // Tính tổng thanh toán
    const totalAmount = subtotal + shippingFee - discount;

    // Cập nhật hiển thị
    const shippingFeeElement = document.getElementById('shippingFee');
    if (shippingFeeElement) {
        shippingFeeElement.textContent = formatCurrency(shippingFee);
    }

    const totalAmountElement = document.getElementById('totalAmount');
    if (totalAmountElement) {
        totalAmountElement.textContent = formatCurrency(totalAmount);
    }

    if (discount > 0) {
        document.getElementById('discountRow').style.display = 'flex';
        document.getElementById('discount').textContent = '-' + formatCurrency(discount);
    }
}

/**
 * Đặt hàng - SHOPEE STYLE
 */
async function placeOrder() {
    // Kiểm tra địa chỉ
    if (!selectedAddressId) {
        showErrorToast('Vui lòng chọn địa chỉ giao hàng');
        return;
    }

    // Lấy danh sách cart item IDs
    const cartItemIdsInput = document.getElementById('cartItemIds');
    let cartItemIds = [];

    if (cartItemIdsInput && cartItemIdsInput.value) {
        cartItemIds = cartItemIdsInput.value.split(',')
            .map(id => parseInt(id.trim()))
            .filter(id => !isNaN(id));
    }

    // Tạo request data
    const orderData = {
        addressId: parseInt(selectedAddressId),
        paymentMethod: selectedPaymentMethod,
        cartItemIds: cartItemIds.length > 0 ? cartItemIds : null,
        shippingFee: shippingFee,
        note: null
    };

    console.log('Đang đặt hàng:', orderData);

    try {
        showLoading();

        const response = await fetch(`${API_BASE}/place-order`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });

        const result = await response.json();

        if (response.ok && result.success) {
            hideLoading();

            // Hiển thị animation đặt hàng thành công
            showOrderSuccessAnimation(() => {
                // Chuyển đến trang chi tiết đơn hàng
                window.location.href = '/user/checkout/success/' + result.orderId;
            });
        } else {
            hideLoading();
            showErrorToast(result.error || 'Không thể đặt hàng');
        }
    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.');
    }
}

// ==================== UI ANIMATION FUNCTIONS (SHOPEE STYLE) ====================

/**
 * Hiển thị animation thành công khi lưu địa chỉ - GIỐNG SHOPEE
 */
function showSuccessAnimation(callback) {
    // Tạo overlay
    const overlay = document.createElement('div');
    overlay.className = 'success-overlay';
    overlay.innerHTML = `
        <div class="success-animation">
            <div class="success-checkmark">
                <div class="check-icon">
                    <span class="icon-line line-tip"></span>
                    <span class="icon-line line-long"></span>
                    <div class="icon-circle"></div>
                    <div class="icon-fix"></div>
                </div>
            </div>
            <h3 class="success-text">Lưu thành công!</h3>
        </div>
    `;

    document.body.appendChild(overlay);

    // Trigger animation
    setTimeout(() => {
        overlay.querySelector('.success-animation').classList.add('active');
    }, 10);

    // Remove sau khi animation hoàn tất
    setTimeout(() => {
        overlay.classList.add('fade-out');
        setTimeout(() => {
            document.body.removeChild(overlay);
            if (callback) callback();
        }, 300);
    }, 1500);
}

/**
 * Hiển thị animation đặt hàng thành công - GIỐNG SHOPEE
 */
function showOrderSuccessAnimation(callback) {
    const overlay = document.createElement('div');
    overlay.className = 'success-overlay';
    overlay.innerHTML = `
        <div class="success-animation">
            <div class="success-checkmark">
                <div class="check-icon">
                    <span class="icon-line line-tip"></span>
                    <span class="icon-line line-long"></span>
                    <div class="icon-circle"></div>
                    <div class="icon-fix"></div>
                </div>
            </div>
            <h3 class="success-text">Đặt hàng thành công!</h3>
            <p class="success-subtext">Cảm ơn bạn đã mua hàng tại NiceSport</p>
        </div>
    `;

    document.body.appendChild(overlay);

    setTimeout(() => {
        overlay.querySelector('.success-animation').classList.add('active');
    }, 10);

    setTimeout(() => {
        overlay.classList.add('fade-out');
        setTimeout(() => {
            document.body.removeChild(overlay);
            if (callback) callback();
        }, 300);
    }, 2000);
}

/**
 * Toast thông báo thành công - GIỐNG SHOPEE
 */
function showSuccessToast(message) {
    showToast(message, 'success');
}

/**
 * Toast thông báo lỗi - GIỐNG SHOPEE
 */
function showErrorToast(message) {
    showToast(message, 'error');
}

/**
 * Hiển thị toast notification - GIỐNG SHOPEE
 */
function showToast(message, type = 'success') {
    // Xóa toast cũ nếu có
    const existingToast = document.querySelector('.custom-toast');
    if (existingToast) {
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;
    toast.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(toast);

    // Trigger animation
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    // Tự động ẩn sau 2.5s
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 2500);
}

/**
 * Animation số tiền thay đổi mượt mà - GIỐNG SHOPEE
 */
function animateNumber(element, targetNumber) {
    const currentText = element.textContent.replace(/[₫,]/g, '');
    const currentNumber = parseInt(currentText) || 0;
    const difference = targetNumber - currentNumber;
    const duration = 500; // ms
    const steps = 30;
    const stepValue = difference / steps;
    const stepDuration = duration / steps;

    let currentStep = 0;

    const interval = setInterval(() => {
        currentStep++;
        const newValue = Math.round(currentNumber + (stepValue * currentStep));
        element.textContent = formatCurrency(newValue);

        if (currentStep >= steps) {
            clearInterval(interval);
            element.textContent = formatCurrency(targetNumber);
        }
    }, stepDuration);
}

/**
 * Format tiền tệ
 */
function formatCurrency(amount) {
    return '₫' + amount.toLocaleString('vi-VN');
}

/**
 * Hiển thị loading overlay
 */
function showLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'flex';
    }
}

/**
 * Ẩn loading overlay
 */
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

function saveAddress(isEdit = false, addressId = null) {
    const form = document.getElementById('addAddressForm');

    // Validate form
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // LẤY KH ID TỪ HIDDEN INPUT - DÒNG NÀY QUAN TRỌNG
    const khachHangId = document.getElementById("khachHangId")?.value;

    if (!khachHangId) {
        showErrorToast('Không tìm thấy thông tin khách hàng. Vui lòng đăng nhập lại');
        return;
    }

    // Lấy dữ liệu từ form
    const data = {
        diaChiId: addressId,
        hoTenNhan: document.getElementById("hoTenNhan").value.trim(),
        sdtNhan: document.getElementById("sdtNhan").value.trim(),
        tinhTP: document.getElementById("tinhTP").value.trim(),
        quanHuyen: document.getElementById("quanHuyen").value.trim(),
        phuongXa: document.getElementById("phuongXa").value.trim(),
        diaChi: document.getElementById("diaChi").value.trim(),
        macDinh: document.getElementById("macDinh").checked
    };

    // Validate số điện thoại
    const phoneRegex = /^[0-9]{10,11}$/;
    if (!phoneRegex.test(data.sdtNhan)) {
        showErrorToast('Số điện thoại không hợp lệ (10-11 chữ số)');
        return;
    }

    const url = isEdit
        ? `/api/dia-chi/update/${khachHangId}`
        : `/api/dia-chi/add/${khachHangId}`;

    const method = isEdit ? "PUT" : "POST";

    showLoading();

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
        .then(res => {
            if (!res.ok) throw new Error("Không thể lưu địa chỉ");
            return res.json();
        })
        .then(result => {
            hideLoading();

            // Đóng modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('addAddressModal'));
            if (modal) {
                modal.hide();
            }

            // Hiển thị animation thành công SHOPEE STYLE
            showSuccessAnimation(() => {
                // Reload trang để hiển thị địa chỉ mới
                location.reload();
            });
        })
        .catch(err => {
            hideLoading();
            showErrorToast(err.message || 'Có lỗi xảy ra, vui lòng thử lại');
        });
}

/**
 * Xóa địa chỉ - SHOPEE STYLE
 */
function deleteAddress(event, addressId) {
    event.stopPropagation();

    // Hiển thị confirmation dialog
    if (!confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) {
        return;
    }

    // Lấy KH ID từ hidden input
    const khachHangId = document.getElementById("khachHangId")?.value;

    if (!khachHangId) {
        showErrorToast('Không tìm thấy thông tin khách hàng');
        return;
    }

    showLoading();

    const url = `/api/dia-chi/delete/${khachHangId}/${addressId}`;

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể xóa địa chỉ');
            }
            return response.json();
        })
        .then(result => {
            hideLoading();

            // Hiển thị animation thành công
            showSuccessAnimation(() => {
                // Reload trang để cập nhật danh sách
                location.reload();
            });
        })
        .catch(error => {
            hideLoading();
            showErrorToast(error.message || 'Có lỗi xảy ra khi xóa địa chỉ');
        });
}