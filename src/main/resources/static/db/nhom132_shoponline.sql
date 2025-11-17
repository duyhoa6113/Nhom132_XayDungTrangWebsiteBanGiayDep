-- Create database
CREATE DATABASE nhom132_shoponline;
GO

USE nhom132_shoponline;
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

-- GioHang 
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

-- ==================== VAI TRÒ ====================
PRINT N'Đang thêm Vai Trò...';

INSERT INTO dbo.VaiTro (VaiTroId, TenVaiTro, MoTa) VALUES
(1, N'Admin', N'Quản trị viên hệ thống'),
(2, N'NhanVien', N'Nhân viên bán hàng'),
(3, N'QuanLy', N'Quản lý cửa hàng');
GO

-- ==================== DANH MỤC ====================
PRINT N'Đang thêm Danh Mục...';

INSERT INTO dbo.DanhMuc (Ten, MoTa, TrangThai) VALUES
(N'Giày Thể Thao', N'Giày chạy bộ, tập gym, thể thao', 1),
(N'Giày Sneakers', N'Giày thời trang, casual', 1),
(N'Giày Sandals', N'Giày dép, sandals nam nữ', 1),
(N'Giày Boot', N'Giày cao cổ, boot', 1),
(N'Giày Công Sở', N'Giày tây, giày da công sở', 1);
GO

GO

-- ==================== THƯƠNG HIỆU ====================
PRINT N'Đang thêm Thương Hiệu...';

INSERT INTO dbo.ThuongHieu (Ten, MoTa, TrangThai) VALUES
(N'Nike', N'Thương hiệu thể thao hàng đầu thế giới', 1),
(N'Adidas', N'Thương hiệu thể thao Đức', 1),
(N'Puma', N'Thương hiệu thể thao quốc tế', 1),
(N'Vans', N'Thương hiệu giày lifestyle', 1),
(N'Converse', N'Thương hiệu giày thời trang Mỹ', 1),
(N'New Balance', N'Thương hiệu giày chạy bộ', 1),
(N'Biti''s Hunter', N'Thương hiệu Việt Nam', 1),
(N'Reebok', N'Thương hiệu thể thao Anh', 1);

GO

-- ==================== MÀU SẮC ====================
PRINT N'Đang thêm Màu Sắc...';

INSERT INTO dbo.MauSac (Ten, MaHex, TrangThai) VALUES
(N'Đen', N'#000000', 1),
(N'Trắng', N'#FFFFFF', 1),
(N'Xám', N'#808080', 1),
(N'Xanh Dương', N'#0000FF', 1),
(N'Đỏ', N'#FF0000', 1),
(N'Xanh Lá', N'#00FF00', 1),
(N'Vàng', N'#FFFF00', 1),
(N'Cam', N'#FFA500', 1),
(N'Hồng', N'#FFC0CB', 1),
(N'Nâu', N'#A52A2A', 1);
GO

GO

-- ==================== KÍCH THƯỚC ====================
PRINT N'Đang thêm Kích Thước...';

INSERT INTO dbo.KichThuoc (Ten, TrangThai) VALUES
(N'35', 1), (N'36', 1), (N'37', 1), (N'38', 1), (N'39', 1),
(N'40', 1), (N'41', 1), (N'42', 1), (N'43', 1), (N'44', 1),
(N'45', 1), (N'46', 1);
GO

GO

-- ==================== CHẤT LIỆU ====================
PRINT N'Đang thêm Chất Liệu...';

INSERT INTO dbo.ChatLieu (Ten, TrangThai) VALUES
(N'Da thật', 1),
(N'Da tổng hợp', 1),
(N'Vải canvas', 1),
(N'Vải lưới', 1),
(N'Nhựa', 1),
(N'Cao su', 1),
(N'Vải dệt kim', 1);
GO

GO

-- ==================== SẢN PHẨM ====================
PRINT N'Đang thêm Sản Phẩm...';

