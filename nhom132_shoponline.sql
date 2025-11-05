-- Create database
CREATE DATABASE nhom132_shoponline;
GO

USE nhom132_shoponline;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =============== LOOKUP TABLES =============== */

-- DanhMuc
CREATE TABLE dbo.DanhMuc
(
    DanhMucId   INT IDENTITY(1,1) PRIMARY KEY,
    Ten         NVARCHAR(150) NOT NULL UNIQUE,
    MoTa        NVARCHAR(1000) NULL,
    TrangThai   TINYINT NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- ThuongHieu
CREATE TABLE dbo.ThuongHieu
(
    ThuongHieuId INT IDENTITY(1,1) PRIMARY KEY,
    Ten          NVARCHAR(150) NOT NULL UNIQUE,
    MoTa         NVARCHAR(1000) NULL,
    TrangThai    TINYINT NOT NULL DEFAULT 1,
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- MauSac
CREATE TABLE dbo.MauSac
(
    MauSacId  INT IDENTITY(1,1) PRIMARY KEY,
    Ten       NVARCHAR(50) NOT NULL UNIQUE,
    MaHex     NVARCHAR(7) NULL,
    TrangThai TINYINT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- KichThuoc
CREATE TABLE dbo.KichThuoc
(
    KichThuocId INT IDENTITY(1,1) PRIMARY KEY,
    Ten         NVARCHAR(50) NOT NULL UNIQUE,
    TrangThai   TINYINT NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- ChatLieu
CREATE TABLE dbo.ChatLieu
(
    ChatLieuId INT IDENTITY(1,1) PRIMARY KEY,
    Ten        NVARCHAR(100) NOT NULL UNIQUE,
    TrangThai  TINYINT NOT NULL DEFAULT 1,
    CreatedAt  DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

/* =============== PRODUCT CATALOG =============== */

-- SanPham
CREATE TABLE dbo.SanPham
(
    SanPhamId     INT IDENTITY(1,1) PRIMARY KEY,
    DanhMucId     INT NOT NULL
        FOREIGN KEY REFERENCES dbo.DanhMuc(DanhMucId),
    ThuongHieuId  INT NULL
        FOREIGN KEY REFERENCES dbo.ThuongHieu(ThuongHieuId),
    ChatLieuId    INT NULL
        FOREIGN KEY REFERENCES dbo.ChatLieu(ChatLieuId),
    Ten           NVARCHAR(200) NOT NULL,
    MoTa          NVARCHAR(MAX) NULL,
    TrangThai     TINYINT NOT NULL DEFAULT 1,
    CreatedAt     DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt     DATETIME2(0) NULL
);
CREATE INDEX IX_SanPham_DanhMuc ON dbo.SanPham(DanhMucId);
CREATE INDEX IX_SanPham_ThuongHieu ON dbo.SanPham(ThuongHieuId);
CREATE INDEX IX_SanPham_TrangThai ON dbo.SanPham(TrangThai);

-- SanPhamChiTiet (Variant)
CREATE TABLE dbo.SanPhamChiTiet
(
    VariantId     INT IDENTITY(1,1) PRIMARY KEY,
    SanPhamId     INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPham(SanPhamId),
    MauSacId      INT NOT NULL
        FOREIGN KEY REFERENCES dbo.MauSac(MauSacId),
    KichThuocId   INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KichThuoc(KichThuocId),
    SKU           NVARCHAR(64) NOT NULL UNIQUE,
    Barcode       NVARCHAR(64) NULL UNIQUE,
    GiaBan        DECIMAL(18,2) NOT NULL CHECK (GiaBan >= 0),
    GiaGoc        DECIMAL(18,2) NULL CHECK (GiaGoc >= 0),
    SoLuongTon    INT NOT NULL DEFAULT 0 CHECK (SoLuongTon >= 0),
    HinhAnh       NVARCHAR(512) NULL,
    TrangThai     TINYINT NOT NULL DEFAULT 1,
    CreatedAt     DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt     DATETIME2(0) NULL,
    CONSTRAINT UQ_Variant_SPMauSize UNIQUE (SanPhamId, MauSacId, KichThuocId)
);
CREATE INDEX IX_Variant_SanPham ON dbo.SanPhamChiTiet(SanPhamId);
CREATE INDEX IX_Variant_TrangThai ON dbo.SanPhamChiTiet(TrangThai);

/* =============== USERS =============== */

-- KhachHang
CREATE TABLE dbo.KhachHang
(
    KhachHangId      INT IDENTITY(1,1) PRIMARY KEY,
    HoTen            NVARCHAR(150) NOT NULL,
    Email            NVARCHAR(255) NOT NULL UNIQUE,
    Sdt              NVARCHAR(20) NULL,
    MatKhauHash      NVARCHAR(255) NOT NULL,
    NgaySinh         DATE NULL,
    GioiTinh         NVARCHAR(10) NULL,
    Avatar           NVARCHAR(500) NULL,
    ResetToken       NVARCHAR(255) NULL,
    ResetTokenExpiry DATETIME2(0) NULL,
    TrangThai        TINYINT NOT NULL DEFAULT 1,
    CreatedAt        DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt        DATETIME2(0) NULL
);
CREATE INDEX IX_KhachHang_Email ON dbo.KhachHang(Email);
CREATE INDEX IX_KhachHang_TrangThai ON dbo.KhachHang(TrangThai);

-- DiaChi
CREATE TABLE dbo.DiaChi
(
    DiaChiId     INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId  INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId) ON DELETE CASCADE,
    HoTenNhan    NVARCHAR(150) NOT NULL,
    SdtNhan      NVARCHAR(20) NOT NULL,
    DiaChi       NVARCHAR(500) NOT NULL,
    PhuongXa     NVARCHAR(150) NULL,
    QuanHuyen    NVARCHAR(150) NULL,
    TinhTP       NVARCHAR(150) NULL,
    MacDinh      BIT NOT NULL DEFAULT 0,
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt    DATETIME2(0) NULL
);
CREATE INDEX IX_DiaChi_KhachHang ON dbo.DiaChi(KhachHangId);
CREATE INDEX IX_DiaChi_MacDinh ON dbo.DiaChi(MacDinh);

-- VaiTro
CREATE TABLE dbo.VaiTro
(
    VaiTroId    INT PRIMARY KEY,
    TenVaiTro   NVARCHAR(50) NOT NULL UNIQUE,
    MoTa        NVARCHAR(200) NULL,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- NhanVien
CREATE TABLE dbo.NhanVien
(
    NhanVienId      INT IDENTITY(1,1) PRIMARY KEY,
    VaiTroId        INT NOT NULL
        FOREIGN KEY REFERENCES dbo.VaiTro(VaiTroId),
    HoTen           NVARCHAR(150) NOT NULL,
    Email           NVARCHAR(255) NOT NULL UNIQUE,
    MatKhauHash     NVARCHAR(255) NOT NULL,
    Sdt             NVARCHAR(20) NULL,
    DiaChi          NVARCHAR(500) NULL,
    NgaySinh        DATE NULL,
    GioiTinh        NVARCHAR(10) NULL,
    ChucVu          NVARCHAR(50) NULL,
    Avatar          NVARCHAR(500) NULL,
    TrangThai       TINYINT NOT NULL DEFAULT 1,
    CreatedAt       DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt       DATETIME2(0) NULL,
    LanDangNhapCuoi DATETIME2(0) NULL
);
CREATE INDEX IX_NhanVien_Email ON dbo.NhanVien(Email);
CREATE INDEX IX_NhanVien_TrangThai ON dbo.NhanVien(TrangThai);

/* =============== CART =============== */

-- GioHang (ĐÃ SỬA - Đơn giản hóa)
CREATE TABLE dbo.GioHang
(
    GioHangId    INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId  INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId) ON DELETE CASCADE,
    VariantId    INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPhamChiTiet(VariantId),
    SoLuong      INT NOT NULL CHECK (SoLuong > 0),
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt    DATETIME2(0) NULL,
    CONSTRAINT UQ_GioHang_KhachHang_Variant UNIQUE (KhachHangId, VariantId)
);
CREATE INDEX IX_GioHang_KhachHang ON dbo.GioHang(KhachHangId);
CREATE INDEX IX_GioHang_Variant ON dbo.GioHang(VariantId);

/* =============== PROMOTIONS =============== */

-- KhuyenMai
CREATE TABLE dbo.KhuyenMai
(
    KhuyenMaiId     INT IDENTITY(1,1) PRIMARY KEY,
    Ma              NVARCHAR(50) NOT NULL UNIQUE,
    Ten             NVARCHAR(200) NOT NULL,
    MoTa            NVARCHAR(MAX) NULL,
    Loai            NVARCHAR(20) NOT NULL CHECK (Loai IN ('percent', 'fixed')),
    GiaTri          DECIMAL(18,2) NOT NULL CHECK (GiaTri >= 0),
    GiamToiDa       DECIMAL(18,2) NULL CHECK (GiamToiDa >= 0),
    DieuKienApDung  DECIMAL(18,2) NULL CHECK (DieuKienApDung >= 0),
    SoLuong         INT NOT NULL DEFAULT 0 CHECK (SoLuong >= 0),
    NgayBatDau      DATE NOT NULL,
    NgayKetThuc     DATE NOT NULL,
    TrangThai       TINYINT NOT NULL DEFAULT 1,
    CreatedAt       DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt       DATETIME2(0) NULL,
    CHECK (NgayKetThuc >= NgayBatDau)
);
CREATE INDEX IX_KhuyenMai_Ma ON dbo.KhuyenMai(Ma);
CREATE INDEX IX_KhuyenMai_TrangThai ON dbo.KhuyenMai(TrangThai);
CREATE INDEX IX_KhuyenMai_NgayBatDau ON dbo.KhuyenMai(NgayBatDau);
CREATE INDEX IX_KhuyenMai_NgayKetThuc ON dbo.KhuyenMai(NgayKetThuc);

/* =============== ORDERS =============== */

-- HoaDon (ĐÃ SỬA - Thêm đầy đủ fields)
CREATE TABLE dbo.HoaDon
(
    HoaDonId            INT IDENTITY(1,1) PRIMARY KEY,
    MaHoaDon            NVARCHAR(50) NOT NULL UNIQUE,
    KhachHangId         INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId),
    NhanVienId          INT NULL
        FOREIGN KEY REFERENCES dbo.NhanVien(NhanVienId),
    KhuyenMaiId         INT NULL
        FOREIGN KEY REFERENCES dbo.KhuyenMai(KhuyenMaiId),
    HoTenNhan           NVARCHAR(150) NOT NULL,
    SdtNhan             NVARCHAR(20) NOT NULL,
    DiaChiNhan          NVARCHAR(500) NOT NULL,
    PhuongXa            NVARCHAR(150) NULL,
    QuanHuyen           NVARCHAR(150) NULL,
    TinhTP              NVARCHAR(150) NULL,
    PhuongThucThanhToan NVARCHAR(50) NOT NULL,
    TrangThai           NVARCHAR(50) NOT NULL DEFAULT N'ChoXuLy',
    TongTien            DECIMAL(18,2) NOT NULL CHECK (TongTien >= 0),
    GiamGia             DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (GiamGia >= 0),
    PhiVanChuyen        DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhiVanChuyen >= 0),
    TongThanhToan       DECIMAL(18,2) NOT NULL CHECK (TongThanhToan >= 0),
    GhiChu              NVARCHAR(MAX) NULL,
    CreatedAt           DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt           DATETIME2(0) NULL
);
CREATE INDEX IX_HoaDon_MaHoaDon ON dbo.HoaDon(MaHoaDon);
CREATE INDEX IX_HoaDon_KhachHang ON dbo.HoaDon(KhachHangId);
CREATE INDEX IX_HoaDon_NhanVien ON dbo.HoaDon(NhanVienId);
CREATE INDEX IX_HoaDon_TrangThai ON dbo.HoaDon(TrangThai);
CREATE INDEX IX_HoaDon_CreatedAt ON dbo.HoaDon(CreatedAt);

-- HoaDonChiTiet (ĐÃ SỬA - Đơn giản hóa)
CREATE TABLE dbo.HoaDonChiTiet
(
    HoaDonChiTietId  INT IDENTITY(1,1) PRIMARY KEY,
    HoaDonId         INT NOT NULL
        FOREIGN KEY REFERENCES dbo.HoaDon(HoaDonId) ON DELETE CASCADE,
    VariantId        INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPhamChiTiet(VariantId),
        SoLuong          INT NOT NULL CHECK (SoLuong > 0),
    DonGia           DECIMAL(18,2) NOT NULL CHECK (DonGia >= 0),
    ThanhTien        DECIMAL(18,2) NOT NULL CHECK (ThanhTien >= 0),
    CreatedAt        DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_HoaDonCT_HoaDon ON dbo.HoaDonChiTiet(HoaDonId);
CREATE INDEX IX_HoaDonCT_Variant ON dbo.HoaDonChiTiet(VariantId);

-- LichSuTrangThai (ĐÃ SỬA - Chỉ giữ 1 bảng)
CREATE TABLE dbo.LichSuTrangThai
(
    LichSuId      INT IDENTITY(1,1) PRIMARY KEY,
    HoaDonId      INT NOT NULL
        FOREIGN KEY REFERENCES dbo.HoaDon(HoaDonId) ON DELETE CASCADE,
    TrangThaiCu   NVARCHAR(50) NULL,
    TrangThaiMoi  NVARCHAR(50) NOT NULL,
    NhanVienId    INT NULL
        FOREIGN KEY REFERENCES dbo.NhanVien(NhanVienId),
    GhiChu        NVARCHAR(MAX) NULL,
    CreatedAt     DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_LichSu_HoaDon ON dbo.LichSuTrangThai(HoaDonId);
CREATE INDEX IX_LichSu_CreatedAt ON dbo.LichSuTrangThai(CreatedAt);

/* =============== REVIEWS & WISHLIST =============== */

-- DanhGia
CREATE TABLE dbo.DanhGia
(
    DanhGiaId   INT IDENTITY(1,1) PRIMARY KEY,
    SanPhamId   INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPham(SanPhamId),
    KhachHangId INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId),
    HoaDonId    INT NULL
        FOREIGN KEY REFERENCES dbo.HoaDon(HoaDonId),
    DiemSao     TINYINT NOT NULL CHECK (DiemSao BETWEEN 1 AND 5),
    NoiDung     NVARCHAR(1000) NULL,
    HinhAnh     NVARCHAR(MAX) NULL,
    TrangThai   TINYINT NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_DanhGia_KH_HD_SP UNIQUE (KhachHangId, HoaDonId, SanPhamId)
);
CREATE INDEX IX_DanhGia_SanPham ON dbo.DanhGia(SanPhamId);
CREATE INDEX IX_DanhGia_KhachHang ON dbo.DanhGia(KhachHangId);
CREATE INDEX IX_DanhGia_TrangThai ON dbo.DanhGia(TrangThai);

-- YeuThich
CREATE TABLE dbo.YeuThich
(
    YeuThichId  INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId) ON DELETE CASCADE,
    SanPhamId   INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPham(SanPhamId) ON DELETE CASCADE,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_YeuThich_KH_SP UNIQUE (KhachHangId, SanPhamId)
);
CREATE INDEX IX_YeuThich_KhachHang ON dbo.YeuThich(KhachHangId);
CREATE INDEX IX_YeuThich_SanPham ON dbo.YeuThich(SanPhamId);

/* =============== NOTIFICATIONS =============== */

-- ThongBao
CREATE TABLE dbo.ThongBao
(
    ThongBaoId  INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId INT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId) ON DELETE CASCADE,
    NhanVienId  INT NULL
        FOREIGN KEY REFERENCES dbo.NhanVien(NhanVienId),
    TieuDe      NVARCHAR(200) NOT NULL,
    NoiDung     NVARCHAR(1000) NOT NULL,
    Loai        NVARCHAR(50) NOT NULL,
    LienKet     NVARCHAR(500) NULL,
    DaDoc       BIT NOT NULL DEFAULT 0,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_ThongBao_KhachHang ON dbo.ThongBao(KhachHangId);
CREATE INDEX IX_ThongBao_NhanVien ON dbo.ThongBao(NhanVienId);
CREATE INDEX IX_ThongBao_DaDoc ON dbo.ThongBao(DaDoc);

-- Newsletter
CREATE TABLE dbo.Newsletter
(
    NewsletterID    INT IDENTITY(1,1) PRIMARY KEY,
    Email           NVARCHAR(255) NOT NULL UNIQUE,
    IsActive        BIT NOT NULL DEFAULT 1,
    SubscribedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UnsubscribedAt  DATETIME2(0) NULL,
    CreatedAt       DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_Newsletter_Email ON dbo.Newsletter(Email);
CREATE INDEX IX_Newsletter_IsActive ON dbo.Newsletter(IsActive);

-- Xóa constraint UNIQUE trên Barcode
IF EXISTS (
    SELECT * FROM sys.indexes 
    WHERE name = 'UQ__SanPhamC__177800D39BA9E2E3' 
    AND object_id = OBJECT_ID('dbo.SanPhamChiTiet')
)
BEGIN
    ALTER TABLE dbo.SanPhamChiTiet 
    DROP CONSTRAINT UQ__SanPhamC__177800D39BA9E2E3;
    PRINT 'Đã xóa UNIQUE constraint trên Barcode';
END
GO

-- Tạo lại UNIQUE INDEX cho phép NULL
CREATE UNIQUE NONCLUSTERED INDEX UQ_SanPhamChiTiet_Barcode
ON dbo.SanPhamChiTiet(Barcode)
WHERE Barcode IS NOT NULL;
GO

PRINT 'Đã tạo lại UNIQUE INDEX cho Barcode (cho phép NULL)';
GO

/* ==================== SEED DATA ==================== */

-- VaiTro
INSERT INTO dbo.VaiTro (VaiTroId, TenVaiTro, MoTa) VALUES 
(1, N'ADMIN', N'Quản trị viên - Toàn quyền hệ thống'),
(2, N'STAFF', N'Nhân viên - Quản lý đơn hàng và sản phẩm'),
(3, N'MANAGER', N'Quản lý - Quản lý nhân viên và báo cáo');

-- DanhMuc
INSERT INTO dbo.DanhMuc (Ten, MoTa) VALUES
(N'Giày chạy bộ', N'Running shoes cho tập luyện'),
(N'Giày casual', N'Giày dạo phố hằng ngày'),
(N'Giày công sở', N'Giày trang trọng cho văn phòng'),
(N'Boots', N'Giày cao cổ, trekking'),
(N'Sneakers', N'Giày thể thao phổ thông'),
(N'Sandals', N'Dép quai hậu thoáng mát');

-- ThuongHieu
INSERT INTO dbo.ThuongHieu (Ten, MoTa) VALUES
(N'Nike', N'Thương hiệu thể thao hàng đầu thế giới'),
(N'Adidas', N'Thương hiệu Đức nổi tiếng với công nghệ Boost'),
(N'Puma', N'Thương hiệu thể thao năng động'),
(N'Converse', N'Giày casual cổ điển'),
(N'Timberland', N'Thương hiệu nổi tiếng với boots'),
(N'Vans', N'Thương hiệu skateboard nổi tiếng'),
(N'New Balance', N'Chuyên về giày chạy bộ'),
(N'Reebok', N'Thương hiệu thể thao đa dạng');

-- MauSac
INSERT INTO dbo.MauSac (Ten, MaHex) VALUES
(N'Đen', N'#000000'),
(N'Trắng', N'#FFFFFF'),
(N'Xám', N'#808080'),
(N'Xanh navy', N'#000080'),
(N'Nâu', N'#8B4513'),
(N'Đỏ', N'#FF0000'),
(N'Xanh dương', N'#0000FF'),
(N'Xanh lá', N'#008000'),
(N'Hồng', N'#FFC0CB'),
(N'Cam', N'#FFA500'),
(N'Vàng', N'#FFFF00'),
(N'Tím', N'#800080'),
(N'Be', N'#F5F5DC'),
(N'Xám đậm', N'#696969'),
(N'Xanh ngọc', N'#40E0D0');

-- KichThuoc
INSERT INTO dbo.KichThuoc (Ten) VALUES
(N'35'), (N'36'), (N'37'), (N'38'), (N'39'), 
(N'40'), (N'41'), (N'42'), (N'43'), (N'44'), (N'45');

-- ChatLieu
INSERT INTO dbo.ChatLieu (Ten) VALUES
(N'Da bò'), (N'Vải lưới'), (N'Da lộn'), (N'Canvas'), (N'PU'),
(N'Da tổng hợp'), (N'Vải cotton'), (N'Cao su'), (N'EVA'), (N'Synthetic');

-- NhanVien (Admin mặc định)
INSERT INTO dbo.NhanVien (VaiTroId, HoTen, Email, MatKhauHash, Sdt, TrangThai)
VALUES 
(1, N'Admin System', N'admin@shopgiaydep.com', N'$2a$10$dummyHashForAdmin123', N'0901234567', 1),
(2, N'Nguyễn Văn Staff', N'staff@shopgiaydep.com', N'$2a$10$dummyHashForStaff123', N'0901234568', 1);

-- KhachHang (Sample)
INSERT INTO dbo.KhachHang (HoTen, Email, Sdt, MatKhauHash, NgaySinh, GioiTinh, TrangThai) VALUES
(N'Nguyễn Văn An', N'nguyenvanan@mail.vn', N'0912345001', N'$2a$10$dummyHash1', '1995-03-15', N'M', 1),
(N'Trần Thị Bích', N'tranthibich@mail.vn', N'0912345002', N'$2a$10$dummyHash2', '1998-07-22', N'F', 1),
(N'Lê Minh Hoàng', N'leminhhoang@mail.vn', N'0912345003', N'$2a$10$dummyHash3', '1992-11-10', N'M', 1),
(N'Phạm Ngọc Anh', N'phamngocanh@mail.vn', N'0912345004', N'$2a$10$dummyHash4', '2000-05-08', N'F', 1),
(N'Hoàng Gia Bảo', N'hoanggiabao@mail.vn', N'0912345005', N'$2a$10$dummyHash5', '1996-09-25', N'M', 1);

-- DiaChi (Sample)
INSERT INTO dbo.DiaChi (KhachHangId, HoTenNhan, SdtNhan, DiaChi, PhuongXa, QuanHuyen, TinhTP, MacDinh) VALUES
(1, N'Nguyễn Văn An', N'0912345001', N'123 Nguyễn Văn Linh', N'Phường Thạc Gián', N'Quận Thanh Khê', N'Đà Nẵng', 1),
(1, N'Nguyễn Văn An', N'0912345001', N'456 Lê Duẩn', N'Phường Hải Châu 1', N'Quận Hải Châu', N'Đà Nẵng', 0),
(2, N'Trần Thị Bích', N'0912345002', N'789 Trần Phú', N'Phường Phước Ninh', N'Quận Hải Châu', N'Đà Nẵng', 1);

-- KhuyenMai (Sample)
INSERT INTO dbo.KhuyenMai (Ma, Ten, MoTa, Loai, GiaTri, GiamToiDa, DieuKienApDung, SoLuong, NgayBatDau, NgayKetThuc, TrangThai) VALUES
(N'WELCOME10', N'Giảm 10% cho khách hàng mới', N'Áp dụng cho đơn hàng đầu tiên', N'percent', 10, 100000, 0, 100, '2024-01-01', '2024-12-31', 1),
(N'SALE50K', N'Giảm 50.000đ', N'Giảm 50k cho đơn từ 500k', N'fixed', 50000, NULL, 500000, 50, '2024-01-01', '2024-12-31', 1),
(N'FREESHIP', N'Miễn phí vận chuyển', N'Miễn phí ship cho đơn từ 300k', N'fixed', 30000, NULL, 300000, 200, '2024-01-01', '2024-12-31', 1),
(N'SUMMER2024', N'Sale mùa hè 2024', N'Giảm 20% tất cả sản phẩm', N'percent', 20, 200000, 300000, 500, '2024-06-01', '2024-08-31', 1);

-- Newsletter (Sample)
INSERT INTO dbo.Newsletter (Email, IsActive) VALUES
(N'test1@example.com', 1),
(N'test2@example.com', 1),
(N'test3@example.com', 0),
(N'newsletter@example.com', 1);

-- SanPham (Sample - 10 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa, TrangThai) VALUES
(1, 1, 2, N'Nike Air Zoom Pegasus 40', N'Giày chạy bộ cao cấp với công nghệ Air Zoom', 1),
(5, 2, 2, N'Adidas Ultraboost 23', N'Giày thể thao với đế Boost êm ái', 1),
(2, 4, 4, N'Converse Chuck Taylor All Star', N'Giày casual cổ điển, phong cách đường phố', 1),
(4, 5, 1, N'Timberland 6-Inch Premium Boot', N'Boots da bò cao cấp, chống nước', 1),
(5, 3, 2, N'Puma RS-X', N'Giày sneaker phong cách retro', 1),
(2, 6, 4, N'Vans Old Skool', N'Giày skateboard cổ điển với sọc đặc trưng', 1),
(1, 7, 2, N'New Balance 990v6', N'Giày chạy bộ cao cấp Made in USA', 1),
(5, 8, 6, N'Reebok Club C 85', N'Giày tennis cổ điển, phong cách tối giản', 1),
(3, 1, 1, N'Nike Air Force 1', N'Giày thể thao kinh điển, phù hợp mọi phong cách', 1),
(5, 2, 2, N'Adidas Stan Smith', N'Giày tennis huyền thoại, thiết kế tối giản', 1);

-- SanPhamChiTiet (Sample - Variants cho 3 sản phẩm đầu)
-- Nike Air Zoom Pegasus 40
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(1, 1, 6, N'NIKE-PEG40-BLK-40', 3200000, 3500000, 50, N'/images/nike-pegasus-black.jpg', 1),
(1, 1, 7, N'NIKE-PEG40-BLK-41', 3200000, 3500000, 45, N'/images/nike-pegasus-black.jpg', 1),
(1, 2, 6, N'NIKE-PEG40-WHT-40', 3200000, 3500000, 40, N'/images/nike-pegasus-white.jpg', 1),
(1, 2, 7, N'NIKE-PEG40-WHT-41', 3200000, 3500000, 35, N'/images/nike-pegasus-white.jpg', 1),
(1, 3, 6, N'NIKE-PEG40-GRY-40', 3200000, 3500000, 30, N'/images/nike-pegasus-grey.jpg', 1);

-- Adidas Ultraboost 23
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(2, 1, 6, N'ADIDAS-UB23-BLK-40', 4500000, 5000000, 60, N'/images/adidas-ultraboost-black.jpg', 1),
(2, 1, 7, N'ADIDAS-UB23-BLK-41', 4500000, 5000000, 55, N'/images/adidas-ultraboost-black.jpg', 1),
(2, 2, 6, N'ADIDAS-UB23-WHT-40', 4500000, 5000000, 50, N'/images/adidas-ultraboost-white.jpg', 1),
(2, 2, 7, N'ADIDAS-UB23-WHT-41', 4500000, 5000000, 45, N'/images/adidas-ultraboost-white.jpg', 1),
(2, 4, 6, N'ADIDAS-UB23-NVY-40', 4500000, 5000000, 40, N'/images/adidas-ultraboost-navy.jpg', 1);

-- Converse Chuck Taylor
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(3, 1, 5, N'CONV-CT-BLK-39', 1200000, 1500000, 100, N'/images/converse-chuck-black.jpg', 1),
(3, 1, 6, N'CONV-CT-BLK-40', 1200000, 1500000, 95, N'/images/converse-chuck-black.jpg', 1),
(3, 2, 5, N'CONV-CT-WHT-39', 1200000, 1500000, 90, N'/images/converse-chuck-white.jpg', 1),
(3, 2, 6, N'CONV-CT-WHT-40', 1200000, 1500000, 85, N'/images/converse-chuck-white.jpg', 1),
(3, 6, 5, N'CONV-CT-RED-39', 1200000, 1500000, 80, N'/images/converse-chuck-red.jpg', 1);

-- Timberland Boot
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(4, 5, 7, N'TIMB-BOOT-BRN-41', 5500000, 6000000, 30, N'/images/timberland-boot-brown.jpg', 1),
(4, 5, 8, N'TIMB-BOOT-BRN-42', 5500000, 6000000, 28, N'/images/timberland-boot-brown.jpg', 1),
(4, 1, 7, N'TIMB-BOOT-BLK-41', 5500000, 6000000, 25, N'/images/timberland-boot-black.jpg', 1),
(4, 1, 8, N'TIMB-BOOT-BLK-42', 5500000, 6000000, 22, N'/images/timberland-boot-black.jpg', 1);

-- Puma RS-X
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(5, 2, 6, N'PUMA-RSX-WHT-40', 2800000, 3200000, 70, N'/images/puma-rsx-white.jpg', 1),
(5, 2, 7, N'PUMA-RSX-WHT-41', 2800000, 3200000, 65, N'/images/puma-rsx-white.jpg', 1),
(5, 1, 6, N'PUMA-RSX-BLK-40', 2800000, 3200000, 60, N'/images/puma-rsx-black.jpg', 1),
(5, 10, 6, N'PUMA-RSX-ORG-40', 2800000, 3200000, 55, N'/images/puma-rsx-orange.jpg', 1);

-- Vans Old Skool
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(6, 1, 6, N'VANS-OS-BLK-40', 1800000, 2000000, 80, N'/images/vans-oldskool-black.jpg', 1),
(6, 1, 7, N'VANS-OS-BLK-41', 1800000, 2000000, 75, N'/images/vans-oldskool-black.jpg', 1),
(6, 2, 6, N'VANS-OS-WHT-40', 1800000, 2000000, 70, N'/images/vans-oldskool-white.jpg', 1),
(6, 4, 6, N'VANS-OS-NVY-40', 1800000, 2000000, 65, N'/images/vans-oldskool-navy.jpg', 1);

-- New Balance 990v6
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(7, 3, 7, N'NB-990V6-GRY-41', 6500000, 7000000, 20, N'/images/nb-990-grey.jpg', 1),
(7, 3, 8, N'NB-990V6-GRY-42', 6500000, 7000000, 18, N'/images/nb-990-grey.jpg', 1),
(7, 1, 7, N'NB-990V6-BLK-41', 6500000, 7000000, 15, N'/images/nb-990-black.jpg', 1);

-- Reebok Club C 85
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(8, 2, 6, N'REEBOK-CC85-WHT-40', 2200000, 2500000, 90, N'/images/reebok-clubc-white.jpg', 1),
(8, 2, 7, N'REEBOK-CC85-WHT-41', 2200000, 2500000, 85, N'/images/reebok-clubc-white.jpg', 1),
(8, 1, 6, N'REEBOK-CC85-BLK-40', 2200000, 2500000, 80, N'/images/reebok-clubc-black.jpg', 1);

-- Nike Air Force 1
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(9, 2, 6, N'NIKE-AF1-WHT-40', 2800000, 3000000, 100, N'/images/nike-af1-white.jpg', 1),
(9, 2, 7, N'NIKE-AF1-WHT-41', 2800000, 3000000, 95, N'/images/nike-af1-white.jpg', 1),
(9, 1, 6, N'NIKE-AF1-BLK-40', 2800000, 3000000, 90, N'/images/nike-af1-black.jpg', 1),
(9, 1, 7, N'NIKE-AF1-BLK-41', 2800000, 3000000, 85, N'/images/nike-af1-black.jpg', 1);

-- Adidas Stan Smith
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
(10, 2, 6, N'ADIDAS-SS-WHT-40', 2500000, 2800000, 110, N'/images/adidas-stansmith-white.jpg', 1),
(10, 2, 7, N'ADIDAS-SS-WHT-41', 2500000, 2800000, 105, N'/images/adidas-stansmith-white.jpg', 1),
(10, 8, 6, N'ADIDAS-SS-GRN-40', 2500000, 2800000, 100, N'/images/adidas-stansmith-green.jpg', 1);

GO

/* ==================== STORED PROCEDURES ==================== */

-- Procedure: Lấy sản phẩm bán chạy
CREATE OR ALTER PROCEDURE sp_GetBestSellingProducts
    @Top INT = 10
AS
BEGIN
    SELECT TOP (@Top)
        sp.SanPhamId,
        sp.Ten,
        sp.TrangThai,
        th.Ten AS ThuongHieu,
        dm.Ten AS DanhMuc,
        SUM(hdct.SoLuong) AS TongSoLuongBan,
        SUM(hdct.ThanhTien) AS TongDoanhThu,
        AVG(CAST(dg.DiemSao AS FLOAT)) AS DiemTrungBinh,
        COUNT(DISTINCT dg.DanhGiaId) AS SoLuotDanhGia
    FROM dbo.SanPham sp
    INNER JOIN dbo.HoaDonChiTiet hdct ON sp.SanPhamId = hdct.VariantId
    INNER JOIN dbo.HoaDon hd ON hdct.HoaDonId = hd.HoaDonId
    LEFT JOIN dbo.ThuongHieu th ON sp.ThuongHieuId = th.ThuongHieuId
    LEFT JOIN dbo.DanhMuc dm ON sp.DanhMucId = dm.DanhMucId
    LEFT JOIN dbo.DanhGia dg ON sp.SanPhamId = dg.SanPhamId AND dg.TrangThai = 1
    WHERE hd.TrangThai = N'HoanThanh'
    GROUP BY sp.SanPhamId, sp.Ten, sp.TrangThai, th.Ten, dm.Ten
    ORDER BY TongSoLuongBan DESC;
END;
GO

-- Procedure: Thống kê doanh thu theo thời gian
CREATE OR ALTER PROCEDURE sp_GetRevenueByDateRange
    @StartDate DATE,
    @EndDate DATE
AS
BEGIN
    SELECT 
        CAST(hd.CreatedAt AS DATE) AS Ngay,
        COUNT(DISTINCT hd.HoaDonId) AS SoDonHang,
        SUM(hd.TongTien) AS TongTien,
        SUM(hd.GiamGia) AS TongGiamGia,
        SUM(hd.PhiVanChuyen) AS TongPhiShip,
        SUM(hd.TongThanhToan) AS DoanhThu
    FROM dbo.HoaDon hd
    WHERE hd.TrangThai = N'HoanThanh'
        AND CAST(hd.CreatedAt AS DATE) BETWEEN @StartDate AND @EndDate
    GROUP BY CAST(hd.CreatedAt AS DATE)
    ORDER BY Ngay;
END;
GO

-- Procedure: Thống kê tồn kho
CREATE OR ALTER PROCEDURE sp_GetInventoryReport
AS
BEGIN
    SELECT 
        sp.SanPhamId,
        sp.Ten AS TenSanPham,
        th.Ten AS ThuongHieu,
        dm.Ten AS DanhMuc,
        COUNT(spct.VariantId) AS SoBienThe,
        SUM(spct.SoLuongTon) AS TongTonKho,
        MIN(spct.SoLuongTon) AS TonKhoThapNhat,
                MAX(spct.SoLuongTon) AS TonKhoCaoNhat,
        AVG(spct.GiaBan) AS GiaTrungBinh,
        SUM(spct.SoLuongTon * spct.GiaBan) AS GiaTriTonKho
    FROM dbo.SanPham sp
    INNER JOIN dbo.SanPhamChiTiet spct ON sp.SanPhamId = spct.SanPhamId
    LEFT JOIN dbo.ThuongHieu th ON sp.ThuongHieuId = th.ThuongHieuId
    LEFT JOIN dbo.DanhMuc dm ON sp.DanhMucId = dm.DanhMucId
    WHERE sp.TrangThai = 1 AND spct.TrangThai = 1
    GROUP BY sp.SanPhamId, sp.Ten, th.Ten, dm.Ten
    ORDER BY TongTonKho ASC;
END;
GO

-- Procedure: Lấy top khách hàng VIP
CREATE OR ALTER PROCEDURE sp_GetTopCustomers
    @Top INT = 10
AS
BEGIN
    SELECT TOP (@Top)
        kh.KhachHangId,
        kh.HoTen,
        kh.Email,
        kh.Sdt,
        COUNT(DISTINCT hd.HoaDonId) AS SoDonHang,
        SUM(hd.TongThanhToan) AS TongChiTieu,
        AVG(hd.TongThanhToan) AS GiaTriTrungBinh,
        MAX(hd.CreatedAt) AS LanMuaCuoi
    FROM dbo.KhachHang kh
    INNER JOIN dbo.HoaDon hd ON kh.KhachHangId = hd.KhachHangId
    WHERE hd.TrangThai = N'HoanThanh'
    GROUP BY kh.KhachHangId, kh.HoTen, kh.Email, kh.Sdt
    ORDER BY TongChiTieu DESC;
END;
GO

-- Procedure: Cập nhật trạng thái đơn hàng
CREATE OR ALTER PROCEDURE sp_UpdateOrderStatus
    @HoaDonId INT,
    @TrangThaiMoi NVARCHAR(50),
    @NhanVienId INT = NULL,
    @GhiChu NVARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @TrangThaiCu NVARCHAR(50);
    
    -- Lấy trạng thái cũ
    SELECT @TrangThaiCu = TrangThai 
    FROM dbo.HoaDon 
    WHERE HoaDonId = @HoaDonId;
    
    IF @TrangThaiCu IS NULL
    BEGIN
        RAISERROR(N'Không tìm thấy đơn hàng', 16, 1);
        RETURN;
    END
    
    -- Cập nhật trạng thái
    UPDATE dbo.HoaDon
    SET TrangThai = @TrangThaiMoi,
        UpdatedAt = SYSUTCDATETIME()
    WHERE HoaDonId = @HoaDonId;
    
    -- Lưu lịch sử
    INSERT INTO dbo.LichSuTrangThai (HoaDonId, TrangThaiCu, TrangThaiMoi, NhanVienId, GhiChu)
    VALUES (@HoaDonId, @TrangThaiCu, @TrangThaiMoi, @NhanVienId, @GhiChu);
    
    -- Nếu hủy đơn, hoàn lại tồn kho
    IF @TrangThaiMoi = N'Huy'
    BEGIN
        UPDATE spct
        SET spct.SoLuongTon = spct.SoLuongTon + hdct.SoLuong
        FROM dbo.SanPhamChiTiet spct
        INNER JOIN dbo.HoaDonChiTiet hdct ON spct.VariantId = hdct.VariantId
        WHERE hdct.HoaDonId = @HoaDonId;
    END
END;
GO

-- Procedure: Tìm kiếm sản phẩm
CREATE OR ALTER PROCEDURE sp_SearchProducts
    @Keyword NVARCHAR(200) = NULL,
    @DanhMucId INT = NULL,
    @ThuongHieuId INT = NULL,
    @MinPrice DECIMAL(18,2) = NULL,
    @MaxPrice DECIMAL(18,2) = NULL,
    @PageNumber INT = 1,
    @PageSize INT = 20
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @Offset INT = (@PageNumber - 1) * @PageSize;
    
    WITH ProductCTE AS
    (
        SELECT DISTINCT
            sp.SanPhamId,
            sp.Ten,
            sp.MoTa,
            sp.TrangThai,
            th.Ten AS ThuongHieu,
            dm.Ten AS DanhMuc,
            MIN(spct.GiaBan) AS GiaThapNhat,
            MAX(spct.GiaBan) AS GiaCaoNhat,
            SUM(spct.SoLuongTon) AS TongTonKho,
            AVG(CAST(dg.DiemSao AS FLOAT)) AS DiemTrungBinh,
            COUNT(DISTINCT dg.DanhGiaId) AS SoLuotDanhGia,
            sp.CreatedAt
        FROM dbo.SanPham sp
        INNER JOIN dbo.SanPhamChiTiet spct ON sp.SanPhamId = spct.SanPhamId
        LEFT JOIN dbo.ThuongHieu th ON sp.ThuongHieuId = th.ThuongHieuId
        LEFT JOIN dbo.DanhMuc dm ON sp.DanhMucId = dm.DanhMucId
        LEFT JOIN dbo.DanhGia dg ON sp.SanPhamId = dg.SanPhamId AND dg.TrangThai = 1
        WHERE sp.TrangThai = 1 
            AND spct.TrangThai = 1
            AND (@Keyword IS NULL OR sp.Ten LIKE N'%' + @Keyword + N'%')
            AND (@DanhMucId IS NULL OR sp.DanhMucId = @DanhMucId)
            AND (@ThuongHieuId IS NULL OR sp.ThuongHieuId = @ThuongHieuId)
        GROUP BY sp.SanPhamId, sp.Ten, sp.MoTa, sp.TrangThai, th.Ten, dm.Ten, sp.CreatedAt
        HAVING (@MinPrice IS NULL OR MIN(spct.GiaBan) >= @MinPrice)
            AND (@MaxPrice IS NULL OR MAX(spct.GiaBan) <= @MaxPrice)
    )
    SELECT *,
        (SELECT COUNT(*) FROM ProductCTE) AS TotalRecords
    FROM ProductCTE
    ORDER BY CreatedAt DESC
    OFFSET @Offset ROWS
    FETCH NEXT @PageSize ROWS ONLY;
END;
GO

/* ==================== VIEWS ==================== */

-- View: Thống kê tổng quan
CREATE OR ALTER VIEW vw_DashboardStats
AS
SELECT
    (SELECT COUNT(*) FROM dbo.SanPham WHERE TrangThai = 1) AS TongSanPham,
    (SELECT COUNT(*) FROM dbo.SanPhamChiTiet WHERE TrangThai = 1) AS TongBienThe,
    (SELECT SUM(SoLuongTon) FROM dbo.SanPhamChiTiet WHERE TrangThai = 1) AS TongTonKho,
    (SELECT COUNT(*) FROM dbo.KhachHang WHERE TrangThai = 1) AS TongKhachHang,
    (SELECT COUNT(*) FROM dbo.HoaDon WHERE TrangThai = N'ChoXuLy') AS DonHangChoXuLy,
    (SELECT COUNT(*) FROM dbo.HoaDon WHERE TrangThai = N'GiaoHang') AS DonHangDangGiao,
    (SELECT COUNT(*) FROM dbo.HoaDon WHERE TrangThai = N'HoanThanh' 
        AND CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)) AS DonHangHomNay,
    (SELECT ISNULL(SUM(TongThanhToan), 0) FROM dbo.HoaDon 
        WHERE TrangThai = N'HoanThanh' 
        AND CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE)) AS DoanhThuHomNay,
    (SELECT ISNULL(SUM(TongThanhToan), 0) FROM dbo.HoaDon 
        WHERE TrangThai = N'HoanThanh' 
        AND MONTH(CreatedAt) = MONTH(GETDATE()) 
        AND YEAR(CreatedAt) = YEAR(GETDATE())) AS DoanhThuThangNay;
GO

-- View: Chi tiết sản phẩm với thông tin đầy đủ
CREATE OR ALTER VIEW vw_ProductDetails
AS
SELECT 
    sp.SanPhamId,
    sp.Ten AS TenSanPham,
    sp.MoTa,
    sp.TrangThai AS TrangThaiSanPham,
    dm.Ten AS DanhMuc,
    th.Ten AS ThuongHieu,
    cl.Ten AS ChatLieu,
    spct.VariantId,
    spct.SKU,
    spct.Barcode,
    ms.Ten AS MauSac,
    ms.MaHex,
    kt.Ten AS KichThuoc,
    spct.GiaBan,
    spct.GiaGoc,
    spct.SoLuongTon,
    spct.HinhAnh,
    spct.TrangThai AS TrangThaiVariant,
    sp.CreatedAt,
    sp.UpdatedAt
FROM dbo.SanPham sp
INNER JOIN dbo.SanPhamChiTiet spct ON sp.SanPhamId = spct.SanPhamId
LEFT JOIN dbo.DanhMuc dm ON sp.DanhMucId = dm.DanhMucId
LEFT JOIN dbo.ThuongHieu th ON sp.ThuongHieuId = th.ThuongHieuId
LEFT JOIN dbo.ChatLieu cl ON sp.ChatLieuId = cl.ChatLieuId
LEFT JOIN dbo.MauSac ms ON spct.MauSacId = ms.MauSacId
LEFT JOIN dbo.KichThuoc kt ON spct.KichThuocId = kt.KichThuocId;
GO

-- View: Chi tiết đơn hàng
CREATE OR ALTER VIEW vw_OrderDetails
AS
SELECT 
    hd.HoaDonId,
    hd.MaHoaDon,
    hd.CreatedAt AS NgayDat,
    hd.TrangThai,
    kh.KhachHangId,
    kh.HoTen AS TenKhachHang,
    kh.Email AS EmailKhachHang,
    kh.Sdt AS SdtKhachHang,
    hd.HoTenNhan,
    hd.SdtNhan,
    hd.DiaChiNhan,
    hd.PhuongXa,
    hd.QuanHuyen,
    hd.TinhTP,
    hd.PhuongThucThanhToan,
    hd.TongTien,
    hd.GiamGia,
    hd.PhiVanChuyen,
    hd.TongThanhToan,
    km.Ma AS MaKhuyenMai,
    km.Ten AS TenKhuyenMai,
    nv.HoTen AS NhanVienXuLy,
    hd.GhiChu
FROM dbo.HoaDon hd
INNER JOIN dbo.KhachHang kh ON hd.KhachHangId = kh.KhachHangId
LEFT JOIN dbo.KhuyenMai km ON hd.KhuyenMaiId = km.KhuyenMaiId
LEFT JOIN dbo.NhanVien nv ON hd.NhanVienId = nv.NhanVienId;
GO

/* ==================== TRIGGERS ==================== */

-- Trigger: Tự động cập nhật UpdatedAt
CREATE OR ALTER TRIGGER trg_UpdateTimestamp_SanPham
ON dbo.SanPham
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.SanPham
    SET UpdatedAt = SYSUTCDATETIME()
    FROM dbo.SanPham sp
    INNER JOIN inserted i ON sp.SanPhamId = i.SanPhamId;
END;
GO

CREATE OR ALTER TRIGGER trg_UpdateTimestamp_SanPhamChiTiet
ON dbo.SanPhamChiTiet
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.SanPhamChiTiet
    SET UpdatedAt = SYSUTCDATETIME()
    FROM dbo.SanPhamChiTiet spct
    INNER JOIN inserted i ON spct.VariantId = i.VariantId;
END;
GO

CREATE OR ALTER TRIGGER trg_UpdateTimestamp_KhachHang
ON dbo.KhachHang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.KhachHang
    SET UpdatedAt = SYSUTCDATETIME()
    FROM dbo.KhachHang kh
    INNER JOIN inserted i ON kh.KhachHangId = i.KhachHangId;
END;
GO

CREATE OR ALTER TRIGGER trg_UpdateTimestamp_HoaDon
ON dbo.HoaDon
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.HoaDon
    SET UpdatedAt = SYSUTCDATETIME()
    FROM dbo.HoaDon hd
    INNER JOIN inserted i ON hd.HoaDonId = i.HoaDonId;
END;
GO

-- Trigger: Kiểm tra tồn kho khi thêm vào giỏ hàng
CREATE OR ALTER TRIGGER trg_CheckStock_GioHang
ON dbo.GioHang
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1
        FROM inserted i
        INNER JOIN dbo.SanPhamChiTiet spct ON i.VariantId = spct.VariantId
        WHERE i.SoLuong > spct.SoLuongTon
    )
    BEGIN
        RAISERROR(N'Số lượng vượt quá tồn kho', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END;
GO

-- Trigger: Kiểm tra tồn kho khi tạo đơn hàng
CREATE OR ALTER TRIGGER trg_CheckStock_HoaDonChiTiet
ON dbo.HoaDonChiTiet
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    IF EXISTS (
        SELECT 1
        FROM inserted i
        INNER JOIN dbo.SanPhamChiTiet spct ON i.VariantId = spct.VariantId
                WHERE i.SoLuong > spct.SoLuongTon
    )
    BEGIN
        RAISERROR(N'Số lượng vượt quá tồn kho', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
    
    -- Trừ tồn kho
    UPDATE spct
    SET spct.SoLuongTon = spct.SoLuongTon - i.SoLuong
    FROM dbo.SanPhamChiTiet spct
    INNER JOIN inserted i ON spct.VariantId = i.VariantId;
END;
GO

-- Trigger: Tự động tính ThanhTien cho HoaDonChiTiet
CREATE OR ALTER TRIGGER trg_CalculateThanhTien_HoaDonChiTiet
ON dbo.HoaDonChiTiet
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE hdct
    SET hdct.ThanhTien = hdct.SoLuong * hdct.DonGia
    FROM dbo.HoaDonChiTiet hdct
    INNER JOIN inserted i ON hdct.HoaDonChiTietId = i.HoaDonChiTietId;
END;
GO

/* ==================== FUNCTIONS ==================== */

-- Function: Tính tổng giá trị đơn hàng
CREATE OR ALTER FUNCTION fn_CalculateOrderTotal
(
    @HoaDonId INT
)
RETURNS DECIMAL(18,2)
AS
BEGIN
    DECLARE @Total DECIMAL(18,2);
    
    SELECT @Total = SUM(ThanhTien)
    FROM dbo.HoaDonChiTiet
    WHERE HoaDonId = @HoaDonId;
    
    RETURN ISNULL(@Total, 0);
END;
GO

-- Function: Kiểm tra voucher có hợp lệ không
CREATE OR ALTER FUNCTION fn_IsVoucherValid
(
    @MaKhuyenMai NVARCHAR(50),
    @NgayKiemTra DATE
)
RETURNS BIT
AS
BEGIN
    DECLARE @IsValid BIT = 0;
    
    IF EXISTS (
        SELECT 1
        FROM dbo.KhuyenMai
        WHERE Ma = @MaKhuyenMai
            AND TrangThai = 1
            AND SoLuong > 0
            AND NgayBatDau <= @NgayKiemTra
            AND NgayKetThuc >= @NgayKiemTra
    )
    BEGIN
        SET @IsValid = 1;
    END
    
    RETURN @IsValid;
END;
GO

-- Function: Tính điểm trung bình sản phẩm
CREATE OR ALTER FUNCTION fn_GetProductRating
(
    @SanPhamId INT
)
RETURNS DECIMAL(3,2)
AS
BEGIN
    DECLARE @Rating DECIMAL(3,2);
    
    SELECT @Rating = AVG(CAST(DiemSao AS DECIMAL(3,2)))
    FROM dbo.DanhGia
    WHERE SanPhamId = @SanPhamId
        AND TrangThai = 1;
    
    RETURN ISNULL(@Rating, 0);
END;
GO

-- Function: Đếm số lượng đã bán của sản phẩm
CREATE OR ALTER FUNCTION fn_GetProductSoldCount
(
    @SanPhamId INT
)
RETURNS INT
AS
BEGIN
    DECLARE @SoldCount INT;
    
    SELECT @SoldCount = SUM(hdct.SoLuong)
    FROM dbo.HoaDonChiTiet hdct
    INNER JOIN dbo.SanPhamChiTiet spct ON hdct.VariantId = spct.VariantId
    INNER JOIN dbo.HoaDon hd ON hdct.HoaDonId = hd.HoaDonId
    WHERE spct.SanPhamId = @SanPhamId
        AND hd.TrangThai = N'HoanThanh';
    
    RETURN ISNULL(@SoldCount, 0);
END;
GO

/* ==================== INDEXES FOR PERFORMANCE ==================== */

-- Additional indexes for better performance
CREATE NONCLUSTERED INDEX IX_HoaDon_KhachHang_TrangThai 
ON dbo.HoaDon(KhachHangId, TrangThai) 
INCLUDE (MaHoaDon, TongThanhToan, CreatedAt);

CREATE NONCLUSTERED INDEX IX_HoaDonChiTiet_HoaDon_Variant 
ON dbo.HoaDonChiTiet(HoaDonId, VariantId) 
INCLUDE (SoLuong, DonGia, ThanhTien);

CREATE NONCLUSTERED INDEX IX_SanPhamChiTiet_SanPham_TrangThai 
ON dbo.SanPhamChiTiet(SanPhamId, TrangThai) 
INCLUDE (GiaBan, SoLuongTon);

CREATE NONCLUSTERED INDEX IX_DanhGia_SanPham_TrangThai 
ON dbo.DanhGia(SanPhamId, TrangThai) 
INCLUDE (DiemSao);

CREATE NONCLUSTERED INDEX IX_GioHang_KhachHang_Variant 
ON dbo.GioHang(KhachHangId, VariantId) 
INCLUDE (SoLuong);

GO