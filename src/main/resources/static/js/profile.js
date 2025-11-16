// profile.js - Xử lý trang cá nhân

document.addEventListener('DOMContentLoaded', function() {
    initTabs();
    setupForms();
});

// ==================== CHANGE EMAIL ====================

function showChangeEmailModal(event) {
    event.preventDefault();
    const modal = new bootstrap.Modal(document.getElementById('changeEmailModal'));
    document.getElementById('changeEmailForm').reset();
    modal.show();
}

async function submitChangeEmail() {
    const form = document.getElementById('changeEmailForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const newEmail = document.getElementById('newEmail').value.trim();
    const password = document.getElementById('emailPassword').value;

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
        showErrorToast('Email không hợp lệ');
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/change-email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                newEmail: newEmail,
                password: password
            })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            // Đóng modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('changeEmailModal'));
            modal.hide();

            // Hiển thị thành công
            showSuccessAnimation(() => {
                showSuccessToast('Thay đổi email thành công');
                setTimeout(() => location.reload(), 1000);
            });
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi thay đổi email');
    }
}

// ==================== CHANGE PHONE ====================

function showChangePhoneModal(event) {
    event.preventDefault();
    const modal = new bootstrap.Modal(document.getElementById('changePhoneModal'));
    document.getElementById('changePhoneForm').reset();
    modal.show();
}

async function submitChangePhone() {
    const form = document.getElementById('changePhoneForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const newPhone = document.getElementById('newPhone').value.trim();
    const password = document.getElementById('phonePassword').value;

    // Validate phone format
    const phoneRegex = /^[0-9]{10,11}$/;
    if (!phoneRegex.test(newPhone)) {
        showErrorToast('Số điện thoại phải có 10-11 chữ số');
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/change-phone', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                newPhone: newPhone,
                password: password
            })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            // Đóng modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('changePhoneModal'));
            modal.hide();

            // Hiển thị thành công
            showSuccessAnimation(() => {
                showSuccessToast('Thay đổi số điện thoại thành công');
                setTimeout(() => location.reload(), 1000);
            });
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi thay đổi số điện thoại');
    }
}

// ==================== TAB NAVIGATION ====================

function initTabs() {
    const menuItems = document.querySelectorAll('.submenu-item[data-tab]');

    menuItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            const tabName = this.getAttribute('data-tab');

            // Remove active from all
            menuItems.forEach(mi => mi.classList.remove('active'));

            // Add active to clicked
            this.classList.add('active');

            // Hide all tabs
            document.querySelectorAll('.tab-content').forEach(tc => {
                tc.classList.remove('active');
            });

            // Show selected tab
            const selectedTab = document.getElementById(tabName + 'Tab');
            if (selectedTab) {
                selectedTab.classList.add('active');
            }
        });
    });
}

// ==================== FORM SETUP ====================

function setupForms() {
    // Profile form
    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', handleProfileSubmit);
    }

    // Password form
    const passwordForm = document.getElementById('passwordForm');
    if (passwordForm) {
        passwordForm.addEventListener('submit', handlePasswordSubmit);
    }
}

// ==================== PROFILE UPDATE ====================

async function handleProfileSubmit(e) {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = {
        hoTen: formData.get('hoTen'),
        email: formData.get('email'),
        sdt: formData.get('sdt'),
        gioiTinh: formData.get('gioiTinh'),
        ngaySinh: combineDate(
            formData.get('nam'),
            formData.get('thang'),
            formData.get('ngay')
        )
    };

    try {
        showLoading();

        const response = await fetch('/profile/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            showSuccessAnimation(() => {
                showSuccessToast('Cập nhật thông tin thành công');
                setTimeout(() => location.reload(), 1000);
            });
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi cập nhật');
    }
}

// ==================== PASSWORD CHANGE ====================

async function handlePasswordSubmit(e) {
    e.preventDefault();

    const formData = new FormData(e.target);
    const currentPassword = formData.get('currentPassword');
    const newPassword = formData.get('newPassword');
    const confirmPassword = formData.get('confirmPassword');

    // Validate
    if (newPassword !== confirmPassword) {
        showErrorToast('Xác nhận mật khẩu không khớp');
        return;
    }

    if (newPassword.length < 6) {
        showErrorToast('Mật khẩu phải có ít nhất 6 ký tự');
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/change-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
                currentPassword,
                newPassword,
                confirmPassword
            })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            showSuccessAnimation(() => {
                e.target.reset();
                showSuccessToast('Đổi mật khẩu thành công');
            });
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi đổi mật khẩu');
    }
}

