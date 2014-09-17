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

import org.everit.osgi.ecm.annotation.metabuilder.MetaBuilder;
import org.everit.osgi.ecm.meta.AttributeMetaHolder;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.everit.osgi.ecm.meta.ReferenceMeta;
import org.junit.Assert;
import org.junit.Test;

public class MetaBuilderTest {

    @Test
    public void testAnnotatedClass() {
        ComponentMeta<AnnotatedClass> componentMeta = MetaBuilder.buildComponentMeta(new DummyBundleContext(),
                AnnotatedClass.class);

        AttributeMetaHolder<?>[] attributeHolders = componentMeta.getAttributeHolders();

        Assert.assertEquals(2, attributeHolders.length);

        ReferenceMeta referenceInReferences = (ReferenceMeta) attributeHolders[0];
        Assert.assertEquals("0.target", referenceInReferences.getAttribute().getName());
        Assert.assertNull(referenceInReferences.getReferenceInterface());

        ReferenceMeta referenceOnField = (ReferenceMeta) attributeHolders[1];
        Assert.assertEquals("referenceWithOnlyDefault.clause", referenceOnField.getAttribute().getName());
        Assert.assertEquals(Runnable.class, referenceOnField.getReferenceInterface());
    }
}
