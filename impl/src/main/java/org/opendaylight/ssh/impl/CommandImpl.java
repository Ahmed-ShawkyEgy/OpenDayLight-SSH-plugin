package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.jcraft.jsch.*;
import java.io.*;
import util.*;


public class CommandImpl  {

  private ConnectionsContainer container;

  public CommandImpl(ConnectionsContainer container)
  {
    this.container = container;
  }


    public Future<RpcResult<CommandOutput>> command(CommandInput input) {
        CommandOutputBuilder sshBuilder = new CommandOutputBuilder();

        // Retrieve input
        String id = input.getSessionID();
        String command = input.getCommand();

        try{
          Connection connection = container.getConnection(id);
          connection.execute(command);
          String response = connection.getResponse();
          sshBuilder.setResponse(response);
        }catch(Exception e)
        {
            sshBuilder.setResponse(e.getStackTrace().toString());
        }
        return RpcResultBuilder.success(sshBuilder.build()).buildFuture();
    }


    private String removeEOL(String s)
    {
    	return s.replaceAll("(\\r|\\n)", " ");
    }

}
