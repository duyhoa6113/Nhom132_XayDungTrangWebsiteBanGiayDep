CREATE DATABASE nhom132_shoponline;
GO
USE nhom132_shoponline;
GO

/* =============== TIỆN ÍCH CHUNG =============== */
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* =============== LOOKUP =============== */
IF OBJECT_ID('dbo.DanhMuc','U') IS NOT NULL DROP TABLE dbo.DanhMuc;
CREATE TABLE dbo.DanhMuc
(
    DanhMucId   INT IDENTITY(1,1) PRIMARY KEY,
    Ten         NVARCHAR(150) NOT NULL UNIQUE,
    MoTa        NVARCHAR(1000) NULL,
    TrangThai   TINYINT NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID('dbo.ThuongHieu','U') IS NOT NULL DROP TABLE dbo.ThuongHieu;
CREATE TABLE dbo.ThuongHieu
(
    ThuongHieuId INT IDENTITY(1,1) PRIMARY KEY,
    Ten          NVARCHAR(150) NOT NULL UNIQUE,
    MoTa         NVARCHAR(1000) NULL,
    TrangThai    TINYINT NOT NULL DEFAULT 1
);

IF OBJECT_ID('dbo.MauSac','U') IS NOT NULL DROP TABLE dbo.MauSac;
CREATE TABLE dbo.MauSac
(
    MauSacId INT IDENTITY(1,1) PRIMARY KEY,
    Ten      NVARCHAR(50) NOT NULL UNIQUE
);

IF OBJECT_ID('dbo.KichThuoc','U') IS NOT NULL DROP TABLE dbo.KichThuoc;
CREATE TABLE dbo.KichThuoc
(
    KichThuocId INT IDENTITY(1,1) PRIMARY KEY,
    Ten         NVARCHAR(50) NOT NULL UNIQUE
);

IF OBJECT_ID('dbo.ChatLieu','U') IS NOT NULL DROP TABLE dbo.ChatLieu;
CREATE TABLE dbo.ChatLieu
(
    ChatLieuId INT IDENTITY(1,1) PRIMARY KEY,
    Ten        NVARCHAR(100) NOT NULL UNIQUE
);

/* =============== CATALOG =============== */
IF OBJECT_ID('dbo.SanPham','U') IS NOT NULL DROP TABLE dbo.SanPham;
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

IF OBJECT_ID('dbo.SanPhamChiTiet','U') IS NOT NULL DROP TABLE dbo.SanPhamChiTiet;
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
    CONSTRAINT UQ_Variant_SPMauSize UNIQUE (SanPhamId, MauSacId, KichThuocId)
);
CREATE INDEX IX_Variant_SanPham ON dbo.SanPhamChiTiet(SanPhamId);

/* =============== KHÁCH HÀNG & ĐỊA CHỈ =============== */
IF OBJECT_ID('dbo.KhachHang','U') IS NOT NULL DROP TABLE dbo.KhachHang;
CREATE TABLE dbo.KhachHang
(
    KhachHangId  INT IDENTITY(1,1) PRIMARY KEY,
    HoTen        NVARCHAR(150) NOT NULL,
    Email        NVARCHAR(255) NOT NULL UNIQUE,
    Sdt          NVARCHAR(20) NULL,
    MatKhauHash  NVARCHAR(255) NOT NULL,
    NgaySinh     DATE NULL,
    GioiTinh     NCHAR(1) NULL CHECK (GioiTinh IN (N'M', N'F', N'O')),
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    TrangThai    TINYINT NOT NULL DEFAULT 1
);

IF OBJECT_ID('dbo.DiaChi','U') IS NOT NULL DROP TABLE dbo.DiaChi;
CREATE TABLE dbo.DiaChi
(
    DiaChiId     INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId  INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId),
    HoTenNhan    NVARCHAR(150) NOT NULL,
    SdtNhan      NVARCHAR(20) NOT NULL,
    DiaChi       NVARCHAR(255) NOT NULL,
    PhuongXa     NVARCHAR(150) NULL,
    QuanHuyen    NVARCHAR(150) NULL,
    TinhTP       NVARCHAR(150) NULL,
    MacDinh      BIT NOT NULL DEFAULT 0,
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_DiaChi_KhachHang ON dbo.DiaChi(KhachHangId);

/* =============== GIỎ HÀNG =============== */
IF OBJECT_ID('dbo.GioHangChiTiet','U') IS NOT NULL DROP TABLE dbo.GioHangChiTiet;
IF OBJECT_ID('dbo.GioHang','U') IS NOT NULL DROP TABLE dbo.GioHang;
CREATE TABLE dbo.GioHang
(
    GioHangId    INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId  INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId),
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    TrangThai    TINYINT NOT NULL DEFAULT 0
);
CREATE INDEX IX_GioHang_KhachHang ON dbo.GioHang(KhachHangId);

CREATE TABLE dbo.GioHangChiTiet
(
    GioHangCTId  INT IDENTITY(1,1) PRIMARY KEY,
    GioHangId    INT NOT NULL
        FOREIGN KEY REFERENCES dbo.GioHang(GioHangId) ON DELETE CASCADE,
    VariantId    INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPhamChiTiet(VariantId),
    SoLuong      INT NOT NULL CHECK (SoLuong > 0),
    DonGia       DECIMAL(18,2) NOT NULL CHECK (DonGia >= 0),
    ThanhTien AS (SoLuong * DonGia) PERSISTED,
    CONSTRAINT UQ_GioHang_Item UNIQUE (GioHangId, VariantId)
);
CREATE INDEX IX_GioHangCT_Variant ON dbo.GioHangChiTiet(VariantId);

