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

package org.apache.cloudstack.mom.inmemory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import com.cloud.utils.Pair;
import org.apache.log4j.Logger;

import org.apache.cloudstack.framework.events.Event;
import org.apache.cloudstack.framework.events.EventBus;
import org.apache.cloudstack.framework.events.EventBusException;
import org.apache.cloudstack.framework.events.EventSubscriber;
import org.apache.cloudstack.framework.events.EventTopic;

import com.cloud.utils.component.ManagerBase;

@Local(value = EventBus.class)
public class InMemoryEventBus extends ManagerBase implements EventBus {

    private String name;
    private static final Logger s_logger = Logger.getLogger(InMemoryEventBus.class);
    private static ConcurrentHashMap<UUID, Pair<EventTopic, EventSubscriber>> s_subscribers;

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        s_subscribers = new ConcurrentHashMap<UUID, Pair<EventTopic, EventSubscriber>>();
        return true;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID subscribe(EventTopic topic, EventSubscriber subscriber) throws EventBusException {
        if (subscriber == null || topic == null) {
            throw new EventBusException("Invalid EventSubscriber/EventTopic object passed.");
        }
        UUID subscriberId = UUID.randomUUID();

        s_subscribers.put(subscriberId, new Pair(topic, subscriber));
        return subscriberId;
    }

    @Override
    public void unsubscribe(UUID subscriberId, EventSubscriber subscriber) throws EventBusException {
        if (s_subscribers != null && s_subscribers.isEmpty()) {
            throw new EventBusException("There are no registered subscribers to unregister.");
        }
        if (s_subscribers.get(subscriberId) == null) {
            throw new EventBusException("No subscriber found with subscriber id " + subscriberId);
        }
        s_subscribers.remove(subscriberId);
    }

    @Override
    public void publish(Event event) throws EventBusException {
        if (s_subscribers == null || s_subscribers.isEmpty()) {
            return; // no subscriber to publish to, so just return
        }

        for (UUID subscriberId : s_subscribers.keySet()) {
            Pair<EventTopic, EventSubscriber>  subscriberDetails =  s_subscribers.get(subscriberId);
            // if the event matches subscribers interested event topic then call back the subscriber with the event
            if (isEventMatchesTopic(event, subscriberDetails.first())) {
                EventSubscriber subscriber =  subscriberDetails.second();
                subscriber.onEvent(event);
            }
        }
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    private String replaceNullWithWildcard(String key) {
        if (key == null || key.isEmpty()) {
            return "*";
        } else {
            return key;
        }
    }

    private boolean isEventMatchesTopic(Event event, EventTopic topic) {

        String eventTopicSource = replaceNullWithWildcard(topic.getEventSource());
        eventTopicSource = eventTopicSource.replace(".", "-");
        String eventSource = replaceNullWithWildcard(event.getEventSource());
        eventSource = eventSource.replace(".", "-");
        if (eventTopicSource != "*" && eventSource != "*" && !eventTopicSource.equalsIgnoreCase(eventSource)) {
            return false;
        }

        String eventTopicCategory = replaceNullWithWildcard(topic.getEventCategory());
        eventTopicCategory = eventTopicCategory.replace(".", "-");
        String eventCategory = replaceNullWithWildcard(event.getEventCategory());
        eventCategory = eventCategory.replace(".", "-");
        if (eventTopicCategory != "*" && eventCategory != "*" && !eventTopicCategory.equalsIgnoreCase(eventCategory)) {
            return false;
        }

        String eventTopicType = replaceNullWithWildcard(topic.getEventType());
        eventTopicType = eventTopicType.replace(".", "-");
        String eventType = replaceNullWithWildcard(event.getEventType());
        eventType = eventType.replace(".", "-");
        if (eventTopicType != "*" && eventType != "*" && !eventTopicType.equalsIgnoreCase(eventType)) {
            return false;
        }

        String eventTopicResourceType = replaceNullWithWildcard(topic.getResourceType());
        eventTopicResourceType = eventTopicResourceType.replace(".", "-");
        String resourceType = replaceNullWithWildcard(event.getResourceType());
        resourceType = resourceType.replace(".", "-");
        if (eventTopicResourceType != "*" && resourceType != "*" && !eventTopicResourceType.equalsIgnoreCase(resourceType)) {
            return false;
        }

        String resourceUuid = replaceNullWithWildcard(event.getResourceUUID());
        resourceUuid = resourceUuid.replace(".", "-");
        String eventTopicresourceUuid = replaceNullWithWildcard(topic.getResourceUUID());
        eventTopicresourceUuid = eventTopicresourceUuid.replace(".", "-");
        if (resourceUuid != "*" && eventTopicresourceUuid != "*" && !resourceUuid.equalsIgnoreCase(eventTopicresourceUuid)) {
            return false;
        }

        return true;
    }
}