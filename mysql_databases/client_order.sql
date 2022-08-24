/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.18 : Database - client_order
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`client_order` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `client_order`;

/*Table structure for table `client_order_info` */

DROP TABLE IF EXISTS `client_order_info`;

CREATE TABLE `client_order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) DEFAULT NULL,
  `out_trade_no` varchar(300) DEFAULT NULL COMMENT '订单交易号',
  `hoscode` varchar(30) DEFAULT NULL COMMENT '医院编号',
  `hosname` varchar(100) DEFAULT NULL COMMENT '医院名称',
  `depcode` varchar(30) DEFAULT NULL COMMENT '科室编号',
  `depname` varchar(20) DEFAULT NULL COMMENT '科室名称',
  `title` varchar(20) DEFAULT NULL COMMENT '医生职称',
  `schedule_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '排班编号（医院自己的排班主键）',
  `reserve_date` date DEFAULT NULL COMMENT '安排日期',
  `reserve_time` tinyint(3) DEFAULT '0' COMMENT '安排时间（0：上午 1：下午）',
  `patient_id` bigint(20) DEFAULT NULL COMMENT '就诊人id',
  `patient_name` varchar(20) DEFAULT NULL COMMENT '就诊人名称',
  `patient_phone` varchar(11) DEFAULT NULL COMMENT '就诊人手机',
  `booking_record_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '预约记录唯一标识（医院预约记录主键）',
  `number` int(11) DEFAULT NULL COMMENT '预约号序',
  `fetch_time` varchar(50) DEFAULT NULL COMMENT '建议取号时间',
  `fetch_address` varchar(255) DEFAULT NULL COMMENT '取号地点',
  `amount` decimal(10,0) DEFAULT NULL COMMENT '医事服务费',
  `quit_time` datetime DEFAULT NULL COMMENT '退号时间',
  `order_status` tinyint(3) DEFAULT NULL COMMENT '订单状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_hoscode` (`hoscode`),
  KEY `idx_hos_schedule_id` (`schedule_id`),
  KEY `idx_hos_record_id` (`booking_record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='订单表';

/*Data for the table `client_order_info` */

insert  into `client_order_info`(`id`,`user_id`,`out_trade_no`,`hoscode`,`hosname`,`depcode`,`depname`,`title`,`schedule_id`,`reserve_date`,`reserve_time`,`patient_id`,`patient_name`,`patient_phone`,`booking_record_id`,`number`,`fetch_time`,`fetch_address`,`amount`,`quit_time`,`order_status`,`create_time`,`update_time`,`is_deleted`) values 
(1,1,'164059024887936','001','北京协和医院','200040878','多发性硬化专科门诊','副主任医师','1475344815803617282','2021-12-30',1,2,'李慧君','13223456789','1',18,'2021-12-3009:00前','一层114窗口',100,'2021-12-29 15:30:00',0,'2021-12-27 15:30:48','2021-12-28 12:47:45',0),
(2,1,'164059051827229','001','北京协和医院','200040878','多发性硬化专科门诊','副主任医师','1475344815174471682','2021-12-28',0,3,'谢峰','13323456789','2',35,'2021-12-2809:00前','一层114窗口',100,'2021-12-27 15:30:00',0,'2021-12-27 15:35:18','2021-12-28 12:47:47',0),
(3,1,'164059060213868','001','北京协和医院','200040878','多发性硬化专科门诊','医师','1475344820299911169','2022-01-05',0,1,'杨先度','13123456789','3',12,'2022-01-0509:00前','一层114窗口',100,'2022-01-04 15:30:00',0,'2021-12-27 15:36:42','2021-12-28 12:47:48',0),
(4,1,'164059218228721','001','北京协和医院','200040878','多发性硬化专科门诊','副主任医师','1475344821109411842','2022-01-05',1,1,'杨先度','13123456789','4',18,'2022-01-0509:00前','一层114窗口',100,'2022-01-04 15:30:00',0,'2021-12-27 16:03:02','2021-12-28 12:47:49',0),
(5,1,'164059245841276','001','北京协和医院','200040878','多发性硬化专科门诊','医师','1475344820299911169','2022-01-05',0,1,'杨先度','13123456789','5',13,'2022-01-0509:00前','一层114窗口',100,'2022-01-04 15:30:00',0,'2021-12-27 16:07:38','2021-12-28 12:47:50',0),
(6,1,'164059254400795','001','北京协和医院','200040878','多发性硬化专科门诊','医师','1475344820299911169','2022-01-05',0,1,'杨先度','13123456789','6',14,'2022-01-0509:00前','一层114窗口',100,'2022-01-04 15:30:00',0,'2021-12-27 16:09:04','2021-12-28 12:47:51',0),
(7,1,'16405927508081','001','北京协和医院','200040878','多发性硬化专科门诊','副主任医师','1475344815665205250','2021-12-30',1,3,'谢峰','13323456789','7',18,'2021-12-3009:00前','一层114窗口',100,'2021-12-29 15:30:00',0,'2021-12-27 16:12:30','2021-12-28 12:48:00',0),
(8,1,'164066701547993','001','北京协和医院','200040878','多发性硬化专科门诊','副主任医师','1475344816432762881','2022-01-01',1,1,'杨先度','13123456789','8',18,'2022-01-0109:00前','一层114窗口',100,'2021-12-31 15:30:00',0,'2021-12-28 12:50:15','2021-12-28 12:50:15',0),
(9,1,'16406844217648','001','北京协和医院','200040878','多发性硬化专科门诊','医师','1475344818093707265','2022-01-03',0,3,'谢峰','13323456789','9',12,'2022-01-0309:00前','一层114窗口',100,'2022-01-02 15:30:00',0,'2021-12-28 17:40:21','2021-12-28 17:40:21',0);

/*Table structure for table `payment_info` */

DROP TABLE IF EXISTS `payment_info`;

CREATE TABLE `payment_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `out_trade_no` varchar(30) DEFAULT NULL COMMENT '对外业务编号',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `payment_type` tinyint(1) DEFAULT NULL COMMENT '支付类型（微信 支付宝）',
  `trade_no` varchar(50) DEFAULT NULL COMMENT '交易编号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `subject` varchar(200) DEFAULT NULL COMMENT '交易内容',
  `payment_status` tinyint(3) DEFAULT NULL COMMENT '支付状态',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `callback_content` varchar(1000) DEFAULT NULL COMMENT '回调信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_out_trade_no` (`out_trade_no`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='支付信息表';

/*Data for the table `payment_info` */

insert  into `payment_info`(`id`,`out_trade_no`,`order_id`,`payment_type`,`trade_no`,`total_amount`,`subject`,`payment_status`,`callback_time`,`callback_content`,`create_time`,`update_time`,`is_deleted`) values 
(1,'164059024887936',1,2,NULL,100.00,'2021-12-30|北京协和医院|多发性硬化专科门诊|副主任医师',1,NULL,NULL,'2021-12-28 19:09:51','2021-12-28 19:10:46',0);

/*Table structure for table `refund_info` */

DROP TABLE IF EXISTS `refund_info`;

CREATE TABLE `refund_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `out_trade_no` varchar(50) DEFAULT NULL COMMENT '对外业务编号',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单编号',
  `payment_type` tinyint(3) DEFAULT NULL COMMENT '支付类型（微信 支付宝）',
  `trade_no` varchar(50) DEFAULT NULL COMMENT '交易编号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `subject` varchar(200) DEFAULT NULL COMMENT '交易内容',
  `refund_status` tinyint(3) DEFAULT NULL COMMENT '退款状态',
  `callback_content` varchar(1000) DEFAULT NULL COMMENT '回调信息',
  `callback_time` datetime DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_out_trade_no` (`out_trade_no`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='退款信息表';

/*Data for the table `refund_info` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
