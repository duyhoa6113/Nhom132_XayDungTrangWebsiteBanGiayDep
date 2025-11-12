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

    console.log('Color thumbnails:', colorThumbnails);

    if (colorThumbnails.length === 0) {
        console.warn('No thumbnails available');
        return;
    }

    colorThumbnails.forEach((thumb, index) => {
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
            console.error('Failed to load image:', thumb.image);
            this.src = 'https://via.placeholder.com/80x80?text=No+Image';
        };

        thumbWrapper.addEventListener('click', function() {
            const colorId = parseInt(this.getAttribute('data-color-id'));
            const colorButton = document.querySelector(`.color-option[data-color-id="${colorId}"]`);
            if (colorButton) {
                colorButton.click();
            }

            document.querySelectorAll('.thumbnail-wrapper').forEach(wrapper => {
                const wrapperColorId = parseInt(wrapper.getAttribute('data-color-id'));
                wrapper.style.border = wrapperColorId === colorId
                    ? '2px solid #ee4d2d'
                    : '2px solid transparent';
            });
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
        if (wrapperColorId === colorId) {
            wrapper.style.border = '2px solid #ee4d2d';
        } else {
            wrapper.style.border = '2px solid transparent';
        }
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

    console.log('Selected color ID:', colorId);

    if (colorId && window.imagesByColor[colorId]) {
        const images = window.imagesByColor[colorId];
        if (images && images.length > 0) {
            changeMainImage(images[0]);
        } else {
            console.warn('No images found for color:', colorId);
        }
    } else {
        console.warn('Color not found in imagesByColor:', colorId);
    }

    updateThumbnailActive(colorId);
    updateAvailableSizes();

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

    console.log('Available sizes for color', selectedColorId, ':', availableSizeIds);

    sizeButtons.forEach(btn => {
        const sizeId = parseInt(btn.getAttribute('data-size-id'));

        if (availableSizeIds.includes(sizeId)) {
            btn.disabled = false;
            btn.style.opacity = '1';
            btn.style.cursor = 'pointer';
            btn.style.position = 'relative';
            const existingStrike = btn.querySelector('.strikethrough');
            if (existingStrike) existingStrike.remove();
        } else {
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

    const sizeId = parseInt(element.getAttribute('data-size-id'));
    selectedSizeId = sizeId;

    console.log('Selected size ID:', sizeId);

    updateSelectedVariant();
}

// ============================================
// VARIANT MANAGEMENT
// ============================================

/**
 * Cập nhật variant đã chọn
 */
function updateSelectedVariant() {
    if (!selectedColorId || !selectedSizeId) {
        console.log('Waiting for both color and size selection');
        return;
    }

    const variants = window.variantsData || [];

    const variant = variants.find(v =>
        v.colorId === selectedColorId && v.sizeId === selectedSizeId
    );

    if (variant) {
        selectedVariantId = variant.variantId;
        maxStock = variant.stock;
        currentPrice = variant.price;

        console.log('Selected variant:', variant);
        console.log('Stock available:', maxStock);

        const quantityInput = document.getElementById('quantity');
        if (quantityInput) {
            quantityInput.max = Math.min(variant.stock, 999);

            if (parseInt(quantityInput.value) > variant.stock) {
                quantityInput.value = variant.stock > 0 ? 1 : 0;
            }
        }

        updateQuantityButtons();
    } else {
        console.warn('No variant found for color:', selectedColorId, 'size:', selectedSizeId);
        maxStock = 0;
        selectedVariantId = null;
    }
}

// ============================================
// QUANTITY CONTROLS
// ============================================

function decreaseQuantity() {
    const input = document.getElementById('quantity');
    if (!input) return;

    const currentValue = parseInt(input.value) || 1;
    if (currentValue > 1) {
        input.value = currentValue - 1;
        updateQuantityButtons();
    }
}

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
        updateQuantityButtons();
    } else {
        showNotification('error', `Chỉ còn ${max} sản phẩm!`);
    }
}

function updateQuantityButtons() {
    const input = document.getElementById('quantity');
    if (!input) return;

    const currentValue = parseInt(input.value) || 1;
    const max = Math.min(parseInt(input.max) || maxStock, maxStock);

    console.log('Quantity:', currentValue, '/', max);
}

// ============================================
// ADD TO CART
// ============================================

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

    console.log('Adding to cart:', {
        variantId: selectedVariantId,
        colorId: selectedColorId,
        sizeId: selectedSizeId,
        quantity: quantity,
        price: currentPrice
    });

    showNotification('success', `Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
    updateCartBadge(quantity);
}

// ============================================
// LIGHTBOX - MỖI MÀU 1 ẢNH
// ============================================

/**
 * Mở lightbox - Hiển thị mỗi màu 1 ảnh đại diện
 */
function openLightbox(imageUrl, colorId = null) {
    const lightbox = document.getElementById('imageLightbox');
    const mainImage = document.getElementById('lightboxMainImage');
    const thumbnailsContainer = document.getElementById('lightboxThumbnails');
    const counter = document.getElementById('lightboxCounter');

    if (!lightbox || !mainImage || !thumbnailsContainer) {
        console.error('Lightbox elements not found');
        return;
    }

    // Lấy MỖI MÀU 1 ẢNH ĐẠI DIỆN (không trùng lặp)
    currentLightboxImages = [];
    currentLightboxColorIds = [];

    const imagesByColor = window.imagesByColor || {};

    Object.keys(imagesByColor).forEach(cId => {
        const images = imagesByColor[cId];
        if (images && images.length > 0) {
            currentLightboxImages.push(images[0]); // Chỉ lấy ảnh đầu tiên
            currentLightboxColorIds.push(parseInt(cId));
        }
    });

    if (currentLightboxImages.length === 0) {
        console.warn('No images available for lightbox');
        return;
    }

    // Tìm index của màu hiện tại
    currentLightboxIndex = 0;
    if (colorId !== null) {
        const foundIndex = currentLightboxColorIds.indexOf(parseInt(colorId));
        if (foundIndex !== -1) {
            currentLightboxIndex = foundIndex;
        }
    }

    // Hiển thị ảnh chính
    mainImage.src = currentLightboxImages[currentLightboxIndex];

    // Cập nhật counter
    updateLightboxCounter();

    // Tạo thumbnails
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

    setTimeout(() => {
        scrollThumbnailIntoView(currentLightboxIndex);
    }, 100);

    lightbox.style.display = 'flex';
    document.body.style.overflow = 'hidden';

    console.log('Lightbox opened with', currentLightboxImages.length, 'colors (one image per color)');
}

function closeLightbox() {
    const lightbox = document.getElementById('imageLightbox');
    if (lightbox) {
        lightbox.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

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
        if (i === index) {
            thumb.classList.add('active');
        } else {
            thumb.classList.remove('active');
        }
    });

    scrollThumbnailIntoView(index);
}

function navigateLightbox(direction) {
    let newIndex = currentLightboxIndex + direction;

    if (newIndex < 0) {
        newIndex = currentLightboxImages.length - 1;
    } else if (newIndex >= currentLightboxImages.length) {
        newIndex = 0;
    }

    selectLightboxImage(newIndex);
}

function updateLightboxCounter() {
    const counter = document.getElementById('lightboxCounter');
    if (counter) {
        counter.textContent = `${currentLightboxIndex + 1}/${currentLightboxImages.length}`;
    }
}

function scrollThumbnailIntoView(index) {
    const thumbnailsContainer = document.getElementById('lightboxThumbnails');
    const thumbnails = thumbnailsContainer.querySelectorAll('.lightbox-thumbnail');

    if (thumbnails[index]) {
        thumbnails[index].scrollIntoView({
            behavior: 'smooth',
            block: 'nearest',
            inline: 'center'
        });
    }
}

// ============================================
// KEYBOARD & EVENT LISTENERS
// ============================================

document.addEventListener('keydown', function(e) {
    const lightbox = document.getElementById('imageLightbox');
    if (!lightbox || lightbox.style.display === 'none') return;

    switch(e.key) {
        case 'Escape':
            closeLightbox();
            break;
        case 'ArrowLeft':
            navigateLightbox(-1);
            break;
        case 'ArrowRight':
            navigateLightbox(1);
            break;
    }
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
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function updateCartBadge(quantity) {
    const badge = document.querySelector('.cart-badge-main');
    if (badge) {
        const current = parseInt(badge.textContent) || 0;
        badge.textContent = current + quantity;
    }
}

function scrollToElement(selector) {
    const element = document.querySelector(selector);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

// ============================================
// PAGE INITIALIZATION
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('=== Product Detail Page Loaded ===');
    console.log('Images by color:', window.imagesByColor);
    console.log('Variants:', window.variantsData);

    if (!window.imagesByColor || Object.keys(window.imagesByColor).length === 0) {
        console.error('imagesByColor is empty or undefined');
    }

    if (!window.variantsData || window.variantsData.length === 0) {
        console.error('variantsData is empty or undefined');
    }

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
            zoomHint.className = 'zoom-hint';
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

    // Auto-select first color
    const firstColor = document.querySelector('.color-option');
    if (firstColor) {
        setTimeout(() => {
            firstColor.click();
        }, 200);
    }

    // Prevent invalid quantity input
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
const navbar = document.querySelector('.navbar');

window.addEventListener('scroll', function() {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

    if (scrollTop > lastScrollTop && scrollTop > 80) {
        if (navbar) navbar.classList.add('hide');
    } else if (scrollTop < lastScrollTop - 5) {
        if (navbar) navbar.classList.remove('hide');
    }

    lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
});