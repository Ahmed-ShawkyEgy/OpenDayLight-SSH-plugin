package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Connection {

	private JSch jsch;
	private Session session;
	private Channel channel;
	private InputStream output;
	private OutputStream input;
	private PrintWriter inputWriter;

	// TODO change JSch variable to be a singleton

	public Connection() {
		jsch = new JSch();
	}

	public boolean connect(String user,String host,int port,String password) throws JSchException, IOException
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

	      sendCommand("");
        getResponse(1);
	      return true;
	}

	/**
	 * Executes the command by sending the command itself followed by an empty command
	 * which is used to determine when the prompt is reached.
	 *
	 * @param command the command to be executed
	 * */
	public void execute(String command) throws IOException
	{
		sendCommand(command);
		sendCommand("");
	}

	public String getResponse() throws IOException
	{
		return getResponse(2);
	}

	public void terminate() throws IOException
	{
		output.close();
		input.close();
		channel.disconnect();
		session.disconnect();

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
	 * */
	private String getResponse(int repeats) throws IOException
	{
		String response = "";
		String l1 = "" , l2 = "";
		int times = repeats+1;
		while(true)
		{
			l2 = readLine();
			response += l2;
			if(!l1.equals("") && !l1.equals("\r") && !l1.equals("\n") && l1.equals(l2))
				repeats--;
			if(repeats<=0)break;
			if(!l2.equals("\r") && !l2.equals("\n"))
				l1 = l2;
		}

		return response.substring(0, response.length()-times*l1.length()).trim();
	}


	private void sendCommand(String command) throws IOException
	{
		inputWriter.println(command);
		inputWriter.flush();
	}


	private String readLine() throws IOException
	{
		String line = "";
		byte[] buffer = new byte[1];
		while(output.available()>=0)
		{
			output.read(buffer,0,1);
			char c = (char) buffer[0];
			if(c=='\n')break;
			line += c;
		}
		return line;
	  }

}
