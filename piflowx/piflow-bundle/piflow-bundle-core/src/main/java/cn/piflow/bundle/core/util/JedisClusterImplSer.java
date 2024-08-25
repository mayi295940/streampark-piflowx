/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.piflow.bundle.core.util;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class JedisClusterImplSer implements Serializable {

    private static final long serialVersionUID = -51L;

    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_REDIRECTIONS = 5;
    private static final JedisPoolConfig DEFAULT_CONFIG = new JedisPoolConfig();

    private HostAndPort hostAndPort;
    private String password;
    private transient JedisCluster jedisCluster;

    /*
     * public JedisClusterImplSer(HostAndPort hostAndPort) { this.hostAndPort = hostAndPort; this.jedisCluster = new
     * JedisCluster(hostAndPort, 3000000); }
     */

    public JedisClusterImplSer(HostAndPort hostAndPort, String password) {
        this.hostAndPort = hostAndPort;
        this.password = password;
        this.jedisCluster =
            new JedisCluster(
                hostAndPort,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_REDIRECTIONS,
                this.password,
                DEFAULT_CONFIG);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // setJedisCluster(new JedisCluster(hostAndPort));
        setJedisCluster(
            new JedisCluster(
                hostAndPort,
                DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT,
                DEFAULT_REDIRECTIONS,
                this.password,
                DEFAULT_CONFIG));
    }

    private void readObjectNoData() throws ObjectStreamException {
    }

    public JedisCluster getJedisCluster() {
        if (jedisCluster == null)
            this.jedisCluster =
                new JedisCluster(
                    hostAndPort,
                    DEFAULT_TIMEOUT,
                    DEFAULT_TIMEOUT,
                    DEFAULT_REDIRECTIONS,
                    this.password,
                    DEFAULT_CONFIG);
        return jedisCluster;
    }

    private void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public void close() {
        try {
            this.jedisCluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
