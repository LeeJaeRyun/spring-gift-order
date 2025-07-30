CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    price INT NOT NULL,
    image_url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS wishlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    CONSTRAINT fk_wishlist_member FOREIGN KEY (member_id) REFERENCES members(id),
    CONSTRAINT fk_wishlist_item FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    item_id BIGINT NOT NULL,
    CONSTRAINT uk_item_option UNIQUE (item_id, name),
    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id)
);