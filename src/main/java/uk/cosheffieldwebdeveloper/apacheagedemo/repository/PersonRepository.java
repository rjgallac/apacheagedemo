package uk.cosheffieldwebdeveloper.apacheagedemo.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.age.jdbc.base.Agtype;
import org.postgresql.PGConnection;
import org.springframework.beans.factory.annotation.Value;

public class PersonRepository {

 @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // @Autowired
    // private DataSource dataSource;

    public String createPerson(String propertyName) {
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

    public String createRelation(String prop1, String prop2){
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

    public String deleteAll(){
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            PGConnection pgConn = (PGConnection) conn;
            pgConn.addDataType("agtype", Agtype.class);

            try (Statement stmt = conn.createStatement()) {
                String cypher = "SELECT * FROM cypher('graph_name', $$ " +
                        "MATCH (n) DETACH DELETE n " +
                        "$$) as (result agtype);";

                ResultSet rs = stmt.executeQuery(cypher);
                rs.close();

                return "All nodes and edges deleted successfully.";
            }
        } catch (Exception e) {
            return "Error deleting all nodes and edges: " + e.getMessage();
        }
    }

}
