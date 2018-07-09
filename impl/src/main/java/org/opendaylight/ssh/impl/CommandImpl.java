package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.CommandOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

public class CommandImpl implements SshService {

    @Override
    public Future<RpcResult<CommandOutput>> command(CommandInput input) {
        CommandOutputBuilder sshBuilder = new CommandOutputBuilder();
        sshBuilder.setResponse("Hello " + input.getCmd());
        return RpcResultBuilder.success(sshBuilder.build()).buildFuture();
    }

}