-- Lấy ID của các bảng lookup để đảm bảo đúng
DECLARE @DanhMucTheThao INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten = N'Giày Thể Thao');
DECLARE @DanhMucSneakers INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten = N'Giày Sneakers');
DECLARE @ThuongHieuNike INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Nike');
DECLARE @ThuongHieuAdidas INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Adidas');
DECLARE @ThuongHieuPuma INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Puma');
DECLARE @ThuongHieuVans INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Vans');
DECLARE @ThuongHieuConverse INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Converse');
DECLARE @ThuongHieuNewBalance INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'New Balance');
DECLARE @ThuongHieuBitis INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten = N'Biti''s Hunter');
DECLARE @ChatLieuDaTongHop INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten = N'Da tổng hợp');
DECLARE @ChatLieuVaiLuoi INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten = N'Vải lưới');
DECLARE @ChatLieuVaiCanvas INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten = N'Vải canvas');

INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa, TrangThai) VALUES
-- Nike
(@DanhMucTheThao, @ThuongHieuNike, @ChatLieuDaTongHop, N'Nike Air Zoom Pegasus 40', 
    N'Giày chạy bộ cao cấp với công nghệ Air Zoom giúp tăng độ êm ái và phản hồi tốt. Thiết kế lưới thoáng khí, đế ngoài bền bỉ.', 1),
(@DanhMucTheThao, @ThuongHieuNike, @ChatLieuVaiLuoi, N'Nike React Infinity Run', 
    N'Giày chạy marathon với đệm React foam, giảm chấn thương. Phù hợp chạy đường dài.', 1),
(@DanhMucSneakers, @ThuongHieuNike, @ChatLieuDaTongHop, N'Nike Air Force 1', 
    N'Giày sneaker kinh điển, biểu tượng văn hóa đường phố. Thiết kế vượt thời gian.', 1),

-- Adidas
(@DanhMucTheThao, @ThuongHieuAdidas, @ChatLieuDaTongHop, N'Adidas Ultraboost 23', 
    N'Giày thể thao với đế Boost êm ái nhất. Công nghệ Primeknit ôm chân tự nhiên.', 1),
(@DanhMucSneakers, @ThuongHieuAdidas, @ChatLieuVaiLuoi, N'Adidas Stan Smith', 
    N'Giày tennis huyền thoại, thiết kế tối giản. Phong cách cổ điển không bao giờ lỗi thời.', 1),
(@DanhMucTheThao, @ThuongHieuAdidas, @ChatLieuDaTongHop, N'Adidas Solarboost', 
    N'Giày chạy bộ với công nghệ Solar Propulsion, tăng tốc độ mỗi bước chạy.', 1),

-- Puma
(@DanhMucSneakers, @ThuongHieuPuma, @ChatLieuDaTongHop, N'Puma RS-X', 
    N'Giày sneaker phong cách retro, màu sắc táo bạo. Thiết kế chunky đang hot.', 1),
(@DanhMucTheThao, @ThuongHieuPuma, @ChatLieuVaiLuoi, N'Puma Velocity Nitro', 
    N'Giày chạy bộ với công nghệ Nitro foam nhẹ và êm. Phù hợp runner nghiệp dư.', 1),

-- Vans
(@DanhMucSneakers, @ThuongHieuVans, @ChatLieuVaiCanvas, N'Vans Old Skool', 
    N'Giày skateboard cổ điển với sọc đặc trưng. Đế waffle chống trượt tốt.', 1),
(@DanhMucSneakers, @ThuongHieuVans, @ChatLieuVaiCanvas, N'Vans Authentic', 
    N'Thiết kế low-top đơn giản, dễ phối đồ. Chất liệu canvas bền bỉ.', 1),

-- Converse
(@DanhMucSneakers, @ThuongHieuConverse, @ChatLieuVaiCanvas, N'Converse Chuck Taylor All Star', 
    N'Giày canvas cổ điển, phong cách đường phố. Biểu tượng văn hóa Mỹ.', 1),
(@DanhMucSneakers, @ThuongHieuConverse, @ChatLieuVaiCanvas, N'Converse Chuck 70', 
    N'Phiên bản cao cấp của Chuck Taylor với đệm êm hơn, chất liệu tốt hơn.', 1),

-- New Balance
(@DanhMucTheThao, @ThuongHieuNewBalance, @ChatLieuDaTongHop, N'New Balance 990v6', 
    N'Giày chạy bộ cao cấp Made in USA. Chất lượng tốt nhất trong dòng 99X.', 1),
