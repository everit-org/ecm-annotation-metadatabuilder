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
import org.everit.osgi.ecm.metadata.MetadataValidationException;
import org.everit.osgi.ecm.util.method.MethodDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class MetadataBuilderTest {

  @Test
  public void testAttributeInheritance() {
    ComponentMetadata buildComponentMetadata = MetadataBuilder
        .buildComponentMetadata(ChildTestComponent.class);

    AttributeMetadata<?>[] attributes = buildComponentMetadata.getAttributes();
    Assert.assertEquals(9, attributes.length);
    int i = 0;
    // redefined in ChildTestComponent
    Assert.assertEquals("classStringAttribute", attributes[i].getAttributeId());
    Assert.assertEquals(0, attributes[i].getPriority(), 0);
    Assert.assertEquals("child", ((String[]) attributes[i++].getDefaultValue())[0]);

    Assert.assertEquals("childByteAttribute", attributes[i++].getAttributeId());

    Assert.assertEquals("parentByteAttribute", attributes[i++].getAttributeId());

    Assert.assertEquals("parentBooleanAttribute", attributes[i++].getAttributeId());

    Assert.assertEquals("childStringAttribute", attributes[i++].getAttributeId());

    Assert.assertEquals("listService.target", attributes[i++].getAttributeId());

    Assert.assertEquals("mapService.target", attributes[i++].getAttributeId());

    // redefined in ChildTestComponent
    Assert.assertEquals("parentFloatAttribute", attributes[i].getAttributeId());
    Assert.assertEquals(null, attributes[i++].getDefaultValue());

    // redefined in ChildTestComponent
    Assert.assertEquals("parentStringAttribute", attributes[i].getAttributeId());
    Assert.assertEquals(null, attributes[i++].getDefaultValue());

    MethodDescriptor activate = buildComponentMetadata.getActivate();
    Assert.assertEquals("childActivate", activate.getMethodName());

    MethodDescriptor update = buildComponentMetadata.getUpdate();
    Assert.assertEquals("update", update.getMethodName());

    MethodDescriptor deactivate = buildComponentMetadata.getDeactivate();
    Assert.assertEquals("parentDeactivate", deactivate.getMethodName());
  }

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
  public void testWrongAttributeInheritance() {
    try {
      MetadataBuilder.buildComponentMetadata(WrongChildTestComponent.class);
      Assert.fail("Expect MetadataValidationException. The overrided 'classStringAttribute' "
          + "attribute type is wrong.");
    } catch (MetadataValidationException e) {
      Assert.assertNotNull(e);
      Assert.assertEquals("Overrided attribute id 'classStringAttribute' attribute type is wrong. "
          + "Parent attribute type 'class org.everit.osgi.ecm.metadata.StringAttributeMetadata', "
          + "child attribute type 'class org.everit.osgi.ecm.metadata.BooleanAttributeMetadata'.",
          e.getMessage());
    }
  }
}
