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

import java.util.List;

import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.ServiceRefs;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttributes;
import org.everit.osgi.ecm.annotation.attribute.FloatAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;

@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
@BooleanAttributes({
    @BooleanAttribute(attributeId = "childByteAttribute", priority = 8),
})
@StringAttributes({
    @StringAttribute(attributeId = "parentStringAttribute"),
    @StringAttribute(attributeId = "classStringAttribute", defaultValue = "child", priority = 0)
})
@ServiceRefs({
    @ServiceRef(referenceId = "listService", setter = "setListService")
})
@Service
public class ChildTestComponent extends ParentTestComponent implements Supplier<String> {

  private String childStringAttribute;

  private List<String> listService;

  @Activate
  public void childActivate() {
    parentActivate();
  }

  @Override
  public String get() {
    return null;
  }

  public String getChildStringAttribute() {
    return childStringAttribute;
  }

  public List<String> getListService() {
    return listService;
  }

  @StringAttribute
  public void setChildStringAttribute(final String childStringAttribute) {
    this.childStringAttribute = childStringAttribute;
  }

  public void setListService(final List<String> listService) {
    this.listService = listService;
  }

  @FloatAttribute
  @Override
  public void setParentFloatAttribute(final float parentFloatAttribute) {
    super.setParentFloatAttribute(parentFloatAttribute);
  }

  @Update
  public void update() {
  }
}
