// API Base URL
const API_BASE_URL = '/admin/role';

// Bootstrap modals
let roleModal, permissionModal, assignPermissionModal;

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap modals
    roleModal = new bootstrap.Modal(document.getElementById('roleModal'));
    permissionModal = new bootstrap.Modal(document.getElementById('permissionModal'));
    assignPermissionModal = new bootstrap.Modal(document.getElementById('assignPermissionModal'));

    // Load initial data
    loadRoles();
    loadPermissions();

    // Load permissions for role form
    loadPermissionsForRoleForm();
});

// ==================== ROLES ====================

/**
 * Load all roles
 */
async function loadRoles() {
    try {
        const response = await fetch(`${API_BASE_URL}/vaitro`);
        const result = await response.json();

        if (result.success) {
            displayRoles(result.data);
        } else {
            showError('Lỗi khi tải danh sách vai trò: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading roles:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Display roles in grid
 */
function displayRoles(roles) {
    const container = document.getElementById('rolesContainer');

    if (!roles || roles.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="fa-solid fa-users-slash"></i>
                    <h5>Chưa có vai trò nào</h5>
                    <p class="text-muted">Nhấn "Thêm vai trò" để tạo vai trò mới</p>
                </div>
            </div>
        `;
        return;
    }

    const html = roles.map(role => `
        <div class="col-md-6 col-lg-4">
            <div class="role-card">
                <div class="role-card-header">
                    <div class="role-name">${escapeHtml(role.tenVaiTro)}</div>
                    <div class="role-stats">
                        <span><i class="fa-solid fa-key me-1"></i>${role.danhSachQuyen ? role.danhSachQuyen.length : 0} quyền</span>
                        <span><i class="fa-solid fa-users me-1"></i>${role.soLuongNhanVien || 0} nhân viên</span>
                    </div>
                </div>
                <div class="role-card-body">
                    <p class="text-muted mb-3">${role.moTa || 'Chưa có mô tả'}</p>
                    
                    <div class="mb-3">
                        <strong class="d-block mb-2">Quyền hạn:</strong>
                        ${role.danhSachQuyen && role.danhSachQuyen.length > 0
        ? role.danhSachQuyen.slice(0, 5).map(p =>
            `<span class="permission-badge">${escapeHtml(p.tenQuyen)}</span>`
        ).join('')
        : '<span class="text-muted">Chưa có quyền nào</span>'}
                        ${role.danhSachQuyen && role.danhSachQuyen.length > 5
        ? `<span class="permission-badge">+${role.danhSachQuyen.length - 5} quyền khác</span>`
        : ''}
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button class="btn btn-sm btn-outline-primary flex-fill" onclick="editRole(${role.vaiTroId})">
                            <i class="fa-solid fa-edit me-1"></i>Sửa
                        </button>
                        <button class="btn btn-sm btn-outline-success flex-fill" onclick="assignPermissions(${role.vaiTroId}, '${escapeHtml(role.tenVaiTro)}')">
                            <i class="fa-solid fa-user-lock me-1"></i>Phân quyền
                        </button>
                        ${role.soLuongNhanVien === 0 ? `
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteRole(${role.vaiTroId}, '${escapeHtml(role.tenVaiTro)}')">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = html;
}

/**
 * Show create role modal
 */
function showCreateRoleModal() {
    document.getElementById('roleModalTitle').innerHTML = '<i class="fa-solid fa-user-shield me-2"></i>Thêm vai trò mới';
    document.getElementById('roleForm').reset();
    document.getElementById('roleId').value = '';

    // Uncheck all permissions
    document.querySelectorAll('#permissionCheckboxes input[type="checkbox"]').forEach(cb => {
        cb.checked = false;
    });

    roleModal.show();
}

/**
 * Edit role
 */
async function editRole(roleId) {
    try {
        const response = await fetch(`${API_BASE_URL}/vaitro/${roleId}`);
        const result = await response.json();

        if (result.success) {
            const role = result.data;

            document.getElementById('roleModalTitle').innerHTML = '<i class="fa-solid fa-user-shield me-2"></i>Chỉnh sửa vai trò';
            document.getElementById('roleId').value = role.vaiTroId;
            document.getElementById('roleName').value = role.tenVaiTro;
            document.getElementById('roleDesc').value = role.moTa || '';

            // Check permissions
            const permissionIds = role.danhSachQuyen ? role.danhSachQuyen.map(p => p.quyenId) : [];
            document.querySelectorAll('#permissionCheckboxes input[type="checkbox"]').forEach(cb => {
                cb.checked = permissionIds.includes(parseInt(cb.value));
            });

            roleModal.show();
        } else {
            showError('Lỗi khi tải thông tin vai trò: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading role:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Save role
 */
async function saveRole() {
    const roleId = document.getElementById('roleId').value;
    const roleName = document.getElementById('roleName').value.trim();
    const roleDesc = document.getElementById('roleDesc').value.trim();

    if (!roleName) {
        showError('Vui lòng nhập tên vai trò');
        return;
    }

    // Get selected permissions
    const quyenIds = [];
    document.querySelectorAll('#permissionCheckboxes input[type="checkbox"]:checked').forEach(cb => {
        quyenIds.push(parseInt(cb.value));
    });

    const data = {
        tenVaiTro: roleName,
        moTa: roleDesc,
        quyenIds: quyenIds
    };

    try {
        let response;
        if (roleId) {
            // Update
            data.vaiTroId = parseInt(roleId);
            response = await fetch(`${API_BASE_URL}/vaitro/${roleId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            // Create
            response = await fetch(`${API_BASE_URL}/vaitro`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        const result = await response.json();

        if (result.success) {
            showSuccess(roleId ? 'Cập nhật vai trò thành công' : 'Tạo vai trò mới thành công');
            roleModal.hide();
            loadRoles();
        } else {
            showError('Lỗi: ' + result.message);
        }
    } catch (error) {
        console.error('Error saving role:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Delete role
 */
async function deleteRole(roleId, roleName) {
    if (!confirm(`Bạn có chắc chắn muốn xóa vai trò "${roleName}"?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/vaitro/${roleId}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
            showSuccess('Xóa vai trò thành công');
            loadRoles();
        } else {
            showError('Lỗi: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting role:', error);
        showError('Không thể kết nối đến server');
    }
}

// ==================== PERMISSIONS ====================

/**
 * Load all permissions
 */
async function loadPermissions() {
    try {
        const response = await fetch(`${API_BASE_URL}/quyen/nhom`);
        const result = await response.json();

        if (result.success) {
            displayPermissions(result.data);
        } else {
            showError('Lỗi khi tải danh sách quyền: ' + result.message);
        }
    } catch (error) {
        console.error('Error loading permissions:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Display permissions grouped
 */
function displayPermissions(permissionsByGroup) {
    const container = document.getElementById('permissionsContainer');

    if (!permissionsByGroup || Object.keys(permissionsByGroup).length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fa-solid fa-key"></i>
                <h5>Chưa có quyền nào</h5>
                <p class="text-muted">Nhấn "Thêm quyền" để tạo quyền mới</p>
            </div>
        `;
        return;
    }

    const groupNames = {
        'PRODUCT': 'Sản phẩm',
        'ORDER': 'Đơn hàng',
        'CUSTOMER': 'Khách hàng',
        'EMPLOYEE': 'Nhân viên',
        'PROMOTION': 'Khuyến mãi',
        'REPORT': 'Báo cáo',
        'PERMISSION': 'Phân quyền',
        'SETTINGS': 'Cấu hình'
    };

    const groupIcons = {
        'PRODUCT': 'fa-box',
        'ORDER': 'fa-shopping-cart',
        'CUSTOMER': 'fa-users',
        'EMPLOYEE': 'fa-user-tie',
        'PROMOTION': 'fa-gift',
        'REPORT': 'fa-chart-bar',
        'PERMISSION': 'fa-shield-halved',
        'SETTINGS': 'fa-cog'
    };

    const html = Object.entries(permissionsByGroup).map(([group, permissions]) => `
        <div class="permission-group">
            <div class="permission-group-header">
                <i class="fa-solid ${groupIcons[group] || 'fa-key'}"></i>
                <span>${groupNames[group] || group}</span>
                <span class="badge bg-light text-dark ms-auto">${permissions.length}</span>
            </div>
            <div class="permission-list">
                ${permissions.map(perm => `
                    <div class="permission-item">
                        <div class="permission-info">
                            <div class="permission-name">${escapeHtml(perm.tenQuyen)}</div>
                            <span class="permission-code">${escapeHtml(perm.maQuyen)}</span>
                            ${perm.moTa ? `<div class="permission-desc">${escapeHtml(perm.moTa)}</div>` : ''}
                        </div>
                        <div class="d-flex gap-2">
                            <button class="btn btn-sm btn-outline-primary" onclick="editPermission(${perm.quyenId})">
                                <i class="fa-solid fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="deletePermission(${perm.quyenId}, '${escapeHtml(perm.tenQuyen)}')">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </div>
                `).join('')}
            </div>
        </div>
    `).join('');

    container.innerHTML = html;
}

/**
 * Load permissions for role form
 */
async function loadPermissionsForRoleForm() {
    try {
        const response = await fetch(`${API_BASE_URL}/quyen/nhom`);
        const result = await response.json();

        if (result.success) {
            displayPermissionCheckboxes(result.data, 'permissionCheckboxes');
        }
    } catch (error) {
        console.error('Error loading permissions for form:', error);
    }
}

/**
 * Display permission checkboxes
 */
function displayPermissionCheckboxes(permissionsByGroup, containerId) {
    const container = document.getElementById(containerId);

    const groupNames = {
        'PRODUCT': 'Sản phẩm',
        'ORDER': 'Đơn hàng',
        'CUSTOMER': 'Khách hàng',
        'EMPLOYEE': 'Nhân viên',
        'PROMOTION': 'Khuyến mãi',
        'REPORT': 'Báo cáo',
        'PERMISSION': 'Phân quyền',
        'SETTINGS': 'Cấu hình'
    };

    const html = Object.entries(permissionsByGroup).map(([group, permissions]) => `
        <div class="badge-group">
            <div class="badge-group-title">${groupNames[group] || group}</div>
            ${permissions.map(perm => `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="${perm.quyenId}" 
                           id="perm_${containerId}_${perm.quyenId}">
                    <label class="form-check-label" for="perm_${containerId}_${perm.quyenId}">
                        ${escapeHtml(perm.tenQuyen)}
                        <small class="text-muted">(${escapeHtml(perm.maQuyen)})</small>
                    </label>
                </div>
            `).join('')}
        </div>
    `).join('');

    container.innerHTML = html;
}

/**
 * Show create permission modal
 */
function showCreatePermissionModal() {
    document.getElementById('permissionModalTitle').innerHTML = '<i class="fa-solid fa-key me-2"></i>Thêm quyền mới';
    document.getElementById('permissionForm').reset();
    document.getElementById('permissionId').value = '';
    permissionModal.show();
}

/**
 * Edit permission
 */
async function editPermission(permissionId) {
    try {
        const response = await fetch(`${API_BASE_URL}/quyen`);
        const result = await response.json();

        if (result.success) {
            const permission = result.data.find(p => p.quyenId === permissionId);

            if (permission) {
                document.getElementById('permissionModalTitle').innerHTML = '<i class="fa-solid fa-key me-2"></i>Chỉnh sửa quyền';
                document.getElementById('permissionId').value = permission.quyenId;
                document.getElementById('permissionCode').value = permission.maQuyen;
                document.getElementById('permissionName').value = permission.tenQuyen;
                document.getElementById('permissionGroup').value = permission.nhom;
                document.getElementById('permissionDesc').value = permission.moTa || '';

                permissionModal.show();
            }
        }
    } catch (error) {
        console.error('Error loading permission:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Save permission
 */
async function savePermission() {
    const permissionId = document.getElementById('permissionId').value;
    const permissionCode = document.getElementById('permissionCode').value.trim().toUpperCase();
    const permissionName = document.getElementById('permissionName').value.trim();
    const permissionGroup = document.getElementById('permissionGroup').value;
    const permissionDesc = document.getElementById('permissionDesc').value.trim();

    if (!permissionCode || !permissionName || !permissionGroup) {
        showError('Vui lòng điền đầy đủ thông tin bắt buộc');
        return;
    }

    const data = {
        maQuyen: permissionCode,
        tenQuyen: permissionName,
        nhom: permissionGroup,
        moTa: permissionDesc,
        trangThai: 1
    };

    try {
        let response;
        if (permissionId) {
            // Update
            data.quyenId = parseInt(permissionId);
            response = await fetch(`${API_BASE_URL}/quyen/${permissionId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            // Create
            response = await fetch(`${API_BASE_URL}/quyen`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        const result = await response.json();

        if (result.success) {
            showSuccess(permissionId ? 'Cập nhật quyền thành công' : 'Tạo quyền mới thành công');
            permissionModal.hide();
            loadPermissions();
            loadPermissionsForRoleForm();
        } else {
            showError('Lỗi: ' + result.message);
        }
    } catch (error) {
        console.error('Error saving permission:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Delete permission
 */
async function deletePermission(permissionId, permissionName) {
    if (!confirm(`Bạn có chắc chắn muốn xóa quyền "${permissionName}"?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/quyen/${permissionId}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
            showSuccess('Xóa quyền thành công');
            loadPermissions();
            loadPermissionsForRoleForm();
        } else {
            showError('Lỗi: ' + result.message);
        }
    } catch (error) {
        console.error('Error deleting permission:', error);
        showError('Không thể kết nối đến server');
    }
}

// ==================== ASSIGN PERMISSIONS ====================

/**
 * Show assign permission modal
 */
async function assignPermissions(roleId, roleName) {
    document.getElementById('assignRoleId').value = roleId;
    document.getElementById('assignRoleName').textContent = `Vai trò: ${roleName}`;

    try {
        // Load role with permissions
        const roleResponse = await fetch(`${API_BASE_URL}/vaitro/${roleId}`);
        const roleResult = await roleResponse.json();

        // Load all permissions
        const permResponse = await fetch(`${API_BASE_URL}/quyen/nhom`);
        const permResult = await permResponse.json();

        if (roleResult.success && permResult.success) {
            const rolePermissionIds = roleResult.data.danhSachQuyen
                ? roleResult.data.danhSachQuyen.map(p => p.quyenId)
                : [];

            displayPermissionCheckboxes(permResult.data, 'assignPermissionCheckboxes');

            // Check existing permissions
            setTimeout(() => {
                document.querySelectorAll('#assignPermissionCheckboxes input[type="checkbox"]').forEach(cb => {
                    cb.checked = rolePermissionIds.includes(parseInt(cb.value));
                });
            }, 100);

            assignPermissionModal.show();
        }
    } catch (error) {
        console.error('Error loading permissions:', error);
        showError('Không thể kết nối đến server');
    }
}

/**
 * Save assigned permissions
 */
async function saveAssignedPermissions() {
    const roleId = document.getElementById('assignRoleId').value;

    // Get selected permissions
    const quyenIds = [];
    document.querySelectorAll('#assignPermissionCheckboxes input[type="checkbox"]:checked').forEach(cb => {
        quyenIds.push(parseInt(cb.value));
    });

    try {
        const response = await fetch(`${API_BASE_URL}/vaitro/${roleId}/quyen`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quyenIds: quyenIds })
        });

        const result = await response.json();

        if (result.success) {
            showSuccess('Cập nhật quyền cho vai trò thành công');
            assignPermissionModal.hide();
            loadRoles();
        } else {
            showError('Lỗi: ' + result.message);
        }
    } catch (error) {
        console.error('Error saving assigned permissions:', error);
        showError('Không thể kết nối đến server');
    }
}

// ==================== UTILITIES ====================

/**
 * Escape HTML
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Show success message
 */
function showSuccess(message) {
    // You can use a toast library or custom notification
    alert(message);
}

/**
 * Show error message
 */
function showError(message) {
    // You can use a toast library or custom notification
    alert(message);
}