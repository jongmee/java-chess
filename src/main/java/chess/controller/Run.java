package chess.controller;

import chess.model.board.ChessBoard;
import chess.model.board.Turn;
import chess.model.evaluation.PositionEvaluation;
import chess.service.ChessGameService;
import chess.view.input.GameArguments;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.output.OutputView;

public class Run implements GameState {
    private final ChessBoard chessBoard;
    private final Turn turn;

    public Run(ChessBoard chessBoard, Turn turn) {
        this.chessBoard = chessBoard;
        this.turn = turn;
    }

    @Override
    public GameState run(InputView inputView, OutputView outputView, ChessGameService chessGameService) {
        if (chessBoard.canNotProgress()) {
            return new Summarize(chessBoard);
        }
        GameArguments gameArguments = inputView.readMoveArguments();
        return playByCommand(gameArguments, outputView);
    }

    private GameState playByCommand(GameArguments gameArguments, OutputView outputView) {
        GameCommand gameCommand = gameArguments.gameCommand();
        if (gameCommand.isEnd()) {
            return new End();
        }
        if (gameCommand.isTie()) {
            return new Summarize(chessBoard);
        }
        if (gameCommand.isMove()) {
            return new Move(chessBoard, turn, gameArguments.moveArguments());
        }
        evaluateCurrentBoard(outputView);
        return this;
    }

    private void evaluateCurrentBoard(OutputView outputView) {
        PositionEvaluation positionEvaluation = chessBoard.evaluateCurrentBoard();
        outputView.printPositionEvaluation(positionEvaluation);
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
