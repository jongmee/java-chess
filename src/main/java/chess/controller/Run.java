package chess.controller;

import chess.model.board.ChessBoard;
import chess.model.board.Turn;
import chess.model.evaluation.PositionEvaluation;
import chess.service.ChessGameService;
import chess.view.input.GameArguments;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.input.MoveArguments;
import chess.view.output.OutputView;

public class Run implements GameState {
    private final ChessBoard chessBoard;
    private final Turn turn;
    private final ChessGameService chessGameService;

    public Run(ChessBoard chessBoard, Turn turn, ChessGameService chessGameService) {
        this.chessBoard = chessBoard;
        this.turn = turn;
        this.chessGameService = chessGameService;
    }

    public static Run initializeWithFirstTurn(ChessBoard chessBoard, ChessGameService chessGameService) {
        Turn initialTurn = chessGameService.createOrGetInitialTurn(chessBoard);
        return new Run(chessBoard, initialTurn, chessGameService);
    }

    @Override
    public GameState run(InputView inputView, OutputView outputView) {
        GameArguments gameArguments = inputView.readMoveArguments();
        GameCommand gameCommand = gameArguments.gameCommand();
        if (gameCommand.isEnd()) {
            return new End();
        }
        if (gameCommand.isMove()) {
            move(gameArguments.moveArguments(), outputView);
            Turn nextTurn = chessGameService.saveNextTurn(chessBoard, turn);
            return new Run(chessBoard, nextTurn, chessGameService);
        }
        if (gameCommand.isTie()) {
            chessGameService.saveGameResult(chessBoard);
            return new End();
        }
        evaluateCurrentBoard(outputView);
        return this;
    }

    private void move(MoveArguments moveArguments, OutputView outputView) {
        chessGameService.move(chessBoard, turn, moveArguments);
        outputView.printChessBoard(chessBoard);
        saveGameResult();
    }

    private void saveGameResult() {
        if (!chessBoard.canContinueToMove()) {
            chessGameService.saveGameResult(chessBoard);
        }
    }

    private void evaluateCurrentBoard(OutputView outputView) {
        PositionEvaluation positionEvaluation = chessBoard.evaluateCurrentBoard();
        outputView.printPositionEvaluation(positionEvaluation);
    }

    @Override
    public boolean canContinue() {
        return chessBoard.canContinueToMove();
    }
}
