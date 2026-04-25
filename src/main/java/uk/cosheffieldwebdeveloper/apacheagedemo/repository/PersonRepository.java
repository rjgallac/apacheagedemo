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

    public String createPerson(String name) {
        try (Connection conn = dataSource.getConnection()) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);
                if (pgConn != null) pgConn.addDataType("agtype", Agtype.class);
            } catch (Exception ignore) {
            }

            try (Statement stmt = conn.createStatement()) {
                    String safe = escape(name);
                    String cypher = String.format(
                            "SELECT * FROM cypher('graph_name', $$ CREATE (a:Person {name:\"%s\"}) RETURN a $$) as (a agtype);",
                            safe);

                    ResultSet rs = stmt.executeQuery(cypher);
                    if (rs != null) rs.close();

                    return "Node created successfully: " + name;
                }
        } catch (Exception e) {
            return "Error creating node: " + e.getMessage();
        }
    }

    public String createRelation(String name1, String name2) {
        try (Connection conn = dataSource.getConnection()) {
                try {
                    PGConnection pgConn = conn.unwrap(PGConnection.class);
                    if (pgConn != null) pgConn.addDataType("agtype", Agtype.class);
                } catch (Exception ignore) {
                }

            try (Statement stmt = conn.createStatement()) {
                String a = escape(name1);
                String b = escape(name2);
                String cypher = "SELECT * FROM cypher('graph_name', $$ "
                        + "MATCH (a:Person), (b:Person) "
                        + "WHERE a.name = '" + a + "' AND b.name = '" + b + "' "
                        + "CREATE (a)-[e:RELTYPE {property:a.name + '<->' + b.name}]->(b) "
                        + "RETURN e $$) as (e agtype);";

                ResultSet rs = stmt.executeQuery(cypher);
                if (rs != null) rs.close();

                return "Edge created successfully: between " + name1 + " and " + name2;
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
