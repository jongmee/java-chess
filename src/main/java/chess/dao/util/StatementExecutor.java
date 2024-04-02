package chess.dao.util;

import chess.dao.exception.DataAccessException;

import java.sql.SQLException;

public class StatementExecutor {
    private final MySqlConnector mySqlConnector;

    public StatementExecutor(MySqlConnector mySqlConnector) {
        this.mySqlConnector = mySqlConnector;
    }

    public void executeUpdate(String query, ParameterBinder parameterBinder) {
        try (var connection = mySqlConnector.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <T> T executeUpdate(String query, String[] keys, ParameterBinder parameterBinder, ResultSetMapper<T> resultSetMapper) {
        try (var connection = mySqlConnector.getConnection();
             var preparedStatement = connection.prepareStatement(query, keys)) {
            parameterBinder.bind(preparedStatement);
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void executeBatch(String query, ParameterBinder parameterBinder) {
        try (var connection = mySqlConnector.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <T> T executeQuery(String query, ParameterBinder parameterBinder, ResultSetMapper<T> resultSetMapper) {
        try (var connection = mySqlConnector.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
