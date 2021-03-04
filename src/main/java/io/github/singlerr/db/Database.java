package io.github.singlerr.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database{
    private Connection con;
    public Database(String url,String id,String pass) throws SQLException,ClassNotFoundException{
        connect(url,id,pass);
    }
    public void connect(String url,String id,String pass) throws SQLException,ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        con = DriverManager.getConnection(url,id,pass);
    }
    public Connection getInstance(){
        return con;
    }
}