async function uploadAvatar(input) {
    if (!input.files || !input.files[0]) return;

    const file = input.files[0];

    // Validate
    if (!file.type.startsWith('image/')) {
        showErrorToast('Chỉ chấp nhận file ảnh (JPEG, PNG)');
        return;
    }

    if (file.size > 1 * 1024 * 1024) {
        showErrorToast('Kích thước file không được vượt quá 1MB');
        return;
    }

    // Preview ảnh ngay lập tức
    const reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('avatarPreview').src = e.target.result;
        const sidebarAvatar = document.querySelector('.sidebar-avatar');
        if (sidebarAvatar) {
            sidebarAvatar.src = e.target.result;
        }
    };
    reader.readAsDataURL(file);

    const formData = new FormData();
    formData.append('avatar', file);

    try {
        showLoading();

        const response = await fetch('/profile/upload-avatar', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            // Update với URL từ server
            document.getElementById('avatarPreview').src = result.avatarUrl + '?t=' + new Date().getTime();
            const sidebarAvatar = document.querySelector('.sidebar-avatar');
            if (sidebarAvatar) {
                sidebarAvatar.src = result.avatarUrl + '?t=' + new Date().getTime();
            }

            showSuccessToast(result.message);
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra khi upload ảnh');
    }
}

// ==================== ADDRESS MANAGEMENT ====================

function showAddAddressModal() {
    const modal = new bootstrap.Modal(document.getElementById('addAddressModal'));
    document.getElementById('addAddressForm').reset();
    modal.show();
}

