/**
 * Product Detail Page JavaScript - Shopee Style
 * @author Nhóm 132
 */

// ============================================
// GLOBAL VARIABLES
// ============================================

let selectedColorId = null;
let selectedSizeId = null;
let selectedVariantId = null;
let maxStock = 999;
let currentPrice = 0;
let currentLightboxImages = [];
let currentLightboxColorIds = [];
let currentLightboxIndex = 0;

// ============================================
// IMAGE GALLERY
// ============================================

/**
 * Thay đổi hình ảnh chính
 */
function changeMainImage(src) {
    const mainImage = document.getElementById('mainImage');
    if (!mainImage) return;

    mainImage.style.opacity = '0.5';
    setTimeout(() => {
        mainImage.src = src;
        mainImage.style.opacity = '1';
    }, 200);
}

/**
 * Tạo thumbnails - MỖI MÀU 1 ẢNH ĐẠI DIỆN
 */
function createThumbnails() {
    const thumbnailContainer = document.getElementById('thumbnailContainer');
    if (!thumbnailContainer) {
        console.error('Thumbnail container not found');
        return;
    }

    thumbnailContainer.style.cssText = `
        display: flex;
        gap: 8px;
        overflow-x: auto;
        flex-wrap: nowrap;
        padding: 4px 0;
    `;

    thumbnailContainer.innerHTML = '';

    const colorThumbnails = [];
    const imagesByColor = window.imagesByColor || {};

    Object.keys(imagesByColor).forEach(colorId => {
        const images = imagesByColor[colorId];
        if (images && images.length > 0) {
            colorThumbnails.push({
                colorId: parseInt(colorId),
                image: images[0]
            });
        }
    });

    if (colorThumbnails.length === 0) {
        console.warn('No thumbnails available');
        return;
    }

    colorThumbnails.forEach((thumb) => {
        const thumbWrapper = document.createElement('div');
        thumbWrapper.className = 'thumbnail-wrapper';
        thumbWrapper.style.cssText = `
            position: relative;
            cursor: pointer;
            border: 2px solid ${thumb.colorId === selectedColorId ? '#ee4d2d' : 'transparent'};
            border-radius: 4px;
            overflow: hidden;
            transition: all 0.2s ease;
            background: #f5f5f5;
            flex-shrink: 0;
            width: 80px;
            height: 80px;
        `;
        thumbWrapper.setAttribute('data-color-id', thumb.colorId);

        const img = document.createElement('img');
        img.src = thumb.image;
        img.alt = `Color ${thumb.colorId}`;
        img.style.cssText = `
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        `;

        img.onerror = function() {
            this.src = 'https://via.placeholder.com/80x80?text=No+Image';
        };

        thumbWrapper.addEventListener('click', function() {
            const colorId = parseInt(this.getAttribute('data-color-id'));
            const colorButton = document.querySelector(`.color-option[data-color-id="${colorId}"]`);
            if (colorButton) {
                colorButton.click();
            }
        });

        thumbWrapper.addEventListener('mouseenter', function() {
            const colorId = parseInt(this.getAttribute('data-color-id'));
            if (colorId !== selectedColorId) {
                this.style.border = '2px solid #ddd';
            }
        });

        thumbWrapper.addEventListener('mouseleave', function() {
            const colorId = parseInt(this.getAttribute('data-color-id'));
            if (colorId !== selectedColorId) {
                this.style.border = '2px solid transparent';
            }
        });

        thumbWrapper.appendChild(img);
        thumbnailContainer.appendChild(thumbWrapper);
    });

    console.log(`Created ${colorThumbnails.length} thumbnails`);
}

/**
 * Cập nhật thumbnail active theo màu đã chọn
 */
function updateThumbnailActive(colorId) {
    document.querySelectorAll('.thumbnail-wrapper').forEach(wrapper => {
        const wrapperColorId = parseInt(wrapper.getAttribute('data-color-id'));
        wrapper.style.border = wrapperColorId === colorId
            ? '2px solid #ee4d2d'
            : '2px solid transparent';
    });
}

// ============================================
// COLOR SELECTION
// ============================================

/**
 * Chọn màu sắc
 */
