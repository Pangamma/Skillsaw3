-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.6.21-MariaDB-0ubuntu0.22.04.2 - Ubuntu 22.04
-- Server OS:                    debian-linux-gnu
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for skillsaw
CREATE DATABASE IF NOT EXISTS `skillsaw` /*!40100 DEFAULT CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci */;
USE `skillsaw`;

-- Dumping structure for table skillsaw.activity_log
CREATE TABLE IF NOT EXISTS `activity_log` (
  `user_id` int(11) NOT NULL,
  `uuid` varchar(64) DEFAULT NULL,
  `server` varchar(64) NOT NULL DEFAULT '?',
  `is_afk` tinyint(4) NOT NULL DEFAULT 0,
  `minutes` int(11) NOT NULL DEFAULT 12,
  `time_online` timestamp NOT NULL DEFAULT current_timestamp(),
  KEY `time_online` (`time_online`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.crreactionstats
CREATE TABLE IF NOT EXISTS `crreactionstats` (
  `uuid` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `wins` int(11) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.donation_log
CREATE TABLE IF NOT EXISTS `donation_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `uuid` varchar(64) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `cost` double(10,2) DEFAULT NULL,
  `package_name` varchar(128) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for event skillsaw.evt_migrate_donator_titles_from_burrow
DELIMITER //
CREATE DEFINER=`pangamma`@`localhost` EVENT `evt_migrate_donator_titles_from_burrow` ON SCHEDULE EVERY 1 HOUR STARTS '2019-03-06 05:38:47' ENDS '2019-05-31 09:38:47' ON COMPLETION PRESERVE DISABLE DO BEGIN
 UPDATE skillsaw_users SET custom_titles = CONCAT(custom_titles,'&aEmerald	&aEmerald','\n') WHERE `uuid` IN (SELECT `child` FROM `permissions`.inheritance WHERE `parent` = 'Emerald') AND custom_titles NOT LIKE '%Emerald%';
 UPDATE skillsaw_users SET custom_titles = CONCAT(custom_titles,'&8Iron	&8Iron','\n') WHERE `uuid` IN (SELECT `child` FROM `permissions`.inheritance WHERE `parent` = 'Iron') AND custom_titles NOT LIKE '%Iron%';
 UPDATE skillsaw_users SET custom_titles = CONCAT(custom_titles,'&9Lapis	&9Lapis','\n') WHERE `uuid` IN (SELECT `child` FROM `permissions`.inheritance WHERE `parent` = 'Lapis') AND custom_titles NOT LIKE '%Lapis%';
 UPDATE skillsaw_users SET custom_titles = CONCAT(custom_titles,'&bDiamond	&bDiamond','\n') WHERE `uuid` IN (SELECT `child` FROM `permissions`.inheritance WHERE `parent` = 'Diamond') AND custom_titles NOT LIKE '%Diamond%';
 UPDATE skillsaw_users SET custom_titles = CONCAT(custom_titles,'&6Gold	&6Gold','\n') WHERE `uuid` IN (SELECT `child` FROM `permissions`.inheritance WHERE `parent` = 'Gold') AND custom_titles NOT LIKE '%Gold%';
END//
DELIMITER ;

-- Dumping structure for event skillsaw.evt_updateActivityLevelCache
DELIMITER //
CREATE DEFINER=`pangamma`@`localhost` EVENT `evt_updateActivityLevelCache` ON SCHEDULE EVERY 6 MINUTE STARTS '2018-12-24 06:03:21' ON COMPLETION PRESERVE ENABLE DO BEGIN
 UPDATE
`skillsaw_users` `u` LEFT JOIN 
(SELECT user_id, (round(SUM(`minutes`)/12,0)) as `c_active` FROM activity_log `a` 
where (`a`.`time_online` > cast((now() - interval 14 day) as datetime))
GROUP BY `user_id`) as `a` ON `a`.user_id = `u`.user_id
SET `u`.activity_score = IFNULL(`a`.c_active,0)  WHERE `u`.activity_score != IFNULL(`a`.c_active, 0);
END//
DELIMITER ;

-- Dumping structure for table skillsaw.messages
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server` varchar(128) NOT NULL,
  `channel` varchar(128) NOT NULL DEFAULT '1',
  `username` varchar(50) NOT NULL,
  `message` varchar(2048) NOT NULL,
  `is_command` smallint(6) NOT NULL DEFAULT 0,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  `uuid` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=537118 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for procedure skillsaw.proc_rollback_user
DELIMITER //
CREATE DEFINER=`pangamma`@`localhost` PROCEDURE `proc_rollback_user`(
	IN `issuer_id` INT,
	IN `time` TIMESTAMP
)
BEGIN
SELECT * FROM (
	SELECT id, issuer_name,issuer_id, target_name,target_id, skill_type, olevel, nlevel,`time` FROM promo_log
	WHERE target_id IN (SELECT target_id FROM promo_log WHERE issuer_id = @issuer_id AND `time` >= @time)
	GROUP BY skill_type, target_id
	ORDER BY id DESC) promo;
	
END//
DELIMITER ;

-- Dumping structure for table skillsaw.promo_log
CREATE TABLE IF NOT EXISTS `promo_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `issuer_name` varchar(50) NOT NULL,
  `target_name` varchar(50) NOT NULL,
  `skill_type` varchar(128) NOT NULL,
  `olevel` int(11) NOT NULL,
  `nlevel` int(11) NOT NULL,
  `issuer_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `location` text NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6770 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for procedure skillsaw.quest_welcomer
DELIMITER //
CREATE DEFINER=`pangamma`@`localhost` PROCEDURE `quest_welcomer`()
BEGIN

SELECT  `id`,  `server`,  `username`,  `uuid`,  LEFT(`message`, 256),  `is_command`,  `time` FROM `skillsaw`.`messages` 
WHERE is_command = 0
AND username = 'R3AL_Sniper' AND (message LIKE '%welcome%' OR message LIKE '%wb%')
ORDER BY `id` DESC LIMIT 3000;
END//
DELIMITER ;

-- Dumping structure for table skillsaw.random_data
CREATE TABLE IF NOT EXISTS `random_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `segment` varchar(50) NOT NULL COMMENT 'For searching later',
  `request_info` varchar(4096) NOT NULL,
  `key` varchar(50) NOT NULL,
  `value` varchar(4096) NOT NULL,
  `created` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2479 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.replog
CREATE TABLE IF NOT EXISTS `replog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rep_type` int(11) NOT NULL DEFAULT 0,
  `issuer_id` int(11) NOT NULL DEFAULT 0,
  `target_id` int(11) NOT NULL DEFAULT 0,
  `issuer_name` varchar(50) NOT NULL,
  `target_name` varchar(50) NOT NULL,
  `amount` double NOT NULL DEFAULT 0,
  `reason` varchar(2048) NOT NULL DEFAULT 'N/A',
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `rep_type` (`rep_type`)
) ENGINE=InnoDB AUTO_INCREMENT=26884 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.sb_servers
CREATE TABLE IF NOT EXISTS `sb_servers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT 1 COMMENT 'Owner of the server',
  `is_active` tinyint(4) NOT NULL DEFAULT 1,
  `name` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  `status` varchar(256) DEFAULT NULL,
  `status_code` int(11) NOT NULL DEFAULT 1,
  `bump_url` varchar(1024) NOT NULL,
  `view_url` varchar(1024) DEFAULT NULL,
  `username` varchar(128) NOT NULL,
  `password` varchar(256) NOT NULL,
  `last_bump` bigint(20) NOT NULL DEFAULT 0,
  `fail_count` int(11) NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.scavenger_hunt
CREATE TABLE IF NOT EXISTS `scavenger_hunt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `uuid` varchar(64) NOT NULL,
  `group_key` varchar(64) NOT NULL DEFAULT 'misc',
  `item_key` varchar(64) NOT NULL,
  `command_sender` varchar(50) NOT NULL,
  `world` varchar(64) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.servers
CREATE TABLE IF NOT EXISTS `servers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(50) DEFAULT NULL,
  `ipv4` varchar(256) NOT NULL DEFAULT '127.0.0.1',
  `max_players` int(11) DEFAULT 0,
  `version` varchar(128) DEFAULT NULL,
  `ext_ip` varchar(256) NOT NULL DEFAULT '0.0.0.0',
  `api_key` varchar(1024) DEFAULT NULL,
  `is_valid` tinyint(4) NOT NULL DEFAULT 0,
  `is_read_only` tinyint(4) NOT NULL DEFAULT 0,
  `last_update` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `json` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='\r\n	public String apikey;\r\n	public String ipv4;\r\n	public String version;\r\n	public String bukkitVersion;\r\n	public String motd;\r\n	public String name;\r\n	public int maxPlayers;\r\n	public int port;';

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.skillsaw_users
CREATE TABLE IF NOT EXISTS `skillsaw_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(40) NOT NULL,
  `username` varchar(40) NOT NULL COMMENT 'Last username used on the server',
  `pw_hash` varchar(40) DEFAULT NULL,
  `is_staff` tinyint(4) NOT NULL DEFAULT 0,
  `is_instructor` tinyint(4) NOT NULL DEFAULT 0,
  `display_name` varchar(128) DEFAULT NULL,
  `ipv4` varchar(128) DEFAULT NULL,
  `current_title` varchar(128) NOT NULL,
  `custom_titles` varchar(4096) NOT NULL,
  `chat_color` varchar(32) NOT NULL,
  `activity_score` int(11) NOT NULL DEFAULT 0,
  `activity_total_minutes` int(11) NOT NULL DEFAULT 0,
  `total_minutes_played` int(11) DEFAULT NULL,
  `tpalock` varchar(32) NOT NULL DEFAULT '?',
  `slog_settings` varchar(256) NOT NULL DEFAULT '{}',
  `rep_level` int(11) NOT NULL DEFAULT 0,
  `natural_rep` double NOT NULL DEFAULT 0,
  `staff_rep` double NOT NULL DEFAULT 0,
  `last_ping_host` varchar(128) DEFAULT NULL,
  `last_ping_time` bigint(20) NOT NULL DEFAULT 0,
  `last_played` bigint(20) NOT NULL DEFAULT 0,
  `first_played` bigint(20) NOT NULL DEFAULT 0,
  `speaking_channel` varchar(1024) NOT NULL DEFAULT '1',
  `sticky_channels` varchar(4096) DEFAULT NULL,
  `ignored_players` varchar(4096) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `s_redstone` int(11) NOT NULL DEFAULT 0,
  `s_organics` int(11) NOT NULL DEFAULT 0,
  `s_pixelart` int(11) NOT NULL DEFAULT 0,
  `s_architecture` int(11) NOT NULL DEFAULT 0,
  `s_terraforming` int(11) NOT NULL DEFAULT 0,
  `s_vehicles` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22752 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Data exporting was unselected.

-- Dumping structure for table skillsaw.titles
CREATE TABLE IF NOT EXISTS `titles` (
  `id` int(11) NOT NULL,
  `short_plain` varchar(64) NOT NULL,
  `short_colored` varchar(64) NOT NULL,
  `long_plain` varchar(64) NOT NULL,
  `long_colored` varchar(64) NOT NULL,
  `thistableisdumb` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Data exporting was unselected.

-- Dumping structure for view skillsaw.view_ip_relations
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_ip_relations` (
	`ipv4` VARCHAR(1) NULL COLLATE 'latin1_swedish_ci',
	`users` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`c` BIGINT(21) NOT NULL,
	`last_updated` TIMESTAMP NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_litebans
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_litebans` (
	`id` INT(11) NOT NULL,
	`server` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`username` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`uuid` VARCHAR(1) NULL COLLATE 'latin1_swedish_ci',
	`message` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`is_command` SMALLINT(6) NOT NULL,
	`time` TIMESTAMP NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_messages_latest
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_messages_latest` (
	`id` INT(11) NOT NULL,
	`channel` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`username` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`message` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`time` TIMESTAMP NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_most_active_instructors
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_most_active_instructors` (
	`username` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`c_active` BIGINT(21) NOT NULL,
	`c_chatting` DECIMAL(26,0) NOT NULL,
	`percent_chatting` DECIMAL(30,0) NOT NULL,
	`last_online` DATETIME(4) NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_most_active_staff
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_most_active_staff` (
	`username` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`c_active` BIGINT(21) NOT NULL,
	`c_chatting` DECIMAL(26,0) NOT NULL,
	`percent_chatting` DECIMAL(30,0) NOT NULL,
	`last_online` DATETIME(4) NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_most_active_staff_and_instructors
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_most_active_staff_and_instructors` (
	`username` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`c_active` BIGINT(21) NOT NULL,
	`c_chatting` DECIMAL(26,0) NOT NULL,
	`percent_chatting` DECIMAL(30,0) NOT NULL,
	`last_online` DATETIME(4) NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_most_active_users
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_most_active_users` (
	`user_id` INT(11) NOT NULL,
	`username` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`c_active` BIGINT(21) NOT NULL,
	`c_chatting` DECIMAL(26,0) NULL,
	`c_afk` DECIMAL(25,0) NULL,
	`minutes` DECIMAL(32,0) NULL,
	`percent_chatting` DECIMAL(30,0) NULL,
	`last_online` DATETIME(4) NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_new_users
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_new_users` (
	`user_id` INT(11) NOT NULL,
	`username` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`join_time` DATETIME(4) NULL,
	`last_login` DATETIME(4) NULL,
	`activity_score` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_replog
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_replog` (
	`id` INT(11) NOT NULL,
	`rep_type` INT(11) NOT NULL,
	`issuer_id` INT(11) NOT NULL,
	`target_id` INT(11) NOT NULL,
	`issuer_name` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`target_name` VARCHAR(1) NOT NULL COMMENT 'Last username used on the server' COLLATE 'latin1_swedish_ci',
	`amount` DOUBLE NOT NULL,
	`reason` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`time` TIMESTAMP NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view skillsaw.view_server_usage
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `view_server_usage` (
	`server` VARCHAR(1) NOT NULL COLLATE 'latin1_swedish_ci',
	`c` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for table skillsaw.votes
CREATE TABLE IF NOT EXISTS `votes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `ipv4` varchar(256) DEFAULT NULL,
  `service_name` varchar(256) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=276 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci COMMENT='I may add count later just to automate some aggregation';

-- Data exporting was unselected.

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_ip_relations`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY INVOKER VIEW `view_ip_relations` AS select `t`.`ipv4` AS `ipv4`,`t`.`users` AS `users`,`t`.`c` AS `c`,`t`.`last_updated` AS `last_updated` from (select `skillsaw_users`.`ipv4` AS `ipv4`,group_concat(`skillsaw_users`.`username` separator ',') AS `users`,count(0) AS `c`,max(`skillsaw_users`.`last_updated`) AS `last_updated` from `skillsaw_users` group by `skillsaw_users`.`ipv4`) `t` where `t`.`c` > 1 order by `t`.`c` desc
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_litebans`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_litebans` AS select `messages`.`id` AS `id`,`messages`.`server` AS `server`,`messages`.`username` AS `username`,`messages`.`uuid` AS `uuid`,`messages`.`message` AS `message`,`messages`.`is_command` AS `is_command`,`messages`.`time` AS `time` from `messages` where `messages`.`is_command` = 1 and (`messages`.`message` like '/ban %' or `messages`.`message` like '/lban %' or `messages`.`message` like '/ipban %' or `messages`.`message` like '/lipban %' or `messages`.`message` like '/banip %' or `messages`.`message` like '/tempipban %' or `messages`.`message` like '/tempbanip %' or `messages`.`message` like '/iptempban %' or `messages`.`message` like '/ipmute %' or `messages`.`message` like '/lipmute %' or `messages`.`message` like '/muteip %' or `messages`.`message` like '/tempipmute %' or `messages`.`message` like '/tempmuteip %' or `messages`.`message` like '/iptempmute %' or `messages`.`message` like '/unmute %' or `messages`.`message` like '/lunmute %' or `messages`.`message` like '/tempunmute %' or `messages`.`message` like '/unmuteip %' or `messages`.`message` like '/ipunmute %' or `messages`.`message` like '/unwarn %' or `messages`.`message` like '/lunwarn %' or `messages`.`message` like '/removewarnings %' or `messages`.`message` like '/rmwarnings %' or `messages`.`message` like '/nmwarn %' or `messages`.`message` like '/tempban %' or `messages`.`message` like '/tban %' or `messages`.`message` like '/ltempban %' or `messages`.`message` like '/tmpban %' or `messages`.`message` like '/tempmute %' or `messages`.`message` like '/ltempmute %' or `messages`.`message` like '/prunehistory %' or `messages`.`message` like '/lprunehistory %' or `messages`.`message` like '/staffrollback %' or `messages`.`message` like '/lstaffrollback %' or `messages`.`message` like '/lockdown %' or `messages`.`message` like '/llockdown %' or `messages`.`message` like '/clearchat %' or `messages`.`message` like '/lclearchat %' or `messages`.`message` like '/mutechat %' or `messages`.`message` like '/lmutechat %' or `messages`.`message` like '/togglechat %' or `messages`.`message` like '/ltogglechat %' or `messages`.`message` like '/unban %' or `messages`.`message` like '/lunban %' or `messages`.`message` like '/lunbanip %' or `messages`.`message` like '/unbanip %' or `messages`.`message` like '/ipunban %' or `messages`.`message` like '/tempunban %' or `messages`.`message` like '/pardon %' or `messages`.`message` like '/pardonip %' or `messages`.`message` like '/tempban %' or `messages`.`message` like '/mute %' or `messages`.`message` like '/lmute %' or `messages`.`message` like '/unmute %' or `messages`.`message` like '/kick %' or `messages`.`message` like '/lkick %' or `messages`.`message` like '/warn %' or `messages`.`message` like '/lwarn %') order by `messages`.`id` desc
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_messages_latest`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_messages_latest` AS select `messages`.`id` AS `id`,`messages`.`channel` AS `channel`,`messages`.`username` AS `username`,`messages`.`message` AS `message`,`messages`.`time` AS `time` from `messages` where `messages`.`is_command` = 0 order by `messages`.`id` desc limit 5000
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_most_active_instructors`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_most_active_instructors` AS select `u`.`username` AS `username`,ifnull(`a`.`c_active`,0) AS `c_active`,ifnull(`a`.`c_chatting`,0) AS `c_chatting`,ifnull(`a`.`percent_chatting`,0) AS `percent_chatting`,ifnull(`a`.`last_online`,from_unixtime(`u`.`last_played` / 1000)) AS `last_online` from (`skillsaw_users` `u` left join `view_most_active_users` `a` on(`a`.`user_id` = `u`.`user_id`)) where `u`.`is_instructor` = 1 order by ifnull(`a`.`c_active`,0) desc limit 500
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_most_active_staff`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_most_active_staff` AS select `u`.`username` AS `username`,ifnull(`a`.`c_active`,0) AS `c_active`,ifnull(`a`.`c_chatting`,0) AS `c_chatting`,ifnull(`a`.`percent_chatting`,0) AS `percent_chatting`,ifnull(`a`.`last_online`,from_unixtime(`u`.`last_played` / 1000)) AS `last_online` from (`skillsaw_users` `u` left join `view_most_active_users` `a` on(`a`.`user_id` = `u`.`user_id`)) where `u`.`is_staff` = 1 order by ifnull(`a`.`c_active`,0) desc limit 500
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_most_active_staff_and_instructors`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_most_active_staff_and_instructors` AS select `u`.`username` AS `username`,ifnull(`a`.`c_active`,0) AS `c_active`,ifnull(`a`.`c_chatting`,0) AS `c_chatting`,ifnull(`a`.`percent_chatting`,0) AS `percent_chatting`,ifnull(`a`.`last_online`,from_unixtime(`u`.`last_played` / 1000)) AS `last_online` from (`skillsaw_users` `u` left join `view_most_active_users` `a` on(`a`.`user_id` = `u`.`user_id`)) where `u`.`is_staff` = 1 or `u`.`is_instructor` = 1 order by ifnull(`a`.`c_active`,0) desc limit 500
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_most_active_users`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_most_active_users` AS select `u`.`user_id` AS `user_id`,`u`.`username` AS `username`,count(0) AS `c_active`,count(0) - sum(`a`.`is_afk`) AS `c_chatting`,sum(`a`.`is_afk`) AS `c_afk`,sum(`a`.`minutes`) AS `minutes`,round(100 * (count(0) - sum(`a`.`is_afk`)) / count(0),0) AS `percent_chatting`,from_unixtime(`u`.`last_played` / 1000) AS `last_online` from (`activity_log` `a` join `skillsaw_users` `u` on(`u`.`user_id` = `a`.`user_id`)) where `a`.`time_online` > cast(current_timestamp() - interval 14 day as datetime) group by `a`.`user_id` order by count(0) desc limit 500
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_new_users`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_new_users` AS select `u`.`user_id` AS `user_id`,`u`.`username` AS `username`,from_unixtime(`u`.`first_played` / 1000) AS `join_time`,from_unixtime(`u`.`last_played` / 1000) AS `last_login`,`a`.`c` AS `activity_score` from (`skillsaw_users` `u` join (select count(0) AS `c`,`activity_log`.`user_id` AS `user_id` from `activity_log` where `activity_log`.`time_online` > cast(current_timestamp() - interval 90 day as datetime) group by `activity_log`.`user_id`) `a` on(`a`.`user_id` = `u`.`user_id`)) order by `u`.`first_played` desc limit 500
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_replog`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_replog` AS select `r`.`id` AS `id`,`r`.`rep_type` AS `rep_type`,`r`.`issuer_id` AS `issuer_id`,`r`.`target_id` AS `target_id`,`ui`.`username` AS `issuer_name`,`ut`.`username` AS `target_name`,`r`.`amount` AS `amount`,`r`.`reason` AS `reason`,`r`.`time` AS `time` from ((`replog` `r` join `skillsaw_users` `ui` on(`ui`.`user_id` = `r`.`issuer_id`)) join `skillsaw_users` `ut` on(`ut`.`user_id` = `r`.`target_id`)) order by `r`.`id` desc
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `view_server_usage`;
CREATE ALGORITHM=UNDEFINED DEFINER=`pangamma`@`localhost` SQL SECURITY DEFINER VIEW `view_server_usage` AS select `a`.`server` AS `server`,count(0) AS `c` from `activity_log` `a` where `a`.`time_online` > cast(current_timestamp() - interval 14 day as datetime) group by `a`.`server` order by count(0) desc
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