/* =============== NHÂN VIÊN =============== */
IF OBJECT_ID('dbo.NhanVien','U') IS NOT NULL DROP TABLE dbo.NhanVien;
CREATE TABLE dbo.NhanVien
(
    NhanVienId INT IDENTITY(1,1) PRIMARY KEY,
    HoTen      NVARCHAR(150) NOT NULL,
    Email      NVARCHAR(255) NOT NULL UNIQUE,
    Sdt        NVARCHAR(20)  NULL,
    VaiTro     NVARCHAR(50)  NOT NULL,
    MatKhauHash NVARCHAR(255) NOT NULL,
    TrangThai   TINYINT NOT NULL DEFAULT 1,
    CreatedAt   DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

/* =============== KHUYẾN MÃI =============== */
IF OBJECT_ID('dbo.KhuyenMai_Variant','U') IS NOT NULL DROP TABLE dbo.KhuyenMai_Variant;
IF OBJECT_ID('dbo.KhuyenMai','U') IS NOT NULL DROP TABLE dbo.KhuyenMai;
CREATE TABLE dbo.KhuyenMai
(
    KhuyenMaiId   INT IDENTITY(1,1) PRIMARY KEY,
    MaCode        NVARCHAR(50) NOT NULL UNIQUE,
    Ten           NVARCHAR(255) NOT NULL,
    Loai          NVARCHAR(20) NOT NULL CHECK (Loai IN (N'percent', N'fixed')),
    GiaTri        DECIMAL(18,2) NOT NULL CHECK (GiaTri >= 0),
    ToiThieu      DECIMAL(18,2) NULL CHECK (ToiThieu >= 0),
    NgayBatDau    DATE NOT NULL,
    NgayKetThuc   DATE NOT NULL,
    TrangThai     TINYINT NOT NULL DEFAULT 1,
    CreatedAt     DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE dbo.KhuyenMai_Variant
(
    KhuyenMaiId INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhuyenMai(KhuyenMaiId),
    VariantId   INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPhamChiTiet(VariantId),
    PRIMARY KEY (KhuyenMaiId, VariantId)
);

/* =============== HÓA ĐƠN =============== */
IF OBJECT_ID('dbo.HoaDonChiTiet','U') IS NOT NULL DROP TABLE dbo.HoaDonChiTiet;
IF OBJECT_ID('dbo.HoaDon','U') IS NOT NULL DROP TABLE dbo.HoaDon;
CREATE TABLE dbo.HoaDon
(
    HoaDonId     INT IDENTITY(1,1) PRIMARY KEY,
    KhachHangId  INT NOT NULL
        FOREIGN KEY REFERENCES dbo.KhachHang(KhachHangId),
    NhanVienId   INT NULL
        FOREIGN KEY REFERENCES dbo.NhanVien(NhanVienId),
    KhuyenMaiId  INT NULL
        FOREIGN KEY REFERENCES dbo.KhuyenMai(KhuyenMaiId),
    HoTenNhan    NVARCHAR(150) NOT NULL,
    SdtNhan      NVARCHAR(20)  NOT NULL,
    DiaChiNhan   NVARCHAR(255) NOT NULL,
    HinhThucTT   NVARCHAR(50)  NOT NULL CHECK (HinhThucTT IN (N'COD', N'Card', N'Bank')),
    TrangThai    NVARCHAR(50)  NOT NULL CHECK (TrangThai IN (N'ChoXuLy', N'Duyet', N'GiaoHang', N'HoanThanh', N'Huy')),
    TongTien     DECIMAL(18,2) NOT NULL CHECK (TongTien >= 0),
    GiamGia      DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (GiamGia >= 0),
    PhiShip      DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhiShip >= 0),
    GhiChu       NVARCHAR(500) NULL,
    CreatedAt    DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt    DATETIME2(0) NULL
);
CREATE INDEX IX_HoaDon_KhachHang ON dbo.HoaDon(KhachHangId);
CREATE INDEX IX_HoaDon_NhanVien ON dbo.HoaDon(NhanVienId);

CREATE TABLE dbo.HoaDonChiTiet
(
    HoaDonCTId     INT IDENTITY(1,1) PRIMARY KEY,
    HoaDonId       INT NOT NULL
        FOREIGN KEY REFERENCES dbo.HoaDon(HoaDonId) ON DELETE CASCADE,
    VariantId      INT NOT NULL
        FOREIGN KEY REFERENCES dbo.SanPhamChiTiet(VariantId),
    TenSPSnapshot  NVARCHAR(200) NOT NULL,
    MauSnapshot    NVARCHAR(50)  NOT NULL,
    SizeSnapshot   NVARCHAR(50)  NOT NULL,
    SoLuong        INT NOT NULL CHECK (SoLuong > 0),
    DonGia         DECIMAL(18,2) NOT NULL CHECK (DonGia >= 0),
    ThanhTien AS (SoLuong * DonGia) PERSISTED
);
CREATE INDEX IX_HoaDonCT_HoaDon ON dbo.HoaDonChiTiet(HoaDonId);

/* ==================== SEED DATA ==================== */
-- ========== DANHMUC ==========
INSERT INTO dbo.DanhMuc (Ten, MoTa) VALUES
(N'Giày chạy bộ',     N'Running shoes cho tập luyện'),
(N'Giày casual',      N'Giày dạo phố hằng ngày'),
(N'Giày công sở',     N'Giày trang trọng cho văn phòng'),
(N'Boots',            N'Giày cao cổ, trekking'),
(N'Sneakers',         N'Giày thể thao phổ thông'),
(N'Sandals',          N'Dép quai hậu thoáng mát');

-- ========== THUONGHIEU ==========
INSERT INTO dbo.ThuongHieu (Ten, MoTa) VALUES
(N'NiceStep',     N'Thương hiệu giày thể thao chuyên nghiệp'),
(N'FastRun',      N'Giày chạy bộ hiệu suất cao'),
(N'UrbanWalk',    N'Giày lifestyle đô thị'),
(N'ClassicFeet',  N'Giày công sở cổ điển'),
(N'MountainPro',  N'Giày outdoor & trekking'),
(N'EasyStep',     N'Giày tiện lợi phong cách'),
(N'SportMax',     N'Giày thể thao đa năng'),
(N'ComfortZone',  N'Giày êm ái thoải mái'),
(N'TrendyShoes',  N'Giày thời trang hiện đại'),
(N'ActiveGear',   N'Trang bị thể thao năng động');

-- ========== MAUSAC ==========
INSERT INTO dbo.MauSac (Ten) VALUES
(N'Đen'), (N'Trắng'), (N'Xám'), (N'Xanh navy'), (N'Nâu'),
(N'Đỏ'), (N'Xanh dương'), (N'Xanh lá'), (N'Hồng'), (N'Cam'),
(N'Vàng'), (N'Tím'), (N'Be'), (N'Xám đậm'), (N'Xanh ngọc');

-- ========== KICHTHUOC ==========
INSERT INTO dbo.KichThuoc (Ten) VALUES
(N'36'), (N'37'), (N'38'), (N'39'), (N'40'), (N'41'), (N'42'), (N'43'), (N'44'), (N'45');

-- ========== CHATLIEU ==========
INSERT INTO dbo.ChatLieu (Ten) VALUES
(N'Da bò'), (N'Vải lưới'), (N'Da lộn'), (N'Canvas'), (N'PU'),
(N'Da tổng hợp'), (N'Vải cotton'), (N'Cao su'), (N'EVA'), (N'Synthetic');

-- ========== KHACH HANG ==========
INSERT INTO dbo.KhachHang (HoTen, Email, Sdt, MatKhauHash, NgaySinh, GioiTinh) VALUES
(N'Nguyễn Văn An',     N'nguyenvanan@mail.vn',     N'0912345001', N'$2a$10$dummyHashValue1', '1995-03-15', N'M'),
(N'Trần Thị Bích',     N'tranthibich@mail.vn',     N'0912345002', N'$2a$10$dummyHashValue2', '1998-07-22', N'F'),
(N'Lê Minh Hoàng',     N'leminhhoang@mail.vn',     N'0912345003', N'$2a$10$dummyHashValue3', '1992-11-10', N'M'),
(N'Phạm Ngọc Anh',     N'phamngocanh@mail.vn',     N'0912345004', N'$2a$10$dummyHashValue4', '2000-05-08', N'F'),
(N'Hoàng Gia Bảo',     N'hoanggiabao@mail.vn',     N'0912345005', N'$2a$10$dummyHashValue5', '1996-09-25', N'M');

-- ========== NHAN VIEN ==========
INSERT INTO dbo.NhanVien (HoTen, Email, Sdt, VaiTro, MatKhauHash) VALUES
(N'Admin Master',      N'admin@nicesport.vn',     N'0901234567', N'Admin', N'$2a$10$dummyAdminHash'),
(N'Võ Thị Ly',         N'vothily@nicesport.vn',   N'0909876543', N'Staff', N'$2a$10$dummyStaffHash1'),
(N'Trịnh Quốc Hưng',   N'trinhquochung@nicesport.vn', N'0909876544', N'Staff', N'$2a$10$dummyStaffHash2');

-- ========== 164 SẢN PHẨM ==========
DECLARE @dmRunning  INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Giày chạy bộ');
DECLARE @dmCasual   INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Giày casual');
DECLARE @dmCongSo   INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Giày công sở');
DECLARE @dmBoots    INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Boots');
DECLARE @dmSneakers INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Sneakers');
DECLARE @dmSandals  INT = (SELECT DanhMucId FROM dbo.DanhMuc WHERE Ten=N'Sandals');

DECLARE @thNiceStep INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'NiceStep');
DECLARE @thFastRun INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'FastRun');
DECLARE @thUrbanWalk INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'UrbanWalk');
DECLARE @thClassicFeet INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'ClassicFeet');
DECLARE @thMountainPro INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'MountainPro');
DECLARE @thEasyStep INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'EasyStep');
DECLARE @thSportMax INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'SportMax');
DECLARE @thComfortZone INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'ComfortZone');
DECLARE @thTrendy INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'TrendyShoes');
DECLARE @thActive INT = (SELECT ThuongHieuId FROM dbo.ThuongHieu WHERE Ten=N'ActiveGear');

DECLARE @clDa INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'Da bò');
DECLARE @clLuoi INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'Vải lưới');
DECLARE @clVai INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'Canvas');
DECLARE @clPU INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'PU');
DECLARE @clDaLon INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'Da lộn');
DECLARE @clSynthetic INT = (SELECT ChatLieuId FROM dbo.ChatLieu WHERE Ten=N'Synthetic');

-- GIÀY CHẠY BỘ (28 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmRunning, @thFastRun, @clLuoi, N'FastRun Speed Pro', N'Giày chạy tốc độ, nhẹ và thoáng khí'),
(@dmRunning, @thFastRun, @clLuoi, N'FastRun Marathon Elite', N'Giày chạy marathon chuyên nghiệp'),
(@dmRunning, @thNiceStep, @clLuoi, N'NiceStep Runner X', N'Giày chạy bộ đệm êm, thoáng khí'),
(@dmRunning, @thNiceStep, @clLuoi, N'NiceStep Sprint Max', N'Giày chạy ngắn hiệu suất cao'),
(@dmRunning, @thActive, @clSynthetic, N'ActiveGear Velocity', N'Giày chạy năng động linh hoạt'),
(@dmRunning, @thActive, @clLuoi, N'ActiveGear Pace Runner', N'Giày chạy kiểm soát nhịp độ'),
(@dmRunning, @thSportMax, @clLuoi, N'SportMax Trail Run', N'Giày chạy địa hình gồ ghề'),
(@dmRunning, @thSportMax, @clSynthetic, N'SportMax Road Racer', N'Giày chạy đường trường'),
(@dmRunning, @thComfortZone, @clLuoi, N'ComfortZone Easy Run', N'Giày chạy êm ái thoải mái'),
(@dmRunning, @thComfortZone, @clLuoi, N'ComfortZone Cloud Step', N'Giày chạy đế mềm như mây'),
(@dmRunning, @thFastRun, @clLuoi, N'FastRun Ultra Light', N'Giày chạy siêu nhẹ'),
(@dmRunning, @thNiceStep, @clSynthetic, N'NiceStep Endurance', N'Giày chạy độ bền cao'),
(@dmRunning, @thActive, @clLuoi, N'ActiveGear Turbo', N'Giày chạy tăng tốc'),
(@dmRunning, @thSportMax, @clLuoi, N'SportMax Energy', N'Giày chạy năng lượng'),
(@dmRunning, @thFastRun, @clSynthetic, N'FastRun Boost', N'Giày chạy tăng đẩy'),
(@dmRunning, @thNiceStep, @clLuoi, N'NiceStep Flex', N'Giày chạy linh hoạt'),
(@dmRunning, @thActive, @clSynthetic, N'ActiveGear Motion', N'Giày chạy chuyển động'),
(@dmRunning, @thComfortZone, @clLuoi, N'ComfortZone Cushion', N'Giày chạy đệm êm'),
(@dmRunning, @thFastRun, @clLuoi, N'FastRun Aero', N'Giày chạy khí động học'),
(@dmRunning, @thSportMax, @clSynthetic, N'SportMax Dynamic', N'Giày chạy năng động'),
(@dmRunning, @thNiceStep, @clLuoi, N'NiceStep Pro Runner', N'Giày chạy chuyên nghiệp'),
(@dmRunning, @thActive, @clLuoi, N'ActiveGear Sprint', N'Giày chạy nước rút'),
(@dmRunning, @thFastRun, @clSynthetic, N'FastRun Performance', N'Giày chạy hiệu suất'),
(@dmRunning, @thComfortZone, @clLuoi, N'ComfortZone Soft Run', N'Giày chạy mềm mại'),
(@dmRunning, @thSportMax, @clLuoi, N'SportMax Racer', N'Giày thi đấu chạy'),
(@dmRunning, @thNiceStep, @clSynthetic, N'NiceStep Swift', N'Giày chạy nhanh nhẹn'),
(@dmRunning, @thActive, @clLuoi, N'ActiveGear Flow', N'Giày chạy mượt mà'),
(@dmRunning, @thFastRun, @clLuoi, N'FastRun Elite', N'Giày chạy cao cấp');

