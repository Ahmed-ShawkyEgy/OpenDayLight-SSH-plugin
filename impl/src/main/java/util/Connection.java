package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

	private JSch jsch;
	private Session session;
	private Channel channel;
	private InputStream output;
	private OutputStream input;
	private PrintWriter inputWriter;

	private boolean connected;


	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
	private String server;

	// TODO change JSch variable to be a singleton

	public Connection() {
		jsch = new JSch();
		connected = false;
	}

	public boolean connect(String user,String host,int port,String password) throws JSchException, IOException,ConnectionException
	{
				server = user+"@"+host+":"+port;

				LOG.debug("Initiating session with {}",server);

				try{
	      session=jsch.getSession(user, host, 22);
			}catch (Exception e) {
				LOG.debug("Failed to initiat session with {}",server,e);
				return false;
			}

				LOG.debug("Successfull session initiation with {}",server);

	      session.setPassword(password);
	      session.setConfig("StrictHostKeyChecking", "no");

				LOG.debug("Trying to connect with {}",server);
				try{
	      session.connect(30000);   // making a connection with timeout.
				}catch (Exception e) {
					LOG.debug("Failed to connect with {}",server,e);
					return false;
				}

				LOG.debug("Successfull connection with {}",server);

				LOG.debug("Trying to open a channel with {}",server);
	      channel=session.openChannel("shell");

				LOG.debug("Trying to get IO streams with {}",server);

	      output = channel.getInputStream();
	      input = channel.getOutputStream();
	      inputWriter = new PrintWriter(input);
				LOG.debug("IO streams retrieved successfully with {}",server);

				LOG.debug("Trying to connect the channel with {}",server);

				try{
	      channel.connect();
			}catch (Exception e) {
				LOG.debug("Failed to connect channel with {}",server,e);
				return false;
			}
				LOG.debug("Successfully connected the channel with {}",server);

				LOG.debug("Trying to send empty command to {} to detect its prompt",server);

				try{
	      sendCommand("");
        getResponse(1);
				}catch (Exception e) {
					LOG.debug("Failed to send empty inital command to {}",server,e);
					return false;
				}
				connected = true;
				LOG.debug("Successfully connected a channel with {} !",server);

	      return true;
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
		if(!isConnected())
		{
				terminate();
				timeout();
		}
    command = command.trim();
    if(command.isEmpty())
      throw new ConnectionException("Can not issue empty commands");
		LOG.debug("Sending the command {} to {}",command,server);
		try{
		sendCommand(command);
		sendCommand("");
		}catch (Exception e) {
			LOG.debug("Failed to send the command:{} to the server {}",command,server);
		}
		LOG.debug("Command send successfully to {}",server);
	}

	public String getResponse() throws IOException, ConnectionException
	{
		String response = getResponse(2);
		LOG.debug("response from {} : {}",server,response);
		return response;
	}

	public boolean isConnected() throws IOException
	{
		return connected;
	}

	public void terminate() throws IOException
	{
		LOG.debug("Trying to close all streams with server{}",server);
		output.close();
		input.close();
		LOG.debug("All streams closed with server {}",server);
		LOG.debug("Trying to disconnect the session with server {}",server);
		channel.disconnect();
		session.disconnect();
		connected = false;
		LOG.debug("Conection with server {} has been terminated successfully",server);
	}


	// Private Methods
	// --------------------------------------------------------------------------------------------

	/**
	 * Reads from the stream line by line and concatenates all of the lines in a single string.
	 * It stops reading lines when the prompt is reached, which is identified by the consecutive similar line
	 * that are shown due to the empty command sent by default
	 *
	 * @param  repeats	the number of expected repetitions of the prompt line
	 * @return          the response received from the stream
	 * @throws ConnectionException
	 * @throws IOException
	 * */
	private String getResponse(int repeats) throws IOException, ConnectionException
	{
		LOG.debug("Start reading response from server {}",server);
		String response = "";
		String l1 = "" , l2 = "";
		int times = repeats+1;
		while(connected)
		{
			LOG.debug("Reading response from {}",server);
			l2 = readLine();
			LOG.debug("Read {} from {}",l2,server);
			response += l2;
			if(!l1.equals("") && !l1.equals("\r") && !l1.equals("\n") && l1.equals(l2))
				repeats--;
			if(repeats<=0)break;
			if(!l2.equals("\r") && !l2.equals("\n"))
				l1 = l2;
		}
		LOG.debug("Finished reading response from {}",server);
		isConnected();
		return response.substring(0, response.length()-times*l1.length()).trim();
	}


	private void sendCommand(String command) throws IOException
	{
		inputWriter.println(command.trim());
		inputWriter.flush();
	}


	private String readLine() throws IOException
	{
		String line = "";
		while(connected && output.available()>=0)
		{
			int c = output.read();
			if(c=='\n')break;
			if(c==-1) {
				connected = false;
				break;
			}
			line += (char)c;
		}
		return line;
	  }

		private void timeout() throws ConnectionException{
			throw new ConnectionException("Connection Timeout");
		}

}
