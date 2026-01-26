CREATE TABLE `customer` (
                            `id` MEDIUMINT(8) UNSIGNED NOT NULL AUTO_INCREMENT,
                            `firstName` VARCHAR(255) DEFAULT NULL,
                            `lastName` VARCHAR(255) DEFAULT NULL,
                            `birthdate` VARCHAR(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) AUTO_INCREMENT=1;

CREATE TABLE `customer2` (
    `id` MEDIUMINT(8) UNSIGNED NOT NULL AUTO_INCREMENT,
    `firstName` VARCHAR(255) DEFAULT NULL,
    `lastName` VARCHAR(255) DEFAULT NULL,
    `birthdate` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) AUTO_INCREMENT=1;