function selectColor(element) {
    document.querySelectorAll('.color-option').forEach(btn => {
        btn.classList.remove('active');
        btn.style.borderColor = 'rgba(0,0,0,.09)';
    });

    element.classList.add('active');
    element.style.borderColor = '#ee4d2d';

    const colorId = parseInt(element.getAttribute('data-color-id'));
    selectedColorId = colorId;

    if (colorId && window.imagesByColor[colorId]) {
        const images = window.imagesByColor[colorId];
        if (images && images.length > 0) {
            changeMainImage(images[0]);
        }
    }

    updateThumbnailActive(colorId);
    updateAvailableSizes();

    // Reset size selection
    document.querySelectorAll('.size-option').forEach(btn => {
        btn.classList.remove('active');
        btn.style.borderColor = 'rgba(0,0,0,.09)';
    });
    selectedSizeId = null;
    selectedVariantId = null;
}

/**
 * Cập nhật sizes có sẵn dựa trên màu đã chọn
 */
function updateAvailableSizes() {
    if (!selectedColorId) return;

    const variants = window.variantsData || [];
    const sizeButtons = document.querySelectorAll('.size-option');

    const availableSizeIds = variants
        .filter(v => v.colorId === selectedColorId && v.stock > 0)
        .map(v => v.sizeId);

    sizeButtons.forEach(btn => {
        const sizeId = parseInt(btn.getAttribute('data-size-id'));

        if (availableSizeIds.includes(sizeId)) {
            // Size có sẵn
            btn.disabled = false;
            btn.style.opacity = '1';
            btn.style.cursor = 'pointer';
            const existingStrike = btn.querySelector('.strikethrough');
            if (existingStrike) existingStrike.remove();
        } else {
            // Size hết hàng
            btn.disabled = true;
            btn.style.opacity = '0.4';
            btn.style.cursor = 'not-allowed';

            if (!btn.querySelector('.strikethrough')) {
                const strike = document.createElement('div');
                strike.className = 'strikethrough';
                strike.style.cssText = `
                    position: absolute;
                    top: 50%;
                    left: 10%;
                    right: 10%;
                    height: 1px;
                    background: #999;
                    transform: translateY(-50%) rotate(-15deg);
                `;
                btn.style.position = 'relative';
                btn.appendChild(strike);
            }
        }
    });
}

// ============================================
// SIZE SELECTION
// ============================================

/**
 * Chọn kích thước
 */
function selectSize(element) {
    if (element.disabled) {
        showNotification('error', 'Size này hiện không có sẵn cho màu đã chọn!');
        return;
    }

    if (!selectedColorId) {
        showNotification('error', 'Vui lòng chọn màu sắc trước!');
        scrollToElement('.color-options');
        return;
    }

    document.querySelectorAll('.size-option').forEach(btn => {
        btn.classList.remove('active');
        btn.style.borderColor = 'rgba(0,0,0,.09)';
    });

    element.classList.add('active');
    element.style.borderColor = '#ee4d2d';

    selectedSizeId = parseInt(element.getAttribute('data-size-id'));
    updateSelectedVariant();
}

// ============================================
// VARIANT MANAGEMENT
// ============================================

/**
 * Cập nhật variant đã chọn
 */
function updateSelectedVariant() {
    if (!selectedColorId || !selectedSizeId) return;

    const variants = window.variantsData || [];
    const variant = variants.find(v =>
        v.colorId === selectedColorId && v.sizeId === selectedSizeId
    );

    if (variant) {
        selectedVariantId = variant.variantId;
        maxStock = variant.stock;
        currentPrice = variant.price;

        const quantityInput = document.getElementById('quantity');
        if (quantityInput) {
            quantityInput.max = Math.min(variant.stock, 999);
            if (parseInt(quantityInput.value) > variant.stock) {
                quantityInput.value = variant.stock > 0 ? 1 : 0;
            }
        }
    } else {
        maxStock = 0;
        selectedVariantId = null;
    }
}

// ============================================
// QUANTITY CONTROLS
// ============================================

/**
 * Giảm số lượng
 */
function decreaseQuantity() {
    const input = document.getElementById('quantity');
    if (!input) return;

    const currentValue = parseInt(input.value) || 1;
    if (currentValue > 1) {
        input.value = currentValue - 1;
    }
}

/**
 * Tăng số lượng
 */