-- GIÀY CASUAL (28 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmCasual, @thUrbanWalk, @clDa, N'UrbanWalk Classic', N'Giày mọi da, phong cách tối giản'),
(@dmCasual, @thUrbanWalk, @clVai, N'UrbanWalk Street Style', N'Giày dạo phố thời trang'),
(@dmCasual, @thEasyStep, @clVai, N'EasyStep Casual Pro', N'Giày casual tiện lợi'),
(@dmCasual, @thEasyStep, @clDa, N'EasyStep Comfort Walk', N'Giày đi bộ thoải mái'),
(@dmCasual, @thTrendy, @clVai, N'TrendyShoes Urban', N'Giày đô thị thời trang'),
(@dmCasual, @thTrendy, @clDa, N'TrendyShoes Loafer', N'Giày lười phong cách'),
(@dmCasual, @thComfortZone, @clVai, N'ComfortZone Relax', N'Giày thư giãn êm ái'),
(@dmCasual, @thUrbanWalk, @clDa, N'UrbanWalk Premium', N'Giày da cao cấp'),
(@dmCasual, @thEasyStep, @clVai, N'EasyStep Light', N'Giày nhẹ nhàng'),
(@dmCasual, @thTrendy, @clDaLon, N'TrendyShoes Suede', N'Giày da lộn sang trọng'),
(@dmCasual, @thUrbanWalk, @clVai, N'UrbanWalk Canvas', N'Giày vải đơn giản'),
(@dmCasual, @thComfortZone, @clDa, N'ComfortZone Soft', N'Giày êm mềm'),
(@dmCasual, @thEasyStep, @clVai, N'EasyStep Daily', N'Giày hằng ngày'),
(@dmCasual, @thTrendy, @clDa, N'TrendyShoes Modern', N'Giày hiện đại'),
(@dmCasual, @thUrbanWalk, @clDaLon, N'UrbanWalk Luxury', N'Giày sang trọng'),
(@dmCasual, @thComfortZone, @clVai, N'ComfortZone Easy', N'Giày dễ đi'),
(@dmCasual, @thEasyStep, @clDa, N'EasyStep Smart', N'Giày lịch sự'),
(@dmCasual, @thTrendy, @clVai, N'TrendyShoes Fresh', N'Giày tươi mới'),
(@dmCasual, @thUrbanWalk, @clDa, N'UrbanWalk Elegant', N'Giày thanh lịch'),
(@dmCasual, @thEasyStep, @clVai, N'EasyStep Cool', N'Giày mát mẻ'),
(@dmCasual, @thComfortZone, @clDa, N'ComfortZone Plus', N'Giày đặc biệt'),
(@dmCasual, @thTrendy, @clDaLon, N'TrendyShoes Chic', N'Giày sành điệu'),
(@dmCasual, @thUrbanWalk, @clVai, N'UrbanWalk Lite', N'Giày nhẹ'),
(@dmCasual, @thEasyStep, @clDa, N'EasyStep Fine', N'Giày tinh tế'),
(@dmCasual, @thTrendy, @clVai, N'TrendyShoes Vibe', N'Giày phong cách'),
(@dmCasual, @thComfortZone, @clDa, N'ComfortZone Gentle', N'Giày nhẹ nhàng'),
(@dmCasual, @thUrbanWalk, @clDaLon, N'UrbanWalk Soft Suede', N'Giày da lộn mềm'),
(@dmCasual, @thEasyStep, @clVai, N'EasyStep Basic', N'Giày cơ bản');

-- GIÀY CÔNG SỞ (28 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Derby', N'Giày Derby công sở cổ điển'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Oxford', N'Giày Oxford lịch sự'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Brogue', N'Giày Brogue trang trọng'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Office Pro', N'Giày văn phòng chuyên nghiệp'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Business', N'Giày công vụ'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Formal', N'Giày trang trọng'),
(@dmCongSo, @thClassicFeet, @clDaLon, N'ClassicFeet Suede Office', N'Giày công sở da lộn'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Executive', N'Giày cao cấp'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Professional', N'Giày chuyên nghiệp'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Corporate', N'Giày doanh nghiệp'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Gentleman', N'Giày quý ông'),
(@dmCongSo, @thUrbanWalk, @clDaLon, N'UrbanWalk Premium Office', N'Giày văn phòng cao cấp'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Elegant', N'Giày thanh lịch'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Smart Office', N'Giày văn phòng thông minh'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Luxury', N'Giày sang trọng'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Manager', N'Giày quản lý'),
(@dmCongSo, @thClassicFeet, @clDaLon, N'ClassicFeet Refined', N'Giày tinh tế'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Director', N'Giày giám đốc'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Noble', N'Giày cao quý'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Boss', N'Giày sếp'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Prestige', N'Giày uy tín'),
(@dmCongSo, @thUrbanWalk, @clDaLon, N'UrbanWalk Distinguished', N'Giày xuất sắc'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Superior', N'Giày vượt trội'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Elite Office', N'Giày văn phòng ưu tú'),
(@dmCongSo, @thClassicFeet, @clDa, N'ClassicFeet Premier', N'Giày hàng đầu'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Supreme', N'Giày tối thượng'),
(@dmCongSo, @thClassicFeet, @clDaLon, N'ClassicFeet Exclusive', N'Giày độc quyền'),
(@dmCongSo, @thUrbanWalk, @clDa, N'UrbanWalk Master', N'Giày bậc thầy');

-- BOOTS (28 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Trek', N'Boot trekking chống nước'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Summit', N'Boot leo núi chuyên nghiệp'),
(@dmBoots, @thMountainPro, @clDaLon, N'MountainPro Adventure', N'Boot phiêu lưu bền bỉ'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Hiker', N'Boot đi bộ đường dài'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Terrain', N'Boot địa hình khắc nghiệt'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Expedition', N'Boot thám hiểm'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Outdoor', N'Boot ngoài trời'),
(@dmBoots, @thMountainPro, @clDaLon, N'MountainPro Alpine', N'Boot núi cao'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Trail Boot', N'Boot đường mòn'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Explorer', N'Boot khám phá'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Tactical', N'Boot chiến thuật'),
(@dmBoots, @thActive, @clDaLon, N'ActiveGear Rugged', N'Boot thô ráp'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Peak', N'Boot đỉnh núi'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Worker', N'Boot lao động'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Ranger', N'Boot tuần tra'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Trekker Pro', N'Boot chuyên nghiệp'),
(@dmBoots, @thSportMax, @clDaLon, N'SportMax Desert', N'Boot sa mạc'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Combat', N'Boot chiến đấu'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Winter', N'Boot mùa đông'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Steel Toe', N'Boot bảo hộ'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Military', N'Boot quân đội'),
(@dmBoots, @thMountainPro, @clDaLon, N'MountainPro Himalaya', N'Boot núi cao cấp'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Engineer', N'Boot kỹ sư'),
(@dmBoots, @thActive, @clDa, N'ActiveGear Commando', N'Boot đặc nhiệm'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Everest', N'Boot siêu cao'),
(@dmBoots, @thSportMax, @clDa, N'SportMax Logger', N'Boot khai thác gỗ'),
(@dmBoots, @thActive, @clDaLon, N'ActiveGear Pathfinder', N'Boot tìm đường'),
(@dmBoots, @thMountainPro, @clDa, N'MountainPro Ultimate', N'Boot tối thượng');

-- SNEAKERS (28 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmSneakers, @thNiceStep, @clVai, N'NiceStep Sneaker V', N'Sneaker vải linh hoạt'),
(@dmSneakers, @thNiceStep, @clSynthetic, N'NiceStep Urban Sneaker', N'Sneaker đô thị'),
(@dmSneakers, @thTrendy, @clVai, N'TrendyShoes Street', N'Sneaker đường phố'),
(@dmSneakers, @thTrendy, @clSynthetic, N'TrendyShoes Fashion', N'Sneaker thời trang'),
(@dmSneakers, @thSportMax, @clVai, N'SportMax Classic', N'Sneaker cổ điển'),
(@dmSneakers, @thSportMax, @clSynthetic, N'SportMax Sport', N'Sneaker thể thao'),
(@dmSneakers, @thNiceStep, @clVai, N'NiceStep Retro', N'Sneaker retro'),
(@dmSneakers, @thTrendy, @clSynthetic, N'TrendyShoes High Top', N'Sneaker cổ cao'),
(@dmSneakers, @thSportMax, @clVai, N'SportMax Low Profile', N'Sneaker cổ thấp'),
(@dmSneakers, @thNiceStep, @clSynthetic, N'NiceStep Lifestyle', N'Sneaker phong cách'),
(@dmSneakers, @thTrendy, @clVai, N'TrendyShoes Vintage', N'Sneaker cổ điển'),
(@dmSneakers, @thSportMax, @clSynthetic, N'SportMax Contemporary', N'Sneaker đương đại'),
(@dmSneakers, @thNiceStep, @clVai, N'NiceStep Comfort Sneaker', N'Sneaker êm ái'),
(@dmSneakers, @thTrendy, @clSynthetic, N'TrendyShoes Limited', N'Sneaker giới hạn'),
(@dmSneakers, @thSportMax, @clVai, N'SportMax Essential', N'Sneaker thiết yếu'),
(@dmSneakers, @thNiceStep, @clSynthetic, N'NiceStep Pro Sneaker', N'Sneaker chuyên nghiệp'),
(@dmSneakers, @thTrendy, @clVai, N'TrendyShoes Designer', N'Sneaker thiết kế'),
(@dmSneakers, @thSportMax, @clSynthetic, N'SportMax Premium', N'Sneaker cao cấp'),
(@dmSneakers, @thNiceStep, @clVai, N'NiceStep Flex Sneaker', N'Sneaker linh hoạt'),
(@dmSneakers, @thTrendy, @clSynthetic, N'TrendyShoes Elite', N'Sneaker ưu tú'),
(@dmSneakers, @thSportMax, @clVai, N'SportMax Original', N'Sneaker nguyên bản'),
(@dmSneakers, @thNiceStep, @clSynthetic, N'NiceStep Modern', N'Sneaker hiện đại'),
(@dmSneakers, @thTrendy, @clVai, N'TrendyShoes Signature', N'Sneaker đặc trưng'),
(@dmSneakers, @thSportMax, @clSynthetic, N'SportMax Heritage', N'Sneaker di sản'),
(@dmSneakers, @thNiceStep, @clVai, N'NiceStep Innovation', N'Sneaker đổi mới'),
(@dmSneakers, @thTrendy, @clSynthetic, N'TrendyShoes Exclusive', N'Sneaker độc quyền'),
(@dmSneakers, @thSportMax, @clVai, N'SportMax Icon', N'Sneaker biểu tượng'),
(@dmSneakers, @thNiceStep, @clSynthetic, N'NiceStep Legend', N'Sneaker huyền thoại');

-- SANDALS (24 sản phẩm)
INSERT INTO dbo.SanPham (DanhMucId, ThuongHieuId, ChatLieuId, Ten, MoTa) VALUES
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Sandal Pro', N'Sandal nhẹ, bám tốt'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Beach', N'Sandal đi biển'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Summer', N'Sandal mùa hè'),
(@dmSandals, @thUrbanWalk, @clDa, N'UrbanWalk Leather Sandal', N'Sandal da cao cấp'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Sport Sandal', N'Sandal thể thao'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Slide', N'Dép lê thoải mái'),
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Outdoor', N'Sandal ngoài trời'),
(@dmSandals, @thEasyStep, @clDa, N'EasyStep Premium Sandal', N'Sandal cao cấp'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Flip Flop', N'Dép xỏ ngón'),
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Active', N'Sandal năng động'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Casual Sandal', N'Sandal thường ngày'),
(@dmSandals, @thComfortZone, @clDa, N'ComfortZone Luxury', N'Sandal sang trọng'),
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Trek Sandal', N'Sandal đi bộ'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Water', N'Sandal đi nước'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Pool', N'Sandal hồ bơi'),
(@dmSandals, @thUrbanWalk, @clDa, N'UrbanWalk Classic Sandal', N'Sandal cổ điển'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Adventure', N'Sandal phiêu lưu'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Breeze', N'Sandal thoáng mát'),
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Trail', N'Sandal đường mòn'),
(@dmSandals, @thEasyStep, @clDa, N'EasyStep Comfort', N'Sandal êm ái'),
(@dmSandals, @thComfortZone, @clPU, N'ComfortZone Yoga', N'Sandal yoga'),
(@dmSandals, @thUrbanWalk, @clPU, N'UrbanWalk Freedom', N'Sandal tự do'),
(@dmSandals, @thEasyStep, @clPU, N'EasyStep Relax', N'Sandal thư giãn'),
(@dmSandals, @thComfortZone, @clDa, N'ComfortZone Elite Sandal', N'Sandal cao cấp');

GO

DECLARE @mauDen INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Đen');
DECLARE @mauTrang INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Trắng');
DECLARE @mauXam INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Xám');
DECLARE @mauNavy INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Xanh navy');
DECLARE @mauNau INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Nâu');
DECLARE @mauDo INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Đỏ');
DECLARE @mauXanhDuong INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Xanh dương');
DECLARE @mauXanhLa INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Xanh lá');
DECLARE @mauHong INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Hồng');
DECLARE @mauCam INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Cam');
DECLARE @mauVang INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Vàng');
DECLARE @mauTim INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Tím');
DECLARE @mauBe INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Be');
DECLARE @mauXamDam INT = (SELECT MauSacId FROM dbo.MauSac WHERE Ten=N'Xám đậm');

-- Lấy ID kích thước
DECLARE @sz36 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'36');
DECLARE @sz37 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'37');
DECLARE @sz38 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'38');
DECLARE @sz39 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'39');
DECLARE @sz40 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'40');
DECLARE @sz41 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'41');
DECLARE @sz42 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'42');
DECLARE @sz43 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'43');
DECLARE @sz44 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'44');
DECLARE @sz45 INT = (SELECT KichThuocId FROM dbo.KichThuoc WHERE Ten=N'45');

