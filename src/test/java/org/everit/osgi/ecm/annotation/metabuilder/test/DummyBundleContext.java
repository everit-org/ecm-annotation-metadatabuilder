/**
 * This file is part of Everit - Component Annotations MetaBuilder.
 *
 * Everit - Component Annotations MetaBuilder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Component Annotations MetaBuilder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Component Annotations MetaBuilder.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.ecm.annotation.metabuilder.test;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class DummyBundleContext implements BundleContext {

    @Override
    public void addBundleListener(BundleListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addServiceListener(ServiceListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub

    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getBundle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getBundle(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getBundle(String location) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle[] getBundles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File getDataFile(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProperty(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S> S getService(ServiceReference<S> reference) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceReference<?> getServiceReference(String clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
            throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle installBundle(String location, InputStream input) throws BundleException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        // TODO Auto-generated method stub
        return false;
    }

}