function increaseQuantity() {
    const input = document.getElementById('quantity');
    if (!input) return;

    const currentValue = parseInt(input.value) || 1;
    const max = Math.min(parseInt(input.max) || maxStock, maxStock);

    if (max === 0) {
        showNotification('error', 'Vui lòng chọn màu sắc và kích thước!');
        return;
    }

    if (currentValue < max) {
        input.value = currentValue + 1;
    } else {
        showNotification('error', `Chỉ còn ${max} sản phẩm!`);
    }
}

// ============================================
// ADD TO CART
// ============================================

/**
 * Thêm vào giỏ hàng
 */
function addToCart() {
    if (!selectedColorId) {
        showNotification('error', 'Vui lòng chọn màu sắc!');
        scrollToElement('.color-options');
        return;
    }

    if (!selectedSizeId) {
        showNotification('error', 'Vui lòng chọn kích thước!');
        scrollToElement('.size-options');
        return;
    }

    const quantity = parseInt(document.getElementById('quantity')?.value) || 1;

    if (maxStock <= 0) {
        showNotification('error', 'Sản phẩm đã hết hàng!');
        return;
    }

    if (quantity > maxStock) {
        showNotification('error', `Chỉ còn ${maxStock} sản phẩm!`);
        return;
    }

    if (!selectedVariantId) {
        showNotification('error', 'Vui lòng chọn màu sắc và kích thước!');
        return;
    }

    showLoadingOverlay();

    fetch('/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            variantId: selectedVariantId,
            soLuong: quantity
        })
    })
        .then(response => response.json())
        .then(data => {
            hideLoadingOverlay();

            if (data.success) {
                showNotification('success', data.message || `Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
                updateCartBadge(data.cartCount);

                const quantityInput = document.getElementById('quantity');
                if (quantityInput) {
                    quantityInput.value = 1;
                }
            } else {
                if (data.message === 'Vui lòng đăng nhập') {
                    showNotification('error', 'Vui lòng đăng nhập để thêm vào giỏ hàng!');
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 1500);
                } else {
                    showNotification('error', data.message || 'Có lỗi xảy ra!');
                }
            }
        })
        .catch(error => {
            hideLoadingOverlay();
            console.error('Error adding to cart:', error);
            showNotification('error', 'Có lỗi xảy ra khi thêm vào giỏ hàng!');
        });
}

/**
 * Mua ngay
 */
function buyNow() {
    if (!selectedColorId) {
        showNotification('error', 'Vui lòng chọn màu sắc!');
        scrollToElement('.color-options');
        return;
    }

    if (!selectedSizeId) {
        showNotification('error', 'Vui lòng chọn kích thước!');
        scrollToElement('.size-options');
        return;
    }

    const quantity = parseInt(document.getElementById('quantity')?.value) || 1;

    if (maxStock <= 0 || quantity > maxStock) {
        showNotification('error', 'Sản phẩm không đủ số lượng!');
        return;
    }

    showLoadingOverlay();

    fetch('/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            variantId: selectedVariantId,
            soLuong: quantity
        })
    })
        .then(response => response.json())
        .then(data => {
            hideLoadingOverlay();

            if (data.success) {
                window.location.href = `/checkout?items=${data.cartItem.id}`;
            } else {
                if (data.message === 'Vui lòng đăng nhập') {
                    showNotification('error', 'Vui lòng đăng nhập để mua hàng!');
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 1500);
                } else {
                    showNotification('error', data.message || 'Có lỗi xảy ra!');
                }
            }
        })
        .catch(error => {
            hideLoadingOverlay();
            console.error('Error:', error);
            showNotification('error', 'Có lỗi xảy ra!');
        });
}

// ============================================
// LIGHTBOX - MỖI MÀU 1 ẢNH
// ============================================

/**
 * Mở lightbox
 */
function openLightbox(imageUrl, colorId = null) {
    const lightbox = document.getElementById('imageLightbox');
    const mainImage = document.getElementById('lightboxMainImage');
    const thumbnailsContainer = document.getElementById('lightboxThumbnails');

    if (!lightbox || !mainImage || !thumbnailsContainer) return;

    currentLightboxImages = [];
    currentLightboxColorIds = [];

    const imagesByColor = window.imagesByColor || {};

    Object.keys(imagesByColor).forEach(cId => {
        const images = imagesByColor[cId];
        if (images && images.length > 0) {
            currentLightboxImages.push(images[0]);
            currentLightboxColorIds.push(parseInt(cId));
        }
    });

    if (currentLightboxImages.length === 0) return;

    currentLightboxIndex = 0;
    if (colorId !== null) {
        const foundIndex = currentLightboxColorIds.indexOf(parseInt(colorId));
        if (foundIndex !== -1) {
            currentLightboxIndex = foundIndex;
        }
    }

    mainImage.src = currentLightboxImages[currentLightboxIndex];
    updateLightboxCounter();

    thumbnailsContainer.innerHTML = '';
    currentLightboxImages.forEach((imgSrc, index) => {
        const thumbDiv = document.createElement('div');
        thumbDiv.className = `lightbox-thumbnail ${index === currentLightboxIndex ? 'active' : ''}`;
        thumbDiv.onclick = () => selectLightboxImage(index);

        const img = document.createElement('img');
        img.src = imgSrc;
        img.alt = `Color ${currentLightboxColorIds[index]}`;
        img.onerror = function() {
            this.src = 'https://via.placeholder.com/100x100?text=No+Image';
        };

        thumbDiv.appendChild(img);
        thumbnailsContainer.appendChild(thumbDiv);
    });

    setTimeout(() => scrollThumbnailIntoView(currentLightboxIndex), 100);

    lightbox.style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

/**
 * Đóng lightbox
 */
function closeLightbox() {
    const lightbox = document.getElementById('imageLightbox');
    if (lightbox) {
        lightbox.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

/**
 * Chọn ảnh trong lightbox
 */
function selectLightboxImage(index) {
    if (index < 0 || index >= currentLightboxImages.length) return;

    currentLightboxIndex = index;

    const mainImage = document.getElementById('lightboxMainImage');
    if (mainImage) {
        mainImage.style.opacity = '0.5';
        setTimeout(() => {
            mainImage.src = currentLightboxImages[index];
            mainImage.style.opacity = '1';
        }, 150);
    }

    updateLightboxCounter();

    document.querySelectorAll('.lightbox-thumbnail').forEach((thumb, i) => {
        thumb.classList.toggle('active', i === index);
    });

    scrollThumbnailIntoView(index);
}

/**
 * Điều hướng lightbox
 */
function navigateLightbox(direction) {
    let newIndex = currentLightboxIndex + direction;

    if (newIndex < 0) {
        newIndex = currentLightboxImages.length - 1;
    } else if (newIndex >= currentLightboxImages.length) {
        newIndex = 0;
    }

    selectLightboxImage(newIndex);
}

/**
 * Cập nhật counter
 */
function updateLightboxCounter() {
    const counter = document.getElementById('lightboxCounter');
    if (counter) {
        counter.textContent = `${currentLightboxIndex + 1}/${currentLightboxImages.length}`;
    }
}

/**
 * Scroll thumbnail vào view
 */
function scrollThumbnailIntoView(index) {
    const thumbnailsContainer = document.getElementById('lightboxThumbnails');
    const thumbnails = thumbnailsContainer?.querySelectorAll('.lightbox-thumbnail');

    if (thumbnails && thumbnails[index]) {
        thumbnails[index].scrollIntoView({
            behavior: 'smooth',
            block: 'nearest',
            inline: 'center'
        });
    }
}

// ============================================
// KEYBOARD EVENTS
// ============================================

document.addEventListener('keydown', function(e) {
    const lightbox = document.getElementById('imageLightbox');
    if (!lightbox || lightbox.style.display === 'none') return;

    if (e.key === 'Escape') closeLightbox();
    else if (e.key === 'ArrowLeft') navigateLightbox(-1);
    else if (e.key === 'ArrowRight') navigateLightbox(1);
});

document.addEventListener('wheel', function(e) {
    const lightbox = document.getElementById('imageLightbox');
    if (lightbox && lightbox.style.display === 'flex') {
        const thumbnails = document.getElementById('lightboxThumbnails');
        if (!thumbnails.contains(e.target)) {
            e.preventDefault();
        }
    }
}, { passive: false });

// ============================================
// UTILITIES
// ============================================

/**
 * Hiển thị thông báo
 */
function showNotification(type, message) {
    const existing = document.querySelector('.custom-notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = 'custom-notification';
    notification.style.cssText = `
        position: fixed;
        top: 80px;
        right: 20px;
        background: ${type === 'success' ? '#26aa99' : '#ee4d2d'};
        color: white;
        padding: 16px 24px;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        z-index: 10000;
        min-width: 300px;
        display: flex;
        align-items: center;
        gap: 12px;
        animation: slideInRight 0.3s ease-out;
        font-size: 14px;
    `;

    const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
    notification.innerHTML = `
        <i class="fas ${icon}" style="font-size: 20px;"></i>
        <span>${message}</span>
    `;

    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
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
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

/**
 * Cập nhật cart badge
 */
function updateCartBadge(count) {
    const badge = document.querySelector('.cart-badge-main');
    if (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'inline-flex' : 'none';

        if (!document.getElementById('badge-animation')) {
            const style = document.createElement('style');
            style.id = 'badge-animation';
            style.textContent = `
                @keyframes bounce {
                    0%, 100% { transform: scale(1); }
                    50% { transform: scale(1.3); }
                }
            `;
            document.head.appendChild(style);
        }

        if (count > 0) {
            badge.style.animation = 'bounce 0.5s ease';
        }
    }
}

/**
 * Scroll đến element
 */
function scrollToElement(selector) {
    const element = document.querySelector(selector);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

/**
 * Hiển thị loading overlay
 */
function showLoadingOverlay() {
    const existing = document.querySelector('.loading-overlay-product');
    if (existing) return;

    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay-product';
    overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
    `;

    overlay.innerHTML = `
        <div style="
            width: 50px;
            height: 50px;
            border: 4px solid #f3f3f3;
            border-top: 4px solid #ee4d2d;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        "></div>
    `;

    if (!document.getElementById('loading-animation')) {
        const style = document.createElement('style');
        style.id = 'loading-animation';
        style.textContent = `
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(overlay);
}

/**
 * Ẩn loading overlay
 */
function hideLoadingOverlay() {
    const overlay = document.querySelector('.loading-overlay-product');
    if (overlay) overlay.remove();
}

// ============================================
// PAGE INITIALIZATION
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('=== Product Detail Page Loaded ===');

    // Click vào ảnh chính mở lightbox
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
        mainImage.style.cursor = 'pointer';
        mainImage.addEventListener('click', function() {
            openLightbox(this.src, selectedColorId);
        });

        // Zoom hint icon
        const mainImageWrapper = mainImage.closest('.main-image-wrapper');
        if (mainImageWrapper) {
            const zoomHint = document.createElement('div');
            zoomHint.innerHTML = '<i class="fas fa-search-plus"></i>';
            zoomHint.style.cssText = `
                position: absolute;
                bottom: 15px;
                right: 15px;
                background: rgba(0, 0, 0, 0.6);
                color: white;
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 18px;
                opacity: 0;
                transition: opacity 0.3s;
                pointer-events: none;
                z-index: 10;
            `;

            mainImageWrapper.appendChild(zoomHint);

            mainImageWrapper.addEventListener('mouseenter', () => {
                zoomHint.style.opacity = '1';
            });

            mainImageWrapper.addEventListener('mouseleave', () => {
                zoomHint.style.opacity = '0';
            });
        }
    }

    // Tạo thumbnails
    createThumbnails();

    // Auto-select màu đầu tiên
    const firstColor = document.querySelector('.color-option');
    if (firstColor) {
        setTimeout(() => firstColor.click(), 200);
    }

    // Chặn nhập ký tự không phải số
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        quantityInput.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }

    console.log('=== Initialization Complete ===');
});

// ============================================
// NAVBAR SCROLL EFFECT
// ============================================

let lastScrollTop = 0;

window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

    if (scrollTop > lastScrollTop && scrollTop > 80) {
        navbar.classList.add('hide');
    } else if (scrollTop < lastScrollTop - 5) {
        navbar.classList.remove('hide');
    }

    lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
});

// ============================================
// STOCK DISPLAY
// ============================================

/**
 * Cập nhật hiển thị tồn kho
 */
function updateStockDisplay() {
    const currentStockElement = document.getElementById('currentStock');
    const stockInfo = document.getElementById('stockInfo');
    const outOfStockWarning = document.getElementById('outOfStockWarning');
    const lowStockWarning = document.getElementById('lowStockWarning');
    const lowStockNumber = document.getElementById('lowStockNumber');
    const quantityInput = document.getElementById('quantity');

    if (!currentStockElement) return;

    // Hiển thị số lượng tồn kho
    currentStockElement.textContent = maxStock;

    // Xử lý trạng thái tồn kho
    if (maxStock === 0) {
        // Hết hàng
        if (stockInfo) stockInfo.style.display = 'none';
        if (outOfStockWarning) outOfStockWarning.style.display = 'block';
        if (lowStockWarning) lowStockWarning.style.display = 'none';

        // Disable quantity input và buttons
        if (quantityInput) {
            quantityInput.disabled = true;
            quantityInput.value = 0;
        }
        document.querySelectorAll('.btn-quantity').forEach(btn => {
            btn.disabled = true;
            btn.style.opacity = '0.5';
            btn.style.cursor = 'not-allowed';
        });
    } else if (maxStock > 0 && maxStock <= 5) {
        // Tồn kho thấp
        if (stockInfo) stockInfo.style.display = 'inline';
        if (outOfStockWarning) outOfStockWarning.style.display = 'none';
        if (lowStockWarning) {
            lowStockWarning.style.display = 'block';
            if (lowStockNumber) lowStockNumber.textContent = maxStock;
        }

        // Set màu đỏ cho số lượng tồn
        if (currentStockElement) currentStockElement.style.color = '#ee4d2d';

        // Enable input nhưng giới hạn max
        if (quantityInput) {
            quantityInput.disabled = false;
            quantityInput.max = maxStock;
            if (parseInt(quantityInput.value) > maxStock) {
                quantityInput.value = maxStock;
            }
        }
        document.querySelectorAll('.btn-quantity').forEach(btn => {
            btn.disabled = false;
            btn.style.opacity = '1';
            btn.style.cursor = 'pointer';
        });
    } else {
        // Còn hàng bình thường
        if (stockInfo) {
            stockInfo.style.display = 'inline';
            currentStockElement.style.color = '#26aa99'; // Màu xanh lá
        }
        if (outOfStockWarning) outOfStockWarning.style.display = 'none';
        if (lowStockWarning) lowStockWarning.style.display = 'none';

        // Enable input
        if (quantityInput) {
            quantityInput.disabled = false;
            quantityInput.max = Math.min(maxStock, 999);
        }
        document.querySelectorAll('.btn-quantity').forEach(btn => {
            btn.disabled = false;
            btn.style.opacity = '1';
            btn.style.cursor = 'pointer';
        });
    }
}

/**
 * Cập nhật variant đã chọn - VERSION CẢI TIẾN
 */
function updateSelectedVariant() {
    if (!selectedColorId || !selectedSizeId) return;

    const variants = window.variantsData || [];
    const variant = variants.find(v =>
        v.colorId === selectedColorId && v.sizeId === selectedSizeId
    );

    if (variant) {
        selectedVariantId = variant.variantId;
        maxStock = variant.stock;
        currentPrice = variant.price;

        console.log('Selected variant:', {
            variantId: variant.variantId,
            stock: variant.stock,
            price: variant.price
        });

        const quantityInput = document.getElementById('quantity');
        if (quantityInput) {
            quantityInput.max = Math.min(variant.stock, 999);
            if (parseInt(quantityInput.value) > variant.stock) {
                quantityInput.value = variant.stock > 0 ? 1 : 0;
            }
        }

        // ✅ CẬP NHẬT HIỂN THỊ TỒN KHO
        updateStockDisplay();
    } else {
        maxStock = 0;
        selectedVariantId = null;
        updateStockDisplay();
    }
}

/**
 * Tăng số lượng - VERSION CẢI TIẾN
 */
function increaseQuantity() {
    const input = document.getElementById('quantity');
    if (!input || input.disabled) return;

    const currentValue = parseInt(input.value) || 1;
    const max = Math.min(parseInt(input.max) || maxStock, maxStock);

    if (max === 0) {
        showNotification('error', 'Vui lòng chọn màu sắc và kích thước!');
        return;
    }

    if (currentValue < max) {
        input.value = currentValue + 1;
    } else {
        showNotification('error', `Chỉ còn ${max} sản phẩm trong kho!`);
    }
}

/**
 * Giảm số lượng - VERSION CẢI TIẾN
 */
function decreaseQuantity() {
    const input = document.getElementById('quantity');
    if (!input || input.disabled) return;

    const currentValue = parseInt(input.value) || 1;
    if (currentValue > 1) {
        input.value = currentValue - 1;
    }
}