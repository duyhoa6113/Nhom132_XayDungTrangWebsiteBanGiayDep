// Global variables
    let selectedColor = null;
    let selectedVariantId = null;

    // Change main image
    function changeMainImage(src) {
    document.getElementById('mainImage').src = src;

    // Update active thumbnail
    document.querySelectorAll('.thumbnail-image').forEach(img => {
    img.classList.remove('active');
});
    event.target.classList.add('active');
}

    // Select color
    function selectColor(element) {
    // Remove active class from all colors
    document.querySelectorAll('.color-option').forEach(opt => {
        opt.classList.remove('active');
    });

    // Add active class to selected color
    element.classList.add('active');
    selectedColor = element.getAttribute('data-color');

    // Filter sizes by selected color
    filterSizesByColor(selectedColor);

    // Reset selected variant
    selectedVariantId = null;
    document.querySelectorAll('.size-option').forEach(opt => {
    opt.classList.remove('active');
});

    // Update hint text
    document.getElementById('sizeHint').textContent = 'Size đã hết hàng sẽ không thể chọn';

    // Disable buttons until size is selected
    document.getElementById('addToCartBtn').disabled = true;
    document.getElementById('buyNowBtn').disabled = true;
}

    // Filter sizes by selected color
    function filterSizesByColor(color) {
    const sizeOptions = document.querySelectorAll('.size-option');

    sizeOptions.forEach(opt => {
    const variantColor = opt.getAttribute('data-color');

    if (variantColor === color) {
    opt.style.display = 'flex';
} else {
    opt.style.display = 'none';
}
});
}

    // Select variant (size)
    function selectVariant(element) {
    if (element.classList.contains('disabled')) {
    return;
}

    // Check if color is selected
    if (!selectedColor) {
    alert('Vui lòng chọn màu sắc trước!');
    return;
}

    // Remove active class from all sizes
    document.querySelectorAll('.size-option').forEach(opt => {
    opt.classList.remove('active');
});

    // Add active class to selected size
    element.classList.add('active');
    selectedVariantId = element.getAttribute('data-variant-id');

    // Update price
    const priceFormatted = element.getAttribute('data-price-formatted');
    if (priceFormatted) {
    document.getElementById('currentPrice').textContent = priceFormatted + '₫';
}

    // Update max quantity
    const stock = parseInt(element.getAttribute('data-stock'));
    const quantityInput = document.getElementById('quantity');
    quantityInput.max = stock;
    if (parseInt(quantityInput.value) > stock) {
    quantityInput.value = stock;
}

    // Enable buttons
    document.getElementById('addToCartBtn').disabled = false;
    document.getElementById('buyNowBtn').disabled = false;
}

    // Increase quantity
    function increaseQuantity() {
    const input = document.getElementById('quantity');
    const max = parseInt(input.max);
    const current = parseInt(input.value);

    if (current < max) {
    input.value = current + 1;
}
}

    // Decrease quantity
    function decreaseQuantity() {
    const input = document.getElementById('quantity');
    const min = parseInt(input.min);
    const current = parseInt(input.value);

    if (current > min) {
    input.value = current - 1;
}
}

    // Add to cart
    function addToCart() {
    if (!selectedColor) {
    alert('Vui lòng chọn màu sắc!');
    return;
}

    if (!selectedVariantId) {
    alert('Vui lòng chọn size!');
    return;
}

    const quantity = document.getElementById('quantity').value;

    // TODO: Add AJAX call to add to cart
    console.log('Added to cart:', {
    variantId: selectedVariantId,
    color: selectedColor,
    quantity: quantity
});

    alert('Đã thêm vào giỏ hàng thành công!');
}

    // Buy now
    function buyNow() {
    if (!selectedColor) {
    alert('Vui lòng chọn màu sắc!');
    return;
}

    if (!selectedVariantId) {
    alert('Vui lòng chọn size!');
    return;
}

    const quantity = document.getElementById('quantity').value;

    // TODO: Redirect to checkout
    console.log('Buy now:', {
    variantId: selectedVariantId,
    color: selectedColor,
    quantity: quantity
});

    window.location.href = '/checkout?variantId=' + selectedVariantId + '&quantity=' + quantity;
}

    // Add to wishlist
    function addToWishlist() {
    // TODO: Add AJAX call to add to wishlist
    alert('Đã thêm vào danh sách yêu thích!');
}

