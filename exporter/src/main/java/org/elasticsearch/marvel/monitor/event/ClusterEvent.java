package org.elasticsearch.marvel.monitor.event;
/*
 * Licensed to ElasticSearch under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

public abstract class ClusterEvent extends Event {

    protected final String event_source;

    public ClusterEvent(long timestamp, String event_source) {
        super(timestamp);
        this.event_source = event_source;
    }

    @Override
    public String type() {
        return "cluster_event";
    }

    protected abstract String event();

    @Override
    public XContentBuilder addXContentBody(XContentBuilder builder, ToXContent.Params params) throws IOException {
        super.addXContentBody(builder, params);
        builder.field("event", event());
        builder.field("event_source", event_source);
        return builder;
    }

    public static class ClusterBlock extends ClusterEvent {
        private final org.elasticsearch.cluster.block.ClusterBlock block;
        private boolean added;

        public ClusterBlock(long timestamp, org.elasticsearch.cluster.block.ClusterBlock block, boolean added, String event_source) {
            super(timestamp, event_source);
            this.block = block;
            this.added = added;
        }

        @Override
        protected String event() {
            return (added ? "block_added" : "block_removed");
        }

        @Override
        String conciseDescription() {
            return (added ? "added" : "removed") + ": [" + block.toString() + "]";
        }

        @Override
        public XContentBuilder addXContentBody(XContentBuilder builder, ToXContent.Params params) throws IOException {
            super.addXContentBody(builder, params);
            builder.startObject("block");
            block.toXContent(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();
            return builder;
        }
    }
}