PRINT N'========================================';
PRINT N'BẮT ĐẦU THÊM 200 SẢN PHẨM CHI TIẾT';
PRINT N'========================================';

-- ========== GIÀY CHẠY BỘ (40 variants) ==========
PRINT N'Đang thêm variants cho Giày chạy bộ...';

-- FastRun Speed Pro (5 variants)
DECLARE @sp1 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'FastRun Speed Pro');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp1, @mauDen, @sz40, N'FRSP-BLK-40', N'8801001', 1200000, 1400000, 25, N'/img/frsp_blk_40.jpg'),
(@sp1, @mauDen, @sz41, N'FRSP-BLK-41', N'8801002', 1200000, 1400000, 30, N'/img/frsp_blk_41.jpg'),
(@sp1, @mauTrang, @sz40, N'FRSP-WHT-40', N'8801003', 1200000, 1400000, 20, N'/img/frsp_wht_40.jpg'),
(@sp1, @mauDo, @sz42, N'FRSP-RED-42', N'8801004', 1250000, 1450000, 15, N'/img/frsp_red_42.jpg'),
(@sp1, @mauXanhDuong, @sz41, N'FRSP-BLU-41', N'8801005', 1200000, 1400000, 22, N'/img/frsp_blu_41.jpg');

-- FastRun Marathon Elite (5 variants)
DECLARE @sp2 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'FastRun Marathon Elite');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp2, @mauDen, @sz40, N'FRME-BLK-40', N'8802001', 1500000, 1700000, 18, N'/img/frme_blk_40.jpg'),
(@sp2, @mauTrang, @sz41, N'FRME-WHT-41', N'8802002', 1500000, 1700000, 20, N'/img/frme_wht_41.jpg'),
(@sp2, @mauXam, @sz42, N'FRME-GRY-42', N'8802003', 1500000, 1700000, 15, N'/img/frme_gry_42.jpg'),
(@sp2, @mauCam, @sz40, N'FRME-ORG-40', N'8802004', 1550000, 1750000, 12, N'/img/frme_org_40.jpg'),
(@sp2, @mauXanhLa, @sz41, N'FRME-GRN-41', N'8802005', 1550000, 1750000, 10, N'/img/frme_grn_41.jpg');

-- NiceStep Runner X (5 variants)
DECLARE @sp3 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'NiceStep Runner X');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp3, @mauDen, @sz39, N'NSRX-BLK-39', N'8803001', 990000, 1090000, 30, N'/img/nsrx_blk_39.jpg'),
(@sp3, @mauDen, @sz40, N'NSRX-BLK-40', N'8803002', 990000, 1090000, 28, N'/img/nsrx_blk_40.jpg'),
(@sp3, @mauTrang, @sz39, N'NSRX-WHT-39', N'8803003', 990000, 1090000, 25, N'/img/nsrx_wht_39.jpg'),
(@sp3, @mauTrang, @sz41, N'NSRX-WHT-41', N'8803004', 990000, 1090000, 22, N'/img/nsrx_wht_41.jpg'),
(@sp3, @mauNavy, @sz40, N'NSRX-NVY-40', N'8803005', 1020000, 1120000, 18, N'/img/nsrx_nvy_40.jpg');

-- NiceStep Sprint Max (5 variants)
DECLARE @sp4 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'NiceStep Sprint Max');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp4, @mauDen, @sz40, N'NSSM-BLK-40', N'8804001', 1100000, 1250000, 20, N'/img/nssm_blk_40.jpg'),
(@sp4, @mauTrang, @sz41, N'NSSM-WHT-41', N'8804002', 1100000, 1250000, 18, N'/img/nssm_wht_41.jpg'),
(@sp4, @mauDo, @sz40, N'NSSM-RED-40', N'8804003', 1150000, 1300000, 15, N'/img/nssm_red_40.jpg'),
(@sp4, @mauXanhDuong, @sz42, N'NSSM-BLU-42', N'8804004', 1100000, 1250000, 16, N'/img/nssm_blu_42.jpg'),
(@sp4, @mauXam, @sz39, N'NSSM-GRY-39', N'8804005', 1100000, 1250000, 14, N'/img/nssm_gry_39.jpg');

-- ActiveGear Velocity (5 variants)
DECLARE @sp5 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ActiveGear Velocity');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp5, @mauDen, @sz40, N'AGVC-BLK-40', N'8805001', 1080000, 1200000, 22, N'/img/agvc_blk_40.jpg'),
(@sp5, @mauTrang, @sz41, N'AGVC-WHT-41', N'8805002', 1080000, 1200000, 20, N'/img/agvc_wht_41.jpg'),
(@sp5, @mauXanhLa, @sz40, N'AGVC-GRN-40', N'8805003', 1100000, 1220000, 15, N'/img/agvc_grn_40.jpg'),
(@sp5, @mauCam, @sz42, N'AGVC-ORG-42', N'8805004', 1100000, 1220000, 12, N'/img/agvc_org_42.jpg'),
(@sp5, @mauXam, @sz39, N'AGVC-GRY-39', N'8805005', 1080000, 1200000, 18, N'/img/agvc_gry_39.jpg');

-- ActiveGear Pace Runner (5 variants)
DECLARE @sp6 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ActiveGear Pace Runner');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp6, @mauDen, @sz40, N'AGPR-BLK-40', N'8806001', 950000, 1100000, 25, N'/img/agpr_blk_40.jpg'),
(@sp6, @mauTrang, @sz41, N'AGPR-WHT-41', N'8806002', 950000, 1100000, 23, N'/img/agpr_wht_41.jpg'),
(@sp6, @mauNavy, @sz40, N'AGPR-NVY-40', N'8806003', 980000, 1130000, 18, N'/img/agpr_nvy_40.jpg'),
(@sp6, @mauXam, @sz42, N'AGPR-GRY-42', N'8806004', 950000, 1100000, 20, N'/img/agpr_gry_42.jpg'),
(@sp6, @mauXanhDuong, @sz39, N'AGPR-BLU-39', N'8806005', 980000, 1130000, 15, N'/img/agpr_blu_39.jpg');

