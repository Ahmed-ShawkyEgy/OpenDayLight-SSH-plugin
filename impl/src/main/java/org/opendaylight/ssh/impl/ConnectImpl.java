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



public class ConnectImpl  {


    public Future<RpcResult<ConnectOutput>> connect(ConnectInput input) {
        ConnectOutputBuilder connectBuilder = new ConnectOutputBuilder();

        // Retrieve input
        String user = input.getUser();
        String ip = input.getIp();
        int port = input.getPort();
        String password = input.getPassword();

        // sshBuilder.setResponse(removeEOL(response.toString()));
       connectBuilder.setStatus(user + "@"+ip +":"+ port +"<br> password:"+password);
       connectBuilder.setSessionID("awd@12asf5g5_a2");
        return RpcResultBuilder.success(connectBuilder.build()).buildFuture();
    }



}
