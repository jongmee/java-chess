package chess.model.board;

import chess.model.evaluation.GameResult;
import chess.model.evaluation.PositionEvaluation;
import chess.model.piece.Blank;
import chess.model.piece.King;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.Path;
import chess.model.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class ChessBoard {
    private final long id;
    private final Map<Position, Piece> board;

    public ChessBoard(long id, Map<Position, Piece> board) {
        this.id = id;
        this.board = new HashMap<>(board);
    }

    public Piece move(Position sourcePosition, Position targetPosition, Turn turn) {
        Piece sourcePiece = board.get(sourcePosition);
        validateSource(sourcePiece, turn);
        Piece targetPiece = board.get(targetPosition);
        validateTargetPiece(sourcePiece, targetPiece);
        Path path = sourcePiece.findPath(sourcePosition, targetPosition, targetPiece);
        validatePathIsEmpty(path);
        validatePathContainsPiece(path);
        replacePiece(sourcePiece, sourcePosition, targetPosition);
        return sourcePiece;
    }

    private void validateSource(Piece sourcePiece, Turn turn) {
        if (sourcePiece.equals(Blank.INSTANCE)) {
            throw new IllegalArgumentException("소스 위치에 기물이 존재하지 않습니다.");
        }
        if (turn.doesNotMatch(sourcePiece)) {
            throw new IllegalArgumentException("소스 위치에 있는 기물이 현재 게임 차례에 맞지 않습니다.");
        }
    }

    private void validateTargetPiece(Piece sourcePiece, Piece targetPiece) {
        if (!targetPiece.equals(Blank.INSTANCE) && sourcePiece.isSameSide(targetPiece)) {
            throw new IllegalArgumentException("타겟 위치에 아군 기물이 존재합니다.");
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

    private void replacePiece(Piece sourcePiece, Position sourcePosition, Position targetPosition) {
        board.put(sourcePosition, Blank.INSTANCE);
        board.put(targetPosition, sourcePiece);
    }

    public boolean canNotProgress() {
        return !Side.colors()
                .stream()
                .allMatch(side -> board.containsValue(King.from(side)));
    }

    public PositionEvaluation evaluateCurrentBoard() {
        return new PositionEvaluation(board);
    }

    public GameResult determineGameResult() {
        List<GameResult> gameResults = Side.colors()
                .stream()
                .filter(side -> board.containsValue(King.from(side)))
                .map(GameResult::from)
                .toList();
        if (gameResults.size() == Side.colorsSize()) {
            return GameResult.TIE;
        }
        return gameResults.stream()
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("게임이 이미 종료되어 결과를 판단할 수 없습니다."));
    }

    public Map<Position, Piece> getBoard() {
        return unmodifiableMap(board);
    }

    public long getId() {
        return id;
    }
}
