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
package org.everit.osgi.ecm.annotation.metadatabuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.AutoDetect;
import org.everit.osgi.ecm.annotation.BundleCapabilityReference;
import org.everit.osgi.ecm.annotation.BundleCapabilityReferences;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ServiceReference;
import org.everit.osgi.ecm.annotation.ServiceReferences;
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
import org.everit.osgi.ecm.metadata.ServiceReferenceMetadata.ServiceReferenceMetadataBuilder;
import org.everit.osgi.ecm.metadata.ShortAttributeMetadata.ShortAttributeMetadataBuilder;
import org.everit.osgi.ecm.metadata.StringAttributeMetadata.StringAttributeMetadataBuilder;
import org.everit.osgi.ecm.util.method.MethodDescriptor;
import org.everit.osgi.ecm.util.method.MethodUtil;

public class MetadataBuilder<C> {

    private static final Set<Class<?>> ANNOTATION_CONTAINER_TYPES;

    static {
        ANNOTATION_CONTAINER_TYPES = new HashSet<Class<?>>();
        ANNOTATION_CONTAINER_TYPES.add(ServiceReferences.class);
        ANNOTATION_CONTAINER_TYPES.add(BundleCapabilityReferences.class);
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
    }

    public static <C> ComponentMetadata buildComponentMetadata(Class<C> clazz) {
        MetadataBuilder<C> metadataBuilder = new MetadataBuilder<C>(clazz);
        return metadataBuilder.build();
    }

    private static <O> O[] convertPrimitiveArray(Object primitiveArray, Class<O> targetType) {
        int length = Array.getLength(primitiveArray);

        if (length == 0) {
            return null;
        }

        @SuppressWarnings("unchecked")
        O[] result = (O[]) Array.newInstance(targetType, length);

        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            O element = (O) Array.get(primitiveArray, i);
            result[i] = element;
        }

