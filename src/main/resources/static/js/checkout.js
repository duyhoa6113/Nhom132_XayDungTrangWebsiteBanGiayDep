// checkout.js

// ==================== BIẾN TOÀN CỤC ====================
let selectedAddressId = null;
let selectedPaymentMethod = 'COD';
let subtotal = 0;
let shippingFee = 30000;
let discount = 0;

// API endpoint - ĐÚNG VỚI BACKEND
const API_ENDPOINT = '/checkout/process';

// ==================== KHỞI TẠO ====================
document.addEventListener('DOMContentLoaded', function() {
    const addressInput = document.getElementById('selectedAddress');
    const paymentInput = document.getElementById('selectedPayment');

    // Lấy địa chỉ từ hidden input
    if (addressInput && addressInput.value) {
        selectedAddressId = addressInput.value;
    }

    // ✨ AUTO-SELECT địa chỉ mặc định nếu chưa có
    if (!selectedAddressId) {
        // Tìm địa chỉ có class "active" (mặc định)
        const defaultAddressItem = document.querySelector('.address-item.active');
        if (defaultAddressItem) {
            selectedAddressId = defaultAddressItem.dataset.addressId;
            console.log('✅ Tự động chọn địa chỉ mặc định:', selectedAddressId);
        } else {
            // Nếu không có active, chọn địa chỉ đầu tiên
            const firstAddress = document.querySelector('.address-item');
            if (firstAddress) {
                selectedAddressId = firstAddress.dataset.addressId;
                firstAddress.classList.add('active');
                console.log('✅ Tự động chọn địa chỉ đầu tiên:', selectedAddressId);
            }
        }
    }

    if (paymentInput && paymentInput.value) {
        selectedPaymentMethod = paymentInput.value;
    }

    calculateTotals();

    const addAddressModal = document.getElementById('addAddressModal');
    if (addAddressModal) {
        addAddressModal.addEventListener('hidden.bs.modal', function () {
            document.getElementById('addAddressForm').reset();
            const saveBtn = document.querySelector('#addAddressModal .btn-primary');
            if (saveBtn) {
                saveBtn.setAttribute('onclick', 'saveAddress(false, null)');
            }
        });
    }

    console.log('✅ Checkout initialized');
    console.log('📍 API Endpoint:', API_ENDPOINT);
});

// ==================== QUẢN LÝ ĐỊA CHỈ ====================

function selectAddress(element) {
    document.querySelectorAll('.address-item').forEach(item => {
        item.classList.remove('active');
    });
    element.classList.add('active');
    selectedAddressId = element.dataset.addressId;

    const addressInput = document.getElementById('selectedAddress');
    if (addressInput) {
        addressInput.value = selectedAddressId;
    }

    showSuccessToast('Đã chọn địa chỉ giao hàng');
    console.log('✅ Đã chọn địa chỉ:', selectedAddressId);
}

function showAddAddressModal() {
    const modal = new bootstrap.Modal(document.getElementById('addAddressModal'));
    document.getElementById('addAddressForm').reset();
    document.querySelector('#addAddressModal .modal-title').textContent = 'Địa Chỉ Mới';
    const saveBtn = document.querySelector('#addAddressModal .btn-primary');
    if (saveBtn) {
        saveBtn.setAttribute('onclick', 'saveAddress(false, null)');
    }
    modal.show();
}

function editAddress(event, addressId) {
    event.stopPropagation();
    showLoading();

    fetch(`/api/dia-chi/${addressId}`)
        .then(response => {
            if (!response.ok) throw new Error('Không thể tải thông tin địa chỉ');
            return response.json();
        })
        .then(data => {
            hideLoading();
            document.getElementById('hoTenNhan').value = data.hoTenNhan || '';
            document.getElementById('sdtNhan').value = data.sdtNhan || '';
            document.getElementById('tinhTP').value = data.tinhTP || '';
            document.getElementById('quanHuyen').value = data.quanHuyen || '';
            document.getElementById('phuongXa').value = data.phuongXa || '';
            document.getElementById('diaChi').value = data.diaChi || '';
            document.getElementById('macDinh').checked = data.macDinh || false;

            document.querySelector('#addAddressModal .modal-title').textContent = 'Cập Nhật Địa Chỉ';
            const saveBtn = document.querySelector('#addAddressModal .btn-primary');
            if (saveBtn) {
                saveBtn.setAttribute('onclick', `saveAddress(true, ${addressId})`);
            }

            const modal = new bootstrap.Modal(document.getElementById('addAddressModal'));
            modal.show();
        })
        .catch(error => {
            hideLoading();
            showErrorToast(error.message || 'Không thể tải thông tin địa chỉ');
        });
}

function saveAddress(isEdit = false, addressId = null) {
    const form = document.getElementById('addAddressForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const khachHangId = document.getElementById("khachHangId")?.value;

    if (!khachHangId) {
        showErrorToast('Không tìm thấy thông tin khách hàng. Vui lòng đăng nhập lại');
        return;
    }

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
            const modal = bootstrap.Modal.getInstance(document.getElementById('addAddressModal'));
            if (modal) {
                modal.hide();
            }
            showSuccessAnimation(() => {
                location.reload();
            });
        })
        .catch(err => {
            hideLoading();
            showErrorToast(err.message || 'Có lỗi xảy ra, vui lòng thử lại');
        });
}

function deleteAddress(event, addressId) {
    event.stopPropagation();

    if (!confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) {
        return;
    }

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
            showSuccessAnimation(() => {
                location.reload();
            });
        })
        .catch(error => {
            hideLoading();
            showErrorToast(error.message || 'Có lỗi xảy ra khi xóa địa chỉ');
        });
}

// ==================== THANH TOÁN ====================

