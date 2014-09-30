/**
 * This file is part of Everit - Component Annotations Metadata Builder.
 *
 * Everit - Component Annotations Metadata Builder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Component Annotations Metadata Builder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Component Annotations Metadata Builder.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.ecm.annotation.metadatabuilder.test;

import org.everit.osgi.ecm.annotation.metadatabuilder.MetadataBuilder;
import org.everit.osgi.ecm.metadata.AttributeMetadata;
import org.everit.osgi.ecm.metadata.ComponentMetadata;
import org.everit.osgi.ecm.metadata.IntegerAttributeMetadata;
import org.everit.osgi.ecm.metadata.ServiceReferenceMetadata;
import org.junit.Assert;
import org.junit.Test;

public class MetadataBuilderTest {

    @Test
    public void testAnnotatedClass() {
        ComponentMetadata<AnnotatedClass> componentMeta = MetadataBuilder.buildComponentMetadata(
                AnnotatedClass.class);

        AttributeMetadata<?>[] attributeHolders = componentMeta.getAttributes();

        Assert.assertEquals(3, attributeHolders.length);

        ServiceReferenceMetadata referenceOnField = (ServiceReferenceMetadata) attributeHolders[0];
        Assert.assertEquals("referenceWithOnlyDefault.clause", referenceOnField.getAttributeId());
        Assert.assertEquals(Runnable.class, referenceOnField.getServiceInterface());

        IntegerAttributeMetadata intValueMetadata = (IntegerAttributeMetadata) attributeHolders[1];
        Assert.assertEquals("intValue", intValueMetadata.getAttributeId());

        ServiceReferenceMetadata referenceInReferences = (ServiceReferenceMetadata) attributeHolders[2];
        Assert.assertEquals("0.target", referenceInReferences.getAttributeId());
        Assert.assertNull(referenceInReferences.getServiceInterface());

    }
}
