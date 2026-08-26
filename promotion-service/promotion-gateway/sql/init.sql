/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

INSERT INTO promotion_types (id, name) VALUES (1, 'PERCENTAGE');
INSERT INTO promotion_types (id, name) VALUES (2, 'FIXED_AMOUNT');
INSERT INTO promotion_types (id, name) VALUES (3, 'NON_CUMULATIVE_PERCENTAGE');
-- id 3 used to be PERCENTAGE_WITH_MAX_AMOUNT (retired in favor of an optional maxDiscountAmount on every
-- type) and is now reassigned to NON_CUMULATIVE_PERCENTAGE — safe only because no promotion ever used it
-- in any real environment yet; on a pre-existing dev database run:
--   UPDATE promotion_types SET name = 'NON_CUMULATIVE_PERCENTAGE' WHERE id = 3;
