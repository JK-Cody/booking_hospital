/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.18 : Database - hospital_member
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`hospital_member` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `hospital_member`;

/*Table structure for table `member_list` */

DROP TABLE IF EXISTS `member_list`;

CREATE TABLE `member_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `hoscode` varchar(30) DEFAULT NULL COMMENT '医院编号',
  `hosname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '医院名称',
  `api_url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'api基础路径(每个医院需转发的统一路径)',
  `sign_key` varchar(50) DEFAULT NULL COMMENT '签名秘钥',
  `contacts_name` varchar(20) DEFAULT NULL COMMENT '联系人',
  `contacts_phone` varchar(11) DEFAULT NULL COMMENT '联系人手机',
  `status` tinyint(3) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hoscode` (`hoscode`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='医院设置表';

/*Data for the table `member_list` */

insert  into `member_list`(`id`,`hoscode`,`hosname`,`api_url`,`sign_key`,`contacts_name`,`contacts_phone`,`status`,`create_time`,`update_time`,`is_deleted`) values 
(1,'001','北京协和医院','http://localhost:9998','3b6f9768cc3438fa60cd1eac3f7c5e2a','北京','010',0,'2021-11-02 21:32:56','2022-01-05 14:17:19',0),
(2,'002','大学华西医院','http://localhost:9998','dfd4fc1a70b9d4c9e06412f21353552d','四川','028',0,'2021-11-02 21:34:44','2022-01-05 14:17:35',0),
(3,'003','中国人民解放军总医院','http://localhost:9998','80a66a6e21ea3b64b993d800f72a0683','北京','010',0,'2021-11-02 21:36:21','2022-01-05 14:18:10',0),
(4,'004','上海交通大学医学院附属瑞金医院','http://localhost:9998','a551a4ba28a0df17512ce256c43063c7','上海','021',0,'2021-11-02 21:36:25','2022-01-05 14:18:06',0),
(5,'005','上海复旦大学附属中山医院','http://localhost:9998','cb80fd7903f7c04d99287954bd35338e','上海','021',0,'2021-11-02 21:37:50','2022-01-05 14:18:03',0),
(6,'006','广州中山大学附属第一医院','http://localhost:9998','616aadad020a477b4aec85cd4fff43d4','广州','020',0,'2021-11-02 21:38:36','2022-01-05 14:18:02',0),
(7,'007','武汉华中科技大学同济医学院附属同济医院','http://localhost:9998','7d0435a0d9b2840a9d83181d7e63c672','武汉','027',0,'2021-11-02 21:39:44','2022-01-05 14:17:59',0),
(8,'008','空军军医大学西京医院','http://localhost:9998','7523dc9b734dd926236a6b2cac1fc8c9','陕西','029',0,'2021-11-02 21:42:33','2022-01-05 14:17:57',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
