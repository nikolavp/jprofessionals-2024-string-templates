package bg.jug.jprofessionals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static java.lang.StringTemplate.RAW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomTemplateProcessorTest {
    record TestRecord(int x, int y) { }
    private final TestRecord input = new TestRecord(1, 2);

    // <editor-fold desc="processor interface and string templates">
    @Test
    void processorInterface() {
        StringTemplate template = RAW."\{input.x} plus \{input.y} is \{input.x + input.y}";
        assertEquals(List.of("", " plus ", " is ", ""), template.fragments());
        assertEquals("1 plus 2 is 3", template.interpolate());
        assertEquals(List.of(1, 2, 3), template.values());

        String result = STR.process(template);
        assertEquals("1 plus 2 is 3", result);
    }
    // </editor-fold>
    // <editor-fold desc="rewrite the STR processor">
    @Test
    void rewriteTheSTRProcessor() {
        var INTERPOLATE = StringTemplate.Processor.of((StringTemplate st) -> {
            StringBuilder sb = new StringBuilder();
            Iterator<String> fragments = st.fragments().iterator();
            for (Object value : st.values()) {
                sb.append(fragments.next());
                sb.append(value);
            }
            sb.append(fragments.next());
            return sb.toString();
        });

        String result = INTERPOLATE."\{input.x} plus \{input.y} is \{input.x + input.y}";
        assertEquals("1 plus 2 is 3", result);
        // same as
        StringTemplate template = RAW."\{input.x} plus \{input.y} is \{input.x + input.y}";
        assertEquals("1 plus 2 is 3", INTERPOLATE.process(template));
    }
    // </editor-fold>
    // <editor-fold desc="write a possible JDBC SQL processor">
    record JDBCQueryBuilder(Connection connection) implements StringTemplate.Processor<PreparedStatement, SQLException> {
        @Override
        public PreparedStatement process(StringTemplate st) throws SQLException {
            // 1. Replace StringTemplate placeholders with PreparedStatement placeholders
            String query = String.join("?", st.fragments());

            // 2. Create the PreparedStatement on the connection
            PreparedStatement ps = connection.prepareStatement(query);

            // 3. Set parameters of the PreparedStatement
            int index = 1;
            for (Object value : st.values()) {
                switch (value) {
                    case Integer i -> ps.setInt(index++, i);
                    case Float f   -> ps.setFloat(index++, f);
                    case Double d  -> ps.setDouble(index++, d);
                    case Boolean b -> ps.setBoolean(index++, b);
                    case BigDecimal decimal -> ps.setBigDecimal(index++, decimal);
                    default        -> ps.setString(index++, String.valueOf(value));
                }
            }

            return ps;
        }
    }

    @Disabled("requires a database connection")
    @Test
    void usageOfQueryBuilder() throws SQLException {
        String name = "Joan";
        int age = 42;
        try (var connection = openConnection();
            var preparedStatement = connection.SQL."INSERT INTO users (name, age) VALUES (\{name}, \{age})") {
            preparedStatement.executeUpdate();
        }
    }

    private static class CustomConnection implements AutoCloseable {
        private final Connection connection;
        private final JDBCQueryBuilder SQL;

        CustomConnection(Connection connection) {
            this.connection = connection;
            SQL = new JDBCQueryBuilder(connection);
        }

        @Override
        public void close() throws SQLException {
            connection.close();
        }
    }

    private CustomConnection openConnection() {
        // get a connection to a database
        return new CustomConnection(null);
    }
    // </editor-fold>
}
