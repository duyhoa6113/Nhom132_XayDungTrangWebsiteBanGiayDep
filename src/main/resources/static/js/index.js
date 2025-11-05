// ========== DOM Elements ==========
const addToCartButtons = document.querySelectorAll('.add-to-cart-btn');
const cartBadge = document.querySelector('.cart-badge');
const searchForm = document.querySelector('.search-form');

// ========== Cart Management ==========
let cartCount = 0;

// Initialize cart from localStorage
function initCart() {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
        const cart = JSON.parse(savedCart);
        cartCount = cart.length;
        updateCartBadge();
    }
}

// Update cart badge
function updateCartBadge() {
    if (cartBadge) {
        cartBadge.textContent = cartCount;
        if (cartCount > 0) {
            cartBadge.style.display = 'inline-block';
        } else {
            cartBadge.style.display = 'none';
        }
    }
}

// Add to cart functionality
function addToCart(productId, productName, price, image) {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];

    // Check if product already in cart
    const existingItem = cart.find(item => item.productId === productId);

    if (existingItem) {
        existingItem.quantity += 1;
        showNotification('Đã cập nhật số lượng trong giỏ hàng!', 'success');
    } else {
        cart.push({
            productId: productId,
            productName: productName,
            price: price,
            image: image,
            quantity: 1
        });
        cartCount++;
        showNotification('Đã thêm vào giỏ hàng!', 'success');
    }

    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartBadge();
}

// ========== Event Listeners ==========
addToCartButtons.forEach(button => {
    button.addEventListener('click', function(e) {
        e.preventDefault();

        const productCard = this.closest('.product-card');
        const productName = productCard.querySelector('.product-name')?.textContent || 'Sản phẩm';
        const priceText = productCard.querySelector('.price-current')?.textContent || '0';
        const price = parseFloat(priceText.replace(/[^\d]/g, ''));
        const image = productCard.querySelector('.product-image')?.src || '';

        // Get product ID from data attribute or generate one
        const productId = Math.random().toString(36).substr(2, 9);

        // Add loading state
        const originalText = this.innerHTML;
        this.innerHTML = '<span class="loading"></span> Đang thêm...';
        this.disabled = true;

        // Simulate API call
        setTimeout(() => {
            addToCart(productId, productName, price, image);
            this.innerHTML = originalText;
            this.disabled = false;
        }, 500);
    });
});

// ========== Search Functionality ==========
if (searchForm) {
    const searchInput = searchForm.querySelector('input[name="search"]');

    // Add search suggestions (optional)
    searchInput?.addEventListener('input', function(e) {
        const query = e.target.value.trim();
        if (query.length >= 3) {
            // You can add AJAX search suggestions here
            console.log('Searching for:', query);
        }
    });
}

// ========== Notification System ==========
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} notification`;
    notification.style.cssText = `
        position: fixed;
        top: 80px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease;
    `;
    notification.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="fas fa-check-circle me-2"></i>
            <span>${message}</span>
            <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
        </div>
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// ========== Smooth Scroll for Navigation ==========
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

// ========== Lazy Loading Images ==========
if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                if (img.dataset.src) {
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                }
                observer.unobserve(img);
            }
        });
    });

    document.querySelectorAll('img[data-src]').forEach(img => {
        imageObserver.observe(img);
    });
}

// ========== Product Card Hover Effect ==========
document.querySelectorAll('.product-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.transition = 'all 0.3s ease';
    });
});

// ========== Category Filter ==========
function filterByCategory(categoryId) {
    const url = new URL(window.location.href);
    url.searchParams.set('category', categoryId);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
}

// ========== Sort Products ==========
function sortProducts(sortBy) {
    const url = new URL(window.location.href);
    url.searchParams.set('sort', sortBy);
    url.searchParams.set('page', '0');
    window.location.href = url.toString();
}

// ========== Wishlist Functionality ==========
function toggleWishlist(productId) {
    const wishlist = JSON.parse(localStorage.getItem('wishlist')) || [];
    const index = wishlist.indexOf(productId);

    if (index > -1) {
        wishlist.splice(index, 1);
        showNotification('Đã xóa khỏi danh sách yêu thích', 'info');
    } else {
        wishlist.push(productId);
        showNotification('Đã thêm vào danh sách yêu thích', 'success');
    }

    localStorage.setItem('wishlist', JSON.stringify(wishlist));
}

// Add event listeners for wishlist buttons
document.querySelectorAll('.product-actions .fa-heart').forEach(icon => {
    icon.closest('button').addEventListener('click', function(e) {
        e.preventDefault();
        const productCard = this.closest('.product-card');
        const productId = Math.random().toString(36).substr(2, 9);

        toggleWishlist(productId);

        // Toggle icon
        const heartIcon = this.querySelector('.fa-heart');
        heartIcon.classList.toggle('far');
        heartIcon.classList.toggle('fas');
    });
});

// ========== Quick View Modal ==========
function showQuickView(productId) {
    // This would typically open a modal with product details
    console.log('Quick view for product:', productId);
    showNotification('Tính năng xem nhanh đang được phát triển', 'info');
}

// Add event listeners for quick view buttons
document.querySelectorAll('.product-actions .fa-eye').forEach(icon => {
    icon.closest('button').addEventListener('click', function(e) {
        e.preventDefault();
        const productId = Math.random().toString(36).substr(2, 9);
        showQuickView(productId);
    });
});

// ========== Newsletter Form ==========
const newsletterForm = document.querySelector('.newsletter-section form');
if (newsletterForm) {
    newsletterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const email = this.querySelector('input[type="email"]').value;

        if (email) {
            showNotification('Đăng ký nhận tin thành công! Cảm ơn bạn.', 'success');
            this.reset();
        }
    });
}

// ========== Animations ==========
const style = document.createElement('style');
style.textContent = `
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
document.head.appendChild(style);

