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
    public String createNode(@RequestParam String propertyName) {

         try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            if (pgConn != null) {
                pgConn.addDataType("agtype", Agtype.class);

                try (Statement stmt = conn.createStatement()) {
                    String cypher = """
                                SELECT * FROM cypher('graph_name', $$ 
                                    CREATE (a:label {property:"%s"}) 
                                    RETURN a
                                $$) as (a agtype);
                            """.formatted(propertyName);

                    ResultSet rs = stmt.executeQuery(cypher);

                    return "Node created successfully: " + propertyName;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Error creating node";
    }

    @GetMapping("/create-edge")
    public String createEdge(
            @RequestParam String prop1,
            @RequestParam String prop2) {
         try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            pgConn.addDataType("agtype", Agtype.class);

            try (Statement stmt = conn.createStatement()) {
                String cypher = "SELECT * FROM cypher('graph_name', $$ " +
                        "MATCH (a:label), (b:label) " +
                        "WHERE a.property = '" + prop1 + "' AND b.property = '" + prop2 + "' " +
                        "CREATE (a)-[e:RELTYPE {property:a.property + '<->' + b.property}]->(b) " +
                        "RETURN e $$) as (e agtype);";


                ResultSet rs = stmt.executeQuery(cypher);
                rs.close();

                return "Edge created successfully: between " + prop1 + " and " + prop2;
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
