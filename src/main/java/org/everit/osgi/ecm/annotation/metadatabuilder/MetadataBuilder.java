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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.annotation.Generated;

import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.AutoDetect;
import org.everit.osgi.ecm.annotation.BundleCapabilityRef;
import org.everit.osgi.ecm.annotation.BundleCapabilityRefs;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ManualServices;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.ServiceRefs;
import org.everit.osgi.ecm.annotation.ThreeStateBoolean;
import org.everit.osgi.ecm.annotation.Update;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttributes;
import org.everit.osgi.ecm.annotation.attribute.ByteAttribute;
import org.everit.osgi.ecm.annotation.attribute.ByteAttributes;
import org.everit.osgi.ecm.annotation.attribute.CharacterAttribute;
import org.everit.osgi.ecm.annotation.attribute.CharacterAttributes;
import org.everit.osgi.ecm.annotation.attribute.DoubleAttribute;
import org.everit.osgi.ecm.annotation.attribute.DoubleAttributes;
import org.everit.osgi.ecm.annotation.attribute.FloatAttribute;
import org.everit.osgi.ecm.annotation.attribute.FloatAttributes;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttribute;
import org.everit.osgi.ecm.annotation.attribute.IntegerAttributes;
import org.everit.osgi.ecm.annotation.attribute.LongAttribute;
import org.everit.osgi.ecm.annotation.attribute.LongAttributes;
import org.everit.osgi.ecm.annotation.attribute.PasswordAttribute;
import org.everit.osgi.ecm.annotation.attribute.PasswordAttributes;
import org.everit.osgi.ecm.annotation.attribute.ShortAttribute;
import org.everit.osgi.ecm.annotation.attribute.ShortAttributes;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ServiceHolder;
import org.everit.osgi.ecm.metadata.AttributeMetadata;
import org.everit.osgi.ecm.metadata.AttributeMetadata.AttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.BooleanAttributeMetadata.BooleanAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.BundleCapabilityReferenceMetadata.BundleCapabilityReferenceMetadataBuilder;
import org.everit.osgi.ecm.metadata.ByteAttributeMetadata.ByteAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.CharacterAttributeMetadata.CharacterAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.ComponentMetadata;
import org.everit.osgi.ecm.metadata.ComponentMetadata.ComponentMetadataBuilder;
import org.everit.osgi.ecm.metadata.ConfigurationPolicy;
import org.everit.osgi.ecm.metadata.DoubleAttributeMetadata.DoubleAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.FloatAttributeMetadata.FloatAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.Icon;
import org.everit.osgi.ecm.metadata.IntegerAttributeMetadata.IntegerAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.LongAttributeMetadata.LongAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.MetadataValidationException;
import org.everit.osgi.ecm.metadata.PasswordAttributeMetadata.PasswordAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.PropertyAttributeMetadata.PropertyAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.ReferenceConfigurationType;
import org.everit.osgi.ecm.metadata.ReferenceMetadata.ReferenceMetadataBuilder;
import org.everit.osgi.ecm.metadata.SelectablePropertyAttributeMetadata.SelectablePropertyAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.ServiceMetadata;
import org.everit.osgi.ecm.metadata.ServiceMetadata.ServiceMetadataBuilder;
import org.everit.osgi.ecm.metadata.ServiceReferenceMetadata.ServiceReferenceMetadataBuilder;
import org.everit.osgi.ecm.metadata.ShortAttributeMetadata.ShortAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.StringAttributeMetadata.StringAttributeMetadataBuilder;
import org.everit.osgi.ecm.util.method.MethodDescriptor;
import org.everit.osgi.ecm.util.method.MethodUtil;
import org.osgi.framework.Version;

/**
 * Programmers can use the {@link MetadataBuilder} to generate component metadata from annotated
 * classes.
 *
 * @param <C>
 *          The type of the Component.
 */
public final class MetadataBuilder<C> {

