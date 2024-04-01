package chess.repository.dto;

import chess.model.board.ChessBoard;
import chess.model.board.Turn;

public record LatestChessBoardDto(ChessBoard chessBoard, Turn turn) {
}
