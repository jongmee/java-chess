GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

CREATE DATABASE IF NOT EXISTS `chess_test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE chess_test;

CREATE TABLE chess_board
(
    id BIGINT      NOT NULL AUTO_INCREMENT,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    game_result    VARCHAR(15) NOT NULL,
    turn           VARCHAR(10) NOT NULL,
    PRIMARY KEY (chess_board_id)
);

CREATE TABLE piece
(
    file           VARCHAR(10) NOT NULL,
    `rank`         INT         NOT NULL,
    type           VARCHAR(10) NOT NULL,
    chess_board_id BIGINT      NOT NULL,
    side           VARCHAR(10) NOT NULL,
    FOREIGN KEY (`chess_board_id`) REFERENCES `chess_board` (`chess_board_id`),
    PRIMARY KEY (file, `rank`, chess_board_id)
);
