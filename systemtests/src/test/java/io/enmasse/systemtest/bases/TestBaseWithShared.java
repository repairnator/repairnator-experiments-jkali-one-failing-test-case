/*
 * Copyright 2017-2018, EnMasse authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.enmasse.systemtest.bases;

import io.enmasse.systemtest.*;
import io.enmasse.systemtest.amqp.AmqpClientFactory;
import io.enmasse.systemtest.clients.AbstractClient;
import io.enmasse.systemtest.mqtt.MqttClientFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.enmasse.systemtest.TestTag.shared;

@Tag(shared)
public abstract class TestBaseWithShared extends TestBase {
    private static final String defaultAddressTemplate = "-shared-";
    private static final Destination dummyAddress = Destination.queue("dummy-address", "pooled-queue");
    protected static AddressSpace sharedAddressSpace;
    private static Logger log = CustomLogger.getLogger();
    private static Map<AddressSpaceType, Integer> spaceCountMap = new HashMap<>();

    private static void deleteSharedAddressSpace(AddressSpace addressSpace) throws Exception {
        TestBase.deleteAddressSpace(addressSpace);
    }

    public AddressSpace getSharedAddressSpace() {
        return sharedAddressSpace;
    }

    @BeforeEach
    public void setupShared() throws Exception {
        spaceCountMap.putIfAbsent(getAddressSpaceType(), 0);
        sharedAddressSpace = new AddressSpace(
                getAddressSpaceType().name().toLowerCase() + defaultAddressTemplate + spaceCountMap.get(getAddressSpaceType()),
                getAddressSpaceType(),
                AuthService.STANDARD);
        log.info("Test is running in multitenant mode");
        createSharedAddressSpace(sharedAddressSpace);
        if (environment.useDummyAddress() && !skipDummyAddress()) {
            if (!addressExists(dummyAddress)) {
                log.info("'{}' address doesn't exist and will be created", dummyAddress);
                super.setAddresses(sharedAddressSpace, dummyAddress);
            }
        }
        defaultCredentials.setUsername("test");
        defaultCredentials.setPassword("test");
        createUser(sharedAddressSpace, defaultCredentials);

        this.managementCredentials = new KeycloakCredentials("artemis-admin", "artemis-admin");
        createUser(sharedAddressSpace,
                managementCredentials,
                Group.ADMIN.toString(),
                Group.SEND_ALL_BROKERED.toString(),
                Group.RECV_ALL_BROKERED.toString(),
                Group.VIEW_ALL_BROKERED.toString(),
                Group.MANAGE_ALL_BROKERED.toString());

        amqpClientFactory = new AmqpClientFactory(kubernetes, environment, sharedAddressSpace, defaultCredentials);
        mqttClientFactory = new MqttClientFactory(kubernetes, environment, sharedAddressSpace, defaultCredentials);
    }

    @AfterEach
    public void tearDownShared(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) { //test failed
            if (!environment.skipCleanup()) {
                log.info(String.format("test failed: %s.%s",
                        context.getTestClass().get().getName(),
                        context.getTestMethod().get().getName()));
                log.info("shared address space '{}' will be removed", sharedAddressSpace);
                try {
                    deleteSharedAddressSpace(sharedAddressSpace);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    spaceCountMap.put(sharedAddressSpace.getType(), spaceCountMap.get(sharedAddressSpace.getType()) + 1);
                }
            } else {
                log.warn("Remove address spaces when test failed - SKIPPED!");
            }
        } else { //succeed
            try {
                setAddresses();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createSharedAddressSpace(AddressSpace addressSpace) throws Exception {
        super.createAddressSpace(addressSpace);
    }

    protected void scale(Destination destination, int numReplicas) throws Exception {
        scale(sharedAddressSpace, destination, numReplicas);
    }

    /**
     * get all addresses except 'dummy-address'
     */
    protected Future<List<String>> getAddresses(Optional<String> addressName) throws Exception {
        return TestUtils.getAddresses(addressApiClient, sharedAddressSpace, addressName, Collections.singletonList(dummyAddress.getAddress()));
    }

    /**
     * check if address exists
     */
    private boolean addressExists(Destination destination) throws Exception {
        Future<List<String>> addresses = TestUtils.getAddresses(addressApiClient, sharedAddressSpace, Optional.empty(),
                new ArrayList<>());
        List<String> address = addresses.get(20, TimeUnit.SECONDS);
        log.info("found addresses");
        address.forEach(addr -> log.info("- address '{}'", addr));
        log.info("looking for '{}' address", destination.getAddress());
        return address.contains(destination.getAddress());
    }

    protected Future<List<Address>> getAddressesObjects(Optional<String> addressName) throws Exception {
        return TestUtils.getAddressesObjects(addressApiClient, sharedAddressSpace, addressName, Collections.singletonList(dummyAddress.getAddress()));
    }

    private Future<List<Destination>> getDestinationsObjects(Optional<String> addressName) throws Exception {
        return TestUtils.getDestinationsObjects(addressApiClient, sharedAddressSpace, addressName, Collections.singletonList(dummyAddress.getAddress()));
    }

    /**
     * delete all addresses except 'dummy-address' and append new addresses
     *
     * @param destinations destinations to create
     * @throws Exception address not ready
     */
    protected void setAddresses(Destination... destinations) throws Exception {
        if (isBrokered(sharedAddressSpace) || !environment.useDummyAddress()) {
            setAddresses(sharedAddressSpace, destinations);
        } else {
            List<Destination> inShared = getDestinationsObjects(Optional.empty())
                    .get(10, TimeUnit.SECONDS);
            if (inShared.size() > 0) {
                deleteAddresses(inShared.toArray(new Destination[0]));
            }
            if (destinations.length > 0) {
                appendAddresses(destinations);
            }
        }
    }

    /**
     * append new addresses into address-space and sharedAddresses list
     *
     * @param destinations destinations to create
     * @throws Exception address not ready
     */
    protected void appendAddresses(Destination... destinations) throws Exception {
        appendAddresses(sharedAddressSpace, destinations);
    }

    /**
     * use DELETE html method for delete specific addresses
     *
     * @param destinations destinations to remove
     * @throws Exception address not detleted
     */
    protected void deleteAddresses(Destination... destinations) throws Exception {
        deleteAddresses(sharedAddressSpace, destinations);
    }

    /**
     * attach N receivers into one address with default username/password
     */
    protected List<AbstractClient> attachReceivers(Destination destination, int receiverCount) throws Exception {
        return attachReceivers(sharedAddressSpace, destination, receiverCount, defaultCredentials);
    }

    /**
     * attach N receivers into one address with own username/password
     */
    protected List<AbstractClient> attachReceivers(Destination destination, int receiverCount, KeycloakCredentials credentials) throws Exception {
        return attachReceivers(sharedAddressSpace, destination, receiverCount, credentials);
    }

    /**
     * attach senders to destinations
     */
    protected List<AbstractClient> attachSenders(List<Destination> destinations) {
        return attachSenders(sharedAddressSpace, destinations);
    }

    /**
     * attach receivers to destinations
     */
    protected List<AbstractClient> attachReceivers(List<Destination> destinations) {
        return attachReceivers(sharedAddressSpace, destinations);
    }

    /**
     * create M connections with N receivers and K senders
     */
    protected AbstractClient attachConnector(Destination destination, int connectionCount,
                                             int senderCount, int receiverCount) {
        return attachConnector(sharedAddressSpace, destination, connectionCount, senderCount, receiverCount, defaultCredentials);
    }
}
