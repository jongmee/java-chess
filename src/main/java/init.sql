USE chess;

CREATE TABLE chess_board
(
    chess_board_id bigint NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (chess_board_id)
);

CREATE TABLE piece
(
    file           varchar(10) NOT NULL,
    `rank`         int         NOT NULL,
    type           varchar(10) NOT NULL,
    chess_board_id bigint      NOT NULL,
    side           varchar(10) NOT NULL,
    FOREIGN KEY (`chess_board_id`) REFERENCES `chess_board` (`chess_board_id`),
    PRIMARY KEY (file, `rank`, chess_board_id)
);

CREATE TABLE turn
(
    side           varchar(10) NOT NULL,
    chess_board_id bigint      NOT NULL,
    FOREIGN KEY (chess_board_id) references `chess_board` (`chess_board_id`),
    PRIMARY KEY (chess_board_id)
);

