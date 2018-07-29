package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.ConnectOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;

import util.ConnectionPool;


public class SshServiceImpl implements SshService {
    private ConnectionPool container;

    public SshServiceImpl(ConnectionPool container)
    {
      this.container = container;
    }

    @Override
    public Future<RpcResult<ConnectOutput>> connect(ConnectInput input) {
        return (new ConnectImpl(container)).connect(input);
    }

    @Override
    public Future<RpcResult<CommandOutput>> command(CommandInput input) {
      return (new CommandImpl(container)).command(input);
    }

    @Override
    public Future<RpcResult<TerminateConnectionOutput>> terminateConnection(TerminateConnectionInput input){
      return (new TerminateConnectionImpl(container)).terminateConnection(input);
    }

}
