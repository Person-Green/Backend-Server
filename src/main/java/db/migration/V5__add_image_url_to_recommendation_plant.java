package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class V5__add_image_url_to_recommendation_plant extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        if (!tableExists(connection, "recommendation_plant")) {
            return;
        }

        if (!columnExists(connection, "recommendation_plant", "image_url")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE recommendation_plant ADD COLUMN image_url VARCHAR(1000)");
            }
        }

        if (tableExists(connection, "plant") && columnExists(connection, "plant", "image_url")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "UPDATE recommendation_plant rp SET image_url = ("
                                + "SELECT p.image_url FROM plant p WHERE p.plant_id = rp.plant_id"
                                + ") WHERE EXISTS ("
                                + "SELECT 1 FROM plant p WHERE p.plant_id = rp.plant_id AND p.image_url IS NOT NULL"
                                + ")"
                );
            }
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
