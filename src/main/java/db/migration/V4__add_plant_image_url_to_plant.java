package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class V4__add_plant_image_url_to_plant extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        if (!tableExists(connection, "plant") || columnExists(connection, "plant", "image_url")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE plant ADD COLUMN image_url VARCHAR(1000)");
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        return objectExists(connection, tableName, null);
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        return objectExists(connection, tableName, columnName);
    }

    private boolean objectExists(Connection connection, String tableName, String columnName) throws SQLException {
        String[] tableNames = {tableName, tableName.toUpperCase()};
        String[] columnNames = columnName == null ? new String[]{null} : new String[]{columnName, columnName.toUpperCase()};

        for (String candidateTableName : tableNames) {
            for (String candidateColumnName : columnNames) {
                try (ResultSet resultSet = connection.getMetaData().getColumns(
                        connection.getCatalog(),
                        null,
                        candidateTableName,
                        candidateColumnName
                )) {
                    if (resultSet.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