-- SportMax Trail Run (5 variants)
DECLARE @sp7 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'SportMax Trail Run');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp7, @mauDen, @sz41, N'SMTR-BLK-41', N'8807001', 1300000, 1500000, 18, N'/img/smtr_blk_41.jpg'),
(@sp7, @mauXam, @sz42, N'SMTR-GRY-42', N'8807002', 1300000, 1500000, 16, N'/img/smtr_gry_42.jpg'),
(@sp7, @mauNau, @sz40, N'SMTR-BRN-40', N'8807003', 1320000, 1520000, 14, N'/img/smtr_brn_40.jpg'),
(@sp7, @mauXanhLa, @sz41, N'SMTR-GRN-41', N'8807004', 1350000, 1550000, 12, N'/img/smtr_grn_41.jpg'),
(@sp7, @mauCam, @sz43, N'SMTR-ORG-43', N'8807005', 1350000, 1550000, 10, N'/img/smtr_org_43.jpg');

-- SportMax Road Racer (5 variants)
DECLARE @sp8 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'SportMax Road Racer');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp8, @mauDen, @sz40, N'SMRR-BLK-40', N'8808001', 1150000, 1300000, 20, N'/img/smrr_blk_40.jpg'),
(@sp8, @mauTrang, @sz41, N'SMRR-WHT-41', N'8808002', 1150000, 1300000, 22, N'/img/smrr_wht_41.jpg'),
(@sp8, @mauDo, @sz40, N'SMRR-RED-40', N'8808003', 1200000, 1350000, 15, N'/img/smrr_red_40.jpg'),
(@sp8, @mauXanhDuong, @sz42, N'SMRR-BLU-42', N'8808004', 1150000, 1300000, 18, N'/img/smrr_blu_42.jpg'),
(@sp8, @mauVang, @sz41, N'SMRR-YLW-41', N'8808005', 1200000, 1350000, 12, N'/img/smrr_ylw_41.jpg');

PRINT N'✓ Đã thêm 40 variants cho Giày chạy bộ';

-- ========== GIÀY CASUAL (40 variants) ==========
PRINT N'Đang thêm variants cho Giày casual...';

-- UrbanWalk Classic (5 variants)
DECLARE @sp9 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Classic');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp9, @mauDen, @sz39, N'UWCL-BLK-39', N'8809001', 850000, 950000, 35, N'/img/uwcl_blk_39.jpg'),
(@sp9, @mauNau, @sz40, N'UWCL-BRN-40', N'8809002', 850000, 950000, 32, N'/img/uwcl_brn_40.jpg'),
(@sp9, @mauXam, @sz41, N'UWCL-GRY-41', N'8809003', 850000, 950000, 28, N'/img/uwcl_gry_41.jpg'),
(@sp9, @mauTrang, @sz39, N'UWCL-WHT-39', N'8809004', 850000, 950000, 25, N'/img/uwcl_wht_39.jpg'),
(@sp9, @mauBe, @sz40, N'UWCL-BGE-40', N'8809005', 880000, 980000, 20, N'/img/uwcl_bge_40.jpg');

-- UrbanWalk Street Style (5 variants)
DECLARE @sp10 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Street Style');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp10, @mauDen, @sz40, N'UWSS-BLK-40', N'8810001', 920000, 1050000, 30, N'/img/uwss_blk_40.jpg'),
(@sp10, @mauTrang, @sz41, N'UWSS-WHT-41', N'8810002', 920000, 1050000, 28, N'/img/uwss_wht_41.jpg'),
(@sp10, @mauXam, @sz39, N'UWSS-GRY-39', N'8810003', 920000, 1050000, 25, N'/img/uwss_gry_39.jpg'),
(@sp10, @mauNavy, @sz40, N'UWSS-NVY-40', N'8810004', 950000, 1080000, 22, N'/img/uwss_nvy_40.jpg'),
(@sp10, @mauDo, @sz41, N'UWSS-RED-41', N'8810005', 950000, 1080000, 18, N'/img/uwss_red_41.jpg');

-- EasyStep Casual Pro (5 variants)
DECLARE @sp11 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'EasyStep Casual Pro');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp11, @mauDen, @sz40, N'ESCP-BLK-40', N'8811001', 780000, 880000, 35, N'/img/escp_blk_40.jpg'),
(@sp11, @mauTrang, @sz41, N'ESCP-WHT-41', N'8811002', 780000, 880000, 32, N'/img/escp_wht_41.jpg'),
(@sp11, @mauXam, @sz39, N'ESCP-GRY-39', N'8811003', 780000, 880000, 28, N'/img/escp_gry_39.jpg'),
(@sp11, @mauNau, @sz40, N'ESCP-BRN-40', N'8811004', 800000, 900000, 25, N'/img/escp_brn_40.jpg'),
(@sp11, @mauBe, @sz41, N'ESCP-BGE-41', N'8811005', 800000, 900000, 22, N'/img/escp_bge_41.jpg');

-- EasyStep Comfort Walk (5 variants)
DECLARE @sp12 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'EasyStep Comfort Walk');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp12, @mauDen, @sz40, N'ESCW-BLK-40', N'8812001', 720000, 820000, 40, N'/img/escw_blk_40.jpg'),
(@sp12, @mauTrang, @sz39, N'ESCW-WHT-39', N'8812002', 720000, 820000, 38, N'/img/escw_wht_39.jpg'),
(@sp12, @mauXam, @sz41, N'ESCW-GRY-41', N'8812003', 720000, 820000, 35, N'/img/escw_gry_41.jpg'),
(@sp12, @mauNavy, @sz40, N'ESCW-NVY-40', N'8812004', 750000, 850000, 30, N'/img/escw_nvy_40.jpg'),
(@sp12, @mauNau, @sz42, N'ESCW-BRN-42', N'8812005', 750000, 850000, 28, N'/img/escw_brn_42.jpg');

-- TrendyShoes Urban (5 variants)
DECLARE @sp13 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'TrendyShoes Urban');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp13, @mauDen, @sz40, N'TSUB-BLK-40', N'8813001', 980000, 1100000, 25, N'/img/tsub_blk_40.jpg'),
(@sp13, @mauTrang, @sz41, N'TSUB-WHT-41', N'8813002', 980000, 1100000, 22, N'/img/tsub_wht_41.jpg'),
(@sp13, @mauXam, @sz39, N'TSUB-GRY-39', N'8813003', 980000, 1100000, 20, N'/img/tsub_gry_39.jpg'),
(@sp13, @mauBe, @sz40, N'TSUB-BGE-40', N'8813004', 1000000, 1120000, 18, N'/img/tsub_bge_40.jpg'),
(@sp13, @mauNavy, @sz41, N'TSUB-NVY-41', N'8813005', 1000000, 1120000, 15, N'/img/tsub_nvy_41.jpg');

-- TrendyShoes Loafer (5 variants)
DECLARE @sp14 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'TrendyShoes Loafer');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp14, @mauDen, @sz40, N'TSLF-BLK-40', N'8814001', 1050000, 1200000, 20, N'/img/tslf_blk_40.jpg'),
(@sp14, @mauNau, @sz41, N'TSLF-BRN-41', N'8814002', 1050000, 1200000, 18, N'/img/tslf_brn_41.jpg'),
(@sp14, @mauBe, @sz40, N'TSLF-BGE-40', N'8814003', 1080000, 1230000, 15, N'/img/tslf_bge_40.jpg'),
(@sp14, @mauXam, @sz42, N'TSLF-GRY-42', N'8814004', 1050000, 1200000, 16, N'/img/tslf_gry_42.jpg'),
(@sp14, @mauNavy, @sz39, N'TSLF-NVY-39', N'8814005', 1080000, 1230000, 12, N'/img/tslf_nvy_39.jpg');

-- ComfortZone Relax (5 variants)
DECLARE @sp15 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ComfortZone Relax');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp15, @mauDen, @sz40, N'CZRX-BLK-40', N'8815001', 820000, 920000, 30, N'/img/czrx_blk_40.jpg'),
(@sp15, @mauTrang, @sz41, N'CZRX-WHT-41', N'8815002', 820000, 920000, 28, N'/img/czrx_wht_41.jpg'),
(@sp15, @mauXam, @sz39, N'CZRX-GRY-39', N'8815003', 820000, 920000, 25, N'/img/czrx_gry_39.jpg'),
(@sp15, @mauBe, @sz40, N'CZRX-BGE-40', N'8815004', 850000, 950000, 22, N'/img/czrx_bge_40.jpg'),
(@sp15, @mauNau, @sz41, N'CZRX-BRN-41', N'8815005', 850000, 950000, 20, N'/img/czrx_brn_41.jpg');

-- UrbanWalk Premium (5 variants)
DECLARE @sp16 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Premium');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp16, @mauDen, @sz40, N'UWPM-BLK-40', N'8816001', 1200000, 1400000, 18, N'/img/uwpm_blk_40.jpg'),
(@sp16, @mauNau, @sz41, N'UWPM-BRN-41', N'8816002', 1200000, 1400000, 16, N'/img/uwpm_brn_41.jpg'),
(@sp16, @mauBe, @sz40, N'UWPM-BGE-40', N'8816003', 1250000, 1450000, 14, N'/img/uwpm_bge_40.jpg'),
(@sp16, @mauXamDam, @sz42, N'UWPM-DGY-42', N'8816004', 1200000, 1400000, 12, N'/img/uwpm_dgy_42.jpg'),
(@sp16, @mauNavy, @sz39, N'UWPM-NVY-39', N'8816005', 1250000, 1450000, 10, N'/img/uwpm_nvy_39.jpg');

PRINT N'✓ Đã thêm 40 variants cho Giày casual';

-- ========== GIÀY CÔNG SỞ (40 variants) ==========
PRINT N'Đang thêm variants cho Giày công sở...';

