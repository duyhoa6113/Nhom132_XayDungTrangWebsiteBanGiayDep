// ========== Authentication Pages JavaScript ==========

document.addEventListener('DOMContentLoaded', function() {

    // ========== Password Toggle ==========
    const passwordToggles = document.querySelectorAll('.btn-toggle-password');

    passwordToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const input = document.getElementById(targetId);
            const icon = this.querySelector('i');

            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });

    // ========== Form Validation ==========
    const forms = document.querySelectorAll('.auth-form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitButton = form.querySelector('.btn-submit');

            // Add loading state
            submitButton.classList.add('loading');
            submitButton.disabled = true;

            // Form will submit normally
            // Remove loading state if validation fails
            setTimeout(() => {
                if (!form.checkValidity()) {
                    submitButton.classList.remove('loading');
                    submitButton.disabled = false;
                }
            }, 100);
        });

        // Real-time validation
        const inputs = form.querySelectorAll('.form-control');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateInput(this);
            });

            input.addEventListener('input', function() {
                if (this.classList.contains('is-invalid')) {
                    validateInput(this);
                }
            });
        });
    });

    // ========== Input Validation Functions ==========
    function validateInput(input) {
        const value = input.value.trim();
        const inputId = input.id;

        // Clear previous validation
        clearValidation(input);

        // Email validation
        if (inputId === 'email') {
            if (!value) {
                showError(input, 'Email không được để trống');
                return false;
            }
            if (!isValidEmail(value)) {
                showError(input, 'Email không hợp lệ');
                return false;
            }
        }

        // Password validation
        if (inputId === 'matKhau' && value) {
            if (value.length < 8) {
                showError(input, 'Mật khẩu phải có ít nhất 8 ký tự');
                return false;
            }
            if (!/(?=.*[a-z])/.test(value)) {
                showError(input, 'Mật khẩu phải chứa chữ thường');
                return false;
            }
            if (!/(?=.*[A-Z])/.test(value)) {
                showError(input, 'Mật khẩu phải chứa chữ hoa');
                return false;
            }
            if (!/(?=.*\d)/.test(value)) {
                showError(input, 'Mật khẩu phải chứa số');
                return false;
            }
        }

        // Confirm password validation
        if (inputId === 'xacNhanMatKhau') {
            const password = document.getElementById('matKhau');
            if (value !== password.value) {
                showError(input, 'Mật khẩu xác nhận không khớp');
                return false;
            }
        }

        // Phone validation
        if (inputId === 'sdt' && value) {
            if (!/^[0-9]{10,11}$/.test(value)) {
                showError(input, 'Số điện thoại phải có 10-11 chữ số');
                return false;
            }
        }

        // Name validation
        if (inputId === 'hoTen' && value) {
            if (value.length < 2) {
                showError(input, 'Họ và tên phải có ít nhất 2 ký tự');
                return false;
            }
        }

        return true;
    }

    function isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    function showError(input, message) {
        input.classList.add('is-invalid');

        let feedback = input.parentElement.querySelector('.invalid-feedback');
        if (!feedback) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';

            // Check if input is inside password wrapper
            if (input.parentElement.classList.contains('password-input-wrapper')) {
                input.parentElement.parentElement.appendChild(feedback);
            } else {
                input.parentElement.appendChild(feedback);
            }
        }
        feedback.textContent = message;
        feedback.style.display = 'block';
    }

    function clearValidation(input) {
        input.classList.remove('is-invalid');

        let feedback;
        if (input.parentElement.classList.contains('password-input-wrapper')) {
            feedback = input.parentElement.parentElement.querySelector('.invalid-feedback');
        } else {
            feedback = input.parentElement.querySelector('.invalid-feedback');
        }

        if (feedback && !feedback.hasAttribute('th:if')) {
            feedback.style.display = 'none';
        }
    }

    // ========== AJAX Email Check (Optional) ==========
    const emailInput = document.getElementById('email');
    if (emailInput && emailInput.form.id !== 'loginForm') {
        let emailCheckTimeout;

        emailInput.addEventListener('input', function() {
            clearTimeout(emailCheckTimeout);
            const email = this.value.trim();

            if (email && isValidEmail(email)) {
                emailCheckTimeout = setTimeout(() => {
                    checkEmailExists(email, this);
                }, 500);
            }
        });
    }

    function checkEmailExists(email, input) {
        fetch(`/auth/api/check-email?email=${encodeURIComponent(email)}`)
            .then(response => response.json())
            .then(exists => {
                if (exists) {
                    showError(input, 'Email đã được sử dụng');
                } else {
                    clearValidation(input);
                }
            })
            .catch(error => {
                console.error('Error checking email:', error);
            });
    }

    // ========== AJAX Phone Check (Optional) ==========
    const phoneInput = document.getElementById('sdt');
    if (phoneInput) {
        let phoneCheckTimeout;

        phoneInput.addEventListener('input', function() {
            clearTimeout(phoneCheckTimeout);
            const phone = this.value.trim();

            if (phone && /^[0-9]{10,11}$/.test(phone)) {
                phoneCheckTimeout = setTimeout(() => {
                    checkPhoneExists(phone, this);
                }, 500);
            }
        });
    }

    function checkPhoneExists(phone, input) {
        fetch(`/auth/api/check-phone?sdt=${encodeURIComponent(phone)}`)
            .then(response => response.json())
            .then(exists => {
                if (exists) {
                    showError(input, 'Số điện thoại đã được sử dụng');
                } else {
                    clearValidation(input);
                }
            })
            .catch(error => {
                console.error('Error checking phone:', error);
            });
    }

    // ========== Password Strength Indicator ==========
    const passwordInput = document.getElementById('matKhau');
    if (passwordInput && passwordInput.form.querySelector('#xacNhanMatKhau')) {
        // Create strength indicator
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength';
        strengthIndicator.innerHTML = `
            <div class="strength-bar">
                <div class="strength-fill"></div>
            </div>
            <span class="strength-text"></span>
        `;

        const wrapper = passwordInput.closest('.password-input-wrapper');
        if (wrapper) {
            wrapper.parentElement.appendChild(strengthIndicator);
        }

        // Add strength indicator styles
        const style = document.createElement('style');
        style.textContent = `
            .password-strength {
                margin-top: 0.5rem;
            }
            .strength-bar {
                height: 4px;
                background: #e0e0e0;
                border-radius: 2px;
                overflow: hidden;
                margin-bottom: 0.25rem;
            }
            .strength-fill {
                height: 100%;
                width: 0;
                transition: all 0.3s ease;
            }
            .strength-fill.weak {
                width: 33%;
                background: #f44336;
            }
            .strength-fill.medium {
                width: 66%;
                background: #ff9800;
            }
            .strength-fill.strong {
                width: 100%;
                background: #4caf50;
            }
            .strength-text {
                font-size: 0.75rem;
                color: #666;
            }
        `;
        document.head.appendChild(style);

        passwordInput.addEventListener('input', function() {
            const password = this.value;
            const fill = strengthIndicator.querySelector('.strength-fill');
            const text = strengthIndicator.querySelector('.strength-text');

            if (!password) {
                fill.className = 'strength-fill';
                text.textContent = '';
                return;
            }

            let strength = 0;
            if (password.length >= 8) strength++;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
            if (/\d/.test(password)) strength++;
            if (/[^a-zA-Z0-9]/.test(password)) strength++;

            if (strength <= 2) {
                fill.className = 'strength-fill weak';
                text.textContent = 'Yếu';
            } else if (strength === 3) {
                fill.className = 'strength-fill medium';
                text.textContent = 'Trung bình';
            } else {
                fill.className = 'strength-fill strong';
                text.textContent = 'Mạnh';
            }
        });
    }

    // ========== Auto-dismiss Alerts ==========
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 5000);
    });

    // ========== Smooth Scroll for Links ==========
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href !== '#') {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    // ========== Form Submit Confirmation ==========
    const cancelButtons = document.querySelectorAll('.btn-cancel');
    cancelButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const form = this.closest('form');
            if (form) {
                const inputs = form.querySelectorAll('.form-control');
                let hasInput = false;

                inputs.forEach(input => {
                    if (input.value.trim()) {
                        hasInput = true;
                    }
                });

                if (hasInput) {
                    if (!confirm('Bạn có chắc muốn hủy? Dữ liệu đã nhập sẽ bị mất.')) {
                        e.preventDefault();
                    }
                }
            }
        });
    });

    // ========== Phone Number Formatting ==========
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            // Remove non-numeric characters
            let value = this.value.replace(/\D/g, '');

            // Limit to 11 digits
            if (value.length > 11) {
                value = value.slice(0, 11);
            }

            this.value = value;
        });
    }

    // ========== Prevent Multiple Form Submissions ==========
    forms.forEach(form => {
        let submitted = false;

        form.addEventListener('submit', function(e) {
            if (submitted) {
                e.preventDefault();
                return false;
            }

            if (form.checkValidity()) {
                submitted = true;
            }
        });
    });

    // ========== Console Welcome Message ==========
    console.log('%cNiceSport Authentication System', 'color: #2196f3; font-size: 20px; font-weight: bold;');
    console.log('%c🔐 Secure Login & Registration', 'color: #666; font-size: 14px;');
    console.log('%cDeveloped with ❤️ by NiceSport Team', 'color: #999; font-size: 12px;');

});

// ========== Utility Functions ==========

// Format phone number for display
function formatPhoneNumber(phone) {
    const cleaned = phone.replace(/\D/g, '');
    const match = cleaned.match(/^(\d{4})(\d{3})(\d{3})$/);
    if (match) {
        return match[1] + ' ' + match[2] + ' ' + match[3];
    }
    return phone;
}

// Copy to clipboard
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
            console.log('Copied to clipboard');
        });
    }
}

// Show toast notification (if needed)
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 3000);
}

// Add toast animations
const toastStyle = document.createElement('style');
toastStyle.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(toastStyle);