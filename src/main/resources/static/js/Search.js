/**
 * Search Page JavaScript
 * Xử lý filters, sort và autocomplete
 */

// ========== GLOBAL STATE ==========
let searchFilters = {
    keyword: '',
    category: null,
    brands: [],
    priceMin: null,
    priceMax: null,
    rating: null,
    sort: 'popular',
    page: 0
};

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('Search page initialized');

    // Lấy keyword từ URL
    const urlParams = new URLSearchParams(window.location.search);
    searchFilters.keyword = urlParams.get('q') || '';

    initializeSortButtons();
    initializeCategoryFilter();
    initializeBrandFilter();
    initializePriceFilter();
    initializeRatingFilter();
    initializeResetButton();
    loadFiltersFromURL();
});

// ========== SORT BUTTONS ==========
function initializeSortButtons() {
    console.log('Initializing sort buttons...');

    // Main sort buttons
    document.querySelectorAll('.sort-btn[data-sort]').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const sortValue = this.getAttribute('data-sort');
            setActiveSortButton(this);
            searchFilters.sort = sortValue;
            searchFilters.page = 0;
            applyFilters();
        });
    });

    // Dropdown sort items
    document.querySelectorAll('.dropdown-item[data-sort]').forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const sortValue = this.getAttribute('data-sort');

            // Gắn active cho nút dropdown chính
            const priceDropdownBtn = document.querySelector('.sort-dropdown .sort-btn');
            if (priceDropdownBtn) setActiveSortButton(priceDropdownBtn);

            searchFilters.sort = sortValue;
            searchFilters.page = 0;
            applyFilters();
        });
    });
}

// Helper: set active button
function setActiveSortButton(button) {
    document.querySelectorAll('.sort-btn').forEach(b => b.classList.remove('active'));
    if (button) button.classList.add('active');
}

// ========== CATEGORY FILTER ==========
function initializeCategoryFilter() {
    document.querySelectorAll('input[name="category"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                searchFilters.category = parseInt(this.value);
                document.querySelectorAll('input[name="category"]').forEach(cb => {
                    if (cb !== this) cb.checked = false;
                });
            } else {
                searchFilters.category = null;
            }
            searchFilters.page = 0;
            applyFilters();
        });
    });
}

// ========== BRAND FILTER ==========
function initializeBrandFilter() {
    document.querySelectorAll('input[name="brand"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const brandId = parseInt(this.value);
            if (this.checked) {
                if (!searchFilters.brands.includes(brandId)) searchFilters.brands.push(brandId);
            } else {
                searchFilters.brands = searchFilters.brands.filter(id => id !== brandId);
            }
            searchFilters.page = 0;
            applyFilters();
        });
    });
}

// ========== PRICE FILTER ==========
function initializePriceFilter() {
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
            searchFilters.priceMin = min;
            searchFilters.priceMax = max;
            searchFilters.page = 0;
            applyFilters();
        });
    }
}

// ========== RATING FILTER ==========
function initializeRatingFilter() {
    document.querySelectorAll('input[name="rating"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                searchFilters.rating = parseInt(this.value);
                document.querySelectorAll('input[name="rating"]').forEach(cb => {
                    if (cb !== this) cb.checked = false;
                });
            } else searchFilters.rating = null;
            searchFilters.page = 0;
            applyFilters();
        });
    });
}

// ========== RESET BUTTON ==========
function initializeResetButton() {
    const btnReset = document.querySelector('.btn-reset-filters');
    if (!btnReset) return;

    btnReset.addEventListener('click', function() {
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        const priceMin = document.getElementById('priceMin');
        const priceMax = document.getElementById('priceMax');
        if (priceMin) priceMin.value = '';
        if (priceMax) priceMax.value = '';

        const keyword = searchFilters.keyword;
        searchFilters = {
            keyword: keyword,
            category: null,
            brands: [],
            priceMin: null,
            priceMax: null,
            rating: null,
            sort: 'popular',
            page: 0
        };

        window.location.href = `/search?q=${encodeURIComponent(keyword)}`;
    });
}

// ========== APPLY FILTERS ==========
function applyFilters() {
    const params = new URLSearchParams();
    params.append('q', searchFilters.keyword);
    if (searchFilters.category) params.append('category', searchFilters.category);
    searchFilters.brands.forEach(id => params.append('brand', id));
    if (searchFilters.priceMin && searchFilters.priceMax)
        params.append('priceRange', `${searchFilters.priceMin}-${searchFilters.priceMax}`);
    if (searchFilters.rating) params.append('rating', searchFilters.rating);
    if (searchFilters.sort) params.append('sort', searchFilters.sort);
    params.append('page', searchFilters.page);

    const newUrl = `/search?${params.toString()}`;
    window.location.href = newUrl;
}

// ========== LOAD FILTERS FROM URL ==========
function loadFiltersFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    searchFilters.keyword = urlParams.get('q') || '';

    const category = urlParams.get('category');
    if (category) {
        searchFilters.category = parseInt(category);
        const checkbox = document.querySelector(`input[name="category"][value="${category}"]`);
        if (checkbox) checkbox.checked = true;
    }

    urlParams.getAll('brand').forEach(id => {
        searchFilters.brands.push(parseInt(id));
        const checkbox = document.querySelector(`input[name="brand"][value="${id}"]`);
        if (checkbox) checkbox.checked = true;
    });

    const priceRange = urlParams.get('priceRange');
    if (priceRange) {
        const [min, max] = priceRange.split('-');
        searchFilters.priceMin = min;
        searchFilters.priceMax = max;
        const priceMinInput = document.getElementById('priceMin');
        const priceMaxInput = document.getElementById('priceMax');
        if (priceMinInput) priceMinInput.value = min;
        if (priceMaxInput) priceMaxInput.value = max;
    }

    const rating = urlParams.get('rating');
    if (rating) {
        searchFilters.rating = parseInt(rating);
        const checkbox = document.querySelector(`input[name="rating"][value="${rating}"]`);
        if (checkbox) checkbox.checked = true;
    }

    const sort = urlParams.get('sort');
    if (sort) {
        searchFilters.sort = sort;
        if (sort === 'price-asc' || sort === 'price-desc') {
            const priceDropdownBtn = document.querySelector('.sort-dropdown .sort-btn');
            if (priceDropdownBtn) priceDropdownBtn.classList.add('active');
        } else {
            const sortBtn = document.querySelector(`.sort-btn[data-sort="${sort}"]`);
            if (sortBtn) setActiveSortButton(sortBtn);
        }
    }

    const page = urlParams.get('page');
    if (page) searchFilters.page = parseInt(page);
}
