package i5.las2peer.services.AWGSbotService.database;

import java.sql.*;

public class Database {
	
	private String dbUser = null;
	private String dbPassword = null;
	private String dbHost = null;
	private int dbPort = -1;
	private String dbName = null;
	
	public Database(String dbUser, String dbPassword, String dbHost, int dbPort, String dbName) {
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dbHost = dbHost;
		this.dbPort = dbPort;
		this.dbName = dbName;
	}
	
	public Connection getConnection() throws Exception {
		
        try {
        	String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        	Connection connection = null;
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            //System.out.println("OK");
            return connection;
        } catch (Exception e) {
            throw e;
        }
    }

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	

}
