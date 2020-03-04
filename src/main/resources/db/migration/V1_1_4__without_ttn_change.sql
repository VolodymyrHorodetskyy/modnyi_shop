ALTER TABLE `modnyi`.`ordered`
ADD COLUMN `withoutTTN` BIT(1) NULL AFTER `full_payment`;

SET SQL_SAFE_UPDATES = 0;
update ordered set withoutTTN= 0;
SET SQL_SAFE_UPDATES = 1;

ALTER TABLE `modnyi`.`ordered`
DROP INDEX `ttn_UNIQUE` ;
;
