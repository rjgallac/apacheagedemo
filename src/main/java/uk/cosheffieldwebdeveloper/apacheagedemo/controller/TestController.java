package uk.cosheffieldwebdeveloper.apacheagedemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.age.jdbc.*;
import java.sql.Connection;
import java.sql.Statement;

@RestController
public class TestController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;


    @GetMapping("/test")
    public String test() {
        try {
            // Create nodes and edges in AGE database
                Connection connection = java.sql.DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
    
                // Create nodes
                statement.executeUpdate("SELECT create_vertex('person', 'name', 'Alice')");
                statement.executeUpdate("SELECT create_vertex('person', 'name', 'Bob')");
    
                // Create edge
                statement.executeUpdate("SELECT create_edge('knows', 'person', 'person', 'Alice', 'Bob')");
    
                connection.close(); 
            return "Nodes and edges created successfully in AGE database!";
        } catch (Exception e) {
            return "Error creating nodes and edges: " + e.getMessage();
        }
    }

}
