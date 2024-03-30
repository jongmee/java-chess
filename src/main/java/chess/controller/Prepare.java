package chess.controller;

import chess.model.board.ChessBoard;
import chess.service.ChessGameService;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.output.OutputView;

public class Prepare implements GameState {
    private final ChessGameService chessGameService;

    public Prepare(ChessGameService chessGameService) {
        this.chessGameService = chessGameService;
    }

    @Override
    public GameState run(InputView inputView, OutputView outputView) {
        GameCommand gameCommand = getFirstGameCommand(inputView);
        if (gameCommand.isEnd()) {
            return new End();
        }
        ChessBoard chessBoard = chessGameService.saveInitialChessBoard();
        outputView.printChessBoard(chessBoard);
        return Run.initializeWithFirstTurn(chessBoard, chessGameService);
    }

    private GameCommand getFirstGameCommand(InputView inputView) {
        String gameCommandInput = inputView.readGameCommand();
        return GameCommand.createInPreparation(gameCommandInput);
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
