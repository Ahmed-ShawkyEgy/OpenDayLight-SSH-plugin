package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Connection;
import util.ConnectionPool;


public class CommandImpl  {

  	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

  	private ConnectionPool container;

  
  	public CommandImpl(ConnectionPool container)
  	{
  		this.container = container;
  	}


    public Future<RpcResult<CommandOutput>> command(CommandInput input) {
        CommandOutputBuilder sshBuilder = new CommandOutputBuilder();

        // Retrieve input
        String id = input.getSessionID();
        String command = input.getCommand();
        long timeout = input.getTimeout();
        
        try{
        	LOG.debug("Searching for a connection with id = {}",id);
        	Connection connection = container.getConnection(id);
        	LOG.debug("Connection with id = {} was found",id);
        	LOG.debug("Trying to execute {} at {}");
        	connection.execute(command);
          
        	String response = connection.getResponse(timeout);
          
        	sshBuilder.setResponse(response);
        }catch(Exception e)
        {
            sshBuilder.setResponse("Failed to execute; either the sessionID has expired or the IO streams have been comprimised or the command paramater was empty");
            try {
            	container.removeConnection(id);
            }catch (Exception e1) {
            	
            }
        }
        return RpcResultBuilder.success(sshBuilder.build()).buildFuture();
    }


}
