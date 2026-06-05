-- 1. Create database if it does not exist
CREATE DATABASE IF NOT EXISTS SWDB2026;

-- Select the database
USE SWDB2026;

-- 2. Create the Category table based on com.product.model.Category attributes
-- categoryId (Integer), category (String), tag (String), status (Integer)
-- It also enforces uniqueness on tag as per standard requirements for this entity
CREATE TABLE IF NOT EXISTS category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL UNIQUE,
    tag VARCHAR(100) NOT NULL UNIQUE,
    status INT NOT NULL DEFAULT 1
);

-- 3. Create user swdb_admin with password Swdb2026! if it does not exist
CREATE USER IF NOT EXISTS 'swdb_admin'@'localhost' IDENTIFIED BY 'Swdb2026!';
CREATE USER IF NOT EXISTS 'swdb_admin'@'%' IDENTIFIED BY 'Swdb2026!';

-- 4. Grant all privileges on the SWDB2026 database to the newly created user
GRANT ALL PRIVILEGES ON SWDB2026.* TO 'swdb_admin'@'localhost';
GRANT ALL PRIVILEGES ON SWDB2026.* TO 'swdb_admin'@'%';

-- Apply the permission changes
FLUSH PRIVILEGES;

-- 5. Insert mock data for categories.
-- We use IGNORE to prevent duplicate errors if script is run multiple times
INSERT IGNORE INTO category (category_id, category, tag, status) VALUES 
(1, 'Lentes', 'Lts', 1),
(2, 'Relojes', 'Rljs', 1),
(3, 'Mochilas', 'Mchs', 1),
(4, 'Zapatos', 'Zpts', 0);

-- 6. Create product table
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product;

CREATE TABLE product(
    product_id INT NOT NULL AUTO_INCREMENT,
    gtin CHAR(13) NOT NULL,
    product VARCHAR(100) NOT NULL,
    description TEXT,
    price FLOAT NOT NULL,
    stock INT NOT NULL,
    category_id INT NOT NULL,
    status TINYINT NOT NULL,
    PRIMARY KEY (product_id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id)
);

CREATE UNIQUE INDEX ux_product_gtin ON product(gtin);
CREATE UNIQUE INDEX ux_product_product ON product(product);

-- 7. Create product_image table
CREATE TABLE product_image(
	product_image_id INT NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    image VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL,
    PRIMARY KEY (product_image_id),
    CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);
