/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

INSERT INTO promotion_types (id, name) VALUES (1, 'CUMULATIVE_PERCENTAGE');
INSERT INTO promotion_types (id, name) VALUES (2, 'FIXED_AMOUNT');
INSERT INTO promotion_types (id, name) VALUES (3, 'NON_CUMULATIVE_PERCENTAGE');
-- id 3 used to be PERCENTAGE_WITH_MAX_AMOUNT (retired in favor of an optional maxDiscountAmount on every
-- type) and is now reassigned to NON_CUMULATIVE_PERCENTAGE — safe only because no promotion ever used it
-- in any real environment yet; on a pre-existing dev database run:
--   UPDATE promotion_types SET name = 'NON_CUMULATIVE_PERCENTAGE' WHERE id = 3;
-- id 1 was renamed from PERCENTAGE to CUMULATIVE_PERCENTAGE for symmetry with NON_CUMULATIVE_PERCENTAGE —
-- same id, same chaining behaviour, only the name changed; on a pre-existing dev database run:
--   UPDATE promotion_types SET name = 'CUMULATIVE_PERCENTAGE' WHERE id = 1;
