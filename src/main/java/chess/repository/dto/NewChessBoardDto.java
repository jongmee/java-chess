package chess.repository.dto;

import chess.model.board.Turn;

public record NewChessBoardDto(long id, Turn turn) {
}
