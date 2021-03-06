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
package org.apache.qpid.client;

public class AMQUndefinedDestination extends AMQDestination
{

    private static final String UNKNOWN_EXCHANGE_CLASS = "unknown";
    private static final long serialVersionUID = -3938019873332367947L;


    public AMQUndefinedDestination(String exchange, String routingKey, String queueName)
    {
        super(exchange, UNKNOWN_EXCHANGE_CLASS, routingKey, queueName);
    }

    public boolean isNameRequired()
    {
        return getAMQQueueName() == null;
    }

    @Override
    public boolean neverDeclare()
    {
        return true;
    }
}
