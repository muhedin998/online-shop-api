-- Add tracking_number column to orders table
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(255) UNIQUE;