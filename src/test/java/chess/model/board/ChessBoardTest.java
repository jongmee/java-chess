package chess.model.board;

import chess.model.evaluation.GameResult;
import chess.model.piece.*;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChessBoardTest {

    private ChessBoard defaltChessBoard;

    @BeforeEach
    void setUp() {
        defaltChessBoard = new ChessBoard(1, new ChessBoardInitializer().create());
    }

    @Test
    @DisplayName("Source 위치의 기물을 Target 위치로 이동한다.")
    void move() {
        Position source = Position.of(File.B, Rank.TWO);
        Position target = Position.of(File.B, Rank.FOUR);
        defaltChessBoard.move(source, target, Turn.from(Side.WHITE));

        Map<Position, Piece> board = defaltChessBoard.getBoard();

        assertAll(
                () -> assertThat(board).containsEntry(source, Blank.INSTANCE),
                () -> assertThat(board).doesNotContainEntry(target, Blank.INSTANCE)
        );
    }

    @Test
    @DisplayName("Source 위치에 기물이 없으면 예외가 발생한다.")
    void moveWithBlankSource() {
        // given
        Position source = Position.of(File.H, Rank.SEVEN);
        Position target = Position.of(File.D, Rank.TWO);

        // when & then
        assertThatThrownBy(() -> defaltChessBoard.move(source, target, Turn.from(Side.BLACK)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("게임 차례에 맞지 않는 Source 기물의 위치를 입력하면 예외가 발생한다.")
    void moveWithInvalidTurn() {
        // given
        Position source = Position.of(File.B, Rank.TWO);
        Position target = Position.of(File.B, Rank.FOUR);
        Turn turn = Turn.from(Side.BLACK);

        // when & then
        assertThatThrownBy(() -> defaltChessBoard.move(source, target, turn))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Target 위치에 아군 기물이 존재하면 예외가 발생한다.")
    void moveWhenTargetIsSameSide() {
        // given
        Position source = Position.of(File.A, Rank.ONE);
        Position target = Position.of(File.A, Rank.TWO);
        ChessBoard chessBoard = new ChessBoard(1, Map.of(source, Pawn.from(Side.BLACK), target, Bishop.from(Side.BLACK)));

        // when & then
        assertThatThrownBy(() -> chessBoard.move(source, target, Turn.from(Side.WHITE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("경로가 비어있다면 예외가 발생한다.")
    void moveWhenPathEmpty() {
        // given
        Position source = Position.of(File.A, Rank.ONE);
        Position target = Position.of(File.D, Rank.TWO);

        // when & then
        assertThatThrownBy(() -> defaltChessBoard.move(source, target, Turn.from(Side.WHITE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("이동 경로에 기물이 존재한다면 예외가 발생한다.")
    void moveWhenPathContainsPiece() {
        // given
        Position source = Position.of(File.A, Rank.ONE);
        Position target = Position.of(File.A, Rank.SIX);

        // when & then
        assertThatThrownBy(() -> defaltChessBoard.move(source, target, Turn.from(Side.WHITE)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Knight는 이동 경로에 기물이 존재해도 뛰어넘을 수 있다.")
    void moveKnightWhenPathContainsPiece() {
        // given
        Knight knight = Knight.from(Side.WHITE);
        Position knightPosition = Position.of(File.A, Rank.ONE);
        Position targetPosition = Position.of(File.B, Rank.THREE);

        TestChessBoardGenerator chessBoardGenerator = new TestChessBoardGenerator(ChessBoardFixture.KNIGHT_MOVEMENT_BOARD);
        ChessBoard customChessBoard = new ChessBoard(1, chessBoardGenerator.create());

        // when
        customChessBoard.move(knightPosition, targetPosition, Turn.from(Side.WHITE));

        // then
        Map<Position, Piece> actualBoard = customChessBoard.getBoard();
        assertThat(actualBoard).containsEntry(targetPosition, knight);
    }

    @Test
    @DisplayName("체스보드에 각 진영의 King이 모두 존재하면 게임을 진행할 수 있다.")
    void canNotProgressExistingKings() {
        // when
        boolean result = defaltChessBoard.canNotProgress();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("한 쪽이 이겨 한 King이 존재하지 않을 경우 게임을 진행할 수 없다.")
    void canNotProgress() {
        // given
        TestChessBoardGenerator chessBoardGenerator = new TestChessBoardGenerator(ChessBoardFixture.WHITE_FOOLS_MATE_LOSE);
        ChessBoard customChessBoard = new ChessBoard(1, chessBoardGenerator.create());

        // when
        boolean result = customChessBoard.canNotProgress();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("게임 결과를 결정한다.")
    void determineGameResult() {
        // given
        TestChessBoardGenerator chessBoardGenerator = new TestChessBoardGenerator(ChessBoardFixture.WHITE_FOOLS_MATE_LOSE);
        ChessBoard customChessBoard = new ChessBoard(1, chessBoardGenerator.create());

        // when
        GameResult gameResult = customChessBoard.determineGameResult();

        // then
        assertThat(gameResult).isEqualTo(GameResult.BLACK_WIN);
    }

    @Test
    @DisplayName("모든 진영의 King이 존재한다면 게임 결과가 무승부다.")
    void determineGameResultInTie() {
        // when
        GameResult gameResult = defaltChessBoard.determineGameResult();

        // then
        assertThat(gameResult).isEqualTo(GameResult.TIE);
    }

    @Test
    @DisplayName("이미 종료된 게임은 결과를 판단할 수 없다.")
    void determineGameResultInEndedGame() {
        // given
        TestChessBoardGenerator chessBoardGenerator = new TestChessBoardGenerator(ChessBoardFixture.EMPTY_BOARD);
        ChessBoard customChessBoard = new ChessBoard(1, chessBoardGenerator.create());

        // when & then
        assertThatThrownBy(customChessBoard::determineGameResult)
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
