
/*
*
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
*
*/


package org.apache.qpid.server.protocol.v1_0.type.messaging.codec;

import org.apache.qpid.server.protocol.v1_0.codec.DescribedTypeConstructor;
import org.apache.qpid.server.protocol.v1_0.codec.DescribedTypeConstructorRegistry;
import org.apache.qpid.server.protocol.v1_0.type.Symbol;
import org.apache.qpid.server.protocol.v1_0.type.UnsignedLong;
import org.apache.qpid.server.protocol.v1_0.type.messaging.JMSSelectorFilter;

public class JMSSelectorFilterConstructor extends DescribedTypeConstructor<JMSSelectorFilter>
{
    private static final Object[] DESCRIPTORS =
    {
            Symbol.valueOf("apache.org:jms-selector-filter:string"),
            UnsignedLong.valueOf(0x0000468C00000004L)
    };

    private static final JMSSelectorFilterConstructor INSTANCE = new JMSSelectorFilterConstructor();

    public static void register(DescribedTypeConstructorRegistry registry)
    {
        for(Object descriptor : DESCRIPTORS)
        {
            registry.register(descriptor, INSTANCE);
        }
    }


    public JMSSelectorFilter construct(Object underlying)
    {

        if(underlying instanceof String)
        {
            return new JMSSelectorFilter((String)underlying);
        }
        else
        {
            // TODO - error
            return null;
        }
    }


}
