package org.opendaylight.ssh.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.TerminateConnectionOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.jcraft.jsch.*;
import java.io.*;



public class TerminateConnectionImpl  {


    public Future<RpcResult<TerminateConnectionOutput>> terminateConnection(TerminateConnectionInput input) {
        ConnectOutputBuilder terminateConnectionBuilder = new ConnectOutputBuilder();

        return RpcResultBuilder.success(terminateConnectionBuilder.build()).buildFuture();
    }



}
