CREATE TABLE IF NOT EXISTS product_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Add category_id column to products table
ALTER TABLE products
ADD COLUMN category_id BIGINT;

-- Add foreign key constraint
ALTER TABLE products
ADD CONSTRAINT fk_products_category
FOREIGN KEY (category_id) REFERENCES product_categories(id) ON DELETE SET NULL;

-- Create index on category_id for better query performance
CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);
