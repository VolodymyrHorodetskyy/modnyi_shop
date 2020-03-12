/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `client`
--


CREATE TABLE `client` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `company` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
);


CREATE TABLE `ordered` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `available` bit(1) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `city_refnp` varchar(255) DEFAULT NULL,
  `date_payed_keepingnp` datetime DEFAULT NULL,
  `last_created_on_the_basis_document_typenp` varchar(255) DEFAULT NULL,
  `last_transaction_date_time` datetime DEFAULT NULL,
  `name_and_surnamenp` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `post_comment` varchar(255) DEFAULT NULL,
  `pre_payment` double DEFAULT NULL,
  `price` double DEFAULT NULL,
  `return_sumnp` double DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `ttn` varchar(255) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcuj0y2f9k1jf28a3xu2e667tx` (`client_id`)
);


CREATE TABLE `ordered_ordered_shoes` (
  `ordered_id` bigint(20) NOT NULL,
  `ordered_shoes_id` bigint(20) NOT NULL,
  KEY `FKf8vp0bmc38pfu8pserrs3lthn` (`ordered_shoes_id`),
  KEY `FK74tnhiaoayisofxtau7sindc1` (`ordered_id`)
);


CREATE TABLE `shoe` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `available` bit(1) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `cost` double DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `imported` bit(1) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `company_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt5ko7n35e8llqgljb3vqboxhi` (`company_id`)
);

LOCK TABLES `shoe` WRITE;
/*!40000 ALTER TABLE `shoe` DISABLE KEYS */;
INSERT INTO `shoe` VALUES (1,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','зелені',NULL,_binary '\0',NULL,_binary '','200',NULL,'https://images.ua.prom.st/2192904432_demisezonni-chereviki-dr.jpg',1499,NULL),(2,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','шкіра',NULL,_binary '\0',NULL,_binary '','191',NULL,'https://images.ua.prom.st/2186249047_demisezonni-chereviki-dr.jpg',1499,NULL),(3,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','лаковані',NULL,_binary '\0',NULL,_binary '','192м',NULL,'https://images.ua.prom.st/2186198494_demisezonni-chereviki-dr.jpg',1599,NULL),(4,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','замш коричневі',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2193024711_napivchereviki-tufli.jpg',1399,NULL),(5,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','білі',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2192962262_napivchereviki-tufli.jpg',1399,NULL),(6,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','марсала',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2192966451_napivchereviki-tufli.jpg',1399,NULL),(7,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','замш',NULL,_binary '\0',NULL,_binary '','200',NULL,'https://images.ua.prom.st/2192909779_demisezonni-chereviki-dr.jpg',1499,NULL),(8,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','замш',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2186278197_napivchereviki-tufli.jpg',1399,NULL),(9,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','шкіра',NULL,_binary '\0',NULL,_binary '','дм-2',NULL,'https://images.ua.prom.st/2186274677_napivchereviki-tufli.jpg',1399,NULL),(10,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','лаковані',NULL,_binary '\0',NULL,_binary '','191',NULL,'https://static.tildacdn.com/tild3436-6561-4530-a136-336135366638/12.jpg',1499,NULL),(11,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','замш сірі',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2193019327_napivchereviki-tufli.jpg',1399,NULL),(12,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','шкіра',NULL,_binary '\0',NULL,_binary '','200',NULL,'https://images.ua.prom.st/2191360587_demisezonni-chereviki-dr.jpg',1499,NULL),(13,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','шкіра',NULL,_binary '\0',NULL,_binary '','192м',NULL,'https://static.tildacdn.com/tild3137-6337-4136-b437-383731623364/photo.jpg',1599,NULL),(14,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','червоні',NULL,_binary '\0',NULL,_binary '','дм',NULL,'https://images.ua.prom.st/2192990260_napivchereviki-tufli.jpg',1399,NULL),(15,'2020-02-27 13:34:29','2020-02-27 13:34:29',_binary '','шкіра',NULL,_binary '\0',NULL,_binary '','196',NULL,'https://images.ua.prom.st/2186258060_demisezonni-chereviki-dr.jpg',1499,NULL);
/*!40000 ALTER TABLE `shoe` ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-27 13:42:41
