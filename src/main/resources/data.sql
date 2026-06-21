IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'Agrimarket')
BEGIN
    CREATE DATABASE Agrimarket;
END;
USE Agrimarket;

-- ========================================================
-- PARTE 1: TABLAS MAESTRAS (Entidades Base)
-- ========================================================

-- 1. UBIGEO (Configuración geográfica básica)
CREATE TABLE ubigeo (
    ubigeo_id INT IDENTITY(1,1) PRIMARY KEY,
    department VARCHAR(50) NOT NULL,
    province VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    full_name AS (department + ' - ' + province + ' - ' + district) PERSISTED,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 2. ROLE (Roles para el personal del sistema)
CREATE TABLE role (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(250) NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
	
-- 3. STORE (Sucursales físicas del negocio)
CREATE TABLE store (
    store_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    ubigeo_id INT NULL,
    phone VARCHAR(20) NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    latitude FLOAT NULL,
    longitude FLOAT NULL,
    location GEOGRAPHY NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_store_ubigeo FOREIGN KEY (ubigeo_id) REFERENCES ubigeo(ubigeo_id)
);

-- 4. USER (Empleados y administradores de las tiendas)
CREATE TABLE [user] (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    user_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    doc_type VARCHAR(20) NOT NULL,
    doc_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    ubigeo_id INT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    store_id INT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    restored_at DATETIME2 NULL,
    CONSTRAINT FK_user_role FOREIGN KEY (role_id) REFERENCES role(role_id),
    CONSTRAINT FK_user_store FOREIGN KEY (store_id) REFERENCES store(store_id),
    CONSTRAINT FK_user_ubigeo FOREIGN KEY (ubigeo_id) REFERENCES ubigeo(ubigeo_id)
);

-- 5. CUSTOMER (Clientes de Agrimarket - ID Corregido a INT)
CREATE TABLE customer (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    doc_type VARCHAR(20) NOT NULL,
    doc_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    ubigeo_id INT NULL,
    password VARCHAR(255) NULL,
    total_visits INT NOT NULL DEFAULT 0,
    last_purchase_date DATE NULL,
    customer_since DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    notes VARCHAR(500) NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    restored_at DATETIME2 NULL,
    CONSTRAINT FK_customer_ubigeo FOREIGN KEY (ubigeo_id) REFERENCES ubigeo(ubigeo_id)
);

-- 6. BRAND (Marcas de los insumos agrícolas)
CREATE TABLE brand (
    brand_id INT IDENTITY(1,1) PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- 7. CATEGORY (Estructura de jerarquía de productos)
CREATE TABLE category (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_category_id INT NULL,
    description VARCHAR(255) NULL,
    level INT NOT NULL DEFAULT 0,
    is_active BIT NOT NULL DEFAULT 1,
    code VARCHAR(20) NULL,
    order_display INT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    restored_at DATETIME2 NULL,
    CONSTRAINT FK_category_parent FOREIGN KEY (parent_category_id) REFERENCES category(category_id)
);

-- 8. SUPPLIER (Proveedores Mayoristas)
CREATE TABLE supplier (
    supplier_id INT IDENTITY(1,1) PRIMARY KEY,
    supplier_code VARCHAR(20) NOT NULL UNIQUE,
    business_name VARCHAR(100) NOT NULL,
    doc_type VARCHAR(20) NOT NULL,
    doc_number VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(200) NOT NULL,
    ubigeo_id INT NULL,
    contact_person VARCHAR(100) NULL,
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    restored_at DATETIME2 NULL,
    CONSTRAINT FK_supplier_ubigeo FOREIGN KEY (ubigeo_id) REFERENCES ubigeo(ubigeo_id)
);

-- 9. PRODUCT (Catálogo de insumos agrícolas disponibles)
CREATE TABLE product (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    active_ingredient VARCHAR(200) NULL,
    presentation VARCHAR(100) NULL,
    base_price DECIMAL(19,2) NOT NULL,
    image_url VARCHAR(500) NULL,
    description TEXT NULL,
    is_active BIT NOT NULL DEFAULT 1,
    sku VARCHAR(50) NOT NULL UNIQUE,
    category_id INT NOT NULL,
    brand_id INT NOT NULL,
    metadata NVARCHAR(MAX) NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    deleted_at DATETIME2 NULL,
    restored_at DATETIME2 NULL,
    CONSTRAINT FK_product_category FOREIGN KEY (category_id) REFERENCES category(category_id),
    CONSTRAINT FK_product_brand FOREIGN KEY (brand_id) REFERENCES brand(brand_id),
    CONSTRAINT CHK_product_metadata_isjson CHECK (metadata IS NULL OR ISJSON(metadata)=1)
);


-- ========================================================
-- PARTE 2: TABLAS TRANSACCIONALES (Operaciones y Procesos)
-- ========================================================

-- 10. INVENTORY (Control dinámico de stock por sucursal)
CREATE TABLE inventory (
    inventory_id INT IDENTITY(1,1) PRIMARY KEY,
    current_stock INT NOT NULL DEFAULT 0,
    minimum_stock INT NOT NULL DEFAULT 0,
    last_update DATETIME2 NOT NULL DEFAULT GETDATE(),
    store_id INT NOT NULL,
    product_id INT NOT NULL,
    CONSTRAINT FK_inventory_store FOREIGN KEY (store_id) REFERENCES store(store_id),
    CONSTRAINT FK_inventory_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 11. ORDER (Pedidos de venta de los clientes - CORREGIDA SINTAXIS)
CREATE TABLE [order] (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    order_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    status VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    delivery_type VARCHAR(20) NOT NULL,
    delivery_address VARCHAR(255) NULL,
    delivery_person_id INT NULL,
    delivery_date DATETIME2 NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    store_id INT NOT NULL,
    customer_id INT NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_order_store FOREIGN KEY (store_id) REFERENCES store(store_id),
    CONSTRAINT FK_order_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT FK_order_delivery_person FOREIGN KEY (delivery_person_id) REFERENCES [user](user_id), -- <--- Arreglado aquí
    CONSTRAINT CHK_order_status CHECK (status IN ('pendiente', 'pagado', 'enviado', 'entregado', 'cancelado')),
    CONSTRAINT CHK_order_delivery_type CHECK (delivery_type IN ('delivery', 'recogida'))
);

-- 12. ORDER_DETAIL (Detalle de productos comprados en un pedido)
CREATE TABLE order_detail (
    order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    CONSTRAINT FK_od_product FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT FK_od_order FOREIGN KEY (order_id) REFERENCES [order](order_id),
    CONSTRAINT CHK_quantity_positive CHECK (quantity > 0)
);

-- 13. PAYMENT (Transacciones económicas de los pedidos)
CREATE TABLE payment (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    payment_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    method VARCHAR(50) NOT NULL,
    installment_quantity INT NOT NULL DEFAULT 1,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'pagado',
    order_id INT NOT NULL UNIQUE,
    CONSTRAINT FK_payment_order FOREIGN KEY (order_id) REFERENCES [order](order_id),
    CONSTRAINT CHK_payment_status CHECK (payment_status IN ('pagado', 'parcial', 'pendiente')),
    CONSTRAINT CHK_installment_quantity CHECK (installment_quantity >= 1)
);

-- 14. INSTALLMENT (Control de cronogramas de pago por cuotas)
CREATE TABLE installment (
    installment_id INT IDENTITY(1,1) PRIMARY KEY,
    installment_number INT NOT NULL,
    installment_amount DECIMAL(19,2) NOT NULL,
    due_date DATE NOT NULL,
    payment_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    payment_id INT NOT NULL,
    CONSTRAINT FK_installment_payment FOREIGN KEY (payment_id) REFERENCES payment(payment_id),
    CONSTRAINT CHK_installment_status CHECK (status IN ('pendiente', 'pagada', 'vencida'))
);

-- 15. PURCHASE_ORDER (Órdenes de compra generadas hacia proveedores)
CREATE TABLE purchase_order (
    purchase_order_id INT IDENTITY(1,1) PRIMARY KEY,
    issue_date DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    expected_delivery_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'emitida',
    total_amount DECIMAL(19,2) NULL,
    store_id INT NOT NULL,
    supplier_id INT NOT NULL,
    CONSTRAINT FK_po_store FOREIGN KEY (store_id) REFERENCES store(store_id),
    CONSTRAINT FK_po_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

-- 16. PURCHASE_ORDER_DETAIL (Productos solicitados en la orden de compra)
CREATE TABLE purchase_order_detail (
    purchase_order_detail_id INT IDENTITY(1,1) PRIMARY KEY,
    quantity INT NOT NULL,
    purchase_price DECIMAL(19,2) NOT NULL,
    product_id INT NOT NULL,
    purchase_order_id INT NOT NULL,
    CONSTRAINT FK_pod_product FOREIGN KEY (product_id) REFERENCES product(product_id),
    CONSTRAINT FK_pod_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(purchase_order_id)
);

-- 17. ADVISORY (Servicios de asesorías agrícolas a clientes)
CREATE TABLE advisory (
    advisory_id INT IDENTITY(1,1) PRIMARY KEY,
    advisory_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    topic VARCHAR(255) NOT NULL,
    notes TEXT NULL,
    type VARCHAR(50) NOT NULL,
    customer_id INT NOT NULL,
    technician_id INT NOT NULL,
    CONSTRAINT FK_advisory_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT FK_advisory_technician FOREIGN KEY (technician_id) REFERENCES [user](user_id),
    CONSTRAINT CHK_advisory_type CHECK (type IN ('presencial', 'virtual', 'telefonica'))
);

-- 18. CART (Estructura transaccional para persistir carritos abandonados o activos)
CREATE TABLE cart (
    cart_id INT IDENTITY(1,1) PRIMARY KEY,
    session_token VARCHAR(255) NULL,
    total_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    customer_id INT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_cart_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);


CREATE INDEX IX_cart_customer_id ON cart(customer_id);
CREATE INDEX IX_cart_session_token ON cart(session_token);
CREATE INDEX IX_cart_updated_at ON cart(updated_at);

-- 19. CART_ITEM (Detalle dinámico de los insumos añadidos al carrito)
CREATE TABLE cart_item (
    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    added_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_cartitem_cart FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    CONSTRAINT FK_cartitem_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX IX_cartitem_cart_id ON cart_item(cart_id);
CREATE INDEX IX_cartitem_product_id ON cart_item(product_id);

INSERT INTO inventory (product_id, store_id, current_stock, minimum_stock, created_at, updated_at) 
VALUES (1, 1, 100, 5, GETDATE(), GETDATE());

INSERT INTO inventory (product_id, store_id, current_stock, minimum_stock, created_at, updated_at) 
VALUES (7, 1, 50, 5, GETDATE(), GETDATE());




-- ========================================================
-- 1. REGISTROS DE UBIGEO
-- ========================================================
INSERT INTO ubigeo (department, province, district) VALUES
('Lima', 'Lima', 'Lima'),
('Lima', 'Lima', 'San Isidro'),
('Lima', 'Lima', 'Miraflores'),
('Lima', 'Cañete', 'San Vicente de Cañete'),
('Lima', 'Cañete', 'Imperial'),
('Lima', 'Cañete', 'Nuevo Imperial'),
('Lima', 'Cañete', 'Quilmaná'),
('Lima', 'Cañete', 'San Luis'),
('Lima', 'Cañete', 'Lunahuaná');


-- ========================================================
-- 2. REGISTROS DE ROLES
-- ========================================================
INSERT INTO role (role_name, description) VALUES
('ADMIN', 'Administrador del sistema - control total'),
('VENDEDOR', 'Empleado de tienda que atiende pedidos'),
('DELIVERY', 'Personal de reparto a domicilio'),
('ASESOR', 'Ingeniero o técnico que brinda asesorías agrícolas');


-- ========================================================
-- 3. REGISTROS DE TIENDAS
-- ========================================================
INSERT INTO store (name, address, ubigeo_id, phone, schedule, latitude, longitude, location) VALUES
('Agrimarket Centro', 'Av. San Vicente N° 123', 4, '01-2345678', 'Lun-Sáb 8am-6pm', -13.0750, -76.3830, geography::Point(-13.0750, -76.3830, 4326)),
('Agrimarket Sur', 'Carretera Panamericana Sur Km 145', 4, '01-8765432', 'Lun-Dom 8am-8pm', -13.0900, -76.3900, geography::Point(-13.0900, -76.3900, 4326)),
('Agrimarket Norte', 'Jr. Los Agricultores 456', 5, '01-4567890', 'Lun-Sáb 9am-5pm', -13.0600, -76.3780, geography::Point(-13.0600, -76.3780, 4326));


-- ========================================================
-- 4. REGISTROS DE USUARIOS (Personal Interno Corregido y Completado)
-- ========================================================
INSERT INTO [user] (user_code, first_name, last_name, doc_type, doc_number, email, phone, address, ubigeo_id, password, role_id, store_id, is_active) VALUES
('USR-001', 'Carlos', 'Admin', 'DNI', '12345678', 'admin@agrimarket.com', '999111222', 'Oficina Central Cañete', 4, 'admin123', (SELECT role_id FROM role WHERE role_name = 'ADMIN'), 1, 1),
('USR-002', 'Ana', 'Ventas', 'DNI', '23456789', 'ana.ventas@agrimarket.com', '999111223', 'Jr. Bolognesi 234', 4, 'vendedor123', (SELECT role_id FROM role WHERE role_name = 'VENDEDOR'), 1, 1),
('USR-003', 'Pedro', 'Reparto', 'DNI', '34567890', 'pedro.delivery@agrimarket.com', '999111224', 'Av. Mariscal Benavides 789', 4, 'delivery123', (SELECT role_id FROM role WHERE role_name = 'DELIVERY'), 1, 1),
('USR-004', 'Ing. Luis', 'Campos', 'DNI', '45678901', 'luis.asesor@agrimarket.com', '999111225', 'Urb. Miraflores S-12', 5, 'asesor123', (SELECT role_id FROM role WHERE role_name = 'ASESOR'), 1, 1);


-- ========================================================
-- 5. REGISTROS DE MARCAS
-- ========================================================
INSERT INTO brand (brand_name) VALUES
('Bayer'), ('Syngenta'), ('Farmgro'), ('TQC'), ('Serfi'), ('Adama'), ('Silvestre'), ('PSW'), ('GDM Vidas'), ('Molinos'), ('Certifex'), ('Hortisgreen');


-- ========================================================
-- 6. REGISTROS DE CATEGORÍAS
-- ========================================================
INSERT INTO category (category_name, parent_category_id, description, level, code, order_display, is_active) VALUES
('Protección de Cultivos', NULL, 'Productos para control de plagas y enfermedades', 0, 'PROT', 1, 1),
('Nutrición Vegetal', NULL, 'Fertilizantes y bioestimulantes para cultivos', 0, 'NUTR', 2, 1),
('Fungicidas', 1, 'Control de hongos en cultivos', 1, 'FUNG', 1, 1),
('Insecticidas', 1, 'Control de insectos y plagas', 1, 'INSE', 2, 1),
('Fertilizantes', 2, 'Abonos y fertilizantes para el suelo', 1, 'FERT', 1, 1),
('Bioestimulantes', 2, 'Estimulantes del crecimiento vegetal', 1, 'BIO', 2, 1);


-- ========================================================
-- 7. REGISTROS DE PROVEEDORES
-- ========================================================
INSERT INTO supplier (supplier_code, business_name, doc_type, doc_number, phone, email, address, ubigeo_id, contact_person, is_active) VALUES
('PROV-001', 'Corporación Molinos del Perú S.A.', 'RUC', '20554433221', '01-7112233', 'contacto@molinos.com.pe', 'Av. Industrial 450, Lima', 1, 'Ing. Manuel Torres', 1),
('PROV-002', 'Distribuidora Agraria del Sur S.A.C.', 'RUC', '20443322115', '01-7156677', 'ventas@agrasur.pe', 'Panamericana Sur Km 140, Cañete', 4, 'Sofía Valencia', 1);


-- ========================================================
-- 8. REGISTROS DE CLIENTES
-- ========================================================
INSERT INTO customer (customer_code, doc_type, doc_number, first_name, last_name, email, phone, address, ubigeo_id, password, total_visits, customer_since, is_active) VALUES 
('CLT-001', 'DNI', '12345670', 'Juan', 'Perez', 'juan1@agricultor.com', '987654321', 'Fundo San Juan', 4, NULL, 0, GETDATE(), 1),
('CLT-002', 'DNI', '87654321', 'Maria', 'Rojas', 'maria1@agricultor.com', '987654322', 'Fundo Santa Maria', 5, 'clave123', 0, GETDATE(), 1),
('CLT-003', 'DNI', '11223344', 'Carlos', 'Lopez', 'carlos1@agricultor.com', '987654323', 'Fundo Los Pinos', 6, NULL, 0, GETDATE(), 1),
('CLT-004', 'RUC', '20123456789', 'Agroexport S.A.', 'Contacto', 'agroexport@empresa.com', '987654324', 'Av. Principal 456', 4, NULL, 0, GETDATE(), 1),
('CLT-005', 'DNI', '99887766', 'Ana', 'Martinez', 'ana1@agricultor.com', '987654325', 'Fundo La Esperanza', 5, 'ana123', 0, GETDATE(), 1),
('CLT-006', 'DNI', '55443322', 'Luis', 'Gomez', 'luis1@agricultor.com', '987654326', 'Fundo San Pedro', 6, NULL, 0, GETDATE(), 1),
('CLT-007', 'DNI', '66778899', 'Rosa', 'Diaz', 'rosa1@agricultor.com', '987654327', 'Fundo La Victoria', 4, NULL, 0, GETDATE(), 1),
('CLT-008', 'DNI', '44332211', 'Pedro', 'Ramirez', 'pedro1@agricultor.com', '987654328', 'Fundo El Milagro', 5, NULL, 0, GETDATE(), 1),
('CLT-009', 'DNI', '55667788', 'Sofia', 'Torres', 'sofia1@agricultor.com', '987654329', 'Fundo Santa Rosa', 6, 'sofia123', 0, GETDATE(), 1),
('CLT-010', 'DNI', '88997766', 'Jorge', 'Castro', 'jorge1@agricultor.com', '987654330', 'Fundo San Luis', 4, NULL, 0, GETDATE(), 1),
('CLT-011', 'DNI', '33445566', 'Lucia', 'Mendoza', 'lucia1@agricultor.com', '987654331', 'Fundo Los Olivos', 5, NULL, 0, GETDATE(), 1),
('CLT-012', 'DNI', '77889900', 'Diego', 'Reyes', 'diego1@agricultor.com', '987654332', 'Fundo El Carmen', 6, NULL, 0, GETDATE(), 1),
('CLT-013', 'DNI', '12344321', 'Marta', 'Flores', 'marta1@agricultor.com', '987654333', 'Fundo La Unión', 4, NULL, 0, GETDATE(), 1),
('CLT-014', 'DNI', '56781234', 'Ricardo', 'Ortiz', 'ricardo1@agricultor.com', '987654334', 'Fundo Los Ángeles', 5, NULL, 0, GETDATE(), 1),
('CLT-015', 'DNI', '98761234', 'Patricia', 'Vega', 'patricia1@agricultor.com', '987654335', 'Fundo San Antonio', 6, NULL, 0, GETDATE(), 1);


-- ========================================================
-- 9. REGISTROS DE PRODUCTOS
-- ========================================================
INSERT INTO product (name, active_ingredient, presentation, base_price, image_url, description, sku, category_id, brand_id, is_active) VALUES
('Abafin 1.8 EC', 'Abamectina', '1 Litro', 65.00, NULL, 'Insecticida acaricida de acción traslaminar', 'ABF-001', 4, 1, 1),
('Amistar Xtra', 'Azoxistrobina + Ciproconazol', '1 Litro', 250.00, NULL, 'Fungicida sistémico de amplio espectro', 'AMX-001', 3, 2, 1),
('Confidor 350 SC', 'Imidacloprid', '1 Litro', 120.00, NULL, 'Insecticida sistémico para control de plagas', 'CNF-001', 4, 1, 1),
('Score 250 EC', 'Difenoconazole', '1 Litro', 180.00, NULL, 'Fungicida para control preventivo y curativo', 'SCR-001', 3, 2, 1),
('Ridomil Gold', 'Metalaxil', '500 Gramos', 95.00, NULL, 'Fungicida de acción sistémica', 'RDM-001', 3, 6, 1),
('Karate Zeon', 'Lambda Cyhalothrin', '1 Litro', 145.00, NULL, 'Insecticida de amplio espectro', 'KRZ-001', 4, 2, 1),
('Urea Agrícola', 'Nitrógeno', '50 Kg', 110.00, NULL, 'Fertilizante nitrogenado', 'URE-001', 5, 10, 1),
('NPK 20-20-20', 'NPK Balanceado', '25 Kg', 150.00, NULL, 'Fertilizante completo para cultivos', 'NPK-001', 5, 11, 1),
('Rooting Plus', 'Extractos Vegetales', '1 Litro', 85.00, NULL, 'Bioestimulante radicular', 'RTP-001', 6, 12, 1),
('AminoGrow', 'Aminoácidos', '1 Litro', 98.00, NULL, 'Bioestimulante foliar', 'AMG-001', 6, 12, 1),
('Tracer 120 SC', 'Spinosad', '250 ml', 210.00, NULL, 'Insecticida biológico', 'TRC-001', 4, 7, 1),
('Cuprofix', 'Oxicloruro de Cobre', '1 Kg', 75.00, NULL, 'Fungicida cúprico de contacto', 'CPF-001', 3, 8, 1),
('Glyphosate 480 SL', 'Glyphosate', '1 Galón', 65.00, NULL, 'Herbicida sistémico no selectivo', 'GLY-001', 4, 5, 1),
('Biozyme TF', 'Fitohormonas', '1 Litro', 135.00, NULL, 'Bioestimulante para crecimiento vegetal', 'BZT-001', 6, 9, 1),
('Fosfato Diamónico', 'Fósforo y Nitrógeno', '50 Kg', 170.00, NULL, 'Fertilizante fosfatado', 'FDA-001', 5, 10, 1);


-- ========================================================
-- 1. TABLAS MAESTRAS (BÁSICAS)
-- ========================================================
SELECT * FROM ubigeo;
SELECT * FROM role;
SELECT * FROM brand;
SELECT * FROM category;

-- ========================================================
-- 2. TABLAS DE ENTIDADES PRINCIPALES
-- ========================================================
SELECT * FROM store;
SELECT * FROM [user];
SELECT * FROM customer;
SELECT * FROM supplier;
SELECT * FROM product;

-- ========================================================
-- 3. TABLAS DE PROCESOS (INVENTARIO Y CARRITO)
-- ========================================================
SELECT * FROM inventory;
SELECT * FROM cart;
SELECT * FROM cart_item;

-- ========================================================
-- 4. TABLAS DE MOVIMIENTOS Y VENTAS (ÓRDENES Y PAGOS)
-- ========================================================
SELECT * FROM [order];
SELECT * FROM order_detail;
SELECT * FROM payment;
SELECT * FROM installment;

-- ========================================================
-- 5. TABLAS DE ABASTECIMIENTO (COMPRAS A PROVEEDORES)
-- ========================================================
SELECT * FROM purchase_order;
SELECT * FROM purchase_order_detail;

-- ========================================================
-- 6. TABLAS DE SERVICIOS
-- ========================================================
SELECT * FROM advisory;








