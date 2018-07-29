package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import util.ConnectionPool;



public class TerminateConnectionImpl  {

  private ConnectionPool container;

  public TerminateConnectionImpl(ConnectionPool container)
  {
    this.container = container;
  }


    public Future<RpcResult<TerminateConnectionOutput>> terminateConnection(TerminateConnectionInput input) {
        TerminateConnectionOutputBuilder terminateConnectionBuilder = new TerminateConnectionOutputBuilder();

        String id = input.getSessionID();

        try{
          container.removeConnection(id);
          terminateConnectionBuilder.setResponse("Connection terminated successfully");
        }catch(Exception e)
        {
          terminateConnectionBuilder.setResponse("Failed to terminate the connection; Either the sessionID has already expired or it never existed");
        }

        return RpcResultBuilder.success(terminateConnectionBuilder.build()).buildFuture();
    }

}
