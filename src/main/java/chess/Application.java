package chess;

import chess.controller.ChessController;
import chess.view.input.InputView;
import chess.view.output.OutputView;

public class Application {
    public static void main(String[] args) {
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        ChessController chessController = new ChessController(inputView, outputView);
        chessController.run();
    }
}