// ========== Initialize ==========
document.addEventListener('DOMContentLoaded', function() {
    initCart();

    // Add fade-in animation to products
    const products = document.querySelectorAll('.product-card');
    products.forEach((product, index) => {
        product.style.opacity = '0';
        product.style.animation = `fadeIn 0.5s ease ${index * 0.1}s forwards`;
    });
});

// Fade in animation
const fadeInStyle = document.createElement('style');
fadeInStyle.textContent = `
    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
`;
document.head.appendChild(fadeInStyle);

// ========== Console Art ==========
console.log('%c NiceSport ', 'background: #667eea; color: white; font-size: 20px; padding: 10px;');
console.log('%c Welcome to NiceSport! ', 'color: #667eea; font-size: 14px;');

function addToWishlist(button) {
    const productCard = button.closest('.product-card-modern');
    const productName = productCard.querySelector('.product-title').textContent;

    // Toggle icon
    const icon = button.querySelector('i');
    if (icon.classList.contains('far')) {
        icon.classList.remove('far');
        icon.classList.add('fas');
        showToast('success', `Đã thêm "${productName}" vào danh sách yêu thích`);
    } else {
        icon.classList.remove('fas');
        icon.classList.add('far');
        showToast('info', `Đã xóa "${productName}" khỏi danh sách yêu thích`);
    }
}

// Hàm xem nhanh sản phẩm
function quickView(button) {
    const productLink = button.closest('.product-link');
    const productUrl = productLink.href;

    // Có thể mở modal hoặc chuyển hướng
    // Ở đây tôi sẽ chuyển hướng đến trang chi tiết
    window.location.href = productUrl;
}

// Hàm thêm nhanh vào giỏ hàng (không cần chọn màu/size)
function addToCartQuick(button) {
    const productId = button.dataset.productId;
    const productCard = button.closest('.product-card-modern');
    const productName = productCard.querySelector('.product-title').textContent;

    // Hiển thị thông báo
    showToast('info', 'Đang thêm vào giỏ hàng...');

    // Gọi API thêm vào giỏ hàng
    // Nếu sản phẩm có nhiều variant, nên chuyển đến trang chi tiết
    const productLink = button.closest('.product-link');
    window.location.href = productLink.href;

    // HOẶC có thể gọi API nếu đã có sẵn
    /*
    fetch(`/api/cart/add-quick/${productId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('success', `Đã thêm "${productName}" vào giỏ hàng`);
            updateCartCount(data.cartCount);
        } else {
            // Nếu cần chọn variant, chuyển đến trang chi tiết
            window.location.href = productLink.href;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'Có lỗi xảy ra!');
    });
    */
}

// Hàm hiển thị thông báo toast
function showToast(type, message) {
    // Tạo element thông báo
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} toast-notification`;
    toast.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
            ${message}
        `;

    // Thêm CSS nếu chưa có
    if (!document.getElementById('toast-style')) {
        const style = document.createElement('style');
        style.id = 'toast-style';
        style.textContent = `
                .toast-notification {
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 9999;
                    min-width: 300px;
                    animation: slideInRight 0.3s ease-out;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                }
                @keyframes slideInRight {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Cập nhật số lượng giỏ hàng
function updateCartCount(count) {
    const cartBadge = document.querySelector('.cart-badge');
    if (cartBadge) {
        cartBadge.textContent = count;
    }
}