(@DanhMucTheThao, @ThuongHieuNewBalance, @ChatLieuVaiLuoi, N'New Balance Fresh Foam 1080', 
    N'Đệm Fresh Foam cực êm, phù hợp chạy đường dài. Thiết kế thoáng khí.', 1),

-- Biti's Hunter
(@DanhMucSneakers, @ThuongHieuBitis, @ChatLieuVaiLuoi, N'Biti''s Hunter Street', 
    N'Giày thương hiệu Việt, chất lượng quốc tế. Thiết kế trẻ trung, năng động.', 1),
(@DanhMucTheThao, @ThuongHieuBitis, @ChatLieuVaiLuoi, N'Biti''s Hunter Running', 
    N'Giày chạy bộ với công nghệ đệm Foam Clouds. Giá cả phải chăng, chất lượng tốt.', 1);
GO

GO

-- ==================== CHI TIẾT SẢN PHẨM ====================
PRINT N'Đang thêm Chi Tiết Sản Phẩm (Variants)...';

-- Lấy ID của sản phẩm để đảm bảo chính xác
DECLARE @NikePegasusId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Pegasus%');
DECLARE @NikeReactId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%React Infinity%');
DECLARE @NikeAF1Id INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Air Force 1%');
DECLARE @AdidasUBId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Ultraboost%');
DECLARE @AdidasStanId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Stan Smith%');
DECLARE @AdidasSolarboostId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Adidas Solarboost%');
DECLARE @PumaRSXId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%RS-X%');
DECLARE @PumaVelocityId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Puma Velocity Nitro%');
DECLARE @VansOSId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Old Skool%');
DECLARE @ConverseCTId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Chuck Taylor All Star%');
DECLARE @NewBalance990Id INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%990v6%');
DECLARE @BitisStreetId INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten LIKE N'%Biti''s Hunter Street%');

-- Lấy ID màu sắc
DECLARE @MauDen INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Đen');
DECLARE @MauTrang INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Trắng');
DECLARE @MauXam INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Xám');
DECLARE @MauXanhDuong INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Xanh Dương');
DECLARE @MauDo INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Đỏ');
DECLARE @MauXanhLa INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Xanh Lá');
DECLARE @MauCam INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten = N'Cam');

-- Lấy ID kích thước
DECLARE @Size40 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten = N'40');
DECLARE @Size41 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten = N'41');
DECLARE @Size42 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten = N'42');
DECLARE @Size43 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten = N'43');
DECLARE @Size44 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten = N'44');

-- Nike Air Zoom Pegasus 40
IF @NikePegasusId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@NikePegasusId, @MauDen, @Size40, N'NIKE-PEG40-BLK-40', N'8001000001', 3200000, 3500000, 50, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@NikePegasusId, @MauDen, @Size41, N'NIKE-PEG40-BLK-41', N'8001000002', 3200000, 3500000, 45, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@NikePegasusId, @MauDen, @Size42, N'NIKE-PEG40-BLK-42', N'8001000003', 3200000, 3500000, 40, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@NikePegasusId, @MauTrang, @Size40, N'NIKE-PEG40-WHT-40', N'8001000004', 3200000, 3500000, 35, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1),
    (@NikePegasusId, @MauTrang, @Size41, N'NIKE-PEG40-WHT-41', N'8001000005', 3200000, 3500000, 30, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1),
    (@NikePegasusId, @MauXam, @Size40, N'NIKE-PEG40-GRY-40', N'8001000006', 3200000, 3500000, 25, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1);
END

-- Nike React Infinity Run
IF @NikeReactId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@NikeReactId, @MauDen, @Size40, N'NIKE-REACT-BLK-40', N'8002000001', 3800000, 4200000, 40, N'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=600', 1),
    (@NikeReactId, @MauDen, @Size41, N'NIKE-REACT-BLK-41', N'8002000002', 3800000, 4200000, 35, N'https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=600', 1),
    (@NikeReactId, @MauXanhDuong, @Size40, N'NIKE-REACT-BLU-40', N'8002000003', 3800000, 4200000, 30, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@NikeReactId, @MauTrang, @Size41, N'NIKE-REACT-WHT-41', N'8002000004', 3800000, 4200000, 25, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1);
