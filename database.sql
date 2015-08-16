CREATE USER 'vanillaplus'@'localhost' IDENTIFIED BY 'projectlife1337';

CREATE DATABASE IF NOT EXISTS  `vanillaplus` ;

GRANT ALL PRIVILEGES ON  `vanillaplus` . * TO  'vanillaplus'@'localhost';

USE vanillaplus;

CREATE TABLE IF NOT EXISTS `user` (
  	`id` int(11) NOT NULL AUTO_INCREMENT,
  	`name` varchar(255) NOT NULL,
  	`joined` int(11) NOT NULL,
  	`lastconnected` int(11) NOT NULL,
  	`buildstatus` int(11) NOT NULL,
  	`tempbankaccount` int(11) NOT NULL,
  	`minutesplayed` int(11) NOT NULL,
  	`levelid` int(11) NOT NULL,
  	`adminid` int(11) DEFAULT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `importexport` (
	`materialid` int(11) NOT NULL AUTO_INCREMENT,
  	`materialname` varchar(255) NOT NULL,
  	`materialsupply` int(11) NOT NULL,
  	`materialdemand` int(11) NOT NULL,
  	PRIMARY KEY (`materialid`)
);

INSERT INTO `importexport` (`materialid`, `materialname`, `materialsupply`, `materialdemand`) VALUES
(1, 'DIAMOND_BLOCK', 1, 1),
(2, 'EMERALD_BLOCK', 1, 1),
(3, 'GOLD_BLOCK', 1, 1),
(4, 'IRON_BLOCK', 1, 1),
(5, 'LAPIS_BLOCK', 1, 1),
(6, 'QUARTZ_BLOCK', 1, 1),
(7, 'REDSTONE_BLOCK', 1, 1);

CREATE TABLE IF NOT EXISTS `block` (
	`blockid` int(11) NOT NULL AUTO_INCREMENT,
  	`userid` int(11) NOT NULL,
  	`username` varchar(255) NOT NULL,
  	`date` int(11) NOT NULL,
  	`type` varchar(255) NOT NULL,
  	`x` int(11) NOT NULL,
  	`y` int(11) NOT NULL,
  	`z` int(11) NOT NULL,
  	`world` varchar(255) NOT NULL,
  	`private` tinyint(1) NOT NULL,
  	`state` tinyint(1) NOT NULL,
  	PRIMARY KEY (`blockid`)
);

CREATE TABLE IF NOT EXISTS `region` (
	`regionid` int(11) NOT NULL AUTO_INCREMENT,
  	`regionname` varchar(255) NOT NULL,
  	`regiondescription` varchar(255) NOT NULL,
  	`minx` int(11) NOT NULL,
  	`miny` int(11) NOT NULL,
  	`minz` int(11) NOT NULL,
  	`maxx` int(11) NOT NULL,
  	`maxy` int(11) NOT NULL,
  	`maxz` int(11) NOT NULL,
  	PRIMARY KEY (`regionid`)
);

CREATE TABLE IF NOT EXISTS `regionresidents` (
	`regionid` int(11) NOT NULL,
	`userid` int(11) NOT NULL,
	`name` varchar(255) NOT NULL,
  	`rank` varchar(255) NOT NULL,
  	PRIMARY KEY (`regionid`, `userid`)
);

CREATE TABLE IF NOT EXISTS `bank` (
	`bankid` int(11) NOT NULL AUTO_INCREMENT,
  	`userid` int(11) NOT NULL,
  	`date` int(11) NOT NULL,
  	`money` int(11) NOT NULL,
  	PRIMARY KEY (`bankid`)
);

CREATE TABLE IF NOT EXISTS `report` (
  	`reportid` int(11) NOT NULL AUTO_INCREMENT,
  	`userid` int(11) NOT NULL,
  	`date` int(11) NOT NULL,
  	`message` varchar(255) NOT NULL,
  	PRIMARY KEY (`reportid`)
);

CREATE TABLE IF NOT EXISTS `log` (
  	`logid` int(11) NOT NULL AUTO_INCREMENT,
  	`userid` int(11) NOT NULL,
  	`date` int(11) NOT NULL,
  	`message` varchar(255) NOT NULL,
  	PRIMARY KEY (`logid`)
);

CREATE TABLE IF NOT EXISTS `data` (
  	`materialname` varchar(255) NOT NULL,
  	`materialdate` datetime NOT NULL,
  	`materialprice` int(11) NOT NULL,
  	`materialsupply` int(11) NOT NULL,  
  	PRIMARY KEY (`materialname`, `materialdate`)
);