function selectPaymentMethod(element) {
    // Kiểm tra nếu phương thức thanh toán bị disabled
    if (element.classList.contains('disabled')) {
        console.warn('⚠️ Phương thức thanh toán này đã bị vô hiệu hóa');
        return;
    }

    document.querySelectorAll('.payment-method').forEach(item => {
        item.classList.remove('active');
    });
    element.classList.add('active');
    selectedPaymentMethod = element.dataset.method;

    const paymentInput = document.getElementById('selectedPayment');
    if (paymentInput) {
        paymentInput.value = selectedPaymentMethod;
    }

    showSuccessToast('Đã chọn phương thức thanh toán');
    console.log('✅ Đã chọn phương thức thanh toán:', selectedPaymentMethod);
}

function updateShippingFee() {
    const shippingMethod = document.querySelector('input[name="shippingMethod"]:checked');

    if (shippingMethod) {
        if (shippingMethod.value === 'standard') {
            shippingFee = 30000;
        } else if (shippingMethod.value === 'economy') {
            shippingFee = 20000;
        }

        const shippingFeeElement = document.getElementById('shippingFee');
        if (shippingFeeElement) {
            animateNumber(shippingFeeElement, shippingFee);
        }

        calculateTotals();
    }
}

function calculateTotals() {
    const subtotalElement = document.getElementById('subtotal');
    if (subtotalElement) {
        const subtotalText = subtotalElement.textContent.replace(/[₫,.]/g, '');
        subtotal = parseFloat(subtotalText) || 0;
    }

    const totalAmount = subtotal + shippingFee - discount;

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

// ==================== ĐẶT HÀNG - FORM SUBMISSION ====================

/**
 * ĐẶT HÀNG - GỬI FORM DATA (không phải JSON)
 * Vì backend dùng @ModelAttribute, không phải @RequestBody
 */
function placeOrder() {
    console.log('\n🚀 === BẮT ĐẦU ĐẶT HÀNG ===');

    // 1. Kiểm tra địa chỉ
    if (!selectedAddressId) {
        console.error('❌ Không có địa chỉ');
        showErrorToast('Vui lòng chọn địa chỉ giao hàng');
        return;
    }
    console.log('✅ Address ID:', selectedAddressId);

    // 2. Lấy cart item IDs
    const cartItemIdsInput = document.getElementById('cartItemIds');
    if (!cartItemIdsInput || !cartItemIdsInput.value) {
        console.error('❌ Giỏ hàng trống');
        showErrorToast('Giỏ hàng trống');
        return;
    }

    const cartItemIds = cartItemIdsInput.value.split(',')
        .map(id => parseInt(id.trim()))
        .filter(id => !isNaN(id));

    if (cartItemIds.length === 0) {
        console.error('❌ Giỏ hàng trống');
        showErrorToast('Giỏ hàng trống');
        return;
    }
    console.log('✅ Cart Items:', cartItemIds);

    // 3. Phương thức vận chuyển
    const shippingMethod = document.querySelector('input[name="shippingMethod"]:checked');
    const selectedShippingMethod = shippingMethod ? shippingMethod.value : 'standard';
    console.log('✅ Shipping Method:', selectedShippingMethod);

    // 4. Tạo FORM (không phải JSON)
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = API_ENDPOINT;

    // Thêm các field vào form
    const fields = {
        'addressId': selectedAddressId,
        'paymentMethod': selectedPaymentMethod,
        'shippingFee': shippingFee,
        'shippingMethod': selectedShippingMethod,
        'note': document.getElementById('orderNote')?.value || ''
    };

    // Thêm từng field
    Object.entries(fields).forEach(([key, value]) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        form.appendChild(input);
    });

    // Thêm cart items (multiple values)
    cartItemIds.forEach(id => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'cartItemIds';
        input.value = id;
        form.appendChild(input);
    });

    console.log('📦 Form Data:', fields);
    console.log('📦 Cart Item IDs:', cartItemIds);

    // 5. Hiển thị animation
    showOrderSuccessAnimation(() => {
        // 6. Submit form sau animation
        console.log('🔄 Submitting form...');
        document.body.appendChild(form);
        form.submit();
    });
}

// ==================== UI ANIMATIONS ====================

function showSuccessAnimation(callback) {
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

    setTimeout(() => {
        overlay.querySelector('.success-animation').classList.add('active');
    }, 10);

    setTimeout(() => {
        overlay.classList.add('fade-out');
        setTimeout(() => {
            document.body.removeChild(overlay);
            if (callback) callback();
        }, 300);
    }, 1500);
}

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
            <h3 class="success-text">Đang xử lý...</h3>
            <p class="success-subtext">Vui lòng chờ trong giây lát</p>
        </div>
    `;

    document.body.appendChild(overlay);

    setTimeout(() => {
        overlay.querySelector('.success-animation').classList.add('active');
    }, 10);

    setTimeout(() => {
        if (callback) callback();
    }, 2000);
}

function showSuccessToast(message) {
    showToast(message, 'success');
}

function showErrorToast(message) {
    showToast(message, 'error');
}

function showToast(message, type = 'success') {
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

    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 2500);
}

function animateNumber(element, targetNumber) {
    const currentText = element.textContent.replace(/[₫,]/g, '');
    const currentNumber = parseInt(currentText) || 0;
    const difference = targetNumber - currentNumber;
    const duration = 500;
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

function formatCurrency(amount) {
    return '₫' + amount.toLocaleString('vi-VN');
}

function showLoading() {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = '<div class="loading-spinner"></div>';
        overlay.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);display:flex;align-items:center;justify-content:center;z-index:99998;';
        document.body.appendChild(overlay);
    }
    overlay.style.display = 'flex';
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}