END

-- Nike Air Force 1
IF @NikeAF1Id IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@NikeAF1Id, @MauTrang, @Size40, N'NIKE-AF1-WHT-40', N'8003000001', 2800000, 3000000, 60, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600', 1),
    (@NikeAF1Id, @MauTrang, @Size41, N'NIKE-AF1-WHT-41', N'8003000002', 2800000, 3000000, 55, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600', 1),
    (@NikeAF1Id, @MauTrang, @Size42, N'NIKE-AF1-WHT-42', N'8003000003', 2800000, 3000000, 50, N'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600', 1),
    (@NikeAF1Id, @MauDen, @Size40, N'NIKE-AF1-BLK-40', N'8003000004', 2800000, 3000000, 45, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@NikeAF1Id, @MauDen, @Size41, N'NIKE-AF1-BLK-41', N'8003000005', 2800000, 3000000, 40, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1);
END

-- Adidas Ultraboost 23
IF @AdidasUBId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@AdidasUBId, @MauDen, @Size40, N'ADIDAS-UB23-BLK-40', N'8004000001', 4500000, 5000000, 60, N'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600', 1),
    (@AdidasUBId, @MauDen, @Size41, N'ADIDAS-UB23-BLK-41', N'8004000002', 4500000, 5000000, 55, N'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600', 1),
    (@AdidasUBId, @MauDen, @Size42, N'ADIDAS-UB23-BLK-42', N'8004000003', 4500000, 5000000, 50, N'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600', 1),
    (@AdidasUBId, @MauTrang, @Size40, N'ADIDAS-UB23-WHT-40', N'8004000004', 4500000, 5000000, 45, N'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=600', 1),
    (@AdidasUBId, @MauTrang, @Size41, N'ADIDAS-UB23-WHT-41', N'8004000005', 4500000, 5000000, 40, N'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=600', 1),
    (@AdidasUBId, @MauXanhDuong, @Size40, N'ADIDAS-UB23-NVY-40', N'8004000006', 4500000, 5000000, 35, N'https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=600', 1);
END

-- Adidas Stan Smith
IF @AdidasStanId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@AdidasStanId, @MauTrang, @Size40, N'ADIDAS-STAN-WHT-40', N'8005000001', 2200000, 2500000, 70, N'https://images.unsplash.com/photo-1622434641406-a158123450f9?w=600', 1),
    (@AdidasStanId, @MauTrang, @Size41, N'ADIDAS-STAN-WHT-41', N'8005000002', 2200000, 2500000, 65, N'https://images.unsplash.com/photo-1622434641406-a158123450f9?w=600', 1),
    (@AdidasStanId, @MauTrang, @Size42, N'ADIDAS-STAN-WHT-42', N'8005000003', 2200000, 2500000, 60, N'https://images.unsplash.com/photo-1622434641406-a158123450f9?w=600', 1),
    (@AdidasStanId, @MauXanhLa, @Size40, N'ADIDAS-STAN-GRN-40', N'8005000004', 2200000, 2500000, 30, N'https://images.unsplash.com/photo-1622434641406-a158123450f9?w=600', 1);
END

-- ADIDAS SOLARBOOST
IF @AdidasSolarboostId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    -- Màu Đen
    (@AdidasSolarboostId, @MauDen, @Size40, N'ADIDAS-SOLAR-BLK-40', N'8006000001', 3500000, 4000000, 55, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@AdidasSolarboostId, @MauDen, @Size41, N'ADIDAS-SOLAR-BLK-41', N'8006000002', 3500000, 4000000, 50, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@AdidasSolarboostId, @MauTrang, @Size41, N'ADIDAS-SOLAR-WHT-41', N'8006000006', 3500000, 4000000, 45, N'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=600', 1),
    (@AdidasSolarboostId, @MauTrang, @Size42, N'ADIDAS-SOLAR-WHT-42', N'8006000007', 3500000, 4000000, 40, N'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=600', 1),
	(@AdidasSolarboostId, @MauCam, @Size40, N'ADIDAS-SOLAR-ORG-40', N'8006000013', 3800000, 4000000, 20, N'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600', 1),
    (@AdidasSolarboostId, @MauCam, @Size41, N'ADIDAS-SOLAR-ORG-41', N'8006000014', 3800000, 4000000, 15, N'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600', 1);
 END   

