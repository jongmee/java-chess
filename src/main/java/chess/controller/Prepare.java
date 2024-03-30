package chess.controller;

import chess.model.board.ChessBoard;
import chess.repository.dto.GameResultDto;
import chess.service.ChessGameService;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.output.OutputView;

import java.util.List;

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
        if (gameCommand.isLogs()) {
            showGameResults(outputView);
            return new End();
        }
        ChessBoard chessBoard = chessGameService.createOrGetInitialChessBoard();
        outputView.printChessBoard(chessBoard);
        return Run.initializeWithFirstTurn(chessBoard, chessGameService);
    }

    private GameCommand getFirstGameCommand(InputView inputView) {
        String gameCommandInput = inputView.readGameCommand();
        return GameCommand.createInPreparation(gameCommandInput);
    }

    private void showGameResults(OutputView outputView) {
        List<GameResultDto> gameResults = chessGameService.findAllGameResults();
        outputView.printGameResults(gameResults);
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
