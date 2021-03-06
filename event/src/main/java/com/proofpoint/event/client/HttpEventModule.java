/*
 * Copyright 2010 Proofpoint, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.proofpoint.event.client;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import static com.proofpoint.configuration.ConfigurationModule.bindConfig;
import static com.proofpoint.discovery.client.DiscoveryBinder.discoveryBinder;
import static com.proofpoint.http.client.HttpClientBinder.httpClientBinder;
import static org.weakref.jmx.guice.MBeanModule.newExporter;

public class HttpEventModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(JsonEventWriter.class).in(Scopes.SINGLETON);

        binder.bind(EventClient.class).to(HttpEventClient.class).in(Scopes.SINGLETON);
        newExporter(binder).export(EventClient.class).withGeneratedName();
        bindConfig(binder).to(HttpEventClientConfig.class);
        discoveryBinder(binder).bindHttpSelector("event");
        discoveryBinder(binder).bindHttpSelector("collector");

        // bind the http client
        httpClientBinder(binder).bindAsyncHttpClient("event", ForEventClient.class);

        // Kick off the binding of Set<EventTypeMetadata> in case no events are bound
        Multibinder.newSetBinder(binder, new TypeLiteral<EventTypeMetadata<?>>() {});
    }
}
