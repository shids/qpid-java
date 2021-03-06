/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.qpid.server.protocol.v0_8;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.apache.qpid.server.flow.FlowCreditManager;
import org.apache.qpid.server.transport.ProtocolEngine;
import org.apache.qpid.test.utils.QpidTestCase;

public class Pre0_10CreditManagerTest extends QpidTestCase
{
    private Pre0_10CreditManager _creditManager;
    private ProtocolEngine _protocolEngine;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        _protocolEngine = mock(ProtocolEngine.class);
    }

    public void testBasicMessageCredit() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        _creditManager.setCreditLimits(0, 2);
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertFalse("Creditmanager should have credit", _creditManager.hasCredit());
        assertFalse("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        _creditManager.restoreCredit(1, 37);
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
    }

    public void testBytesLimitDoesNotPreventLargeMessage() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        _creditManager.setCreditLimits(10, 0);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(3));
        assertFalse("Creditmanager should not be able to useCredit", _creditManager.useCreditForMessage(30));
        _creditManager.restoreCredit(1, 3);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(30));
    }

    public void testUseCreditWithNegativeMessageCredit() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        _creditManager.setCreditLimits(0, 3);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        _creditManager.setCreditLimits(0, 1); // This should get us to credit=-2
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        assertFalse("Creditmanager should not be able to useCredit", _creditManager.useCreditForMessage(37));
        _creditManager.restoreCredit(1, 37);
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        _creditManager.restoreCredit(1, 37);
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        _creditManager.restoreCredit(1, 37);
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
    }

    public void testUseCreditWithNegativeBytesCredit() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        _creditManager.setCreditLimits(3, 0);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(1));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(1));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(1));
        _creditManager.setCreditLimits(1, 0); // This should get us to credit=-2
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        assertFalse("Creditmanager should not be able to useCredit", _creditManager.useCreditForMessage(1));
        _creditManager.restoreCredit(1, 1);
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        _creditManager.restoreCredit(1, 1);
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        _creditManager.restoreCredit(1, 1);
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
    }

    public void testCreditAccountingWhileMessageLimitNotSet() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        _creditManager.restoreCredit(1, 37);
        _creditManager.setCreditLimits(37, 1); // This should get us to credit=-1
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        assertFalse("Creditmanager should not be able to useCredit", _creditManager.useCreditForMessage(37));
        _creditManager.restoreCredit(1, 37);
        assertFalse("Creditmanager should not have credit", _creditManager.hasCredit());
        _creditManager.restoreCredit(1, 37);
        assertTrue("Creditmanager should have credit", _creditManager.hasCredit());
    }

    public void testMessageCreditExhaustionSuspends() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        final FlowCreditManager.FlowCreditManagerListener flowListener =
                mock(FlowCreditManager.FlowCreditManagerListener.class);
        _creditManager.addStateListener(flowListener);
        _creditManager.setCreditLimits(0, 1);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        verify(flowListener, never()).creditStateChanged(anyBoolean());
        assertFalse("Creditmanager should not be able to useCredit", _creditManager.useCreditForMessage(37));
        verify(flowListener).creditStateChanged(false);
        _creditManager.restoreCredit(1, 0);
        verify(flowListener).creditStateChanged(true);
    }

    public void testBytesCreditExhaustionSuspends() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        final FlowCreditManager.FlowCreditManagerListener flowListener =
                mock(FlowCreditManager.FlowCreditManagerListener.class);
        _creditManager.addStateListener(flowListener);
        _creditManager.setCreditLimits(10, 0);
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(7));
        assertFalse("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(37));
        verify(flowListener, never()).creditStateChanged(anyBoolean());
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(3));
        assertFalse("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(3));
        verify(flowListener).creditStateChanged(false);
        _creditManager.restoreCredit(1, 3);
        verify(flowListener).creditStateChanged(true);
    }

    public void testNotifiedAfterBytesCreditIncreased() throws Exception
    {
        _creditManager = new Pre0_10CreditManager(0, 0, _protocolEngine);
        final FlowCreditManager.FlowCreditManagerListener flowListener =
                mock(FlowCreditManager.FlowCreditManagerListener.class);
        _creditManager.addStateListener(flowListener);
        _creditManager.setCreditLimits(10, 0);

        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(4));
        assertTrue("Creditmanager should be able to useCredit", _creditManager.useCreditForMessage(4));
        verify(flowListener, never()).creditStateChanged(anyBoolean());

        _creditManager.restoreCredit(1, 4);
        verify(flowListener).creditStateChanged(true);
    }
}
