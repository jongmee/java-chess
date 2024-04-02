package chess.dao.dto;

import chess.model.board.Turn;

public record NewChessBoardDto(long id, Turn turn) {
}
