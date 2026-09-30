SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `skills`
--

-- --------------------------------------------------------

--
-- Table structure for table `paste`
--

CREATE TABLE `paste`
(
    `id`           varchar(255)                                                NOT NULL,
    `content`      mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `content_type` varchar(50)                                                 NOT NULL,
    `encoding`     varchar(20)                                                 NOT NULL,
    `expiration`   timestamp                                                   NOT NULL,
    `title`        varchar(300)                                                NOT NULL,
    `created_at`   timestamp                                                   NOT NULL,
    `updated_at`   timestamp                                                   NOT NULL,
    `access_token` varchar(255)                                                NOT NULL,
    `edit_token`   varchar(255)                                                NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `paste`
--

INSERT INTO `paste` (`id`, `content`, `content_type`, `encoding`, `expiration`, `title`, `created_at`, `updated_at`,
                     `access_token`, `edit_token`)
VALUES ('8103108a-a837-4b76-865f-d60948362a0a', 'blabb\r\nblubb\r\nblobb\r\n', 'text/plain', 'UTF-8',
        '2032-01-31 22:59:35', 'test title', '2022-01-07 22:59:35', '2022-01-07 22:59:35',
        'dd4a3f94-5cfe-461f-b0dd-0879b76af109', 'be97b4a8-602c-4004-95ed-a37c6903d123'),
       ('1031aade-cb51-465d-a2b6-2596dedf6634', 'foo bar', 'text/plain', 'UTF-8', '2032-01-31 22:59:35', 'baz',
        '2022-01-07 22:59:35', '2022-01-07 22:59:35', 'a8fce094-4c6d-4f04-8588-d35d087c4432',
        'c0f32e2f-36d4-4ea5-ba49-f1b32860d972'),
       ('cce781b6-9204-42be-9e46-e1f09d20c126', 'expired paste', 'text/plain', 'UTF-8', '2021-01-31 22:59:35',
        'expired paste', '2022-02-15 22:19:41', '2022-02-15 22:19:41', '04df12cf-c508-4d91-83a0-0adad6754d4c',
        '40aa9fea-430d-4ca2-964e-1d02767226ad');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `paste`
--
ALTER TABLE `paste`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `access_token` (`access_token`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
