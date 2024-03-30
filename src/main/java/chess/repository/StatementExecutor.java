package chess.repository;

import java.sql.SQLException;

public class StatementExecutor {
    public static final StatementExecutor INSTANCE = new StatementExecutor();

    private final MySqlConnection mySqlConnection = MySqlConnection.INSTANCE;

    private StatementExecutor() {
    }

    public void executeUpdate(String query, ParameterBinder parameterBinder) {
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <T> T executeUpdate(String query, String[] keys, ResultSetMapper<T> resultSetMapper) {
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query, keys)) {
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void executeBatch(String query, ParameterBinder parameterBinder) {
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <T> T executeQuery(String query, ParameterBinder parameterBinder, ResultSetMapper<T> resultSetMapper) {
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            parameterBinder.bind(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            return resultSetMapper.map(resultSet);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
