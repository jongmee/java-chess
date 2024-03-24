package chess.controller;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import chess.model.position.ChessPosition;
import chess.view.input.GameArguments;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.input.MoveArguments;
import chess.view.output.OutputView;

import java.util.Objects;
import java.util.function.Supplier;

public class ChessGame {
    private final InputView inputView;
    private final OutputView outputView;

    public ChessGame(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        GameCommand gameCommand = retryOnException(this::getFirstGameCommand);
        if (gameCommand.isEnd()) {
            return;
        }
        ChessBoard chessBoard = new ChessBoard(ChessBoardInitializer.INITIAL_BOARD);
        outputView.printChessBoard(chessBoard);
        retryOnException(() -> play(chessBoard));
    }

    private GameCommand getFirstGameCommand() {
        return GameCommand.createInStart(inputView.readGameCommand());
    }

    private void play(ChessBoard chessBoard) {
        while (true) {
            GameArguments gameArguments = inputView.readMoveArguments();
            GameCommand gameCommand = gameArguments.gameCommand();
            if (gameCommand.isEnd()) {
                break;
            }
            MoveArguments moveArguments = gameArguments.moveArguments();
            move(chessBoard, moveArguments);
        }
    }

    private void move(ChessBoard chessBoard, MoveArguments moveArguments) {
        ChessPosition source = moveArguments.createSourcePosition();
        ChessPosition target = moveArguments.createTargetPosition();
        chessBoard.move(source, target);
        outputView.printChessBoard(chessBoard);
    }

    private <T> T retryOnException(Supplier<T> retryOperation) {
        boolean retry = true;
        T result = null;
        while (retry) {
            result = tryOperation(retryOperation);
            retry = Objects.isNull(result);
        }
        return result;
    }

    private <T> T tryOperation(Supplier<T> operation) {
        try {
            return operation.get();
        } catch (IllegalArgumentException e) {
            outputView.printException(e.getMessage());
            return null;
        }
    }

    private void retryOnException(Runnable retryOperation) {
        boolean retry = true;
        while (retry) {
            retry = tryOperation(retryOperation);
        }
    }

    private boolean tryOperation(Runnable operation) {
        try {
            operation.run();
            return false;
        } catch (IllegalArgumentException e) {
            outputView.printException(e.getMessage());
            return true;
        }
    }
}
