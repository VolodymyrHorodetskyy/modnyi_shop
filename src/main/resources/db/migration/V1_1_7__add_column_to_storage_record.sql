ALTER TABLE `modnyi`.`storagerecord`
ADD COLUMN `ttn` VARCHAR(45) NULL AFTER `size`;
ALTER TABLE `modnyi`.`storagerecord`
ADD COLUMN `created_date` DATETIME NULL AFTER `ttn`,
ADD COLUMN `last_modified_date` DATETIME NULL AFTER `created_date`;

ALTER TABLE `modnyi`.`storagerecord`
CHANGE COLUMN `shoe` `shoe_id` BIGINT(20) NULL DEFAULT NULL ;

ALTER TABLE `modnyi`.`storagerecord`
ADD COLUMN `available` BIT(1) NULL AFTER `last_modified_date`;

ALTER TABLE `modnyi`.`ordered`
ADD COLUMN `from_storage` BIT(1) NULL AFTER `withoutTTN`;

ALTER TABLE `modnyi`.`ordered`
ADD COLUMN `fromttn` VARCHAR(45) NULL AFTER `from_storage`;

SET SQL_SAFE_UPDATES = 0;
update ordered set from_storage = 0;
SET SQL_SAFE_UPDATES = 1;
