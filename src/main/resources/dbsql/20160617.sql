/*
Navicat MySQL Data Transfer

Source Server         : zhiqu
Source Server Version : 50625
Source Host           : 192.168.0.116:3306
Source Database       : caipu

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2016-06-17 10:50:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for recipe_comment_language
-- ----------------------------
DROP TABLE IF EXISTS `recipe_comment_language`;
CREATE TABLE `recipe_comment_language` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) DEFAULT NULL,
  `language_id` int(11) DEFAULT NULL,
  `content` text,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for recipe_discuss_comment_language
-- ----------------------------
DROP TABLE IF EXISTS `recipe_discuss_comment_language`;
CREATE TABLE `recipe_discuss_comment_language` (
  `id` int(11) NOT NULL,
  `disscuss_comment_id` int(11) DEFAULT NULL,
  `language_id` int(11) DEFAULT NULL,
  `content` text,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table recipe_comment add language_id int(11);

alter table recipe_discuss_comment add language_id int(11);


update recipe_comment set language_id = 3;

update recipe_discuss_comment set language_id = 3;


