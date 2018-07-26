package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectOutputBuilder;

import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.common.RpcError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.jcraft.jsch.*;
import java.io.*;
import util.*;


public class ConnectImpl  {

    private ConnectionsContainer container;


  	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
    private String server;

    public ConnectImpl(ConnectionsContainer container)
    {
      this.container = container;
    }

    public Future<RpcResult<ConnectOutput>> connect(ConnectInput input) {
        ConnectOutputBuilder connectBuilder = new ConnectOutputBuilder();

        // Retrieve input
        String user = input.getUser();
        String ip = input.getIp();
        int port = input.getPort();
        String password = input.getPassword();

        server = user+"@"+ip+":"+port;

        Connection connection = new Connection();

        try{
        boolean isConnected = connection.connect(user,ip,port,password);
        if(!isConnected)
        {
          // TODO return error status code
          LOG.debug("Failed to connect with {}",server);
          // return RpcResultBuilder.failed().withError(RpcError.ErrorType.APPLICATION,"Failed to connect with the server").buildFuture();
          // return RpcResultBuilder.newError(RpcError.ErrorType.APPLICATION,"Connection timeout","Failed to connect with the server");
          // return RpcResultBuilder.failed().buildFuture();
          throw new Exception("Failed to connect");
        }
        String id = container.addConnection(connection);

        connectBuilder.setStatus("Connected Successfully");
        connectBuilder.setSessionID(id);

        }
        catch(Exception e)
        {
          connectBuilder.setStatus("Connection refused; either the ip is invalid or the maximum number of connections has been reached");
          connectBuilder.setSessionID("undefined");

        }

        return RpcResultBuilder.success(connectBuilder.build()).buildFuture();
    }



}
