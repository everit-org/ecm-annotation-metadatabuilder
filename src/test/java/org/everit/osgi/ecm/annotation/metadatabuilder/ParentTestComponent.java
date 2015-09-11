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

import java.util.Map;
import java.util.function.Function;

import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.ByteAttribute;
import org.everit.osgi.ecm.annotation.attribute.FloatAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;

/**
 * Component to test initialization via attributes priority.
 */
public class ParentTestComponent implements Function<String, String> {

  private Map<String, String> mapService;

  private boolean parentBooleanAttribute;

  private byte parentByteAttribute;

  private float parentFloatAttribute;

  private String parentStringAttribute;

  @Override
  public String apply(final String t) {
    return null;
  }

  public Map<String, String> getMapService() {
    return mapService;
  }

  public byte getParentByteAttribute() {
    return parentByteAttribute;
  }

  public float getParentFloatAttribute() {
    return parentFloatAttribute;
  }

  public String getParentStringAttribute() {
    return parentStringAttribute;
  }

  public boolean isParentBooleanAttribute() {
    return parentBooleanAttribute;
  }

  @Activate
  public void parentActivate() {
  }

  @Deactivate
  public void parentDeactivate() {
  }

  @ServiceRef
  public void setMapService(final Map<String, String> mapService) {
    this.mapService = mapService;
  }

  @BooleanAttribute(priority = 10, defaultValue = true)
  public void setParentBooleanAttribute(final boolean parentBooleanAttribute) {
    this.parentBooleanAttribute = parentBooleanAttribute;
  }

  @ByteAttribute(priority = 9)
  public void setParentByteAttribute(final byte parentByteAttribute) {
    this.parentByteAttribute = parentByteAttribute;
  }

  @FloatAttribute(priority = 10, description = "description")
  public void setParentFloatAttribute(final float parentFloatAttribute) {
    this.parentFloatAttribute = parentFloatAttribute;
  }

  @StringAttribute(attributeId = "parentStringAttribute", priority = 10,
      defaultValue = "Type here some numbers")
  public void setParentStringAttribute(final String parentStringAttribute) {
    this.parentStringAttribute = parentStringAttribute;
  }

}
