ALTER TABLE `modnyi`.`ordered`
CHANGE COLUMN `ttn` `ttn` VARCHAR(100) NULL DEFAULT NULL ,
ADD UNIQUE INDEX `ttn_UNIQUE` (`ttn` ASC) VISIBLE;
;