  /**
   * Compares {@link AttributeMetadata} classes based on their priority and than their names.
   */
  private static final class AttributeMetadataComparator
      implements Comparator<AttributeMetadata<?>>, Serializable {

    private static final long serialVersionUID = -7796104393729198249L;

    @Override
    public int compare(final AttributeMetadata<?> attr1, final AttributeMetadata<?> attr2) {
      float attr1Priority = attr1.getPriority();
      float attr2Priority = attr2.getPriority();
      int compare = Float.compare(attr1Priority, attr2Priority);
      if (compare != 0) {
        return compare;
      }

      String attr1Id = attr1.getAttributeId();
      String attr2Id = attr2.getAttributeId();
      return attr1Id.compareTo(attr2Id);
    }
  }

  private static final Set<Class<?>> ANNOTATION_CONTAINER_TYPES;

  private static final AttributeMetadataComparator ATTRIBUTE_METADATA_COMPARATOR =
      new AttributeMetadataComparator();

  private static final Map<Class<?>, Class<?>> PRIMITIVE_BOXING_TYPE_MAPPING;

  static {
    ANNOTATION_CONTAINER_TYPES = new HashSet<Class<?>>();
    ANNOTATION_CONTAINER_TYPES.add(ServiceRefs.class);
    ANNOTATION_CONTAINER_TYPES.add(BundleCapabilityRefs.class);
    ANNOTATION_CONTAINER_TYPES.add(BooleanAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(ByteAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(CharacterAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(DoubleAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(FloatAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(IntegerAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(LongAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(PasswordAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(ShortAttributes.class);
    ANNOTATION_CONTAINER_TYPES.add(StringAttributes.class);

    PRIMITIVE_BOXING_TYPE_MAPPING = new HashMap<>();
    PRIMITIVE_BOXING_TYPE_MAPPING.put(boolean.class, Boolean.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(byte.class, Byte.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(char.class, Character.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(double.class, Double.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(float.class, Float.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(int.class, Integer.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(long.class, Long.class);
    PRIMITIVE_BOXING_TYPE_MAPPING.put(short.class, Short.class);

  }

  /**
   * Generates ECM Component Metadata from an annotated class.
   *
   * @param clazz
   *          The type of the class that is annotated.
   * @return The generated Metadata that can be used to set up an ECM Component Container.
   */
  public static <C> ComponentMetadata buildComponentMetadata(final Class<C> clazz) {
    MetadataBuilder<C> metadataBuilder = new MetadataBuilder<C>(clazz);
    return metadataBuilder.build();
  }

  /**
   * Generates ECM Component Metadata from an annotated class. This method is introduced as
   * technologies with bytecode weaving might change the result of {@link Class#getName()} that
   * causes issues during automatic OSGi service registration based on the name of the type.
   *
   * @param className
   *          The name of the class that is annotated.
   * @param classLoader
   *          The classLoader that can load the class specified with className.
   *
   * @return The generated Metadata that can be used to set up an ECM Component Container.
   * @since 3.1.0
   */
  public static ComponentMetadata buildComponentMetadata(String className,
      final ClassLoader classLoader) {
    MetadataBuilder<?> metadataBuilder = new MetadataBuilder<>(className, classLoader);
    return metadataBuilder.build();
  }

  private static String getClassNameOrNull(final Class<?> clazz) {
    return (clazz != null) ? clazz.getName() : null;
  }

  private final Map<String, Class<?>> attributeClasses = new HashMap<>();

  private final Map<String, AttributeMetadata<?>> attributes = new HashMap<>();

  private final String originalClassName;

  private final Class<C> originalClazz;

  private Class<?> processedClazz;

  private final Stack<Class<?>> superClazzes = new Stack<>();

  private MetadataBuilder(final Class<C> clazz) {
    this.originalClazz = clazz;
    this.originalClassName = clazz.getName();
  }

  private MetadataBuilder(final String className, ClassLoader classLoader) {
    this.originalClassName = className;
    try {
      @SuppressWarnings("unchecked")
      Class<C> clazz = (Class<C>) classLoader.loadClass(className);
      this.originalClazz = clazz;
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private ComponentMetadata build() {
    Component componentAnnotation = originalClazz.getAnnotation(Component.class);
    if (componentAnnotation == null) {
      throw new ComponentAnnotationMissingException("Component annotation is missing on type "
          + originalClazz.toString());
    }
    ComponentMetadataBuilder componentMetaBuilder = new ComponentMetadataBuilder()
        .withType(originalClassName)
        .withComponentId(makeStringNullIfEmpty(componentAnnotation.componentId()))
        .withConfigurationPid(makeStringNullIfEmpty(componentAnnotation.configurationPid()))
        .withConfigurationPolicy(
            convertConfigurationPolicy(componentAnnotation.configurationPolicy()))
        .withDescription(makeStringNullIfEmpty(componentAnnotation.description()))
        .withIcons(convertIcons(componentAnnotation.icons()))
        .withMetatype(componentAnnotation.metatype())
        .withLabel(makeStringNullIfEmpty(componentAnnotation.label()))
        .withLocalizationBase(makeStringNullIfEmpty(componentAnnotation.localizationBase()))
        .withVersion(resolveVersion(componentAnnotation));

    processServiceAnnotation(componentMetaBuilder);
    processManualServicesAnnotation(componentMetaBuilder);

    superClazzes.push(originalClazz);
    Class<?> superclass = originalClazz.getSuperclass();
    while ((superclass != null)
        && !Object.class.getName().equals(superclass.getName())) {
      superClazzes.push(superclass);
      superclass = superclass.getSuperclass();
    }

    while ((processedClazz = nextClass()) != null) {
      MethodDescriptor activateMethod = findMethodWithAnnotation(Activate.class);
      MethodDescriptor deactivateMethod = findMethodWithAnnotation(Deactivate.class);
      MethodDescriptor updateMethod = findMethodWithAnnotation(Update.class);
      componentMetaBuilder = componentMetaBuilder
          .withActivate(getOldOrNewValue(componentMetaBuilder.getActivate(), activateMethod))
          .withDeactivate(getOldOrNewValue(componentMetaBuilder.getDeactivate(), deactivateMethod))
          .withUpdate(getOldOrNewValue(componentMetaBuilder.getUpdate(), updateMethod));

      generateMetaForAttributeHolders();
    }

    NavigableSet<AttributeMetadata<?>> attributes = new TreeSet<>(ATTRIBUTE_METADATA_COMPARATOR);
    attributes.addAll(this.attributes.values());
    componentMetaBuilder.withAttributes(attributes.toArray(
        new AttributeMetadata<?>[attributes.size()]));

    return componentMetaBuilder.build();
  }

  private <R> R callMethodOfAnnotation(final Annotation annotation, final String fieldName) {
    Class<? extends Annotation> annotationType = annotation.getClass();
    Method method;
    try {
      method = annotationType.getMethod(fieldName);

      @SuppressWarnings("unchecked")
      R result = (R) method.invoke(annotation);

      return result;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

  }

  private String[] convertClassArrayToClassNameArray(final Class<?>[] clazzes) {
    String[] result = new String[clazzes.length];
    for (int i = 0; i < clazzes.length; i++) {
      result[i] = clazzes[i].getName();
    }
    return result;
  }

  private ConfigurationPolicy convertConfigurationPolicy(
      final org.everit.osgi.ecm.annotation.ConfigurationPolicy configurationPolicy) {

    switch (configurationPolicy) {
      case IGNORE:
        return ConfigurationPolicy.IGNORE;
      case REQUIRE:
        return ConfigurationPolicy.REQUIRE;
      case FACTORY:
        return ConfigurationPolicy.FACTORY;
      default:
        return ConfigurationPolicy.OPTIONAL;
    }
  }

  private Icon[] convertIcons(final org.everit.osgi.ecm.annotation.Icon[] icons) {
    if (icons == null) {
      final Icon[] noIconDefined = null;
      return noIconDefined;
    }
    Icon[] result = new Icon[icons.length];
    for (int i = 0; i < icons.length; i++) {
      result[i] = new Icon(icons[i].path(), icons[i].size());
    }
    return result;
  }

  private ReferenceConfigurationType convertReferenceConfigurationType(
      final org.everit.osgi.ecm.annotation.ReferenceConfigurationType attributeType) {

    if (attributeType.equals(org.everit.osgi.ecm.annotation.ReferenceConfigurationType.CLAUSE)) {
      return ReferenceConfigurationType.CLAUSE;
    } else {
      return ReferenceConfigurationType.FILTER;
    }
  }

  private String deriveReferenceId(final Member member, final Annotation annotation) {
    String name = callMethodOfAnnotation(annotation, "referenceId");
    name = makeStringNullIfEmpty(name);
    if (name != null) {
      return name;
    }

    if (member != null) {
      String memberName = member.getName();
      if (member instanceof Field) {
        return memberName;
      } else if (member instanceof Method) {
        return resolveIdIfMethodNameStartsWith(memberName, "set");
      }
    }

    return null;
  }

  private Class<?> deriveServiceInterface(final Member member, final ServiceRef annotation) {
    Class<?> referenceInterface = annotation.referenceInterface();
    if (!AutoDetect.class.equals(referenceInterface)) {
      if (Void.class.equals(referenceInterface)) {
        return null;
      }
      return referenceInterface;
    }

    if (member != null) {
      if (member instanceof Field) {
        return resolveServiceInterfaceBasedOnGenericType(((Field) member).getGenericType());
      } else if (member instanceof Method) {
        Method method = ((Method) member);
        Class<?>[] parameterTypes = method.getParameterTypes();
        if ((parameterTypes.length != 1) || parameterTypes[0].isPrimitive()) {
          throw new InconsistentAnnotationException(
              "Reference auto detection can work only on a method that has one"
                  + " non-primitive parameter:" + method.toGenericString());
        }

        return resolveServiceInterfaceBasedOnGenericType(method.getGenericParameterTypes()[0]);
      }
    }

    return null;
  }

  private <V_ARRAY, B extends AttributeMetadataBuilder<V_ARRAY, B>> void fillAttributeMetaBuilder(
      final Member member,
      final Annotation annotation,
      final AttributeMetadataBuilder<V_ARRAY, B> builder) {

    Boolean dynamic = callMethodOfAnnotation(annotation, "dynamic");
    Boolean optional = callMethodOfAnnotation(annotation, "optional");
    boolean multiple = resolveMultiple(annotation, member);
    Boolean metatype = callMethodOfAnnotation(annotation, "metatype");
    String label = callMethodOfAnnotation(annotation, "label");
    String description = callMethodOfAnnotation(annotation, "description");

    Class<? extends Annotation> annotationType = annotation.annotationType();
    float priority = 0;
    if (annotationType.equals(ServiceRef.class)
        || annotationType.equals(BundleCapabilityRef.class)) {
      priority = callMethodOfAnnotation(annotation, "attributePriority");
    } else {
      priority = callMethodOfAnnotation(annotation, "priority");
    }

    Object defaultValueArray = callMethodOfAnnotation(annotation, "defaultValue");

    @SuppressWarnings("unchecked")
    V_ARRAY typedDefaultValueArray = (V_ARRAY) defaultValueArray;

    if (!multiple && (Array.getLength(typedDefaultValueArray) == 0)) {
      typedDefaultValueArray = null;
    }

    builder.withDynamic(dynamic)
        .withOptional(optional)
        .withMultiple(multiple)
        .withMetatype(metatype)
        .withLabel(makeStringNullIfEmpty(label))
        .withDescription(makeStringNullIfEmpty(description))
        .withPriority(priority)
        .withDefaultValue(typedDefaultValueArray);
  }

  private <V, B extends PropertyAttributeMetadataBuilder<V, B>> void fillPropertyAttributeBuilder(
      final Member member,
      final Annotation annotation,
      final PropertyAttributeMetadataBuilder<V, B> builder) {

    String attributeId = callMethodOfAnnotation(annotation, "attributeId");
    attributeId = makeStringNullIfEmpty(attributeId);

    if ((attributeId == null) && (member != null)) {
      String memberName = member.getName();
      if (member instanceof Field) {
        attributeId = memberName;
      } else if (member instanceof Method) {
        attributeId = resolveIdIfMethodNameStartsWith(memberName, "set");
      }
    }
    builder.withAttributeId(attributeId);

    fillAttributeMetaBuilder(member, annotation, builder);

    String setter = callMethodOfAnnotation(annotation, "setter");
    setter = makeStringNullIfEmpty(setter);

    if (setter != null) {
      builder.withSetter(new MethodDescriptor(setter));
    } else if (member != null) {
      if (member instanceof Method) {
        builder.withSetter(new MethodDescriptor((Method) member));
      } else if (member instanceof Field) {
        String fieldName = member.getName();
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault())
            + fieldName.substring(1);

        MethodDescriptor methodDescriptor = resolveSetter(builder, setterName);

        if (methodDescriptor != null) {
          builder.withSetter(methodDescriptor);
        }
      }
    }
  }

  private <B extends ReferenceMetadataBuilder<B>> void fillReferenceBuilder(final Member member,
      final Annotation annotation, final ReferenceMetadataBuilder<B> builder) {

    fillAttributeMetaBuilder(member, annotation, builder);

    org.everit.osgi.ecm.annotation.ReferenceConfigurationType configurationType =
        callMethodOfAnnotation(
            annotation, "configurationType");

    ReferenceConfigurationType convertedConfigurationType = convertReferenceConfigurationType(
        configurationType);

    String referenceId = deriveReferenceId(member, annotation);

    if (referenceId == null) {
      throw new MetadataValidationException(
          "Reference id for one of the references could not be determined in class "
              + processedClazz.getName());
    }

    String setterName = makeStringNullIfEmpty(
        (String) callMethodOfAnnotation(annotation, "setter"));

    if (setterName != null) {
      builder.withSetter(new MethodDescriptor(setterName));
    } else if (member instanceof Method) {
      builder.withSetter(new MethodDescriptor((Method) member));
    }

    builder
        .withReferenceId(referenceId)
        .withAttributeId(
            makeStringNullIfEmpty((String) callMethodOfAnnotation(annotation, "attributeId")))
        .withReferenceConfigurationType(convertedConfigurationType);
  }

  private <V_ARRAY, B extends SelectablePropertyAttributeMetadataBuilder<V_ARRAY, B>> void fillSelectablePropertyAttributeBuilder(// CS_DISABLE_LINE_LENGTH
      final Member member, final Annotation annotation,
      final SelectablePropertyAttributeMetadataBuilder<V_ARRAY, B> builder) {
    fillPropertyAttributeBuilder(member, annotation, builder);

    Object optionAnnotationArray = callMethodOfAnnotation(annotation, "options");
    int length = Array.getLength(optionAnnotationArray);
    if (length == 0) {
      builder.withOptions(null, null);
      return;
    }

    String[] labels = new String[length];

    @SuppressWarnings("unchecked")
    V_ARRAY values = (V_ARRAY) Array.newInstance(builder.getValueType(), length);

    for (int i = 0; i < length; i++) {
      Annotation optionAnnotation = (Annotation) Array.get(optionAnnotationArray, i);

      String label = callMethodOfAnnotation(optionAnnotation, "label");
      Object value = callMethodOfAnnotation(optionAnnotation, "value");

      label = makeStringNullIfEmpty(label);
      if (label == null) {
        label = value.toString();
      }

      labels[i] = label;
      Array.set(values, i, value);
    }
    builder.withOptions(labels, values);
  }

  private MethodDescriptor findMethodWithAnnotation(
      final Class<? extends Annotation> annotationClass) {
    Method foundMethod = null;
    Method[] methods = processedClazz.getDeclaredMethods();
    for (Method method : methods) {
      Annotation annotation = method.getAnnotation(annotationClass);
      if (annotation != null) {
        if (foundMethod != null) {
          throw new InconsistentAnnotationException("The '" + annotationClass.getName()
              + "' annotation is attached to more than one method in class '"
              + processedClazz.getName() + "'.");
        }

        foundMethod = method;

      }
    }

    if (foundMethod != null) {
      return new MethodDescriptor(foundMethod);
    } else {
      return null;
    }
  }

  private void generateAttributeMetaForAnnotatedElements(
      final AnnotatedElement[] annotatedElements) {

    for (AnnotatedElement annotatedElement : annotatedElements) {
      Annotation[] annotations = annotatedElement.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotatedElement instanceof Member) {
          processAttributeHolderAnnotation((Member) annotatedElement, annotation);
        } else {
          processAttributeHolderAnnotation(null, annotation);
        }
      }
    }
  }

  private void generateMetaForAttributeHolders() {
    generateAttributeMetaForAnnotatedElements(new AnnotatedElement[] { processedClazz });
    generateAttributeMetaForAnnotatedElements(processedClazz.getDeclaredFields());
    generateAttributeMetaForAnnotatedElements(processedClazz.getDeclaredMethods());
  }

  private <R> R getOldOrNewValue(final R oldValue, final R newValue) {
    return newValue != null ? newValue : oldValue;
  }

  private String makeStringNullIfEmpty(final String text) {
    if (text == null) {
      return null;
    }

    if ("".equals(text.trim())) {
      return null;
    }
    return text;
  }

  private Class<?> nextClass() {
    if (superClazzes.isEmpty()) {
      return null;
    }
    return superClazzes.pop();
  }

  private void processAnnotationContainer(final Annotation annotationContainer) {

    try {
      Method method = annotationContainer.annotationType().getMethod("value");
      Annotation[] annotations = (Annotation[]) method.invoke(annotationContainer);
      for (Annotation annotation : annotations) {
        processAttributeHolderAnnotation(null, annotation);
      }
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new RuntimeException("Error during processing class " + processedClazz.getName(), e);
    }

  }

  @Generated("avoid_checkstyle_alert_about_complexity")
  private void processAttributeHolderAnnotation(final Member element, final Annotation annotation) {
    Class<? extends Annotation> annotationType = annotation.annotationType();

    if (ANNOTATION_CONTAINER_TYPES.contains(annotationType)) {
      processAnnotationContainer(annotation);
    } else if (annotationType.equals(ServiceRef.class)) {
      processServiceReferenceAnnotation(element, (ServiceRef) annotation);
    } else if (annotationType.equals(BundleCapabilityRef.class)) {
      processBundleCapabilityReferenceAnnotation(element, (BundleCapabilityRef) annotation);
    } else if (annotationType.equals(BooleanAttribute.class)) {
      processBooleanAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(ByteAttribute.class)) {
      processByteAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(CharacterAttribute.class)) {
      processCharacterAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(DoubleAttribute.class)) {
      processDoubleAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(FloatAttribute.class)) {
      processFloatAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(IntegerAttribute.class)) {
      processIntegerAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(LongAttribute.class)) {
      processLongAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(PasswordAttribute.class)) {
      processPasswordAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(ShortAttribute.class)) {
      processShortAttributeAnnotation(element, annotation);
    } else if (annotationType.equals(StringAttribute.class)) {
      processStringAttributeAnnotation(element, annotation);
    }
  }

  private void processBooleanAttributeAnnotation(final Member element,
      final Annotation annotation) {
    BooleanAttributeMetadataBuilder builder = new BooleanAttributeMetadataBuilder();
    fillPropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processBundleCapabilityReferenceAnnotation(final Member member,
      final BundleCapabilityRef annotation) {
    BundleCapabilityReferenceMetadataBuilder builder =
        new BundleCapabilityReferenceMetadataBuilder();

    fillReferenceBuilder(member, annotation, builder);
    builder.withNamespace(annotation.namespace()).withStateMask(annotation.stateMask());
    putIntoOrUpdateAttributes(builder);
  }

  private void processByteAttributeAnnotation(final Member element, final Annotation annotation) {
    ByteAttributeMetadataBuilder builder = new ByteAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processCharacterAttributeAnnotation(final Member element,
      final Annotation annotation) {
    CharacterAttributeMetadataBuilder builder = new CharacterAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processDoubleAttributeAnnotation(final Member element, final Annotation annotation) {
    DoubleAttributeMetadataBuilder builder = new DoubleAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processFloatAttributeAnnotation(final Member element, final Annotation annotation) {
    FloatAttributeMetadataBuilder builder = new FloatAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processIntegerAttributeAnnotation(final Member element,
      final Annotation annotation) {
    IntegerAttributeMetadataBuilder builder = new IntegerAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processLongAttributeAnnotation(final Member element, final Annotation annotation) {
    LongAttributeMetadataBuilder builder = new LongAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processManualServicesAnnotation(
      final ComponentMetadataBuilder componentMetaBuilder) {
    ManualServices annotation = originalClazz.getAnnotation(ManualServices.class);
    if (annotation != null) {
      ManualService[] value = annotation.value();
      ServiceMetadata[] serviceMetadataArray = new ServiceMetadata[value.length];
      for (int i = 0; i < value.length; i++) {
        ManualService manualService = value[i];
        serviceMetadataArray[i] = new ServiceMetadataBuilder()
            .withClazzes(convertClassArrayToClassNameArray(manualService.value())).build();
      }
      componentMetaBuilder.withManualServices(serviceMetadataArray);
    }
  }

  private void processPasswordAttributeAnnotation(final Member element,
      final Annotation annotation) {
    PasswordAttributeMetadataBuilder builder = new PasswordAttributeMetadataBuilder();
    fillPropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processServiceAnnotation(final ComponentMetadataBuilder componentMetaBuilder) {
    Service serviceAnnotation = originalClazz.getAnnotation(Service.class);
    if (serviceAnnotation != null) {
      ServiceMetadataBuilder serviceMetadataBuilder = new ServiceMetadataBuilder();
      Class<?>[] serviceInterfaces = serviceAnnotation.value();
      serviceMetadataBuilder.withClazzes(convertClassArrayToClassNameArray(serviceInterfaces));
      componentMetaBuilder.withService(serviceMetadataBuilder.build());
    }
  }

  private void processServiceReferenceAnnotation(final Member member, final ServiceRef annotation) {
    ServiceReferenceMetadataBuilder builder = new ServiceReferenceMetadataBuilder();
    fillReferenceBuilder(member, annotation, builder);
    builder.withServiceInterface(getClassNameOrNull(deriveServiceInterface(member, annotation)));
    putIntoOrUpdateAttributes(builder);
  }

  private void processShortAttributeAnnotation(final Member element, final Annotation annotation) {
    ShortAttributeMetadataBuilder builder = new ShortAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void processStringAttributeAnnotation(final Member element, final Annotation annotation) {
    StringAttributeMetadataBuilder builder = new StringAttributeMetadataBuilder();
    fillSelectablePropertyAttributeBuilder(element, annotation, builder);
    putIntoOrUpdateAttributes(builder);
  }

  private void putIntoOrUpdateAttributes(final AttributeMetadataBuilder<?, ?> builder) {
    AttributeMetadata<?> attributeMetadata = builder.build();
    String attributeId = attributeMetadata.getAttributeId();

    AttributeMetadata<?> oldAttributeMetadata = attributes.get(attributeId);
    if (oldAttributeMetadata != null) {
      if (attributeClasses.get(attributeId).equals(processedClazz)) {
        throw new MetadataValidationException("Duplicate attribute id '"
            + attributeMetadata.getAttributeId()
            + "' found in class '" + processedClazz.getName() + "'.");
      }

      if (!oldAttributeMetadata.getClass().equals(attributeMetadata.getClass())) {
        throw new MetadataValidationException("Overrided attribute id '"
            + attributeMetadata.getAttributeId()
            + "' attribute type is wrong. Parent attribute type '"
            + oldAttributeMetadata.getClass() + "', child attribute type '"
            + attributeMetadata.getClass() + "'.");
      }
    }

    attributes.put(attributeId, attributeMetadata);
    attributeClasses.put(attributeId, processedClazz);
  }

  private String resolveIdIfMethodNameStartsWith(final String memberName, final String prefix) {
    int prefixLength = prefix.length();
    if (memberName.startsWith(prefix) && (memberName.length() > prefixLength)) {
      return memberName.substring(prefixLength, prefixLength + 1).toLowerCase(Locale.getDefault())
          + memberName.substring(prefixLength + 1);
    } else {
      return null;
    }
  }

  private boolean resolveMultiple(final Annotation annotation, final Member member) {
    ThreeStateBoolean multiple = callMethodOfAnnotation(annotation, "multiple");
    if (multiple == ThreeStateBoolean.TRUE) {
      return true;
    }
    if (multiple == ThreeStateBoolean.FALSE) {
      return false;
    }

    if (member == null) {
      return false;
    }

    if (member instanceof Method) {
      Class<?>[] parameterTypes = ((Method) member).getParameterTypes();
      if (parameterTypes.length == 0) {
        throw new InconsistentAnnotationException(
            "Could not determine the multiplicity of attribute based on annotation '"
                + annotation.toString() + "' that is defined on the method '" + member.toString()
                + "' in the class " + processedClazz.getName());
      }
      return parameterTypes[0].isArray();
    } else if (member instanceof Field) {
      Class<?> fieldType = ((Field) member).getType();
      return fieldType.isArray();
    }
    throw new InconsistentAnnotationException(
        "Could not determine the multiplicity of attribute based on annotation '"
            + annotation.toString() + "' in the class " + originalClazz.getName());
  }

  private Class<?> resolveServiceInterfaceBasedOnClassType(final Class<?> classType) {
    if (classType.equals(ServiceHolder.class)) {
      return null;
    } else {
      return classType;
    }
  }

  private Class<?> resolveServiceInterfaceBasedOnGenericType(final Type genericType) {
    if (genericType instanceof Class) {
      Class<?> classType = (Class<?>) genericType;
      if (!classType.isArray()) {
        return classType;
      }
      Class<?> componentType = classType.getComponentType();
      return resolveServiceInterfaceBasedOnClassType(componentType);
    }

    if (genericType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType) genericType;
      Type genericComponentType = genericArrayType.getGenericComponentType();
      if (genericComponentType instanceof Class) {
        return resolveServiceInterfaceBasedOnClassType((Class<?>) genericComponentType);
      }

      if (genericComponentType instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) genericComponentType;
        return resolveServiceInterfaceBasedOnParameterizedType(parameterizedType);
      }
    }

    if (genericType instanceof ParameterizedType) {
      return resolveServiceInterfaceBasedOnParameterizedType((ParameterizedType) genericType);
    }

    throw new MetadataValidationException(
        "Could not determine the OSGi service interface based on type "
            + genericType + " in class " + processedClazz.getName());

  }

  private Class<?> resolveServiceInterfaceBasedOnParameterizedType(
      final ParameterizedType parameterizedType) {
    Type rawType = parameterizedType.getRawType();
    if (rawType instanceof Class) {
      Class<?> classType = (Class<?>) rawType;
      if (!classType.equals(ServiceHolder.class)) {
        return classType;
      }

      Type serviceInterfaceType = parameterizedType.getActualTypeArguments()[0];
      if (serviceInterfaceType instanceof WildcardType) {
        return null;
      }

      if (serviceInterfaceType instanceof Class) {
        return (Class<?>) serviceInterfaceType;
      }

      if (serviceInterfaceType instanceof ParameterizedType) {
        Type raw = ((ParameterizedType) serviceInterfaceType).getRawType();
        if (raw instanceof Class) {
          return (Class<?>) raw;
        }
      }

    }
    throw new MetadataValidationException(
        "Could not determine the OSGi service interface based on type "
            + parameterizedType + " in class " + originalClazz.getName());
  }

  private <V, B extends PropertyAttributeMetadataBuilder<V, B>> MethodDescriptor resolveSetter(
      final PropertyAttributeMetadataBuilder<V, B> builder, final String setterName) {

    List<MethodDescriptor> potentialDescriptors = new ArrayList<MethodDescriptor>();
    Class<?> attributeType = builder.getValueType();
    boolean multiple = builder.isMultiple();

    if (multiple) {
      String parameterTypeName = attributeType.getCanonicalName() + "[]";
      potentialDescriptors
          .add(new MethodDescriptor(setterName, new String[] { parameterTypeName }));
    } else {
      if (attributeType.isPrimitive()) {
        Class<?> boxingType = PRIMITIVE_BOXING_TYPE_MAPPING.get(attributeType);
        potentialDescriptors.add(new MethodDescriptor(setterName,
            new String[] { boxingType.getCanonicalName() }));
      }
      potentialDescriptors
          .add(new MethodDescriptor(setterName, new String[] { attributeType.getCanonicalName() }));

    }

    Method method = MethodUtil.locateMethodByPreference(processedClazz, false,
        potentialDescriptors.toArray(new MethodDescriptor[potentialDescriptors.size()]));

    if (method == null) {
      return null;
    }
    return new MethodDescriptor(method);
  }

  private Version resolveVersion(final Component componentAnnotation) {
    String versionString = componentAnnotation.version();
    if ("".equals(versionString)) {
      return null;
    }
    return new Version(versionString);
  }
}
