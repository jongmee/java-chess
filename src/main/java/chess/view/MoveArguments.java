package chess.view;

import chess.model.position.ChessPosition;
import chess.model.position.File;
import chess.model.position.Rank;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MoveArguments(String sourceFile, int sourceRank, String targetFile, int targetRank) {

    private static final String MOVE_REGEX = "([a-zA-Z])(\\d)";
    private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_REGEX);
    private static final int ARGUMENTS_SIZE = 4;

    public static MoveArguments from(List<String> inputs) {
        List<String> arguments = convertArguments(inputs);
        validateArgumentsSize(arguments);
        return new MoveArguments(arguments.get(0), parseInt(arguments.get(1)),
                arguments.get(2), parseInt(arguments.get(3)));
    }

    private static List<String> convertArguments(List<String> arguments) {
        return arguments.stream()
                .skip(1)
                .filter(MoveArguments::validateMoveArgument)
                .flatMap(s -> Arrays.stream(s.split("")))
                .toList();
    }

    private static void validateArgumentsSize(List<String> results) {
        if (results.size() != ARGUMENTS_SIZE) {
            throw new IllegalArgumentException("Source 위치와 Target 위치가 정확하지 않습니다.");
        }
    }

    private static boolean validateMoveArgument(String argument) {
        Matcher matcher = MOVE_PATTERN.matcher(argument);
        return matcher.matches();
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Rank가 숫자형식의 입력값이 아닙니다.");
        }
    }

    public ChessPosition createSourcePosition() {
        return new ChessPosition(File.from(sourceFile), Rank.from(sourceRank));
    }

    public ChessPosition createTargetPosition() {
        return new ChessPosition(File.from(targetFile), Rank.from(targetRank));
    }
}
