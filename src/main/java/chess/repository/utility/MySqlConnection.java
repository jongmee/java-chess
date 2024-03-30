package chess.repository.utility;

import chess.repository.exception.DataAccessException;
import chess.repository.exception.PropertyLoadException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySqlConnection {
    private static final String propertiesPath = System.getProperty("user.dir") + "/src/main/resources/db.properties";
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SERVER;
    private static final String DATABASE;
    private static final String USERNAME;
    private static final String PASSWORD;

    public static final MySqlConnection INSTANCE = new MySqlConnection();

    static {
        Properties properties = new Properties();
        try (FileReader resources = new FileReader(propertiesPath)) {
            properties.load(resources);
            SERVER = (String) properties.get("SERVER");
            USERNAME = (String) properties.get("USERNAME");
            DATABASE = (String) properties.get("DATABASE");
            PASSWORD = (String) properties.get("PASSWORD");
        } catch (IOException e) {
            throw new PropertyLoadException(e.getMessage());
        }
    }

    private MySqlConnection() {
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + OPTION, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("DB 연결 오류:" + e.getMessage());
        }
    }
}
