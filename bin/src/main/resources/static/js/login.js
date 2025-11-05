// Toggle password visibility
    function togglePassword() {
    const input = document.getElementById('matKhau');
    const icon = document.querySelector('.password-toggle i');

    if (input.type === 'password') {
    input.type = 'text';
    icon.classList.remove('fa-eye');
    icon.classList.add('fa-eye-slash');
} else {
    input.type = 'password';
    icon.classList.remove('fa-eye-slash');
    icon.classList.add('fa-eye');
}
}

    // Social login (placeholder)
    function socialLogin(provider) {
    alert(`Đăng nhập với ${provider} đang được phát triển`);
    // TODO: Implement OAuth flow
}

    // Form submit loading
    document.getElementById('loginForm').addEventListener('submit', function(e) {
    const submitBtn = document.getElementById('submitBtn');
    if (this.checkValidity()) {
    submitBtn.classList.add('loading');
    submitBtn.disabled = true;
}
});

    // Email validation
    document.getElementById('email').addEventListener('input', function() {
    const email = this.value.trim();
    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    if (email && !isValid) {
    this.classList.add('is-invalid');
} else {
    this.classList.remove('is-invalid');
}
});

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
    alert.style.transition = 'opacity 0.5s';
    alert.style.opacity = '0';
    setTimeout(() => alert.remove(), 500);
});
}, 5000);

    // Enter key submit
    document.querySelectorAll('.form-control').forEach(input => {
    input.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            document.getElementById('loginForm').dispatchEvent(new Event('submit'));
        }
    });
});