
// ========== GLOBAL STATE ==========
let currentFilters = {
    categoryId: null,
    brands: [],
    priceMin: null,
    priceMax: null,
    sizes: [],
    colors: [],
    materials: [],
    sort: 'popular',
    page: 0
};

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('Category page initialized');

    // Get category ID from URL
    const pathSegments = window.location.pathname.split('/');
    currentFilters.categoryId = pathSegments[pathSegments.length - 1];

    // Initialize all components
    initializeSortButtons();
    initializeBrandFilter();
    initializePriceFilter();
    initializeSizeFilter();
    initializeColorFilter();
    initializeMaterialFilter();
    initializeResetButton();
    initializeBrandSearch();
    initializePagination();
    initializeScrollToTop();
    initializeStickyNavbar();

    // Load filters from URL
    loadFiltersFromURL();

    console.log('Current filters:', currentFilters);
});

// ========== SORT BUTTONS - FIXED ==========
function initializeSortButtons() {
    console.log('Initializing sort buttons...');

    // ✅ FIXED: Main sort buttons
    const sortButtons = document.querySelectorAll('.sort-btn[data-sort]');
    sortButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const sortValue = this.getAttribute('data-sort');

            console.log('Sort clicked:', sortValue);

            // Update active state
            document.querySelectorAll('.sort-btn').forEach(b => {
                b.classList.remove('active');
            });
            this.classList.add('active');

            // Update filter and apply
            currentFilters.sort = sortValue;
            currentFilters.page = 0; // Reset to first page
            applyFilters();
        });
    });

    // ✅ FIXED: Dropdown sort items - RIÊNG BIỆT
    const dropdownItems = document.querySelectorAll('.dropdown-item[data-sort]');
    dropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const sortValue = this.getAttribute('data-sort');

            console.log('Dropdown sort clicked:', sortValue);

            // ✅ CRITICAL: Cũng update active cho dropdown toggle button
            document.querySelectorAll('.sort-btn').forEach(b => {
                b.classList.remove('active');
            });

            // Find and activate the price dropdown button
            const priceDropdownBtn = document.querySelector('.sort-dropdown .sort-btn');
            if (priceDropdownBtn) {
                priceDropdownBtn.classList.add('active');
            }

            // Update filter and apply
            currentFilters.sort = sortValue;
            currentFilters.page = 0;
            applyFilters();
        });
    });

    console.log('Sort buttons initialized:', sortButtons.length, 'buttons +', dropdownItems.length, 'dropdown items');
}

// ========== BRAND FILTER ==========
function initializeBrandFilter() {
    console.log('Initializing brand filter...');

    const brandCheckboxes = document.querySelectorAll('input[name="brand"]');
    brandCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const brandId = parseInt(this.value);

            if (this.checked) {
                if (!currentFilters.brands.includes(brandId)) {
                    currentFilters.brands.push(brandId);
                }
            } else {
                currentFilters.brands = currentFilters.brands.filter(id => id !== brandId);
            }

            console.log('Brands updated:', currentFilters.brands);

            currentFilters.page = 0;
            applyFilters();
        });
    });

    console.log('Brand checkboxes initialized:', brandCheckboxes.length);
}

// ========== PRICE FILTER ==========
function initializePriceFilter() {
    console.log('Initializing price filter...');

    const btnApplyPrice = document.querySelector('.btn-apply-price');
    const priceMin = document.getElementById('priceMin');
    const priceMax = document.getElementById('priceMax');

    if (btnApplyPrice && priceMin && priceMax) {
        btnApplyPrice.addEventListener('click', function() {
            const min = priceMin.value || '0';
            const max = priceMax.value || '999999999';

            if (parseInt(min) > parseInt(max)) {
                alert('Giá tối thiểu không được lớn hơn giá tối đa!');
                return;
            }

            currentFilters.priceMin = min;
            currentFilters.priceMax = max;
            currentFilters.page = 0;

            console.log('Price range:', min, '-', max);

            applyFilters();
        });

        console.log('Price filter initialized');
    } else {
        console.warn('Price filter elements not found');
    }
}

// ========== SIZE FILTER ==========
function initializeSizeFilter() {
    console.log('Initializing size filter...');

    const sizeCheckboxes = document.querySelectorAll('input[name="size"]');
    sizeCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const sizeId = parseInt(this.value);

            if (this.checked) {
                if (!currentFilters.sizes.includes(sizeId)) {
                    currentFilters.sizes.push(sizeId);
                }
            } else {
                currentFilters.sizes = currentFilters.sizes.filter(id => id !== sizeId);
            }

            console.log('Sizes updated:', currentFilters.sizes);

            currentFilters.page = 0;
            applyFilters();
        });
    });

    console.log('Size checkboxes initialized:', sizeCheckboxes.length);
}

