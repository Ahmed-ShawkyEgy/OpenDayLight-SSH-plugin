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



public class CommandImpl implements SshService {

    @Override
    public Future<RpcResult<CommandOutput>> command(CommandInput input) {
        CommandOutputBuilder sshBuilder = new CommandOutputBuilder();

        // Retrieve input
        String user = input.getUser();
        String ip = input.getIp();
        int port = input.getPort();
        String password = input.getPassword();
        String cmd = input.getCmd();

        StringBuilder response = new StringBuilder();

        try{

        // Initiate session
        JSch jsch=new JSch();

        Session session=jsch.getSession(user, ip, port);
        session.setPassword(password);

        // Security flaw
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        
        session.connect();


        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(cmd);


        channel.setInputStream(null);
        // ((ChannelExec)channel).setErrStream(System.err);

        InputStream in=channel.getInputStream();

        channel.connect();

        byte[] tmp=new byte[1024];
        while(true){
          while(in.available()>0){
            int i=in.read(tmp, 0, 1024);
            if(i<0)break;
            // System.out.print(new String(tmp, 0, i));
            response.append(new String(tmp, 0, i));
          }
          if(channel.isClosed()){
            if(in.available()>0) continue;
            // System.out.println("exit-status: "+channel.getExitStatus());
            response.append("exit-status: "+channel.getExitStatus()+"\n");

            break;
          }
          try{Thread.sleep(1000);}catch(Exception ee){}
        }
        channel.disconnect();
        session.disconnect();
      }
      catch(Exception e){
        // System.out.println(e);
//        sshBuilder.setResponse(e.toString());
    	  response = new StringBuilder(e.toString());
      }
        sshBuilder.setResponse(removeEOL(response.toString()));
//        sshBuilder.setResponse(user + "@"+ip +":"+ port +" password:"+password + "cmd:"+cmd);
        return RpcResultBuilder.success(sshBuilder.build()).buildFuture();
    }
    
    
    private String removeEOL(String s)
    {
    	return s.replaceAll("(\\r|\\n)", " ");
    }

}
