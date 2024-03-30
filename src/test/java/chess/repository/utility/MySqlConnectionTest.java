package chess.repository.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MySqlConnectionTest {

    @Test
    @DisplayName("MySQL Connection을 얻는다.")
    void getConnection() {
        // given
        MySqlConnection connection = MySqlConnection.INSTANCE;

        // when
        Connection connected = connection.getConnection();

        // then
        assertThat(connected).isNotNull();
    }
}
