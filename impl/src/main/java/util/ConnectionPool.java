package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import util.exception.ConnectionException;

public class ConnectionPool {

	// WARNING HashMaps are not thread safe
	// Consider using other relevant structures
	private HashMap<String, Connection> container;
	private HashSet<String> IDpool;

  	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);


	public ConnectionPool()
	{
		container = new HashMap<String,Connection>();
		IDpool = new HashSet<String>();
	}

	public Connection getConnection(String id) throws ConnectionException , IOException
	{
		LOG.debug("Searching for connection with id = {}",id);
		Connection connection = container.get(id);
		if(connection == null)
		{
			LOG.debug("Connection not found");
			throw new ConnectionException("SessionID doesn't exist");
		}
		if(!connection.isConnected())
		{
			LOG.debug("Connection has been terminated before");
			connection.terminate();
			removeConnection(id);
			throw new ConnectionException("Connection Timeout");
		}
		return connection;
	}

	public String addConnection(Connection connection) throws ConnectionException
	{
		String id = generateID();
		container.put(id, connection);
		return id;
	}

	public void removeConnection(String id) throws ConnectionException, IOException
	{
		if(!container.containsKey(id))
		{
			throw new ConnectionException("Session ID not found");
		}
		Connection connection = container.get(id);
		connection.terminate();
		container.remove(id);
		IDpool.remove(id);
	}

	public Iterator<String> getAllID()
	{
		return IDpool.iterator();
	}

	public void closeAllConnections() throws IOException
	{
		for(Entry<String, Connection> e : container.entrySet())
		{
			e.getValue().terminate();
		}
	}

	private String generateID() throws ConnectionException
	{
		String id;
		for(int i = 0; i < 50;i++)
		{
			if(!IDpool.contains(id=UUID.randomUUID().toString()))
			{
				IDpool.add(id);
				return id;
			}
		}
		throw new ConnectionException("Could not create a new Connection; Session ID pool is congested");
	}
}
