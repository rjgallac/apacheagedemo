package uk.cosheffieldwebdeveloper.apacheagedemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.age.jdbc.base.Agtype;
import org.postgresql.PGConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class TestController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // @Autowired
    // private DataSource dataSource;

    @GetMapping("/create-node")
    public String createNode(
            @RequestParam String label,
            @RequestParam String propertyName,
            @RequestParam String propertyValue) {

         try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            if (pgConn != null) {
                pgConn.addDataType("agtype", Agtype.class);

                try (Statement stmt = conn.createStatement()) {
                    String cypher = "SET search_path TO ag_catalog, public;SELECT * FROM cypher('graph_name', $$ " +
                            "CREATE (:" + label + " {" + propertyName + ": '" + propertyValue + "'}) $$) as (v agtype);";

                    ResultSet rs = stmt.executeQuery(cypher);
                    rs.close();

                    return "Node created successfully: " + label + " with " + propertyName + " = " + propertyValue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Error creating node";
    }

    @GetMapping("/create-edge")
    public String createEdge(
            @RequestParam String relType,
            @RequestParam String label,
            @RequestParam String prop1,
            @RequestParam String value1,
            @RequestParam String prop2,
            @RequestParam String value2) {
         try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            pgConn.addDataType("agtype", Agtype.class);

            try (Statement stmt = conn.createStatement()) {
                String cypher = "SET search_path TO ag_catalog, public;SELECT * FROM cypher('graph_name', $$ " +
                        "MATCH (a:" + label + "), (b:" + label + ") " +
                        "WHERE a." + prop1 + " = '" + value1 + "' AND b." + prop2 + " = '" + value2 + "' " +
                        "CREATE (a)-[e:" + relType + " {property:a." + prop1 + " + '<->' + b." + prop2 + "}]->(b) " +
                        "RETURN e $$) as (e agtype);";

                ResultSet rs = stmt.executeQuery(cypher);
                rs.close();

                return "Edge created successfully: " + relType + " between " + value1 + " and " + value2;
            }
        } catch (Exception e) {
            return "Error creating edge: " + e.getMessage();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "AGE Database Controller is running!";
    }

    @GetMapping("/setup")
    public String setup() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            pgConn.addDataType("agtype", Agtype.class);

            try (Statement stmt = conn.createStatement()) {
                String createGraph = "SELECT create_graph('demo_graph');";
                stmt.execute(createGraph);

                String createExtension = "CREATE EXTENSION IF NOT EXISTS age;";
                stmt.execute(createExtension);

                return "Database setup completed successfully!";
            }
        } catch (SQLException e) {
            return "Error during setup: " + e.getMessage();
        }
    }
    
    @DeleteMapping("/delete-all")
    public String deleteAll(){

        //SELECT * FROM cypher('graph_name', $$
        //   MATCH (n)
        //   CALL { WITH n DETACH DELETE n } IN TRANSACTIONS OF 10000 ROWS
        // $$) AS (n agtype);   

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            pgConn.addDataType("agtype", Agtype.class);

            try (Statement stmt = conn.createStatement()) {
                String cypher = "SET search_path TO ag_catalog, public;SELECT * FROM cypher('graph_name', $$ MATCH (n)  CALL { WITH n DETACH DELETE n } IN TRANSACTIONS OF 10000 ROWS $$) AS (n agtype); ";

                ResultSet rs = stmt.executeQuery(cypher);
                rs.close();

                return "deleted";
            }
        } catch (Exception e) {
            return "Error creating edge: " + e.getMessage();
        }

    }

}