-- ClassicFeet Derby (5 variants)
DECLARE @sp17 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ClassicFeet Derby');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp17, @mauDen, @sz40, N'CFDB-BLK-40', N'8817001', 1500000, 1700000, 20, N'/img/cfdb_blk_40.jpg'),
(@sp17, @mauDen, @sz41, N'CFDB-BLK-41', N'8817002', 1500000, 1700000, 22, N'/img/cfdb_blk_41.jpg'),
(@sp17, @mauNau, @sz40, N'CFDB-BRN-40', N'8817003', 1550000, 1750000, 15, N'/img/cfdb_brn_40.jpg'),
(@sp17, @mauNavy, @sz42, N'CFDB-NVY-42', N'8817004', 1550000, 1750000, 12, N'/img/cfdb_nvy_42.jpg'),
(@sp17, @mauNau, @sz41, N'CFDB-BRN-41', N'8817005', 1550000, 1750000, 14, N'/img/cfdb_brn_41.jpg');

-- ClassicFeet Oxford (5 variants)
DECLARE @sp18 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ClassicFeet Oxford');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp18, @mauDen, @sz40, N'CFOX-BLK-40', N'8818001', 1600000, 1850000, 18, N'/img/cfox_blk_40.jpg'),
(@sp18, @mauDen, @sz41, N'CFOX-BLK-41', N'8818002', 1600000, 1850000, 20, N'/img/cfox_blk_41.jpg'),
(@sp18, @mauNau, @sz40, N'CFOX-BRN-40', N'8818003', 1650000, 1900000, 15, N'/img/cfox_brn_40.jpg'),
(@sp18, @mauDen, @sz42, N'CFOX-BLK-42', N'8818004', 1600000, 1850000, 16, N'/img/cfox_blk_42.jpg'),
(@sp18, @mauNavy, @sz41, N'CFOX-NVY-41', N'8818005', 1650000, 1900000, 12, N'/img/cfox_nvy_41.jpg');

-- ClassicFeet Brogue (5 variants)
DECLARE @sp19 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ClassicFeet Brogue');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp19, @mauDen, @sz40, N'CFBR-BLK-40', N'8819001', 1700000, 1950000, 15, N'/img/cfbr_blk_40.jpg'),
(@sp19, @mauNau, @sz41, N'CFBR-BRN-41', N'8819002', 1700000, 1950000, 14, N'/img/cfbr_brn_41.jpg'),
(@sp19, @mauDen, @sz42, N'CFBR-BLK-42', N'8819003', 1700000, 1950000, 12, N'/img/cfbr_blk_42.jpg'),
(@sp19, @mauNau, @sz40, N'CFBR-BRN-40', N'8819004', 1750000, 2000000, 10, N'/img/cfbr_brn_40.jpg'),
(@sp19, @mauNavy, @sz41, N'CFBR-NVY-41', N'8819005', 1750000, 2000000, 8, N'/img/cfbr_nvy_41.jpg');

-- UrbanWalk Office Pro (5 variants)
DECLARE @sp20 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Office Pro');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp20, @mauDen, @sz40, N'UWOP-BLK-40', N'8820001', 1350000, 1550000, 22, N'/img/uwop_blk_40.jpg'),
(@sp20, @mauDen, @sz41, N'UWOP-BLK-41', N'8820002', 1350000, 1550000, 20, N'/img/uwop_blk_41.jpg'),
(@sp20, @mauNau, @sz40, N'UWOP-BRN-40', N'8820003', 1400000, 1600000, 16, N'/img/uwop_brn_40.jpg'),
(@sp20, @mauDen, @sz42, N'UWOP-BLK-42', N'8820004', 1350000, 1550000, 18, N'/img/uwop_blk_42.jpg'),
(@sp20, @mauNavy, @sz41, N'UWOP-NVY-41', N'8820005', 1400000, 1600000, 14, N'/img/uwop_nvy_41.jpg');

-- UrbanWalk Business (5 variants)
DECLARE @sp21 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Business');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp21, @mauDen, @sz40, N'UWBS-BLK-40', N'8821001', 1450000, 1650000, 20, N'/img/uwbs_blk_40.jpg'),
(@sp21, @mauDen, @sz41, N'UWBS-BLK-41', N'8821002', 1450000, 1650000, 18, N'/img/uwbs_blk_41.jpg'),
(@sp21, @mauNau, @sz40, N'UWBS-BRN-40', N'8821003', 1500000, 1700000, 15, N'/img/uwbs_brn_40.jpg'),
(@sp21, @mauNavy, @sz42, N'UWBS-NVY-42', N'8821004', 1500000, 1700000, 12, N'/img/uwbs_nvy_42.jpg'),
(@sp21, @mauDen, @sz42, N'UWBS-BLK-42', N'8821005', 1450000, 1650000, 16, N'/img/uwbs_blk_42.jpg');

-- ClassicFeet Formal (5 variants)
DECLARE @sp22 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ClassicFeet Formal');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp22, @mauDen, @sz40, N'CFFM-BLK-40', N'8822001', 1800000, 2100000, 15, N'/img/cffm_blk_40.jpg'),
(@sp22, @mauDen, @sz41, N'CFFM-BLK-41', N'8822002', 1800000, 2100000, 14, N'/img/cffm_blk_41.jpg'),
(@sp22, @mauNau, @sz40, N'CFFM-BRN-40', N'8822003', 1850000, 2150000, 12, N'/img/cffm_brn_40.jpg'),
(@sp22, @mauDen, @sz42, N'CFFM-BLK-42', N'8822004', 1800000, 2100000, 13, N'/img/cffm_blk_42.jpg'),
(@sp22, @mauNavy, @sz41, N'CFFM-NVY-41', N'8822005', 1850000, 2150000, 10, N'/img/cffm_nvy_41.jpg');

-- ClassicFeet Suede Office (5 variants)
DECLARE @sp23 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ClassicFeet Suede Office');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp23, @mauNau, @sz40, N'CFSO-BRN-40', N'8823001', 1900000, 2200000, 12, N'/img/cfso_brn_40.jpg'),
(@sp23, @mauXam, @sz41, N'CFSO-GRY-41', N'8823002', 1900000, 2200000, 10, N'/img/cfso_gry_41.jpg'),
(@sp23, @mauBe, @sz40, N'CFSO-BGE-40', N'8823003', 1950000, 2250000, 8, N'/img/cfso_bge_40.jpg'),
(@sp23, @mauNavy, @sz42, N'CFSO-NVY-42', N'8823004', 1900000, 2200000, 9, N'/img/cfso_nvy_42.jpg'),
(@sp23, @mauNau, @sz41, N'CFSO-BRN-41', N'8823005', 1900000, 2200000, 11, N'/img/cfso_brn_41.jpg');

PRINT N'✓ Đã thêm 40 variants cho Giày công sở';

-- ========== BOOTS (40 variants) ==========
PRINT N'Đang thêm variants cho Boots...';

-- MountainPro Trek (5 variants)
DECLARE @sp24 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'MountainPro Trek');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp24, @mauDen, @sz41, N'MPTK-BLK-41', N'8824001', 1800000, 2100000, 18, N'/img/mptk_blk_41.jpg'),
(@sp24, @mauDen, @sz42, N'MPTK-BLK-42', N'8824002', 1800000, 2100000, 16, N'/img/mptk_blk_42.jpg'),
(@sp24, @mauNau, @sz41, N'MPTK-BRN-41', N'8824003', 1850000, 2150000, 14, N'/img/mptk_brn_41.jpg'),
(@sp24, @mauXam, @sz43, N'MPTK-GRY-43', N'8824004', 1850000, 2150000, 12, N'/img/mptk_gry_43.jpg'),
(@sp24, @mauDen, @sz43, N'MPTK-BLK-43', N'8824005', 1800000, 2100000, 15, N'/img/mptk_blk_43.jpg');

-- MountainPro Summit (5 variants)
DECLARE @sp25 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'MountainPro Summit');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp25, @mauDen, @sz41, N'MPSM-BLK-41', N'8825001', 2200000, 2600000, 12, N'/img/mpsm_blk_41.jpg'),
(@sp25, @mauDen, @sz42, N'MPSM-BLK-42', N'8825002', 2200000, 2600000, 10, N'/img/mpsm_blk_42.jpg'),
(@sp25, @mauXam, @sz41, N'MPSM-GRY-41', N'8825003', 2250000, 2650000, 8, N'/img/mpsm_gry_41.jpg'),
(@sp25, @mauNau, @sz43, N'MPSM-BRN-43', N'8825004', 2250000, 2650000, 7, N'/img/mpsm_brn_43.jpg'),
(@sp25, @mauDen, @sz43, N'MPSM-BLK-43', N'8825005', 2200000, 2600000, 9, N'/img/mpsm_blk_43.jpg');

-- MountainPro Adventure (5 variants)
DECLARE @sp26 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'MountainPro Adventure');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp26, @mauDen, @sz41, N'MPAD-BLK-41', N'8826001', 1950000, 2300000, 15, N'/img/mpad_blk_41.jpg'),
(@sp26, @mauNau, @sz42, N'MPAD-BRN-42', N'8826002', 1950000, 2300000, 13, N'/img/mpad_brn_42.jpg'),
(@sp26, @mauXam, @sz41, N'MPAD-GRY-41', N'8826003', 2000000, 2350000, 11, N'/img/mpad_gry_41.jpg'),
(@sp26, @mauDen, @sz43, N'MPAD-BLK-43', N'8826004', 1950000, 2300000, 12, N'/img/mpad_blk_43.jpg'),
(@sp26, @mauNau, @sz41, N'MPAD-BRN-41', N'8826005', 2000000, 2350000, 10, N'/img/mpad_brn_41.jpg');

