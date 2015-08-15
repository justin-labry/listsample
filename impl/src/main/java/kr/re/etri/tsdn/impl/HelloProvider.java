/*
 * 2015 copyright ETRI all rights reserved and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package kr.re.etri.tsdn.impl;

import java.util.LinkedList;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.DeviceInfo;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.DeviceInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.device.info.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev150105.device.info.NodeBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);
    private DataBroker db;
    private NotificationProviderService notificationService;
    private ListenerRegistration<org.opendaylight.yangtools.yang.binding.NotificationListener> listenerRegistration;

    @Override
    public void onSessionInitiated(ProviderContext session) {
    	
		db = session.getSALService(DataBroker.class);
		notificationService = session.getSALService(NotificationProviderService.class);
		
		List<Node> listNode = new LinkedList<Node>();
		Node node1 = new NodeBuilder().setId("1").setDescription("this is the first node").build();
		Node node2 = new NodeBuilder().setId("2").setDescription("this is the second node").build();
		listNode.add(node1);
		listNode.add(node2);
		
        DeviceInfo operationalData = new DeviceInfoBuilder()
        .setName("Test Name")
        .setDescription("Test Desc")
        .setNode(listNode)
        .build();
        
        WriteTransaction tx = db.newWriteOnlyTransaction();
        InstanceIdentifier<DeviceInfo> DEVICE_IID = InstanceIdentifier.builder(DeviceInfo.class).build();
        
        tx.put(LogicalDatastoreType.OPERATIONAL, DEVICE_IID, operationalData);
        tx.submit();
		
        LOG.info("HelloProvider Session Initiated");
    }

    @Override
    public void close() throws Exception {
        LOG.info("HelloProvider Closed");
    }
}