        return result;
    }

    private LinkedHashMap<String, AttributeMetadata<?>> attributes =
            new LinkedHashMap<String, AttributeMetadata<?>>();

    private Class<C> clazz;

    private MetadataBuilder() {
    }

    private MetadataBuilder(Class<C> clazz) {
        this.clazz = clazz;
    }

    private ComponentMetadata build() {
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new ComponentAnnotationMissingException("Component annotation is missing on type " + clazz.toString());
        }

        ComponentMetadataBuilder componentMetaBuilder = new ComponentMetadataBuilder()
                .withComponentId(makeStringNullIfEmpty(componentAnnotation.componentId()))
                .withConfigurationPid(makeStringNullIfEmpty(componentAnnotation.configurationPid()))
                .withConfigurationPolicy(convertConfigurationPolicy(componentAnnotation.configurationPolicy()))
                .withDescription(makeStringNullIfEmpty(componentAnnotation.description()))
                .withIcons(convertIcons(componentAnnotation.icons()))
                .withMetatype(componentAnnotation.metatype())
                .withLabel(makeStringNullIfEmpty(componentAnnotation.label()))
                .withLocalizationBase(makeStringNullIfEmpty(componentAnnotation.localizationBase()))
                .withType(clazz.getName())
                .withActivate(findMethodWithAnnotation(Activate.class))
                .withDeactivate(findMethodWithAnnotation(Deactivate.class))
                .withUpdate(findMethodWithAnnotation(Update.class));

        generateMetaForAttributeHolders();

        orderedAttributes();
        componentMetaBuilder.withAttributes(attributes.values().toArray(
                new AttributeMetadata<?>[0]));

        return componentMetaBuilder.build();
    }

    private <R> R callMethodOfAnnotation(Annotation annotation, String fieldName) {
        Class<? extends Annotation> annotationType = annotation.getClass();
        Method method;
        try {
            method = annotationType.getMethod(fieldName);

            @SuppressWarnings("unchecked")
            R result = (R) method.invoke(annotation);

            return result;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private ConfigurationPolicy convertConfigurationPolicy(
            org.everit.osgi.ecm.annotation.ConfigurationPolicy configurationPolicy) {

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

    private Icon[] convertIcons(org.everit.osgi.ecm.annotation.Icon[] icons) {
        if (icons == null) {
            return null;
        }
        Icon[] result = new Icon[icons.length];
        for (int i = 0; i < icons.length; i++) {
            result[i] = new Icon(icons[i].path(), icons[i].size());
        }
        return result;
    }

    private ReferenceConfigurationType convertReferenceConfigurationType(
            org.everit.osgi.ecm.annotation.ReferenceConfigurationType attributeType) {

        if (attributeType.equals(org.everit.osgi.ecm.annotation.ReferenceConfigurationType.CLAUSE)) {
            return ReferenceConfigurationType.CLAUSE;
        } else {
            return ReferenceConfigurationType.FILTER;
        }
    }

    private String deriveReferenceId(Member member, Annotation annotation) {
        String name = callMethodOfAnnotation(annotation, "referenceId");
        name = makeStringNullIfEmpty(name);
        if (name != null) {
            return name;
        }

        if (member != null && member instanceof Field) {
            return member.getName();
        }

        return null;
    }

    private Class<?> deriveServiceInterface(Member member, ServiceReference annotation) {
        Class<?> referenceInterface = annotation.referenceInterface();
        if (!AutoDetect.class.equals(referenceInterface)) {
            return referenceInterface;
        }

        if (member != null && member instanceof Field) {
            return ((Field) member).getType();
        }

        return null;
    }

    private <V, B extends AttributeMetadataBuilder<V, B>> void fillAttributeMetaBuilder(
            Member member,
            Annotation annotation,
            AttributeMetadataBuilder<V, B> builder) {

        Boolean dynamic = callMethodOfAnnotation(annotation, "dynamic");
        Boolean optional = callMethodOfAnnotation(annotation, "optional");
        Boolean multiple = callMethodOfAnnotation(annotation, "multiple");
        Boolean metatype = callMethodOfAnnotation(annotation, "metatype");
        String label = callMethodOfAnnotation(annotation, "label");
        String description = callMethodOfAnnotation(annotation, "description");

        Object defaultValueArray = callMethodOfAnnotation(annotation, "defaultValue");
        V[] convertedDefaultValues = convertPrimitiveArray(defaultValueArray, builder.getValueType());

        builder.withDynamic(dynamic)
                .withOptional(optional)
                .withMultiple(multiple)
                .withMetatype(metatype)
                .withLabel(makeStringNullIfEmpty(label))
                .withDescription(makeStringNullIfEmpty(description))
                .withDefaultValue(convertedDefaultValues);
    }

    private <V, B extends PropertyAttributeMetadataBuilder<V, B>> void fillPropertyAttributeBuilder(
            Member member,
            Annotation annotation,
            PropertyAttributeMetadataBuilder<V, B> builder) {

        String attributeId = callMethodOfAnnotation(annotation, "attributeId");
        attributeId = makeStringNullIfEmpty(attributeId);

        if (attributeId == null && member != null) {
            String memberName = member.getName();
            if (member instanceof Field) {
                attributeId = memberName;
            } else if (member instanceof Method && memberName.startsWith("set") && memberName.length() > 3) {
                attributeId = memberName.substring(3, 4).toLowerCase() + memberName.substring(4);
            }
        }

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
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                MethodDescriptor methodDescriptor = resolveSetter(annotation, builder, setterName);

                if (methodDescriptor != null) {
                    builder.withSetter(methodDescriptor);
                }
            }
        }
    }

    private <B extends ReferenceMetadataBuilder<B>> void fillReferenceBuilder(Member member,
            Annotation annotation, ReferenceMetadataBuilder<B> builder) {

        fillAttributeMetaBuilder(member, annotation, builder);

        org.everit.osgi.ecm.annotation.ReferenceConfigurationType configurationType = callMethodOfAnnotation(
                annotation, "configurationType");

        String bindName = makeStringNullIfEmpty((String) callMethodOfAnnotation(annotation, "bind"));

        if (bindName != null) {
            builder.withBind(new MethodDescriptor(bindName));
        } else if (member instanceof Method) {
            builder.withBind(new MethodDescriptor((Method) member));
        }

        String unbindName = makeStringNullIfEmpty((String) callMethodOfAnnotation(annotation, "unbind"));
        if (unbindName != null) {
            builder.withUnbind(new MethodDescriptor(unbindName));
        }

        builder.withReferenceId(deriveReferenceId(member, annotation))
                .withAttributeId(makeStringNullIfEmpty((String) callMethodOfAnnotation(annotation, "attributeId")))
                .withReferenceConfigurationType(convertReferenceConfigurationType(configurationType));
    }

    private <V, B extends SelectablePropertyAttributeMetadataBuilder<V, B>> void fillSelectablePropertyAttributeBuilder(
            Member member, Annotation annotation,
            SelectablePropertyAttributeMetadataBuilder<V, B> builder) {
        fillPropertyAttributeBuilder(member, annotation, builder);

        Object optionAnnotationArray = callMethodOfAnnotation(annotation, "options");
        int length = Array.getLength(optionAnnotationArray);
        if (length == 0) {
            builder.withOptions(null);
            return;
        }

        Map<V, String> options = new LinkedHashMap<V, String>();
        for (int i = 0; i < length; i++) {
            Annotation optionAnnotation = (Annotation) Array.get(optionAnnotationArray, i);

            String label = callMethodOfAnnotation(optionAnnotation, "label");
            V value = callMethodOfAnnotation(optionAnnotation, "value");

            label = makeStringNullIfEmpty(label);
            if (label == null) {
                label = value.toString();
            }

            options.put(value, label);
        }
        builder.withOptions(options);
    }

    private MethodDescriptor findMethodWithAnnotation(Class<? extends Annotation> annotationClass) {
        Method foundMethod = null;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                if (foundMethod != null) {
                    throw new InconsistentAnnotationException("The '" + annotationClass.getName()
                            + "' annotation is attached to more than one method in class '" + clazz.getName() + "'.");
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

    private void generateAttributeMetaForAnnotatedElements(AnnotatedElement[] annotatedElements) {
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
        generateAttributeMetaForAnnotatedElements(new AnnotatedElement[] { clazz });
        generateAttributeMetaForAnnotatedElements(clazz.getDeclaredFields());
        generateAttributeMetaForAnnotatedElements(clazz.getDeclaredMethods());
    }

    private String makeStringNullIfEmpty(String text) {
        if (text == null) {
            return null;
        }

        if (text.trim().equals("")) {
            return null;
        }
        return text;
    }

    private void orderedAttributes() {
        AttributeOrder attributeOrder = clazz.getAnnotation(AttributeOrder.class);
        if (attributeOrder == null) {
            return;
        }

        String[] orderArray = attributeOrder.value();
        if (orderArray.length == 0) {
            return;
        }

        LinkedHashMap<String, AttributeMetadata<?>> orderedAttributes =
                new LinkedHashMap<String, AttributeMetadata<?>>();
        for (String attributeId : orderArray) {
            AttributeMetadata<?> attributeMetadata = attributes.remove(attributeId);
            if (attributeMetadata == null) {
                throw new InconsistentAnnotationException("Attribute with id '" + attributeId
                        + "' that is defined in AttributeOrder on the class '" + clazz.getName() + "' does not exist.");
            }
            orderedAttributes.put(attributeId, attributeMetadata);
        }

        Set<Entry<String, AttributeMetadata<?>>> attributeEntries = attributes.entrySet();
        for (Entry<String, AttributeMetadata<?>> attributeEntry : attributeEntries) {
            orderedAttributes.put(attributeEntry.getKey(), attributeEntry.getValue());
        }

        this.attributes = orderedAttributes;
    }

    private void processAnnotationContainer(Annotation annotationContainer) {

        try {
            Method method = annotationContainer.annotationType().getMethod("value");
            Annotation[] annotations = (Annotation[]) method.invoke(annotationContainer);
            for (Annotation annotation : annotations) {
                processAttributeHolderAnnotation(null, annotation);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private void processAttributeHolderAnnotation(Member element, Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();

        if (ANNOTATION_CONTAINER_TYPES.contains(annotationType)) {
            processAnnotationContainer(annotation);
        } else if (annotationType.equals(ServiceReference.class)) {
            processServiceReferenceAnnotation(element, (ServiceReference) annotation);
        } else if (annotationType.equals(BundleCapabilityReference.class)) {
            processBundleCapabilityReferenceAnnotation(element, (BundleCapabilityReference) annotation);
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

    private void processBooleanAttributeAnnotation(Member element, Annotation annotation) {
        BooleanAttributeMetadataBuilder builder = new BooleanAttributeMetadataBuilder();
        fillPropertyAttributeBuilder(element, annotation, builder);

        putIntoAttributes(builder);
    }

    private void processBundleCapabilityReferenceAnnotation(Member member, BundleCapabilityReference annotation) {
        BundleCapabilityReferenceMetadataBuilder builder = new BundleCapabilityReferenceMetadataBuilder();
        fillReferenceBuilder(member, annotation, builder);
        builder.withNamespace(annotation.namespace());
        putIntoAttributes(builder);
    }

    private void processByteAttributeAnnotation(Member element, Annotation annotation) {
        ByteAttributeMetadataBuilder builder = new ByteAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processCharacterAttributeAnnotation(Member element, Annotation annotation) {
        CharacterAttributeMetadataBuilder builder = new CharacterAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processDoubleAttributeAnnotation(Member element, Annotation annotation) {
        DoubleAttributeMetadataBuilder builder = new DoubleAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processFloatAttributeAnnotation(Member element, Annotation annotation) {
        FloatAttributeMetadataBuilder builder = new FloatAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processIntegerAttributeAnnotation(Member element, Annotation annotation) {
        IntegerAttributeMetadataBuilder builder = new IntegerAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processLongAttributeAnnotation(Member element, Annotation annotation) {
        LongAttributeMetadataBuilder builder = new LongAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processPasswordAttributeAnnotation(Member element, Annotation annotation) {
        PasswordAttributeMetadataBuilder builder = new PasswordAttributeMetadataBuilder();
        fillPropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processServiceReferenceAnnotation(Member member, ServiceReference annotation) {
        ServiceReferenceMetadataBuilder builder = new ServiceReferenceMetadataBuilder();
        fillReferenceBuilder(member, annotation, builder);
        builder.withServiceInterface(deriveServiceInterface(member, annotation));
        putIntoAttributes(builder);
    }

    private void processShortAttributeAnnotation(Member element, Annotation annotation) {
        ShortAttributeMetadataBuilder builder = new ShortAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        putIntoAttributes(builder);
    }

    private void processStringAttributeAnnotation(Member element, Annotation annotation) {
        StringAttributeMetadataBuilder builder = new StringAttributeMetadataBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        Boolean multiLine = callMethodOfAnnotation(annotation, "multiLine");
        builder.withMultiLine(multiLine);
        putIntoAttributes(builder);
    }

    private void putIntoAttributes(AttributeMetadataBuilder<?, ?> builder) {
        AttributeMetadata<?> attributeMetadata = builder.build();
        AttributeMetadata<?> existing = attributes.put(attributeMetadata.getAttributeId(), builder.build());
        if (existing != null) {
            throw new MetadataValidationException("Duplicate attribute id '" + attributeMetadata.getAttributeId()
                    + "' found in class '" + clazz.getName() + "'.");
        }
    }

    private <V, B extends PropertyAttributeMetadataBuilder<V, B>> MethodDescriptor resolveSetter(
            Annotation annotation, PropertyAttributeMetadataBuilder<V, B> builder, String setterName) {

        List<MethodDescriptor> potentialDescriptors = new ArrayList<MethodDescriptor>();
        Class<?> primitiveType = builder.getPrimitiveType();
        Boolean multiple = callMethodOfAnnotation(annotation, "multiple");

        if (multiple) {
            Class<?> multipleType = primitiveType;
            if (primitiveType == null) {
                multipleType = builder.getValueType();
            }
            String parameterTypeName = multipleType.getCanonicalName() + "[]";
            potentialDescriptors.add(new MethodDescriptor(setterName, new String[] { parameterTypeName }));
        } else {
            if (primitiveType != null) {
                potentialDescriptors.add(new MethodDescriptor(setterName,
                        new String[] { primitiveType.getSimpleName() }));
            }
            potentialDescriptors.add(new MethodDescriptor(setterName, new String[] { builder.getValueType()
                    .getCanonicalName() }));

        }

        Method method = MethodUtil.locateMethodByPreference(clazz, false,
                potentialDescriptors.toArray(new MethodDescriptor[potentialDescriptors.size()]));

        if (method == null) {
            return null;
        }
        return new MethodDescriptor(method);
    }
}