-- Puma RS-X
IF @PumaRSXId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@PumaRSXId, @MauDen, @Size40, N'PUMA-RSX-BLK-40', N'8007000001', 2500000, 2800000, 40, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@PumaRSXId, @MauDen, @Size41, N'PUMA-RSX-BLK-41', N'8007000002', 2500000, 2800000, 35, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@PumaRSXId, @MauDo, @Size40, N'PUMA-RSX-RED-40', N'8007000003', 2500000, 2800000, 30, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1);
END

-- PUMA VELOCITY NITRO
IF @PumaVelocityId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    -- Màu Đen
    (@PumaVelocityId, @MauDen, @Size40, N'PUMA-VELO-BLK-40', N'8008000002', 2800000, 3200000, 50, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@PumaVelocityId, @MauDen, @Size41, N'PUMA-VELO-BLK-41', N'8008000003', 2800000, 3200000, 45, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
    (@PumaVelocityId, @MauDen, @Size42, N'PUMA-VELO-BLK-42', N'8008000004', 2800000, 3200000, 40, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1),
	(@PumaVelocityId, @MauTrang, @Size40, N'PUMA-VELO-WHT-40', N'8008000007', 2800000, 3200000, 45, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1),
    (@PumaVelocityId, @MauTrang, @Size41, N'PUMA-VELO-WHT-41', N'8008000008', 2800000, 3200000, 40, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1),
	(@PumaVelocityId, @MauDo, @Size41, N'PUMA-VELO-RED-41', N'8008000014', 2800000, 3200000, 20, N'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600', 1),
    (@PumaVelocityId, @MauDo, @Size42, N'PUMA-VELO-RED-42', N'8008000015', 2800000, 3200000, 15, N'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600', 1);
END    

-- Vans Old Skool
IF @VansOSId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@VansOSId, @MauDen, @Size40, N'VANS-OS-BLK-40', N'8009000001', 1500000, 1800000, 50, N'https://images.unsplash.com/photo-1588099768523-f4e6a5679d88?w=600', 1),
    (@VansOSId, @MauDen, @Size41, N'VANS-OS-BLK-41', N'8009000002', 1500000, 1800000, 45, N'https://images.unsplash.com/photo-1588099768523-f4e6a5679d88?w=600', 1),
    (@VansOSId, @MauDen, @Size42, N'VANS-OS-BLK-42', N'8009000003', 1500000, 1800000, 40, N'https://images.unsplash.com/photo-1588099768523-f4e6a5679d88?w=600', 1),
    (@VansOSId, @MauDo, @Size40, N'VANS-OS-RED-40', N'8009000004', 1500000, 1800000, 35, N'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600', 1);
END

-- Converse Chuck Taylor
IF @ConverseCTId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@ConverseCTId, @MauDen, @Size40, N'CONV-CT-BLK-40', N'8011000001', 1200000, 1400000, 80, N'https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=600', 1),
    (@ConverseCTId, @MauDen, @Size41, N'CONV-CT-BLK-41', N'8011000002', 1200000, 1400000, 75, N'https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=600', 1),
    (@ConverseCTId, @MauTrang, @Size40, N'CONV-CT-WHT-40', N'8011000003', 1200000, 1400000, 70, N'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=600', 1),
    (@ConverseCTId, @MauTrang, @Size41, N'CONV-CT-WHT-41', N'8011000004', 1200000, 1400000, 65, N'https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=600', 1),
    (@ConverseCTId, @MauDo, @Size40, N'CONV-CT-RED-40', N'8011000005', 1200000, 1400000, 60, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1);
END

-- New Balance 990v6
IF @NewBalance990Id IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@NewBalance990Id, @MauXam, @Size40, N'NB-990V6-GRY-40', N'8013000001', 5500000, 6000000, 30, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@NewBalance990Id, @MauXam, @Size41, N'NB-990V6-GRY-41', N'8013000002', 5500000, 6000000, 25, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@NewBalance990Id, @MauDen, @Size40, N'NB-990V6-BLK-40', N'8013000003', 5500000, 6000000, 20, N'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600', 1);
END

