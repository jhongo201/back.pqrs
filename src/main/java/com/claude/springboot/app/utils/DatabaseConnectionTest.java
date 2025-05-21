package com.claude.springboot.app.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        testDatabaseConnection();
    }

    public void testDatabaseConnection() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=roles;encrypt=false";
        String username = "desarrollo";
        String password = "152020";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