// ========== COLOR FILTER ==========
function initializeColorFilter() {
    console.log('Initializing color filter...');

    const colorCheckboxes = document.querySelectorAll('input[name="color"]');
    colorCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const colorId = parseInt(this.value);

            if (this.checked) {
                if (!currentFilters.colors.includes(colorId)) {
                    currentFilters.colors.push(colorId);
                }
            } else {
                currentFilters.colors = currentFilters.colors.filter(id => id !== colorId);
            }

            console.log('Colors updated:', currentFilters.colors);

            currentFilters.page = 0;
            applyFilters();
        });
    });

    console.log('Color checkboxes initialized:', colorCheckboxes.length);
}

// ========== MATERIAL FILTER ==========
function initializeMaterialFilter() {
    console.log('Initializing material filter...');

    const materialCheckboxes = document.querySelectorAll('input[name="material"]');
    materialCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const materialId = parseInt(this.value);

            if (this.checked) {
                if (!currentFilters.materials.includes(materialId)) {
                    currentFilters.materials.push(materialId);
                }
            } else {
                currentFilters.materials = currentFilters.materials.filter(id => id !== materialId);
            }

            console.log('Materials updated:', currentFilters.materials);

            currentFilters.page = 0;
            applyFilters();
        });
    });

    console.log('Material checkboxes initialized:', materialCheckboxes.length);
}

// ========== RESET BUTTON ==========
function initializeResetButton() {
    console.log('Initializing reset button...');

    const btnReset = document.querySelector('.btn-reset-filters');

    if (btnReset) {
        btnReset.addEventListener('click', function() {
            console.log('Reset filters clicked');

            // Reset all checkboxes
            document.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                cb.checked = false;
            });

            // Reset price inputs
            const priceMin = document.getElementById('priceMin');
            const priceMax = document.getElementById('priceMax');
            if (priceMin) priceMin.value = '';
            if (priceMax) priceMax.value = '';

            // Reset filters object
            currentFilters = {
                categoryId: currentFilters.categoryId,
                brands: [],
                priceMin: null,
                priceMax: null,
                sizes: [],
                colors: [],
                materials: [],
                sort: 'popular',
                page: 0
            };

            console.log('Filters reset');

            // Redirect to clean URL
            window.location.href = `/category/${currentFilters.categoryId}`;
        });

        console.log('Reset button initialized');
    } else {
        console.warn('Reset button not found');
    }
}

// ========== BRAND SEARCH ==========
function initializeBrandSearch() {
    const brandSearch = document.getElementById('brandSearch');

    if (brandSearch) {
        brandSearch.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const brandItems = document.querySelectorAll('#brandList li');

            let visibleCount = 0;
            brandItems.forEach(item => {
                const text = item.textContent.toLowerCase();
                const isVisible = text.includes(searchTerm);
                item.style.display = isVisible ? '' : 'none';
                if (isVisible) visibleCount++;
            });

            console.log('Brand search:', searchTerm, '- visible:', visibleCount);
        });

        console.log('Brand search initialized');
    }
}

// ========== PAGINATION ==========
function initializePagination() {
    console.log('Initializing pagination...');

    // ✅ FIXED: Chỉ handle button pagination nếu button KHÔNG phải là <a> tag
    const pageNavButtons = document.querySelectorAll('.page-nav-btn:not(a)');
    pageNavButtons.forEach(btn => {
        if (btn.tagName === 'BUTTON') {
            btn.addEventListener('click', function() {
                if (this.disabled) return;

                const isPrev = this.querySelector('.fa-chevron-left');
                if (isPrev) {
                    currentFilters.page = Math.max(0, currentFilters.page - 1);
                } else {
                    currentFilters.page++;
                }

                console.log('Page navigation:', currentFilters.page);
                applyFilters();
            });
        }
    });

    // Page number links - let them work naturally (no preventDefault needed)
    console.log('Pagination initialized');
}

