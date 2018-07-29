package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import util.exception.*;

public class Connection {
	private JSch jsch;
	private Session session;
	private Channel channel;
	private InputStream output;
	private OutputStream input;
	private PrintWriter inputWriter;	
	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
  	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
  	private String server;

	private boolean isConnected;
	
	// TODO change JSch variable to be a singleton

	public Connection() {
		jsch = new JSch();
		isConnected = false;
	}

	public boolean connect(String user,String host,int port,String password) throws JSchException, IOException, ConnectionException, InterruptedException, ExecutionException, TimeoutException
	{
	      session=jsch.getSession(user, host, 22);
	      session.setPassword(password);
	      session.setConfig("StrictHostKeyChecking", "no");

	      session.connect(30000);   // making a connection with timeout.

	      channel=session.openChannel("shell");
	      
	      output = channel.getInputStream();
	      input = channel.getOutputStream();
	      inputWriter = new PrintWriter(input);
	      
	      channel.connect();
	    
	      isConnected = true;
	      server = user+"@"+host+":"+port;
	      
	      sendCommand("");

	      getResponse(1,10*1000);
	      
	      return isConnected;
	}
	
	/**
	 * Executes the command by sending the command itself followed by an empty command 
	 * which is used to determine when the prompt is reached.
	 * 
	 * @param command the command to be executed
	 * @throws ConnectionException 
	 * @throws IOException 
	 * */
	public void execute(String command) throws ConnectionException, IOException 
	{
		if(!isConnected)
		{
			terminate();
			throw new ConnectException("Connection terminated");
		}
		
		sendCommand(command);
		sendCommand("");
	}
	
	public String getResponse(long timeout) throws IOException, ConnectionException, InterruptedException, ExecutionException, TimeoutException
	{
		if(!isConnected)
		{
			terminate();
			return "Connection terminated\n";
		}
		return getResponse(2,timeout);
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	public void terminate() throws IOException
	{
		output.close();
		input.close();
		channel.disconnect();
		session.disconnect();
		executor.shutdownNow();
		isConnected = false;	
	}
	
	
	// Private Methods
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Reads from the stream line by line and concatenates all of the lines in a single string.
	 * It stops reading lines when the prompt is reached, which is identified by the consecutive similar line
	 * that are shown due to the empty command sent by default
	 * 
	 * @param  numberOfRetries	the number of expected repetitions of the prompt line
	 * @return          the response received from the stream 			 
	 * @throws ConnectionException 
	 * @throws IOException
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * */
	private String getResponse(int numberOfRetries,long timeout) throws IOException, ConnectionException, InterruptedException, ExecutionException, TimeoutException 
	{
		String response = "";
		String l1 = "" , l2 = "";
		int times = numberOfRetries+1;
		while(isConnected)
		{
//			l2 = readLine(timeout);
			l2 = tryReadLine(3, timeout);
			LOG.debug("Read line : {} from {} ",l2,server);
//			System.err.println(l2);
			response += l2;
			if(!l1.equals("") && !l1.equals("\r") && !l1.equals("\n") && !l1.equals("\r\n") && l1.equals(l2))
				numberOfRetries--;
			if(numberOfRetries<=0)break;
			if(!l2.equals("\r") && !l2.equals("\n"))
				l1 = l2;
		}
		if(!isConnected)
		{
			terminate();
			return response;
		}
		return response.substring(0, response.length()-times*l1.length()).trim();
	}
	
	
	private void sendCommand(String command) throws IOException
	{
		inputWriter.println(command);
		inputWriter.flush();	
	}
	
	private String tryReadLine(int numberOfRetries,long timeout) throws IOException, InterruptedException
	{
		String line = "";
		do
		{
			try 
			{
				line += readLine(timeout);
				break;
			}
			catch (IOException e) 
			{
				line += " Connection terminated";
				isConnected = false;
			}
			catch (ExecutionException|TimeoutException|InterruptedException e)
			{
				numberOfRetries--;
				if(numberOfRetries<=0)
				{
					line += " Connection timedOut";
					isConnected = false;
				}
				else
				{
					Thread.sleep(1000);
				}
			}
		}while(numberOfRetries>0);
//		System.err.println("Line= "+line);
		return line;
	}

	
	private String readLine(long timeout) throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		String line = "";
		while(isConnected && output.available()>=0)
		{
			int c = read(timeout);

			if(c==-1) 
			{
				isConnected = false;
				line += " Connection terminated";
				break;
			}
			line += (char)c;
			if(c=='\n')break;
		
		}
		return line;
	 }
	
	private int read(long timeout) throws InterruptedException, ExecutionException, TimeoutException
	{
		Future<Integer> future = executor.submit(new Reader(output));
		return future.get(timeout,TimeUnit.MILLISECONDS);		
	}
	
	
	private class Reader implements Callable<Integer>
	{
		
		private InputStream output;
		
		public Reader(InputStream output) {
			this.output = output;
		}

		public Integer call() throws Exception {
			return output.read();
		}
		
	}
	
}
