package chess.controller;

import chess.model.board.ChessBoard;
import chess.model.board.Turn;
import chess.service.ChessGameService;
import chess.view.input.InputView;
import chess.view.input.MoveArguments;
import chess.view.output.OutputView;

public class Move implements GameState {
    private final ChessBoard chessBoard;
    private final Turn turn;
    private final MoveArguments moveArguments;

    public Move(ChessBoard chessBoard, Turn turn, MoveArguments moveArguments) {
        this.chessBoard = chessBoard;
        this.turn = turn;
        this.moveArguments = moveArguments;
    }

    @Override
    public GameState run(InputView inputView, OutputView outputView, ChessGameService chessGameService) {
        chessGameService.move(chessBoard, turn, moveArguments);
        outputView.printChessBoard(chessBoard);
        Turn nextTurn = chessGameService.saveNextTurn(chessBoard, turn);
        return new Run(chessBoard, nextTurn);
    }

    @Override
    public boolean canContinue() {
        return true;
    }
}
