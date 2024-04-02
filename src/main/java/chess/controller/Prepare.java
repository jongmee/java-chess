package chess.controller;

import chess.dao.dto.GameResultDto;
import chess.dao.dto.LatestChessBoardDto;
import chess.service.ChessGameService;
import chess.view.input.GameCommand;
import chess.view.input.InputView;
import chess.view.output.OutputView;

import java.util.List;

public class Prepare implements GameState {

    @Override
    public GameState run(InputView inputView, OutputView outputView, ChessGameService chessGameService) {
        GameCommand gameCommand = getFirstGameCommand(inputView);
        if (gameCommand.isEnd()) {
            return new End();
        }
        if (gameCommand.isLogs()) {
            showGameResults(outputView, chessGameService);
            return new End();
        }
        LatestChessBoardDto latestChessBoardDto = chessGameService.createOrGetInitialChessBoard();
        outputView.printChessBoard(latestChessBoardDto.chessBoard());
        return new Run(latestChessBoardDto.chessBoard(), latestChessBoardDto.turn());
    }

    private GameCommand getFirstGameCommand(InputView inputView) {
        String gameCommandInput = inputView.readGameCommand();
        return GameCommand.createInPreparation(gameCommandInput);
    }

    private void showGameResults(OutputView outputView, ChessGameService chessGameService) {
        List<GameResultDto> gameResults = chessGameService.findAllGameResults();
        outputView.printGameResults(gameResults);
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
