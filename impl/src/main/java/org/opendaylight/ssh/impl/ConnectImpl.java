package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.jcraft.jsch.*;
import java.io.*;
import util.*;


public class ConnectImpl  {
    private ConnectionsContainer container;

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

        Connection connection = new Connection();

        try{
        String id = container.addConnection(connection);
        connection.connect(user,ip,port,password);

        connectBuilder.setStatus("Connected Successfully");
        connectBuilder.setSessionID(id);

        }
        catch(Exception e)
        {
          connectBuilder.setStatus(e.getStackTrace().toString());
          connectBuilder.setSessionID("undefined");

        }

        return RpcResultBuilder.success(connectBuilder.build()).buildFuture();
    }



}
