package chess.controller;

import chess.model.board.ChessBoard;
import chess.service.ChessGameService;
import chess.view.input.InputView;
import chess.view.output.OutputView;

public class Summarize implements GameState {
    private final ChessBoard chessBoard;

    public Summarize(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    @Override
    public GameState run(InputView inputView, OutputView outputView, ChessGameService chessGameService) {
        chessGameService.saveGameResult(chessBoard);
        return new End();
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