-- Biti's Hunter Street
IF @BitisStreetId IS NOT NULL
BEGIN
    INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh, TrangThai) VALUES
    (@BitisStreetId, @MauDen, @Size40, N'BITIS-ST-BLK-40', N'8015000001', 850000, 1000000, 100, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@BitisStreetId, @MauDen, @Size41, N'BITIS-ST-BLK-41', N'8015000002', 850000, 1000000, 95, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1),
    (@BitisStreetId, @MauTrang, @Size40, N'BITIS-ST-WHT-40', N'8015000003', 850000, 1000000, 90, N'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600', 1),
    (@BitisStreetId, @MauDo, @Size40, N'BITIS-ST-RED-40', N'8015000004', 850000, 1000000, 85, N'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600', 1);
END
GO

GO

-- ==================== KHÁCH HÀNG ====================
PRINT N'Đang thêm Khách Hàng...';

-- Password mẫu: 123456 (đã hash với BCrypt)
INSERT INTO dbo.KhachHang (HoTen, Email, Sdt, MatKhauHash, NgaySinh, GioiTinh, TrangThai) VALUES
(N'Nguyễn Văn An', N'nguyenvanan@gmail.com', N'0901234567', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', '1990-01-15', N'Nam', 1),
(N'Trần Thị Bình', N'tranthibinh@gmail.com', N'0902345678', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', '1992-05-20', N'Nữ', 1),
(N'Lê Văn Cường', N'levancuong@gmail.com', N'0903456789', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', '1988-08-10', N'Nam', 1),
(N'Phạm Thị Dung', N'phamthidung@gmail.com', N'0904567890', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', '1995-12-25', N'Nữ', 1),
(N'Hoàng Văn Em', N'hoangvanem@gmail.com', N'0905678901', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', '1998-07-08', N'Nam', 1);
GO

GO

-- ==================== NHÂN VIÊN ====================
PRINT N'Đang thêm Nhân Viên...';

-- Password mẫu: admin123 (đã hash)
INSERT INTO dbo.NhanVien (VaiTroId, HoTen, Email, MatKhauHash, Sdt, ChucVu, TrangThai) VALUES
(1, N'Admin System', N'admin@shoponline.vn', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', N'0909999999', N'Quản trị viên', 1),
(2, N'Hoàng Văn Hùng', N'hunghoang@shoponline.vn', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', N'0905555555', N'Nhân viên bán hàng', 1),
(3, N'Võ Thị Lan', N'lanvo@shoponline.vn', N'$2a$10$abcdefghijklmnopqrstuvwxyz123456789', N'0906666666', N'Quản lý kho', 1);
GO

GO

-- ==================== ĐỊA CHỈ ====================
PRINT N'Đang thêm Địa Chỉ...';

-- Lấy ID khách hàng
DECLARE @KH1 INT = (SELECT KhachHangId FROM dbo.KhachHang WHERE Email = N'nguyenvanan@gmail.com');
DECLARE @KH2 INT = (SELECT KhachHangId FROM dbo.KhachHang WHERE Email = N'tranthibinh@gmail.com');
DECLARE @KH3 INT = (SELECT KhachHangId FROM dbo.KhachHang WHERE Email = N'levancuong@gmail.com');
DECLARE @KH4 INT = (SELECT KhachHangId FROM dbo.KhachHang WHERE Email = N'phamthidung@gmail.com');

IF @KH1 IS NOT NULL
BEGIN
    INSERT INTO dbo.DiaChi (KhachHangId, HoTenNhan, SdtNhan, DiaChi, PhuongXa, QuanHuyen, TinhTP, MacDinh) VALUES
    (@KH1, N'Nguyễn Văn An', N'0901234567', N'123 Nguyễn Huệ', N'Phường Bến Nghé', N'Quận 1', N'TP. Hồ Chí Minh', 1),
    (@KH1, N'Nguyễn Văn An', N'0901234567', N'456 Võ Văn Ngân', N'Phường Linh Chiểu', N'Thủ Đức', N'TP. Hồ Chí Minh', 0);
END

IF @KH2 IS NOT NULL
BEGIN
    INSERT INTO dbo.DiaChi (KhachHangId, HoTenNhan, SdtNhan, DiaChi, PhuongXa, QuanHuyen, TinhTP, MacDinh) VALUES
    (@KH2, N'Trần Thị Bình', N'0902345678', N'789 Lê Lợi', N'Phường Bến Thành', N'Quận 1', N'TP. Hồ Chí Minh', 1);
END

IF @KH3 IS NOT NULL
BEGIN
    INSERT INTO dbo.DiaChi (KhachHangId, HoTenNhan, SdtNhan, DiaChi, PhuongXa, QuanHuyen, TinhTP, MacDinh) VALUES
    (@KH3, N'Lê Văn Cường', N'0903456789', N'321 Hai Bà Trưng', N'Phường Đakao', N'Quận 1', N'TP. Hồ Chí Minh', 1);
END

IF @KH4 IS NOT NULL
BEGIN
    INSERT INTO dbo.DiaChi (KhachHangId, HoTenNhan, SdtNhan, DiaChi, PhuongXa, QuanHuyen, TinhTP, MacDinh) VALUES
    (@KH4, N'Phạm Thị Dung', N'0904567890', N'654 Pasteur', N'Phường 6', N'Quận 3', N'TP. Hồ Chí Minh', 1);
END
GO

GO

-- ==================== KHUYẾN MÃI ====================
PRINT N'Đang thêm Khuyến Mãi...';

INSERT INTO dbo.KhuyenMai (Ma, Ten, MoTa, Loai, GiaTri, GiamToiDa, DieuKienApDung, SoLuong, NgayBatDau, NgayKetThuc, TrangThai) VALUES
(N'WELCOME10', N'Giảm 10% cho khách hàng mới', N'Áp dụng cho đơn hàng đầu tiên từ 500k', N'percent', 10, 100000, 500000, 100, '2024-01-01', '2025-12-31', 1),
(N'FREESHIP', N'Miễn phí vận chuyển', N'Áp dụng cho đơn từ 1 triệu', N'fixed', 30000, NULL, 1000000, 500, '2024-01-01', '2025-12-31', 1),
(N'SUMMER2024', N'Giảm 15% mùa hè', N'Áp dụng cho tất cả sản phẩm', N'percent', 15, 200000, 300000, 200, '2024-06-01', '2024-08-31', 1),
(N'BLACKFRIDAY', N'Giảm 500k Black Friday', N'Giảm cố định 500k cho đơn từ 3 triệu', N'fixed', 500000, NULL, 3000000, 50, '2024-11-24', '2024-11-30', 1);
GO

-- ==================== THỐNG KÊ ====================
SELECT 'VaiTro' AS [Bảng], COUNT(*) AS [Số Lượng] FROM dbo.VaiTro
UNION ALL
SELECT 'DanhMuc', COUNT(*) FROM dbo.DanhMuc
UNION ALL
SELECT 'ThuongHieu', COUNT(*) FROM dbo.ThuongHieu
UNION ALL
SELECT 'MauSac', COUNT(*) FROM dbo.MauSac
UNION ALL
SELECT 'KichThuoc', COUNT(*) FROM dbo.KichThuoc
UNION ALL
SELECT 'ChatLieu', COUNT(*) FROM dbo.ChatLieu
UNION ALL
SELECT 'SanPham', COUNT(*) FROM dbo.SanPham
UNION ALL
SELECT 'SanPhamChiTiet', COUNT(*) FROM dbo.SanPhamChiTiet
UNION ALL
SELECT 'KhachHang', COUNT(*) FROM dbo.KhachHang
UNION ALL
SELECT 'NhanVien', COUNT(*) FROM dbo.NhanVien
UNION ALL
SELECT 'DiaChi', COUNT(*) FROM dbo.DiaChi
UNION ALL
SELECT 'KhuyenMai', COUNT(*) FROM dbo.KhuyenMai
GO

PRINT N'- Tài khoản khách hàng: nguyenvanan@gmail.com / 123456';
PRINT N'- Tài khoản admin: admin@shoponline.vn / admin123';
PRINT N'- Mã khuyến mãi: WELCOME10, FREESHIP, SUMMER2024, BLACKFRIDAY';
GO

ALTER TABLE SanPham
ADD soLuongDaBan INT DEFAULT 0;
