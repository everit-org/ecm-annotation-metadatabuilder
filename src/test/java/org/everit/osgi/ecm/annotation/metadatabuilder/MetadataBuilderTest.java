/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.osgi.ecm.annotation.metadatabuilder;

import org.everit.osgi.ecm.metadata.AttributeMetadata;
import org.everit.osgi.ecm.metadata.ComponentMetadata;
import org.everit.osgi.ecm.util.method.MethodDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class MetadataBuilderTest {

  @Test
  public void testAttributePriorityOrder() {
    ComponentMetadata buildComponentMetadata = MetadataBuilder
        .buildComponentMetadata(TestPriorityComponent.class);

    AttributeMetadata<?>[] attributes = buildComponentMetadata.getAttributes();
    int i = 0;
    Assert.assertEquals("The attributes lenght not the expected.", 16, attributes.length);
    Assert.assertEquals("service", attributes[i++].getAttributeId());
    Assert.assertEquals("shortAttribute", attributes[i++].getAttributeId());
    Assert.assertEquals("stringAttribute1", attributes[i++].getAttributeId());
    Assert.assertEquals("stringAttribute2", attributes[i++].getAttributeId());
    Assert.assertEquals("passwordAttribute", attributes[i++].getAttributeId());
    Assert.assertEquals("intAttribute", attributes[i++].getAttributeId());
    Assert.assertEquals("floatAttribute2", attributes[i++].getAttributeId());
    Assert.assertEquals("floatAttribute1", attributes[i++].getAttributeId());
    Assert.assertEquals("byteAttribute2", attributes[i++].getAttributeId());
    Assert.assertEquals("charAttribute", attributes[i++].getAttributeId());
    Assert.assertEquals("byteAttribute1", attributes[i++].getAttributeId());
    Assert.assertEquals("booleanAttribute", attributes[i++].getAttributeId());
    Assert.assertEquals("byteAttribute3", attributes[i++].getAttributeId());
    Assert.assertEquals("byteAttribute4", attributes[i++].getAttributeId());
    Assert.assertEquals("stringAttributeDefaultPriority", attributes[i++].getAttributeId());
    Assert.assertEquals("stringAttributeAfterDefaultPriority", attributes[i++].getAttributeId());
  }

  @Test
  public void testValami() {
    ComponentMetadata buildComponentMetadata = MetadataBuilder
        .buildComponentMetadata(ChildTestComponent.class);

    AttributeMetadata<?>[] attributes = buildComponentMetadata.getAttributes();
    Assert.assertEquals(6, attributes.length);

    MethodDescriptor activate = buildComponentMetadata.getActivate();
    Assert.assertEquals("childActivate", activate.getMethodName());

    MethodDescriptor update = buildComponentMetadata.getUpdate();
    Assert.assertEquals("update", update.getMethodName());

    MethodDescriptor deactivate = buildComponentMetadata.getDeactivate();
    Assert.assertEquals("parentDeactivate", deactivate.getMethodName());
  }
}
