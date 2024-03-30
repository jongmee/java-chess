package chess.controller;

import chess.service.ChessGameService;
import chess.view.input.InputView;
import chess.view.output.OutputView;

import java.util.Objects;
import java.util.function.Supplier;

public class ChessGame {
    private final InputView inputView = new InputView();
    private final OutputView outputView = new OutputView();
    private final ChessGameService chessGameService = new ChessGameService();

    public void start() {
        GameState gameState = retryOnException(this::prepare);
        while (gameState.canContinue()) {
            GameState currentGameState = gameState;
            gameState = retryOnException(() -> currentGameState.run(inputView, outputView));
        }
    }

    private GameState prepare() {
        GameState prepare = new Prepare(chessGameService);
        return prepare.run(inputView, outputView);
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
}
