package chess.repository.dto;

import chess.model.evaluation.GameResult;

import java.time.LocalDateTime;

public record GameResultDto(LocalDateTime createdAt, GameResult gameResult) {
}