-- ActiveGear Hiker (5 variants)
DECLARE @sp27 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ActiveGear Hiker');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp27, @mauDen, @sz41, N'AGHK-BLK-41', N'8827001', 1650000, 1900000, 16, N'/img/aghk_blk_41.jpg'),
(@sp27, @mauDen, @sz42, N'AGHK-BLK-42', N'8827002', 1650000, 1900000, 14, N'/img/aghk_blk_42.jpg'),
(@sp27, @mauNau, @sz41, N'AGHK-BRN-41', N'8827003', 1700000, 1950000, 12, N'/img/aghk_brn_41.jpg'),
(@sp27, @mauXam, @sz43, N'AGHK-GRY-43', N'8827004', 1700000, 1950000, 10, N'/img/aghk_gry_43.jpg'),
(@sp27, @mauDen, @sz43, N'AGHK-BLK-43', N'8827005', 1650000, 1900000, 13, N'/img/aghk_blk_43.jpg');

-- ActiveGear Terrain (5 variants)
DECLARE @sp28 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ActiveGear Terrain');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp28, @mauDen, @sz41, N'AGTR-BLK-41', N'8828001', 1750000, 2000000, 14, N'/img/agtr_blk_41.jpg'),
(@sp28, @mauDen, @sz42, N'AGTR-BLK-42', N'8828002', 1750000, 2000000, 12, N'/img/agtr_blk_42.jpg'),
(@sp28, @mauNau, @sz41, N'AGTR-BRN-41', N'8828003', 1800000, 2050000, 10, N'/img/agtr_brn_41.jpg'),
(@sp28, @mauXam, @sz43, N'AGTR-GRY-43', N'8828004', 1800000, 2050000, 8, N'/img/agtr_gry_43.jpg'),
(@sp28, @mauXanhLa, @sz42, N'AGTR-GRN-42', N'8828005', 1850000, 2100000, 7, N'/img/agtr_grn_42.jpg');

-- MountainPro Expedition (5 variants)
DECLARE @sp29 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'MountainPro Expedition');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp29, @mauDen, @sz41, N'MPEX-BLK-41', N'8829001', 2400000, 2800000, 10, N'/img/mpex_blk_41.jpg'),
(@sp29, @mauDen, @sz42, N'MPEX-BLK-42', N'8829002', 2400000, 2800000, 9, N'/img/mpex_blk_42.jpg'),
(@sp29, @mauXam, @sz41, N'MPEX-GRY-41', N'8829003', 2450000, 2850000, 7, N'/img/mpex_gry_41.jpg'),
(@sp29, @mauNau, @sz43, N'MPEX-BRN-43', N'8829004', 2450000, 2850000, 6, N'/img/mpex_brn_43.jpg'),
(@sp29, @mauDen, @sz43, N'MPEX-BLK-43', N'8829005', 2400000, 2800000, 8, N'/img/mpex_blk_43.jpg');

-- SportMax Outdoor (5 variants)
DECLARE @sp30 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'SportMax Outdoor');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp30, @mauDen, @sz41, N'SMOD-BLK-41', N'8830001', 1550000, 1800000, 16, N'/img/smod_blk_41.jpg'),
(@sp30, @mauDen, @sz42, N'SMOD-BLK-42', N'8830002', 1550000, 1800000, 14, N'/img/smod_blk_42.jpg'),
(@sp30, @mauNau, @sz41, N'SMOD-BRN-41', N'8830003', 1600000, 1850000, 12, N'/img/smod_brn_41.jpg'),
(@sp30, @mauXam, @sz43, N'SMOD-GRY-43', N'8830004', 1600000, 1850000, 10, N'/img/smod_gry_43.jpg'),
(@sp30, @mauXanhLa, @sz42, N'SMOD-GRN-42', N'8830005', 1650000, 1900000, 9, N'/img/smod_grn_42.jpg');

-- MountainPro Alpine (5 variants)
DECLARE @sp31 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'MountainPro Alpine');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp31, @mauDen, @sz41, N'MPAL-BLK-41', N'8831001', 2100000, 2500000, 11, N'/img/mpal_blk_41.jpg'),
(@sp31, @mauDen, @sz42, N'MPAL-BLK-42', N'8831002', 2100000, 2500000, 10, N'/img/mpal_blk_42.jpg'),
(@sp31, @mauXam, @sz41, N'MPAL-GRY-41', N'8831003', 2150000, 2550000, 8, N'/img/mpal_gry_41.jpg'),
(@sp31, @mauNau, @sz43, N'MPAL-BRN-43', N'8831004', 2150000, 2550000, 7, N'/img/mpal_brn_43.jpg'),
(@sp31, @mauDen, @sz44, N'MPAL-BLK-44', N'8831005', 2100000, 2500000, 6, N'/img/mpal_blk_44.jpg');

PRINT N'✓ Đã thêm 40 variants cho Boots';

-- ========== SNEAKERS (40 variants) ==========
PRINT N'Đang thêm variants cho Sneakers...';

-- NiceStep Sneaker V (5 variants)
DECLARE @sp32 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'NiceStep Sneaker V');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp32, @mauDen, @sz39, N'NSSV-BLK-39', N'8832001', 950000, 1050000, 30, N'/img/nssv_blk_39.jpg'),
(@sp32, @mauTrang, @sz40, N'NSSV-WHT-40', N'8832002', 950000, 1050000, 28, N'/img/nssv_wht_40.jpg'),
(@sp32, @mauXam, @sz39, N'NSSV-GRY-39', N'8832003', 950000, 1050000, 25, N'/img/nssv_gry_39.jpg'),
(@sp32, @mauNavy, @sz41, N'NSSV-NVY-41', N'8832004', 980000, 1080000, 22, N'/img/nssv_nvy_41.jpg'),
(@sp32, @mauDo, @sz40, N'NSSV-RED-40', N'8832005', 980000, 1080000, 20, N'/img/nssv_red_40.jpg');

-- NiceStep Urban Sneaker (5 variants)
DECLARE @sp33 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'NiceStep Urban Sneaker');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp33, @mauDen, @sz40, N'NSUS-BLK-40', N'8833001', 1080000, 1200000, 25, N'/img/nsus_blk_40.jpg'),
(@sp33, @mauTrang, @sz41, N'NSUS-WHT-41', N'8833002', 1080000, 1200000, 23, N'/img/nsus_wht_41.jpg'),
(@sp33, @mauXam, @sz39, N'NSUS-GRY-39', N'8833003', 1080000, 1200000, 20, N'/img/nsus_gry_39.jpg'),
(@sp33, @mauBe, @sz40, N'NSUS-BGE-40', N'8833004', 1100000, 1220000, 18, N'/img/nsus_bge_40.jpg'),
(@sp33, @mauNavy, @sz41, N'NSUS-NVY-41', N'8833005', 1100000, 1220000, 15, N'/img/nsus_nvy_41.jpg');

-- TrendyShoes Street (5 variants)
DECLARE @sp34 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'TrendyShoes Street');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp34, @mauDen, @sz40, N'TSST-BLK-40', N'8834001', 890000, 990000, 32, N'/img/tsst_blk_40.jpg'),
(@sp34, @mauTrang, @sz41, N'TSST-WHT-41', N'8834002', 890000, 990000, 30, N'/img/tsst_wht_41.jpg'),
(@sp34, @mauXam, @sz39, N'TSST-GRY-39', N'8834003', 890000, 990000, 28, N'/img/tsst_gry_39.jpg'),
(@sp34, @mauDo, @sz40, N'TSST-RED-40', N'8834004', 920000, 1020000, 25, N'/img/tsst_red_40.jpg'),
(@sp34, @mauXanhDuong, @sz41, N'TSST-BLU-41', N'8834005', 920000, 1020000, 22, N'/img/tsst_blu_41.jpg');

-- TrendyShoes Fashion (5 variants)
DECLARE @sp35 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'TrendyShoes Fashion');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp35, @mauDen, @sz40, N'TSFN-BLK-40', N'8835001', 1050000, 1180000, 20, N'/img/tsfn_blk_40.jpg'),
(@sp35, @mauTrang, @sz41, N'TSFN-WHT-41', N'8835002', 1050000, 1180000, 18, N'/img/tsfn_wht_41.jpg'),
(@sp35, @mauHong, @sz39, N'TSFN-PNK-39', N'8835003', 1080000, 1210000, 15, N'/img/tsfn_pnk_39.jpg'),
(@sp35, @mauTim, @sz40, N'TSFN-PPL-40', N'8835004', 1080000, 1210000, 13, N'/img/tsfn_ppl_40.jpg'),
(@sp35, @mauBe, @sz41, N'TSFN-BGE-41', N'8835005', 1050000, 1180000, 16, N'/img/tsfn_bge_41.jpg');

-- SportMax Classic (5 variants)
DECLARE @sp36 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'SportMax Classic');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp36, @mauDen, @sz40, N'SMCL-BLK-40', N'8836001', 820000, 920000, 35, N'/img/smcl_blk_40.jpg'),
(@sp36, @mauTrang, @sz41, N'SMCL-WHT-41', N'8836002', 820000, 920000, 32, N'/img/smcl_wht_41.jpg'),
(@sp36, @mauXam, @sz39, N'SMCL-GRY-39', N'8836003', 820000, 920000, 30, N'/img/smcl_gry_39.jpg'),
(@sp36, @mauNavy, @sz40, N'SMCL-NVY-40', N'8836004', 850000, 950000, 28, N'/img/smcl_nvy_40.jpg'),
(@sp36, @mauDo, @sz41, N'SMCL-RED-41', N'8836005', 850000, 950000, 25, N'/img/smcl_red_41.jpg');

-- SportMax Sport (5 variants)
DECLARE @sp37 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'SportMax Sport');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp37, @mauDen, @sz40, N'SMSP-BLK-40', N'8837001', 980000, 1100000, 28, N'/img/smsp_blk_40.jpg'),
(@sp37, @mauTrang, @sz41, N'SMSP-WHT-41', N'8837002', 980000, 1100000, 26, N'/img/smsp_wht_41.jpg'),
(@sp37, @mauXanhDuong, @sz40, N'SMSP-BLU-40', N'8837003', 1000000, 1120000, 22, N'/img/smsp_blu_40.jpg'),
(@sp37, @mauCam, @sz42, N'SMSP-ORG-42', N'8837004', 1000000, 1120000, 20, N'/img/smsp_org_42.jpg'),
(@sp37, @mauXam, @sz39, N'SMSP-GRY-39', N'8837005', 980000, 1100000, 24, N'/img/smsp_gry_39.jpg');

