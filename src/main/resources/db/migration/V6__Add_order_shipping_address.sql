-- Add shipping address snapshot columns onto orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_full_name VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_address_line1 VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_address_line2 VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_city VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_state VARCHAR(255);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_postal_code VARCHAR(32);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_country_code VARCHAR(2);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS shipping_phone VARCHAR(64);

