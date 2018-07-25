/*
 * Copyright © 2018 2018 and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ssh.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ssh.rev150105.SshService;

public class SshProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SshProvider.class);

    private final DataBroker dataBroker;
    private final RpcProviderRegistry rpcProviderRegistry;

    private RpcRegistration<SshService> serviceRegistration;
    private RpcRegistration<SshService> connectRegistration;

    public SshProvider(final DataBroker dataBroker, RpcProviderRegistry rpcProviderRegistry) {
        this.dataBroker = dataBroker;
        this.rpcProviderRegistry = rpcProviderRegistry;

    }

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
      // serviceRegistration = rpcProviderRegistry.addRpcImplementation(SshService.class, new CommandImpl());
      // connectRegistration = rpcProviderRegistry.addRpcImplementation(SshService.class, new ConnectImpl());
      serviceRegistration = rpcProviderRegistry.addRpcImplementation(SshService.class, new SshServiceImpl());
        LOG.info("SshProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
      serviceRegistration.close();
      // connectRegistration.close();
        LOG.info("SshProvider Closed");
    }
}
