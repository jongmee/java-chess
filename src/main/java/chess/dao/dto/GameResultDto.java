package chess.dao.dto;

import chess.model.evaluation.GameResult;

import java.time.LocalDateTime;

public record GameResultDto(LocalDateTime createdAt, GameResult gameResult) {
}
