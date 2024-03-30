package chess.view.input;

import java.util.Arrays;

public enum GameCommand {
    START("start"),
    MOVE("move"),
    STATUS("status"),
    END("end"),
    LOG("logs");

    private final String text;

    GameCommand(String text) {
        this.text = text;
    }

    public static GameCommand createInPreparation(String input) {
        return Arrays.stream(values())
                .filter(value -> value.text.equals(input))
                .filter(GameCommand::isInPreparation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("start, end, logs 중 하나를 입력해주세요."));
    }

    private static boolean isInPreparation(GameCommand gameCommand) {
        return gameCommand != MOVE && gameCommand != STATUS;
    }

    public static GameCommand createInProgress(String input) {
        return Arrays.stream(values())
                .filter(value -> value.text.equals(input))
                .filter(GameCommand::isInProgress)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("move, status, end 중 하나를 입력해주세요."));
    }

    private static boolean isInProgress(GameCommand gameCommand) {
        return gameCommand != START && gameCommand != LOG;
    }

    public boolean isEnd() {
        return this == END;
    }

    public boolean isMove() {
        return this == MOVE;
    }

    public boolean isLogs() {
        return this == LOG;
    }
}
