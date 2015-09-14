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

import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttributes;
import org.everit.osgi.ecm.annotation.attribute.ByteAttribute;
import org.everit.osgi.ecm.annotation.attribute.CharacterAttribute;
import org.everit.osgi.ecm.annotation.attribute.FloatAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.PasswordAttribute;
import org.everit.osgi.ecm.annotation.attribute.ShortAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;

/**
 * Component to test initialization via attributes priority.
 */
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
@BooleanAttributes({
    @BooleanAttribute(attributeId = "byteAttribute2", priority = 8),
    @BooleanAttribute(attributeId = "byteAttribute3"),
    @BooleanAttribute(attributeId = "byteAttribute4")
})
@Service
public class TestPriorityComponent {

  private boolean booleanAttribute;

  private byte byteAttribute1;

  private char charAttribute;

  private float floatAttribute1;

  private float floatAttribute2;

  private int intAttribute;

  private String passwordAttribute;

  private MetadataBuilderTest service;

  private short shortAttribute;

  private String stringAttribute1;

  private String stringAttribute2;

  private String stringAttributeAfterDefaultPriority;

  private String stringAttributeDefaultPriority;

  public byte getByteAttribute() {
    return byteAttribute1;
  }

  public char getCharAttribute() {
    return charAttribute;
  }

  public float getFloatAttribute1() {
    return floatAttribute1;
  }

  public float getFloatAttribute2() {
    return floatAttribute2;
  }

  public int getIntAttribute() {
    return intAttribute;
  }

  public String getPasswordAttribute() {
    return passwordAttribute;
  }

  public MetadataBuilderTest getService() {
    return service;
  }

  public short getShortAttribute() {
    return shortAttribute;
  }

  public String getStringAttribute1() {
    return stringAttribute1;
  }

  public String getStringAttribute2() {
    return stringAttribute2;
  }

  public String getStringAttributeAfterDefaultPriority() {
    return stringAttributeAfterDefaultPriority;
  }

  public String getStringAttributeDefaultPriority() {
    return stringAttributeDefaultPriority;
  }

  public boolean isBooleanAttribute() {
    return booleanAttribute;
  }

  @BooleanAttribute(attributeId = "booleanAttribute", priority = 10)
  public void setBooleanAttribute(final boolean booleanAttribute) {
    this.booleanAttribute = booleanAttribute;
  }

  @ByteAttribute(attributeId = "byteAttribute1", priority = 9)
  public void setByteAttribute1(final byte byteAttribute1) {
    this.byteAttribute1 = byteAttribute1;
  }

  @CharacterAttribute(attributeId = "charAttribute", priority = 8)
  public void setCharAttribute(final char charAttribute) {
    this.charAttribute = charAttribute;
  }

  @FloatAttribute(attributeId = "floatAttribute1", priority = 7)
  public void setFloatAttribute1(final float floatAttribute1) {
    this.floatAttribute1 = floatAttribute1;
  }

  @FloatAttribute(attributeId = "floatAttribute2", priority = 6)
  public void setFloatAttribute2(final float floatAttribute2) {
    this.floatAttribute2 = floatAttribute2;
  }

  @IntegerAttribute(attributeId = "intAttribute", priority = 5)
  public void setIntAttribute(final int intAttribute) {
    this.intAttribute = intAttribute;
  }

  @PasswordAttribute(attributeId = "passwordAttribute", priority = 4)
  public void setPasswordAttribute(final String passwordAttribute) {
    this.passwordAttribute = passwordAttribute;
  }

  @ServiceRef(attributeId = "service", attributePriority = 1)
  public void setService(final MetadataBuilderTest service) {
    this.service = service;
  }

  @ShortAttribute(attributeId = "shortAttribute", priority = 2)
  public void setShortAttribute(final short shortAttribute) {
    this.shortAttribute = shortAttribute;
  }

  @StringAttribute(attributeId = "stringAttribute1", priority = 3)
  public void setStringAttribute1(final String stringAttribute1) {
    this.stringAttribute1 = stringAttribute1;
  }

  @StringAttribute(attributeId = "stringAttribute2", priority = 3)
  public void setStringAttribute2(final String stringAttribute2) {
    this.stringAttribute2 = stringAttribute2;
  }

  @StringAttribute(attributeId = "stringAttributeAfterDefaultPriority", priority = 5000)
  public void setStringAttributeAfterDefaultPriority(
      final String stringAttributeAfterDefaultPriority) {
    this.stringAttributeAfterDefaultPriority = stringAttributeAfterDefaultPriority;
  }

  @StringAttribute(attributeId = "stringAttributeDefaultPriority")
  public void setStringAttributeDefaultPriority(final String stringAttributeDefaultPriority) {
    this.stringAttributeDefaultPriority = stringAttributeDefaultPriority;
  }

}
