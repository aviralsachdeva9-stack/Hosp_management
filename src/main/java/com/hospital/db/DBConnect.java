package com.hospital.db;

// Yeh class database se connection banati hai.
// config.properties file se settings padhti hai
// taaki password Java code mein kabhi na dikhe.

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnect {

    // DB settings store karne ke liye
    private static String url;
    private static String user;
    private static String password;

    // Static block: class load hote hi ek baar chalta hai
    // config.properties file read karke settings set karta hai
    static {
        try {
            Properties props = new Properties();

            // Resources folder se file padhna
            InputStream input = DBConnect.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                throw new RuntimeException("config.properties file nahi mili resources mein!");
            }

            props.load(input);

            // Properties file se values nikalna
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (Exception e) {
            // Agar file nahi mili to program chalna band karo
            throw new RuntimeException("DB config load nahi ho saka: " + e.getMessage());
        }
    }

    // Naya database connection return karta hai.
    // Caller ko try-with-resources mein use karna chahiye
    // taaki connection automatically band ho jaye.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