// ========== APPLY FILTERS ==========
function applyFilters() {
    console.log('Applying filters...', currentFilters);

    // Build URL with parameters
    const params = new URLSearchParams();

    // Add brand filters
    currentFilters.brands.forEach(id => params.append('brand', id));

    // Add price range
    if (currentFilters.priceMin && currentFilters.priceMax) {
        params.append('priceRange', `${currentFilters.priceMin}-${currentFilters.priceMax}`);
    }

    // Add size filters
    currentFilters.sizes.forEach(id => params.append('size_filter', id));

    // Add color filters
    currentFilters.colors.forEach(id => params.append('color', id));

    // Add material filters
    currentFilters.materials.forEach(id => params.append('material', id));

    // Add sort
    if (currentFilters.sort) {
        params.append('sort', currentFilters.sort);
    }

    // Add page
    params.append('page', currentFilters.page);

    // Build final URL
    const newUrl = `/category/${currentFilters.categoryId}?${params.toString()}`;

    console.log('Redirecting to:', newUrl);

    // Redirect
    window.location.href = newUrl;
}

// ========== LOAD FILTERS FROM URL ==========
function loadFiltersFromURL() {
    console.log('Loading filters from URL...');

    const urlParams = new URLSearchParams(window.location.search);

    // Load brands
    urlParams.getAll('brand').forEach(id => {
        const brandId = parseInt(id);
        currentFilters.brands.push(brandId);
        const checkbox = document.querySelector(`input[name="brand"][value="${id}"]`);
        if (checkbox) checkbox.checked = true;
    });

    // Load price range
    const priceRange = urlParams.get('priceRange');
    if (priceRange) {
        const [min, max] = priceRange.split('-');
        currentFilters.priceMin = min;
        currentFilters.priceMax = max;

        const priceMinInput = document.getElementById('priceMin');
        const priceMaxInput = document.getElementById('priceMax');
        if (priceMinInput) priceMinInput.value = min;
        if (priceMaxInput) priceMaxInput.value = max;
    }

    // Load sizes
    urlParams.getAll('size_filter').forEach(id => {
        const sizeId = parseInt(id);
        currentFilters.sizes.push(sizeId);
        const checkbox = document.querySelector(`input[name="size"][value="${id}"]`);
        if (checkbox) checkbox.checked = true;
    });

    // Load colors
    urlParams.getAll('color').forEach(id => {
        const colorId = parseInt(id);
        currentFilters.colors.push(colorId);
        const checkbox = document.querySelector(`input[name="color"][value="${id}"]`);
        if (checkbox) checkbox.checked = true;
    });

    // Load materials
    urlParams.getAll('material').forEach(id => {
        const materialId = parseInt(id);
        currentFilters.materials.push(materialId);
        const checkbox = document.querySelector(`input[name="material"][value="${id}"]`);
        if (checkbox) checkbox.checked = true;
    });

    // ✅ FIXED: Load sort và update active state
    const sort = urlParams.get('sort');
    if (sort) {
        currentFilters.sort = sort;

        // Update button active state
        document.querySelectorAll('.sort-btn').forEach(b => b.classList.remove('active'));

        const sortBtn = document.querySelector(`.sort-btn[data-sort="${sort}"]`);
        if (sortBtn) {
            sortBtn.classList.add('active');
        } else if (sort === 'price-asc' || sort === 'price-desc') {
            // Price sort - activate dropdown button
            const priceDropdownBtn = document.querySelector('.sort-dropdown .sort-btn');
            if (priceDropdownBtn) {
                priceDropdownBtn.classList.add('active');
            }

            // Also mark dropdown item as active
            const dropdownItem = document.querySelector(`.dropdown-item[data-sort="${sort}"]`);
            if (dropdownItem) {
                dropdownItem.classList.add('active');
            }
        }
    }

    // Load page
    const page = urlParams.get('page');
    if (page) {
        currentFilters.page = parseInt(page);
    }

    console.log('Filters loaded from URL:', currentFilters);
}

// ========== SCROLL TO TOP ==========
function initializeScrollToTop() {
    const scrollBtn = document.getElementById('scrollToTop');
    if (scrollBtn) {
        window.addEventListener('scroll', () => {
            scrollBtn.classList.toggle('show', window.pageYOffset > 300);
        });
        scrollBtn.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }
}

// ========== STICKY NAVBAR ==========
function initializeStickyNavbar() {
    let lastScrollTop = 0;
    const navbar = document.querySelector('.navbar');

    if (navbar) {
        window.addEventListener('scroll', function() {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            if (scrollTop > lastScrollTop && scrollTop > 80) {
                navbar.classList.add('hide');
            } else if (scrollTop < lastScrollTop - 5) {
                navbar.classList.remove('hide');
            }
            lastScrollTop = scrollTop <= 0 ? 0 : scrollTop;
        });
    }
}

console.log('Category.js loaded successfully');