-- NiceStep Retro (5 variants)
DECLARE @sp38 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'NiceStep Retro');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp38, @mauDen, @sz40, N'NSRT-BLK-40', N'8838001', 1120000, 1250000, 18, N'/img/nsrt_blk_40.jpg'),
(@sp38, @mauTrang, @sz41, N'NSRT-WHT-41', N'8838002', 1120000, 1250000, 16, N'/img/nsrt_wht_41.jpg'),
(@sp38, @mauXam, @sz40, N'NSRT-GRY-40', N'8838003', 1120000, 1250000, 15, N'/img/nsrt_gry_40.jpg'),
(@sp38, @mauVang, @sz39, N'NSRT-YLW-39', N'8838004', 1150000, 1280000, 12, N'/img/nsrt_ylw_39.jpg'),
(@sp38, @mauXanhDuong, @sz41, N'NSRT-BLU-41', N'8838005', 1150000, 1280000, 14, N'/img/nsrt_blu_41.jpg');

-- TrendyShoes High Top (5 variants)
DECLARE @sp39 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'TrendyShoes High Top');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp39, @mauDen, @sz40, N'TSHT-BLK-40', N'8839001', 1180000, 1320000, 16, N'/img/tsht_blk_40.jpg'),
(@sp39, @mauTrang, @sz41, N'TSHT-WHT-41', N'8839002', 1180000, 1320000, 15, N'/img/tsht_wht_41.jpg'),
(@sp39, @mauDo, @sz40, N'TSHT-RED-40', N'8839003', 1200000, 1350000, 12, N'/img/tsht_red_40.jpg'),
(@sp39, @mauNavy, @sz42, N'TSHT-NVY-42', N'8839004', 1180000, 1320000, 13, N'/img/tsht_nvy_42.jpg'),
(@sp39, @mauXam, @sz39, N'TSHT-GRY-39', N'8839005', 1180000, 1320000, 14, N'/img/tsht_gry_39.jpg');

PRINT N'✓ Đã thêm 40 variants cho Sneakers';

-- ========== SANDALS (40 variants) ==========
PRINT N'Đang thêm variants cho Sandals...';

-- UrbanWalk Sandal Pro (5 variants)
DECLARE @sp40 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Sandal Pro');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp40, @mauDen, @sz39, N'UWSP-BLK-39', N'8840001', 620000, 690000, 45, N'/img/uwsp_blk_39.jpg'),
(@sp40, @mauDen, @sz40, N'UWSP-BLK-40', N'8840002', 620000, 690000, 42, N'/img/uwsp_blk_40.jpg'),
(@sp40, @mauNau, @sz39, N'UWSP-BRN-39', N'8840003', 650000, 720000, 38, N'/img/uwsp_brn_39.jpg'),
(@sp40, @mauXam, @sz41, N'UWSP-GRY-41', N'8840004', 620000, 690000, 40, N'/img/uwsp_gry_41.jpg'),
(@sp40, @mauNavy, @sz40, N'UWSP-NVY-40', N'8840005', 650000, 720000, 35, N'/img/uwsp_nvy_40.jpg');

-- EasyStep Beach (5 variants)
DECLARE @sp41 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'EasyStep Beach');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp41, @mauDen, @sz39, N'ESBH-BLK-39', N'8841001', 450000, 520000, 50, N'/img/esbh_blk_39.jpg'),
(@sp41, @mauXanhDuong, @sz40, N'ESBH-BLU-40', N'8841002', 450000, 520000, 48, N'/img/esbh_blu_40.jpg'),
(@sp41, @mauCam, @sz39, N'ESBH-ORG-39', N'8841003', 480000, 550000, 45, N'/img/esbh_org_39.jpg'),
(@sp41, @mauXam, @sz41, N'ESBH-GRY-41', N'8841004', 450000, 520000, 42, N'/img/esbh_gry_41.jpg'),
(@sp41, @mauNau, @sz40, N'ESBH-BRN-40', N'8841005', 480000, 550000, 40, N'/img/esbh_brn_40.jpg');

-- ComfortZone Summer (5 variants)
DECLARE @sp42 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ComfortZone Summer');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp42, @mauDen, @sz39, N'CZSM-BLK-39', N'8842001', 550000, 620000, 40, N'/img/czsm_blk_39.jpg'),
(@sp42, @mauTrang, @sz40, N'CZSM-WHT-40', N'8842002', 550000, 620000, 38, N'/img/czsm_wht_40.jpg'),
(@sp42, @mauXam, @sz39, N'CZSM-GRY-39', N'8842003', 550000, 620000, 35, N'/img/czsm_gry_39.jpg'),
(@sp42, @mauXanhDuong, @sz41, N'CZSM-BLU-41', N'8842004', 580000, 650000, 32, N'/img/czsm_blu_41.jpg'),
(@sp42, @mauBe, @sz40, N'CZSM-BGE-40', N'8842005', 580000, 650000, 30, N'/img/czsm_bge_40.jpg');

-- UrbanWalk Leather Sandal (5 variants)
DECLARE @sp43 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Leather Sandal');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp43, @mauDen, @sz39, N'UWLS-BLK-39', N'8843001', 880000, 980000, 25, N'/img/uwls_blk_39.jpg'),
(@sp43, @mauNau, @sz40, N'UWLS-BRN-40', N'8843002', 880000, 980000, 22, N'/img/uwls_brn_40.jpg'),
(@sp43, @mauBe, @sz39, N'UWLS-BGE-39', N'8843003', 920000, 1020000, 18, N'/img/uwls_bge_39.jpg'),
(@sp43, @mauDen, @sz41, N'UWLS-BLK-41', N'8843004', 880000, 980000, 20, N'/img/uwls_blk_41.jpg'),
(@sp43, @mauNau, @sz41, N'UWLS-BRN-41', N'8843005', 920000, 1020000, 16, N'/img/uwls_brn_41.jpg');

-- EasyStep Sport Sandal (5 variants)
DECLARE @sp44 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'EasyStep Sport Sandal');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp44, @mauDen, @sz40, N'ESSS-BLK-40', N'8844001', 680000, 760000, 35, N'/img/esss_blk_40.jpg'),
(@sp44, @mauXam, @sz41, N'ESSS-GRY-41', N'8844002', 680000, 760000, 32, N'/img/esss_gry_41.jpg'),
(@sp44, @mauNavy, @sz40, N'ESSS-NVY-40', N'8844003', 700000, 780000, 28, N'/img/esss_nvy_40.jpg'),
(@sp44, @mauXanhLa, @sz42, N'ESSS-GRN-42', N'8844004', 700000, 780000, 25, N'/img/esss_grn_42.jpg'),
(@sp44, @mauCam, @sz39, N'ESSS-ORG-39', N'8844005', 720000, 800000, 22, N'/img/esss_org_39.jpg');

-- ComfortZone Slide (5 variants)
DECLARE @sp45 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'ComfortZone Slide');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp45, @mauDen, @sz39, N'CZSL-BLK-39', N'8845001', 420000, 480000, 55, N'/img/czsl_blk_39.jpg'),
(@sp45, @mauTrang, @sz40, N'CZSL-WHT-40', N'8845002', 420000, 480000, 52, N'/img/czsl_wht_40.jpg'),
(@sp45, @mauXam, @sz39, N'CZSL-GRY-39', N'8845003', 420000, 480000, 48, N'/img/czsl_gry_39.jpg'),
(@sp45, @mauNavy, @sz41, N'CZSL-NVY-41', N'8845004', 450000, 510000, 45, N'/img/czsl_nvy_41.jpg'),
(@sp45, @mauHong, @sz40, N'CZSL-PNK-40', N'8845005', 450000, 510000, 40, N'/img/czsl_pnk_40.jpg');

-- UrbanWalk Outdoor (5 variants)
DECLARE @sp46 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'UrbanWalk Outdoor');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp46, @mauDen, @sz40, N'UWOD-BLK-40', N'8846001', 750000, 850000, 30, N'/img/uwod_blk_40.jpg'),
(@sp46, @mauXam, @sz41, N'UWOD-GRY-41', N'8846002', 750000, 850000, 28, N'/img/uwod_gry_41.jpg'),
(@sp46, @mauNau, @sz40, N'UWOD-BRN-40', N'8846003', 780000, 880000, 25, N'/img/uwod_brn_40.jpg'),
(@sp46, @mauXanhLa, @sz42, N'UWOD-GRN-42', N'8846004', 780000, 880000, 22, N'/img/uwod_grn_42.jpg'),
(@sp46, @mauDen, @sz42, N'UWOD-BLK-42', N'8846005', 750000, 850000, 26, N'/img/uwod_blk_42.jpg');

-- EasyStep Premium Sandal (5 variants)
DECLARE @sp47 INT = (SELECT SanPhamId FROM dbo.SanPham WHERE Ten=N'EasyStep Premium Sandal');
INSERT INTO dbo.SanPhamChiTiet (SanPhamId, MauSacId, KichThuocId, SKU, Barcode, GiaBan, GiaGoc, SoLuongTon, HinhAnh)
VALUES
(@sp47, @mauDen, @sz39, N'ESPS-BLK-39', N'8847001', 920000, 1050000, 20, N'/img/esps_blk_39.jpg'),
(@sp47, @mauNau, @sz40, N'ESPS-BRN-40', N'8847002', 920000, 1050000, 18, N'/img/esps_brn_40.jpg'),
(@sp47, @mauBe, @sz39, N'ESPS-BGE-39', N'8847003', 950000, 1080000, 15, N'/img/esps_bge_39.jpg'),
(@sp47, @mauDen, @sz41, N'ESPS-BLK-41', N'8847004', 920000, 1050000, 16, N'/img/esps_blk_41.jpg'),
(@sp47, @mauXam, @sz40, N'ESPS-GRY-40', N'8847005', 950000, 1080000, 14, N'/img/esps_gry_40.jpg');
GO