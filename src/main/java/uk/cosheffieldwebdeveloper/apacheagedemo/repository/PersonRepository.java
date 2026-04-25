package uk.cosheffieldwebdeveloper.apacheagedemo.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.age.jdbc.base.Agtype;
import org.postgresql.PGConnection;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository {

    private final DataSource dataSource;

    public PersonRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    public String createPerson(String propertyName) {
        try (Connection conn = dataSource.getConnection()) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                if (pgConn != null) pgConn.addDataType("agtype", Agtype.class);
            } catch (Exception ignore) {
            }

            try (Statement stmt = conn.createStatement()) {
                    String safe = escape(propertyName);
                    String cypher = String.format(
                            "SELECT * FROM cypher('graph_name', $$ CREATE (a:label {property:\"%s\"}) RETURN a $$) as (a agtype);",
                            safe);

                    ResultSet rs = stmt.executeQuery(cypher);
                    if (rs != null) rs.close();

                    return "Node created successfully: " + propertyName;
                }
        } catch (Exception e) {
            return "Error creating node: " + e.getMessage();
        }
    }

    public String createRelation(String prop1, String prop2) {
        try (Connection conn = dataSource.getConnection()) {
                try {
                    PGConnection pgConn = conn.unwrap(PGConnection.class);
                    if (pgConn != null) pgConn.addDataType("agtype", Agtype.class);
                } catch (Exception ignore) {
                }

            try (Statement stmt = conn.createStatement()) {
                String a = escape(prop1);
                String b = escape(prop2);
                String cypher = "SELECT * FROM cypher('graph_name', $$ "
                        + "MATCH (a:label), (b:label) "
                        + "WHERE a.property = '" + a + "' AND b.property = '" + b + "' "
                        + "CREATE (a)-[e:RELTYPE {property:a.property + '<->' + b.property}]->(b) "
                        + "RETURN e $$) as (e agtype);";

                ResultSet rs = stmt.executeQuery(cypher);
                if (rs != null) rs.close();

                return "Edge created successfully: between " + prop1 + " and " + prop2;
            }
        } catch (Exception e) {
            return "Error creating edge: " + e.getMessage();
        }
    }

    public String deleteAll() {
        try (Connection conn = dataSource.getConnection()) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                if (pgConn != null) pgConn.addDataType("agtype", Agtype.class);
            } catch (Exception ignore) {
            }

            try (Statement stmt = conn.createStatement()) {
                String cypher = "SELECT * FROM cypher('graph_name', $$ MATCH (n) DETACH DELETE n $$) as (result agtype);";

                ResultSet rs = stmt.executeQuery(cypher);
                if (rs != null) rs.close();

                return "All nodes and edges deleted successfully.";
            }
        } catch (Exception e) {
            return "Error deleting all nodes and edges: " + e.getMessage();
        }
    }

}
