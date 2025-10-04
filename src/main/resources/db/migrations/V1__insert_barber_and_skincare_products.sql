INSERT INTO products (name, description, price, stock_quantity, featured, main_image_url) VALUES
                                                                                              ('Matte Finish Hair Pomade', 'Strong hold, no-shine pomade for a natural, textured look. Water-based for easy washout.', 18.50, 120, true, 'https://images.unsplash.com/photo-1621607512214-6c34903a4282?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Cedarwood Beard Oil', 'Nourishing beard oil with natural cedarwood and jojoba oils to soften and condition your beard.', 22.00, 90, true, 'https://images.unsplash.com/photo-1631751241973-31f4e184e3a4?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Sandalwood Shaving Cream', 'Rich lathering shaving cream that protects the skin for a close, comfortable shave.', 16.00, 150, false, 'https://images.unsplash.com/photo-1599388121453-e3c35832115e?q=80&w=1964&auto=format&fit=crop'),
                                                                                              ('Cooling Aftershave Balm', 'Alcohol-free aftershave balm with mint and aloe vera to soothe and hydrate skin post-shave.', 19.99, 100, false, 'https://images.unsplash.com/photo-1622226296998-61427b6f6a73?q=80&w=1999&auto=format&fit=crop'),
                                                                                              ('Charcoal Face Wash', 'Deep-cleansing face wash with activated charcoal to remove impurities and excess oil.', 15.50, 200, true, 'https://images.unsplash.com/photo-1620916566398-39f19a3a76e4?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Daily Hydrating Moisturizer', 'A lightweight, non-greasy daily moisturizer with SPF 20 to protect and hydrate skin.', 25.00, 130, true, 'https://images.unsplash.com/photo-1600958932537-338e3a5a5c68?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Exfoliating Face Scrub', 'Gentle face scrub with walnut shell powder to remove dead skin cells and unclog pores.', 17.00, 110, false, 'https://images.unsplash.com/photo-1631751241943-31e5488c1c46?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Anti-Fatigue Eye Cream', 'Reduces the appearance of dark circles and puffiness with a caffeine-infused formula.', 28.50, 75, false, 'https://images.unsplash.com/photo-1620916723238-0130e55b6c38?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Detoxifying Clay Mask', 'Bentonite clay mask to draw out toxins and minimize the appearance of pores. Use once a week.', 24.00, 85, false, 'https://images.unsplash.com/photo-1618214981403-172960f25e79?q=80&w=1974&auto=format&fit=crop'),
                                                                                              ('Classic Double-Edge Safety Razor', 'A durable, chrome-plated safety razor for a superior, close shave. Includes 5 blades.', 35.00, 60, false, 'https://images.unsplash.com/photo-1621607512213-4e3a323a5e81?q=80&w=1974&auto=format&fit=crop');

-- Insert carousel images for Matte Finish Hair Pomade (assuming ID=6)
INSERT INTO product_images (product_id, image_url) VALUES
                                                       (6, 'https://images.unsplash.com/photo-1570175591995-f0951163d763?q=80&w=1974&auto=format&fit=crop'),
                                                       (6, 'https://images.unsplash.com/photo-1621607512224-4332e2a0f836?q=80&w=1974&auto=format&fit=crop');

-- Insert carousel images for Cedarwood Beard Oil (assuming ID=7)
INSERT INTO product_images (product_id, image_url) VALUES
                                                       (7, 'https://images.unsplash.com/photo-1591024386821-2f3b3a3283b4?q=80&w=1974&auto=format&fit=crop'),
                                                       (7, 'https://images.unsplash.com/photo-1631751241854-e737c02283a0?q=80&w=1974&auto=format&fit=crop');

-- Insert carousel images for Daily Hydrating Moisturizer (assuming ID=11)
INSERT INTO product_images (product_id, image_url) VALUES
    (11, 'https://images.unsplash.com/photo-1620916566398-39f19a3a76e4?q=80&w=1974&auto=format&fit=crop');