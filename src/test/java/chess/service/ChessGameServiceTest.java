package chess.service;

import chess.model.board.*;
import chess.model.evaluation.GameResult;
import chess.model.piece.Blank;
import chess.model.piece.Pawn;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import chess.dao.DataBaseCleaner;
import chess.dao.ChessBoardDao;
import chess.dao.PieceDao;
import chess.dao.dto.GameResultDto;
import chess.dao.util.TestMySqlConnector;
import chess.view.input.MoveArguments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChessGameServiceTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final ChessGameService chessGameService = new ChessGameService(TestMySqlConnector.INSTANCE);
    private final ChessBoardDao chessBoardDao = new ChessBoardDao(TestMySqlConnector.INSTANCE);
    private final PieceDao pieceDao = new PieceDao(TestMySqlConnector.INSTANCE);

    @BeforeEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("이전에 게임하던 체스판이 존재하지 않으면 체스판을 생성한다.")
    void createInitialChessBoard() {
        // when
        ChessBoard chessBoard = chessGameService.createOrGetInitialChessBoard().chessBoard();

        // then
        assertThat(chessBoard.getBoard()).hasSize(64);
    }

    @Test
    @DisplayName("이전에 게임하던 체스판이 존재하면 체스판을 조회한다.")
    void getInitialChessBoard() {
        // given
        ChessBoard existingChessBoard = createTestChessBoard();
        long expectedChessBoardId = existingChessBoard.getId();

        // when
        ChessBoard chessBoard = chessGameService.createOrGetInitialChessBoard().chessBoard();

        // then
        long actualChessBoardId = chessBoard.getId();
        assertThat(actualChessBoardId).isSameAs(expectedChessBoardId);
    }

    @Test
    @DisplayName("기물을 사용자의 입력으로 이동시킨다.")
    void move() {
        // given
        ChessBoard existingChessBoard = createTestChessBoard();
        Turn turn = Turn.from(Side.WHITE);
        MoveArguments moveArguments = new MoveArguments("b", 2, "b", 3);

        // when
        chessGameService.move(existingChessBoard, turn, moveArguments);

        // then
        ChessBoard chessBoard = chessBoardDao.findLatest().get().chessBoard();
        assertThat(chessBoard.getBoard())
                .containsAllEntriesOf(Map.of(
                        Position.of(File.B, Rank.THREE), Pawn.from(Side.WHITE),
                        Position.of(File.B, Rank.TWO), Blank.INSTANCE)
                );
    }

    @Test
    @DisplayName("체스판의 다음 순서를 저장한다.")
    void saveNextTurn() {
        // given
        ChessBoard chessBoard = createTestChessBoard();
        Turn turn = Turn.from(Side.WHITE);

        // when
        Turn nextTurn = chessGameService.saveNextTurn(chessBoard, turn);

        // then
        assertThat(nextTurn).isEqualTo(Turn.from(Side.BLACK));
    }

    @Test
    @DisplayName("체스 게임 결과를 저장한다.")
    void saveGameResult() {
        // given
        Map<Position, Piece> allPieces = new TestChessBoardGenerator(ChessBoardFixture.WHITE_FOOLS_MATE_LOSE).create();
        long chessBoardId = chessBoardDao.save(Turn.from(Side.WHITE)).get().id();
        pieceDao.saveAll(allPieces, chessBoardId);

        ChessBoard chessBoard = new ChessBoard(chessBoardId, allPieces);

        // when
        chessGameService.saveGameResult(chessBoard);

        // then
        List<GameResultDto> gameResults = chessBoardDao.findAllGameResult();
        assertThat(gameResults).hasSize(1)
                .extracting(GameResultDto::gameResult)
                .contains(GameResult.BLACK_WIN);
    }

    private ChessBoard createTestChessBoard() {
        ChessBoardInitializer chessBoardInitializer = new ChessBoardInitializer();
        Map<Position, Piece> allPieces = chessBoardInitializer.create();
        long chessBoardId = chessBoardDao.save(Turn.from(Side.WHITE)).get().id();
        pieceDao.saveAll(allPieces, chessBoardId);
        return new ChessBoard(chessBoardId, allPieces);
    }
}
