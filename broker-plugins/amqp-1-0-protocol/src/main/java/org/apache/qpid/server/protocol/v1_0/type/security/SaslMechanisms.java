
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


package org.apache.qpid.server.protocol.v1_0.type.security;


import java.util.Arrays;
import org.apache.qpid.server.protocol.v1_0.SASLEndpoint;
import org.apache.qpid.server.protocol.v1_0.type.SaslFrameBody;
import org.apache.qpid.server.protocol.v1_0.type.Symbol;

public class SaslMechanisms
  implements SaslFrameBody
  {


    private Symbol[] _saslServerMechanisms;

    public Symbol[] getSaslServerMechanisms()
    {
        return _saslServerMechanisms;
    }

    public void setSaslServerMechanisms(Symbol[] saslServerMechanisms)
    {
        _saslServerMechanisms = saslServerMechanisms;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("SaslMechanisms{");
        final int origLength = builder.length();

        if(_saslServerMechanisms != null)
        {
            if(builder.length() != origLength)
            {
                builder.append(',');
            }
            builder.append("saslServerMechanisms=").append(Arrays.asList(_saslServerMechanisms));
        }

        builder.append('}');
        return builder.toString();
    }

    public void invoke(final short channel, SASLEndpoint conn)
    {
        conn.receiveSaslMechanisms(this);
    }


  }