async function saveAddress() {
    const form = document.getElementById('addAddressForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const khachHangId = document.getElementById('khachHangId').value;
    const formData = new FormData(form);

    const data = {
        hoTenNhan: formData.get('hoTenNhan'),
        sdtNhan: formData.get('sdtNhan'),
        diaChi: formData.get('diaChi'),
        macDinh: formData.get('macDinh') === 'on'
    };

    try {
        showLoading();

        const response = await fetch(`/api/dia-chi/add/${khachHangId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (!response.ok) throw new Error('Không thể lưu địa chỉ');

        hideLoading();

        const modal = bootstrap.Modal.getInstance(document.getElementById('addAddressModal'));
        modal.hide();

        showSuccessAnimation(() => {
            location.reload();
        });

    } catch (error) {
        hideLoading();
        showErrorToast(error.message || 'Có lỗi xảy ra');
    }
}

async function editAddress(addressId) {
    // TODO: Implement edit
    showErrorToast('Chức năng đang phát triển');
}

async function deleteAddress(addressId) {
    if (!confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) return;

    const khachHangId = document.getElementById('khachHangId').value;

    try {
        showLoading();

        const response = await fetch(`/api/dia-chi/delete/${khachHangId}/${addressId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Không thể xóa địa chỉ');

        hideLoading();

        showSuccessAnimation(() => {
            location.reload();
        });

    } catch (error) {
        hideLoading();
        showErrorToast(error.message || 'Có lỗi xảy ra');
    }
}

// ==================== HELPERS ====================

function combineDate(year, month, day) {
    if (!year || !month || !day) return null;
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
}

function showLoading() {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.className = 'loading-overlay';
        overlay.innerHTML = '<div class="loading-spinner"></div>';
        document.body.appendChild(overlay);
    }
    overlay.style.display = 'flex';
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = 'none';
}

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
            <h3 class="success-text">Thành công!</h3>
        </div>
    `;

    document.body.appendChild(overlay);

    setTimeout(() => {
        overlay.querySelector('.success-animation').classList.add('active');
    }, 10);

    setTimeout(() => {
        document.body.removeChild(overlay);
        if (callback) callback();
    }, 1500);
}

function showSuccessToast(message) {
    showToast(message, 'success');
}

function showErrorToast(message) {
    showToast(message, 'error');
}

function showToast(message, type = 'success') {
    const existingToast = document.querySelector('.custom-toast');
    if (existingToast) existingToast.remove();

    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;
    toast.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 10);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) toast.parentNode.removeChild(toast);
        }, 300);
    }, 2500);
}

// ==================== SHOW MODALS ====================

function showChangeEmailModal(event) {
    if (event) event.preventDefault();
    const modal = new bootstrap.Modal(document.getElementById('changeEmailModal'));
    document.getElementById('changeEmailForm').reset();
    modal.show();
}

function showChangePhoneModal(event) {
    if (event) event.preventDefault();
    const modal = new bootstrap.Modal(document.getElementById('changePhoneModal'));
    document.getElementById('changePhoneForm').reset();
    modal.show();
}

// ==================== EMAIL CHANGE WITH OTP ====================

let newEmailGlobal = '';
let resendCountdown = 0;

async function sendOtpForEmailChange() {
    const newEmail = document.getElementById('newEmail').value.trim();

    // Validate
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newEmail)) {
        showErrorToast('Email không hợp lệ');
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/send-otp-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newEmail: newEmail })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            // Lưu email
            newEmailGlobal = newEmail;

            // Đóng modal email, mở modal OTP
            const emailModal = bootstrap.Modal.getInstance(document.getElementById('changeEmailModal'));
            emailModal.hide();

            document.getElementById('otpEmailDisplay').textContent = newEmail;
            const otpModal = new bootstrap.Modal(document.getElementById('verifyOtpModal'));
            otpModal.show();

            // Setup OTP inputs
            setupOtpInputs();

            // Countdown
            startResendCountdown(60);

            showSuccessToast('Mã OTP đã được gửi tới email');
        } else {
            showErrorToast(result.message || 'Có lỗi xảy ra');
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Không thể gửi OTP');
    }
}

function setupOtpInputs() {
    const inputs = document.querySelectorAll('.otp-input');

    inputs.forEach((input, index) => {
        input.value = '';

        input.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');

            if (e.target.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
            }
        });

        input.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text').replace(/[^0-9]/g, '').slice(0, 6);

            pasteData.split('').forEach((char, i) => {
                if (inputs[i]) inputs[i].value = char;
            });

            if (pasteData.length === 6) inputs[5].focus();
        });
    });

    inputs[0].focus();
}

async function verifyAndChangeEmail() {
    const inputs = document.querySelectorAll('.otp-input');
    const otpCode = Array.from(inputs).map(input => input.value).join('');

    if (otpCode.length !== 6) {
        showErrorToast('Vui lòng nhập đủ 6 số');
        inputs.forEach(input => input.classList.add('error'));
        setTimeout(() => inputs.forEach(input => input.classList.remove('error')), 500);
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/verify-and-change-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                newEmail: newEmailGlobal,
                otpCode: otpCode
            })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            const otpModal = bootstrap.Modal.getInstance(document.getElementById('verifyOtpModal'));
            otpModal.hide();

            showSuccessAnimation(() => {
                showSuccessToast('Thay đổi email thành công!');
                setTimeout(() => location.reload(), 1000);
            });
        } else {
            showErrorToast(result.message || 'Mã OTP không đúng');
            inputs.forEach(input => {
                input.value = '';
                input.classList.add('error');
            });
            setTimeout(() => {
                inputs.forEach(input => input.classList.remove('error'));
                inputs[0].focus();
            }, 500);
        }

    } catch (error) {
        hideLoading();
        console.error('Error:', error);
        showErrorToast('Có lỗi xảy ra');
    }
}

async function resendOtp() {
    if (resendCountdown > 0) {
        showErrorToast(`Vui lòng đợi ${resendCountdown} giây`);
        return;
    }
    await sendOtpForEmailChange();
}

function startResendCountdown(seconds) {
    resendCountdown = seconds;
    const btn = document.getElementById('resendOtpBtn');
    const timer = document.getElementById('resendTimer');

    btn.disabled = true;

    const interval = setInterval(() => {
        resendCountdown--;
        timer.textContent = `Gửi lại sau ${resendCountdown}s`;

        if (resendCountdown <= 0) {
            clearInterval(interval);
            btn.disabled = false;
            timer.textContent = '';
        }
    }, 1000);
}

// ==================== PHONE CHANGE (Simple) ====================

async function submitChangePhone() {
    const form = document.getElementById('changePhoneForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const newPhone = document.getElementById('newPhone').value.trim();
    const password = document.getElementById('phonePassword').value;

    if (!/^[0-9]{10,11}$/.test(newPhone)) {
        showErrorToast('Số điện thoại phải có 10-11 chữ số');
        return;
    }

    try {
        showLoading();

        const response = await fetch('/profile/change-phone', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newPhone, password })
        });

        const result = await response.json();
        hideLoading();

        if (result.success) {
            const modal = bootstrap.Modal.getInstance(document.getElementById('changePhoneModal'));
            modal.hide();

            showSuccessAnimation(() => {
                showSuccessToast('Thay đổi SĐT thành công');
                setTimeout(() => location.reload(), 1000);
            });
        } else {
            showErrorToast(result.message);
        }

    } catch (error) {
        hideLoading();
        showErrorToast('Có lỗi xảy ra');
    }
}