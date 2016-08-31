/*
Navicat MySQL Data Transfer

Source Server         : 56
Source Server Version : 50712
Source Host           : 121.40.210.56:3306
Source Database       : daydaycook

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2016-06-01 19:34:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for recommend
-- ----------------------------
DROP TABLE IF EXISTS `recommend`;
CREATE TABLE `recommend` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipe_id` bigint(20) NOT NULL,
  `recommend_type` char(1) NOT NULL,
  `str_date` varchar(20) DEFAULT NULL,
  `sort_id` int(11) DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `seq_id` (`id`) USING HASH,
  KEY `seq_recipeid` (`recipe_id`,`recommend_type`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=latin1;


/*****************2016-06-17*********************************/
----HAN.HAN
CREATE TABLE  searchtag  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `createTime` datetime NOT NULL COMMENT '创建时间',
    `modifyTime` datetime NOT NULL COMMENT '修改时间',
    `search_count` bigint(20) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=`InnoDB` AUTO_INCREMENT=18 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='' CHECKSUM=0 DELAY_KEY_WRITE=0;

CREATE TABLE  searchtag_multilanguage (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tag_id` bigint(20) NOT NULL COMMENT '搜索标签id',
    `tag_name` varchar(200) NOT NULL COMMENT '搜索标签名称',
    `language_id` bigint(20) NOT NULL COMMENT '对应语言',
    PRIMARY KEY (`id`)
) ENGINE=`InnoDB` AUTO_INCREMENT=31 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ROW_FORMAT=COMPACT COMMENT='' CHECKSUM=0 DELAY_KEY_WRITE=0;