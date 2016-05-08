package homeworks.task2;

import javax.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {

	private BlockingQueue<Connection> available;
	private BlockingQueue<Connection> used;
	private final String url;
	private final String user;
	private final String password;
	private final static int CONNECTION_SIZE = 8;
	private Connection connection;


	public ConnectionPool(final String url, final String user, final String password) {
		this.url = url;
		this.user = user;
		this.password = password;


		try {
			Class.forName("org.h2.Driver");
			used = new ArrayBlockingQueue<Connection>(CONNECTION_SIZE);
			available = new ArrayBlockingQueue<Connection>(CONNECTION_SIZE);
			for (int index = 0; index < CONNECTION_SIZE; index++) {
				connection = getConnection();
				PooledConnection pooledConnection = new PooledConnection(connection, this);
				available.add(pooledConnection);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Connection takeConnection() {
		connection = null;
		try {
			connection = available.take();
			used.add(connection);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return connection;
	}
	
	public void giveBackConnection(PooledConnection connection) {

		if (connection!=null) {
			used.remove(connection);
			available.add(connection);
		}
	}




}
