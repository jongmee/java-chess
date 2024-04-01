package chess.repository.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MySqlConnectorTest {

    @Test
    @DisplayName("MySQL Connection을 얻는다.")
    void getConnection() {
        // given
        MySqlConnector connection = MySqlConnector.PRODUCT_CONNECTION;

        // when
        Connection connected = connection.getConnection();

        // then
        assertThat(connected).isNotNull();
    }
}
