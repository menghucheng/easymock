/**
 * Copyright 2001-2011 the original author or authors.
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
package org.easymock.internal;

import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

import org.easymock.ConstructorArgs;
import org.easymock.internal.ClassProxyFactory.MockMethodInterceptor;

/**
 * @author Henri Tremblay
 */
public final class ClassExtensionHelper {

    private static final ThreadLocal<ConstructorArgs> currentConstructorArgs = new ThreadLocal<ConstructorArgs>();

    // ///CLOVER:OFF
    private ClassExtensionHelper() {
    }

    // ///CLOVER:ON

    public static void setCurrentConstructorArgs(final ConstructorArgs args) {
        currentConstructorArgs.set(args);
    }

    public static ConstructorArgs getCurrentConstructorArgs() {
        return currentConstructorArgs.get();
    }

    public static MockMethodInterceptor getInterceptor(final Object mock) {
        final Factory factory = (Factory) mock;
        return (MockMethodInterceptor) factory.getCallback(0);
    }

    public static MocksControl getControl(final Object mock) {
        try {
            ObjectMethodsFilter handler;

            if (Proxy.isProxyClass(mock.getClass())) {
                handler = (ObjectMethodsFilter) Proxy.getInvocationHandler(mock);
            } else if (Enhancer.isEnhanced(mock.getClass())) {
                handler = (ObjectMethodsFilter) getInterceptor(mock).getHandler();
            } else {
                throw new IllegalArgumentException("Not a mock: " + mock.getClass().getName());
            }
            return handler.getDelegate().getControl();
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Not a mock: " + mock.getClass().getName());
        }
    }

    /**
     * Return the class of interface (depending on the mock type) that was
     * mocked
     * 
     * @param <T>
     *            Mocked class
     * @param <V>
     *            Mock class
     * @param proxy
     *            Mock object
     * @return the mocked class or interface
     */
    @SuppressWarnings("unchecked")
    public static <T, V extends T> Class<T> getMockedType(final V proxy) {
        if (Proxy.isProxyClass(proxy.getClass())) {
            return (Class<T>) proxy.getClass().getInterfaces()[0];
        }
        return (Class<T>) proxy.getClass().getSuperclass();
    }
}
