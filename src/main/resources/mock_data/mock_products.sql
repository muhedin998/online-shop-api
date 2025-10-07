-- Mock Products for Barber Store
-- Insert 12 barber-related products (4 featured)

INSERT INTO products (name, description, price, stock_quantity, featured, main_image_url) VALUES
-- Featured Products (4)
('Professional Beard Trimmer Pro X', 'High-precision cordless beard trimmer with titanium-coated blades. 120-minute battery life, 40 length settings, and waterproof design for easy cleaning.', 89.99, 45, true, 'https://images.unsplash.com/photo-1621607512214-68297480165e'),

('Premium Barber Scissors Set', 'Japanese stainless steel barber scissors set including 6.5" cutting scissors and 5.5" thinning shears. Ergonomic design with adjustable tension screws.', 129.99, 30, true, 'https://images.unsplash.com/photo-1599351431613-66c0c53c3466'),

('Deluxe Hot Towel Cabinet', 'Professional-grade UV sterilizer and towel warmer. Holds up to 24 towels, maintains temperature at 176°F, perfect for hot towel shaves and facials.', 249.99, 12, true, 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70'),

('Elite Barber Chair Hydraulic', 'Heavy-duty hydraulic barber chair with 360° swivel, reclining backrest, and adjustable headrest. Premium leather upholstery with chrome base. Weight capacity 450lbs.', 599.99, 8, true, 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1'),

-- Regular Products (8)
('Classic Straight Razor Kit', 'Traditional straight razor with genuine horn handle, leather strop, and shaving soap. Perfect for achieving the closest shave possible.', 69.99, 55, false, 'https://images.unsplash.com/photo-1594035910387-fea47794261f'),

('Barber Cape Professional Black', 'Water-resistant nylon barber cape with adjustable snap closure. 55" x 63" size provides full coverage. Easy to clean and lightweight.', 24.99, 120, false, 'https://images.unsplash.com/photo-1622286342621-4bd786c2447c'),

('Clipper Blade Oil & Spray', 'Premium maintenance kit for clippers and trimmers. Includes lubricating oil, coolant spray, and cleaning brush. Extends blade life and ensures optimal performance.', 19.99, 200, false, 'https://images.unsplash.com/photo-1583512603806-077998240c7a'),

('Neck Duster Brush', 'Soft synthetic bristle neck duster with ergonomic wooden handle. Perfect for removing hair clippings after cuts and shaves.', 14.99, 85, false, 'https://images.unsplash.com/photo-1621607512214-68297480165e'),

('Styling Pomade Strong Hold', 'Water-based pomade with strong hold and medium shine. Vanilla and cedarwood scent. Easy to wash out, no flaking. 4oz jar.', 18.99, 150, false, 'https://images.unsplash.com/photo-1571875257727-256c39da42af'),

('Professional Hair Dryer 2000W', 'Ionic hair dryer with ceramic technology. 2000W motor, 3 heat settings, 2 speed settings, and cool shot button. Includes concentrator nozzle and diffuser.', 79.99, 40, false, 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da'),

('Barber Station Mirror LED', 'Wall-mounted salon mirror with built-in LED lighting. 24" x 36" with adjustable brightness. Modern frameless design with anti-fog coating.', 189.99, 20, false, 'https://images.unsplash.com/photo-1595435742656-5272d0bc5dd5'),

('Shaving Brush Badger Hair', 'Handcrafted shaving brush with genuine badger hair bristles and premium resin handle. Creates rich, thick lather for traditional wet shaving.', 45.99, 65, false, 'https://images.unsplash.com/photo-1564069114553-7215e1ff1890');

-- Insert sample carousel images for featured products
INSERT INTO product_images (product_id, image_url) VALUES
(1, 'https://images.unsplash.com/photo-1621607512214-68297480165e'),
(1, 'https://images.unsplash.com/photo-1622287162716-f311baa67f59'),
(1, 'https://images.unsplash.com/photo-1617060005641-64861c4f66d9'),

(2, 'https://images.unsplash.com/photo-1599351431613-66c0c53c3466'),
(2, 'https://images.unsplash.com/photo-1619451334792-150fd785ee74'),
(2, 'https://images.unsplash.com/photo-1605497788044-5a32c7078486'),

(3, 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70'),
(3, 'https://images.unsplash.com/photo-1607748851746-7bcf6e7e5b56'),

(4, 'https://images.unsplash.com/photo-1503951914875-452162b0f3f1'),
(4, 'https://images.unsplash.com/photo-1585747860715-2ba37e788b70'),
(4, 'https://images.unsplash.com/photo-1595476108010-b4d1f102b1b1');
