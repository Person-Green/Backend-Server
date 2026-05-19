package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class V6__seed_plant_image_urls extends BaseJavaMigration {

    private static final Map<String, String> PLANT_IMAGE_URLS = new LinkedHashMap<>();

    static {
        PLANT_IMAGE_URLS.put("PLT-001", "https://www.jojaemin.com/images/2026/05/19/dd067d4e-eba8-4479-8f6e-582d7725bd14.jpg");
        PLANT_IMAGE_URLS.put("PLT-002", "https://www.jojaemin.com/images/2026/05/19/41b5db0e-b69b-4442-9cbf-4c6e9d344678.jpg");
        PLANT_IMAGE_URLS.put("PLT-003", "https://www.jojaemin.com/images/2026/05/19/df02f15b-4d29-4fa2-be0a-6c1dfaffb958.jpg");
        PLANT_IMAGE_URLS.put("PLT-004", "https://www.jojaemin.com/images/2026/05/19/e5b13192-4e7b-4bd8-98ff-68d76d83c2a0.jpg");
        PLANT_IMAGE_URLS.put("PLT-005", "https://www.jojaemin.com/images/2026/05/19/f19bf490-1628-4e82-9b22-85b880a5743d.jpg");
        PLANT_IMAGE_URLS.put("PLT-010", "https://www.jojaemin.com/images/2026/05/19/99906280-c545-4339-9b4d-cef532c3c5e1.jpg");
        PLANT_IMAGE_URLS.put("PLT-011", "https://www.jojaemin.com/images/2026/05/19/94bfc2d9-355a-4b72-9818-d313c59947dc.jpg");
        PLANT_IMAGE_URLS.put("PLT-015", "https://www.jojaemin.com/images/2026/05/19/2fb7b9bc-ddc4-4599-a396-4ade07c20e39.jpg");
        PLANT_IMAGE_URLS.put("PLT-016", "https://www.jojaemin.com/images/2026/05/19/3c8dba8a-54b3-442e-a32f-7b3dfc14ccc5.jpg");
        PLANT_IMAGE_URLS.put("PLT-017", "https://www.jojaemin.com/images/2026/05/19/cd385088-ac3c-4c47-8088-8e71890f367b.jpg");
        PLANT_IMAGE_URLS.put("PLT-018", "https://www.jojaemin.com/images/2026/05/19/4eec0d51-ff36-4364-9ecc-2a9c12e3b9c2.jpg");
        PLANT_IMAGE_URLS.put("PLT-020", "https://www.jojaemin.com/images/2026/05/19/3043679b-6abd-47b5-8bf5-2270cbe12b96.jpg");
        PLANT_IMAGE_URLS.put("PLT-022", "https://www.jojaemin.com/images/2026/05/19/0a5a2ed7-125b-4bba-b89c-e9ebfbf6c18e.jpg");
        PLANT_IMAGE_URLS.put("PLT-023", "https://www.jojaemin.com/images/2026/05/19/14ab337b-de6c-4c81-961d-6c2a2984f64a.jpg");
        PLANT_IMAGE_URLS.put("PLT-025", "https://www.jojaemin.com/images/2026/05/19/5fb00251-5bda-4183-af24-3cdc26a4a181.jpg");
        PLANT_IMAGE_URLS.put("PLT-029", "https://www.jojaemin.com/images/2026/05/19/69f3b96a-dcc5-45da-bd3e-0505ef811232.jpg");
        PLANT_IMAGE_URLS.put("PLT-030", "https://www.jojaemin.com/images/2026/05/19/c25257e2-9a9b-4ac2-a35b-a271209dd508.jpg");
    }

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        if (tableExists(connection, "plant") && columnExists(connection, "plant", "image_url")) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE plant SET image_url = ? WHERE plant_id = ? AND image_url IS NULL"
            )) {
                for (Map.Entry<String, String> entry : PLANT_IMAGE_URLS.entrySet()) {
                    statement.setString(1, entry.getValue());
                    statement.setString(2, entry.getKey());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }

        if (tableExists(connection, "recommendation_plant")
                && columnExists(connection, "recommendation_plant", "image_url")
                && tableExists(connection, "plant")
                && columnExists(connection, "plant", "image_url")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                        "UPDATE recommendation_plant rp SET image_url = ("
                                + "SELECT p.image_url FROM plant p WHERE p.plant_id = rp.plant_id"
                                + ") WHERE rp.image_url IS NULL AND EXISTS ("
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
