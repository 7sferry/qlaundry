/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

INSERT INTO order_statuses (id, name) VALUES (1, 'PENDING');
INSERT INTO order_statuses (id, name) VALUES (2, 'CONFIRMED');
INSERT INTO order_statuses (id, name) VALUES (3, 'PICKED_UP');
INSERT INTO order_statuses (id, name) VALUES (4, 'IN_PROGRESS');
INSERT INTO order_statuses (id, name) VALUES (5, 'READY');
INSERT INTO order_statuses (id, name) VALUES (6, 'OUT_FOR_DELIVERY');
INSERT INTO order_statuses (id, name) VALUES (7, 'COMPLETED');
INSERT INTO order_statuses (id, name) VALUES (8, 'CANCELLED');

INSERT INTO order_priorities (id, name) VALUES (1, 'NORMAL');
INSERT INTO order_priorities (id, name) VALUES (2, 'EXPRESS');

-- cash only for now; TRANSFER (2) and QRIS (3) get seeded when those flows exist
INSERT INTO payment_methods (id, name) VALUES (1, 'CASH');

INSERT INTO payment_statuses (id, name) VALUES (1, 'UNPAID');
INSERT INTO payment_statuses (id, name) VALUES (2, 'PAID');

INSERT INTO clothing_types (id, name) VALUES (1, 'SHIRT');
INSERT INTO clothing_types (id, name) VALUES (2, 'PANTS');
INSERT INTO clothing_types (id, name) VALUES (3, 'DRESS');
INSERT INTO clothing_types (id, name) VALUES (4, 'JACKET');
INSERT INTO clothing_types (id, name) VALUES (5, 'BED_LINEN');
INSERT INTO clothing_types (id, name) VALUES (6, 'TOWEL');
INSERT INTO clothing_types (id, name) VALUES (7, 'UNIFORM');
INSERT INTO clothing_types (id, name) VALUES (8, 'OTHER');

INSERT INTO service_units (id, name) VALUES (1, 'KG');
INSERT INTO service_units (id, name) VALUES (2, 'ITEM');
INSERT INTO service_units (id, name) VALUES (3, 'LOAD');
INSERT INTO service_units (id, name) VALUES (4, 'SET');

INSERT INTO service_categories (id, name) VALUES (1, 'WASH');
INSERT INTO service_categories (id, name) VALUES (2, 'DRY_CLEAN');
INSERT INTO service_categories (id, name) VALUES (3, 'IRON');
INSERT INTO service_categories (id, name) VALUES (4, 'SPECIALTY');

-- Default laundry service price list (mirrors web/'s old orderFallbackData.ts).
-- laundry_services is tenant-scoped (tenant_id NOT NULL), so this is a per-tenant
-- seed, not a global lookup like the tables above — replace REPLACE_WITH_TENANT_ID
-- and REPLACE_WITH_STAFF_ID with a real tenants.id / staffs.id before running.
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-wash-fold', 'REPLACE_WITH_TENANT_ID', 'Wash & Fold', 'Freshly washed, dried and neatly folded. Perfect for everyday laundry.', 8000, 1, 1, 24, 1.5, true, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-wash-iron', 'REPLACE_WITH_TENANT_ID', 'Wash & Iron', 'Complete clean with a crisp, professional press. Great for workwear.', 12000, 1, 1, 48, 1.5, true, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-dry-clean', 'REPLACE_WITH_TENANT_ID', 'Dry Cleaning', 'Gentle solvent-based care for suits, dresses and delicate fabrics.', 35000, 2, 2, 72, 2.0, false, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-iron-only', 'REPLACE_WITH_TENANT_ID', 'Iron Only', 'Professional pressing for an already-clean wardrobe.', 5000, 2, 3, 12, 1.5, false, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-bed-linen', 'REPLACE_WITH_TENANT_ID', 'Bed Linen', 'Full wash and fold for sheets, duvet covers, pillow cases and more.', 45000, 4, 4, 48, 1.5, false, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
INSERT INTO laundry_services (id, tenant_id, name, description, price_per_unit, unit_id, category_id, estimated_hours, express_multiplier, popular, active, version, deleted, created_by, created_at, updated_by, updated_at)
VALUES ('svc-sneaker-clean', 'REPLACE_WITH_TENANT_ID', 'Sneaker Cleaning', 'Deep clean and refresh for your favourite sneakers and shoes.', 50000, 2, 4, 72, 2.0, true, true, 0, false, 'REPLACE_WITH_STAFF_ID', now(), 'REPLACE_WITH_STAFF_ID', now());
