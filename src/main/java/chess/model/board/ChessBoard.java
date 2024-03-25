package chess.model.board;

import chess.model.piece.Blank;
import chess.model.piece.Piece;
import chess.model.position.ChessPosition;
import chess.model.position.Path;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class ChessBoard {
    private final Map<ChessPosition, Piece> board;

    public ChessBoard(Map<ChessPosition, Piece> board) {
        this.board = new HashMap<>(board);
    }

    public void move(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Piece sourcePiece = board.get(sourcePosition);
        validateSourceIsBlank(sourcePiece);
        Piece targetPiece = board.get(targetPosition);
        Path path = sourcePiece.findPath(sourcePosition, targetPosition, targetPiece);
        validatePathIsEmpty(path);
        validatePathContainsPiece(path);
        replacePiece(sourcePiece, sourcePosition, targetPosition);
    }

    private void validateSourceIsBlank(Piece sourcePiece) {
        if (sourcePiece.equals(Blank.INSTANCE)) {
            throw new IllegalArgumentException("소스 위치에 기물이 존재하지 않습니다.");
        }
    }

    private void validatePathIsEmpty(Path path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("타겟 위치로 이동할 수 없습니다.");
        }
    }

    private void validatePathContainsPiece(Path path) {
        if (path.containsPiece(board)) {
            throw new IllegalArgumentException("이동 경로에 기물이 존재하여 움직일 수 없습니다.");
        }
    }

    private void replacePiece(Piece sourcePiece, ChessPosition sourcePosition, ChessPosition targetPosition) {
        board.put(sourcePosition, Blank.INSTANCE);
        board.put(targetPosition, sourcePiece);
    }

    public Map<ChessPosition, Piece> getBoard() {
        return unmodifiableMap(board);
    }
}
