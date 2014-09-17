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
package org.everit.osgi.ecm.annotation.metabuilder;

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
import java.util.Set;

import org.everit.osgi.ecm.annotation.AutoDetect;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.Reference;
import org.everit.osgi.ecm.annotation.References;
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
import org.everit.osgi.ecm.annotation.attribute.ReferenceAttribute;
import org.everit.osgi.ecm.annotation.attribute.ShortAttribute;
import org.everit.osgi.ecm.annotation.attribute.ShortAttributes;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.meta.AttributeMeta.AttributeMetaBuilder;
import org.everit.osgi.ecm.meta.AttributeMetaHolder;
import org.everit.osgi.ecm.meta.BooleanAttributeMeta.BooleanAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ByteAttributeMeta.ByteAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.CharacterAttributeMeta.CharacterAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ComponentMeta;
import org.everit.osgi.ecm.meta.ComponentMeta.ComponentMetaBuilder;
import org.everit.osgi.ecm.meta.DoubleAttributeMeta.DoubleAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.FloatAttributeMeta.FloatAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.IntegerAttributeMeta.IntegerAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.LongAttributeMeta.LongAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.PasswordAttributeMeta.PasswordAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.PropertyAttributeMeta.PropertyAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta;
import org.everit.osgi.ecm.meta.ReferenceAttributeMeta.ReferenceAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ReferenceCardinality;
import org.everit.osgi.ecm.meta.ReferenceConfigurationType;
import org.everit.osgi.ecm.meta.ReferenceMeta;
import org.everit.osgi.ecm.meta.ReferenceMeta.ReferenceMetaBuilder;
import org.everit.osgi.ecm.meta.SelectablePropertyAttributeMeta.SelectablePropertyAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.ShortAttributeMeta.ShortAttributeMetaBuilder;
import org.everit.osgi.ecm.meta.StringAttributeMeta.StringAttributeMetaBuilder;
import org.osgi.framework.BundleContext;

public class MetaBuilder<C> {

    private static final Set<Class<?>> ANNOTATION_CONTAINER_TYPES;

    static {
        ANNOTATION_CONTAINER_TYPES = new HashSet<Class<?>>();
        ANNOTATION_CONTAINER_TYPES.add(References.class);
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

    public static <C> ComponentMeta<C> buildComponentMeta(BundleContext bundleContext, Class<C> clazz) {
        MetaBuilder<C> metaBuilder = new MetaBuilder<C>(clazz, bundleContext);
        return metaBuilder.build();
    }

    private static <O> O[] convertPrimitiveArray(Object primitiveArray, Class<O> targetType) {
        int length = Array.getLength(primitiveArray);

        @SuppressWarnings("unchecked")
        O[] result = (O[]) Array.newInstance(targetType, length);

        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            O element = (O) Array.get(primitiveArray, i);
            result[i] = element;
        }

        return result;
    }

    private final List<AttributeMetaHolder<?>> attributes = new ArrayList<AttributeMetaHolder<?>>();

    private BundleContext bundleContext;

    private Class<C> clazz;

    private MetaBuilder() {
    }

    private MetaBuilder(Class<C> clazz, BundleContext bundleContext) {
        this.clazz = clazz;
        this.bundleContext = bundleContext;
    }

    private ComponentMeta<C> build() {
        Component componentAnnotation = clazz.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new ComponentAnnotationMissingException("Component annotation is missing on type " + clazz.toString());
        }

        ComponentMetaBuilder<C> componentMetaBuilder = new ComponentMetaBuilder<C>()
                .withBundleContext(bundleContext)
                .withConfigurationFactory(componentAnnotation.configurationFactory())
                .withName(makeStringNullIfEmpty(componentAnnotation.name()))
                .withConfigurationPid(makeStringNullIfEmpty(componentAnnotation.configurationPid()))
                .withConfigurationRequired(componentAnnotation.configurationRequired())
                .withDescription(makeStringNullIfEmpty(componentAnnotation.description()))
                .withIcon(makeStringNullIfEmpty(componentAnnotation.icon()))
                .withLabel(makeStringNullIfEmpty(componentAnnotation.label()))
                .withLocalization(makeStringNullIfEmpty(componentAnnotation.localization()))
                .withType(clazz);

        generateMetaForAttributeHolders();

        componentMetaBuilder.withAttributeHolders(attributes.toArray(new AttributeMetaHolder<?>[0]));

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

    private ReferenceCardinality convertAnnotationReferenceCardinality(
            org.everit.osgi.ecm.annotation.ReferenceCardinality annotationCardinality) {

        switch (annotationCardinality) {
        case AT_LEAST_ONE:
            return ReferenceCardinality.AT_LEAST_ONE;
        case OPTIONAL:
            return ReferenceCardinality.OPTIONAL;
        case MULTIPLE:
            return ReferenceCardinality.MULTIPLE;
        case MANDATORY:
            return ReferenceCardinality.MANDATORY;
        }
        return null;
    }

    private ReferenceConfigurationType convertReferenceAttributeType(
            org.everit.osgi.ecm.annotation.attribute.ReferenceConfigurationType attributeType) {

        if (attributeType.equals(org.everit.osgi.ecm.annotation.attribute.ReferenceConfigurationType.CLAUSE)) {
            return ReferenceConfigurationType.CLAUSE;
        } else {
            return ReferenceConfigurationType.FILTER;
        }
    }

    private String deriveName(Member member, Annotation annotation) {
        String name = callMethodOfAnnotation(annotation, "name");
        name = makeStringNullIfEmpty(name);
        if (name != null) {
            return name;
        }

        if (member != null && member instanceof Field) {
            return member.getName();
        }

        return null;
    }

    private Class<?> deriveReferenceInterface(Member member, Reference annotation) {
        Class<?> referenceInterface = annotation.referenceInterface();
        if (!AutoDetect.class.equals(referenceInterface)) {
            return referenceInterface;
        }

        if (member != null && member instanceof Field) {
            return ((Field) member).getType();
        }

        return null;
    }

    private <V, B extends AttributeMetaBuilder<V, B>> void fillAttributeMetaBuilder(
            Member member,
            Annotation annotation,
            AttributeMetaBuilder<V, B> builder) {

        String name = callMethodOfAnnotation(annotation, "name");
        name = makeStringNullIfEmpty(name);
        String memberName = member.getName();
        if (name == null && member != null) {
            if (member instanceof Field) {
                name = memberName;
            } else if (member instanceof Method && memberName.startsWith("set") && memberName.length() > 3) {
                name = memberName.substring(3, 4).toLowerCase() + memberName.substring(4);
            }
        }

        Boolean metatype = callMethodOfAnnotation(annotation, "metatype");
        String label = callMethodOfAnnotation(annotation, "label");
        String description = callMethodOfAnnotation(annotation, "description");

        Object defaultValueArray = callMethodOfAnnotation(annotation, "defaultValue");
        V[] convertedDefaultValues = convertPrimitiveArray(defaultValueArray, builder.getValueType());

        builder.withName(name)
                .withMetatype(metatype)
                .withLabel(makeStringNullIfEmpty(label))
                .withDescription(makeStringNullIfEmpty(description))
                .withDefaultValue(convertedDefaultValues);
    }

    private <V, B extends PropertyAttributeMetaBuilder<V, B>> void fillPropertyAttributeBuilder(
            Member member,
            Annotation annotation,
            PropertyAttributeMetaBuilder<V, B> builder) {

        String setter = callMethodOfAnnotation(annotation, "setter");
        setter = makeStringNullIfEmpty(setter);
        if (setter == null && member != null) {
            if (member instanceof Method) {
                setter = member.getName();
            } else if (member instanceof Field) {
                String fieldName = member.getName();
                try {
                    Method method = clazz.getMethod("set" + fieldName, ((Field) member).getType());
                    setter = method.getName();
                } catch (NoSuchMethodException | SecurityException e) {
                    // Do nothing as in this case there will be no setter
                }
            }
        }

        builder.withSetter(setter);

        Boolean dynamic = callMethodOfAnnotation(annotation, "dynamic");
        builder.withDynamic(dynamic);

        Integer cardinality = callMethodOfAnnotation(annotation, "cardinality");
        builder.withCardinality(cardinality);

        fillAttributeMetaBuilder(member, annotation, builder);

    }

    private <V, B extends SelectablePropertyAttributeMetaBuilder<V, B>> void fillSelectablePropertyAttributeBuilder(
            Member member, Annotation annotation,
            SelectablePropertyAttributeMetaBuilder<V, B> builder) {
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
        } else if (annotationType.equals(Reference.class)) {
            processReferenceAnnotation(element, (Reference) annotation);
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
        BooleanAttributeMetaBuilder builder = new BooleanAttributeMetaBuilder();
        fillPropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processByteAttributeAnnotation(Member element, Annotation annotation) {
        ByteAttributeMetaBuilder builder = new ByteAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processCharacterAttributeAnnotation(Member element, Annotation annotation) {
        CharacterAttributeMetaBuilder builder = new CharacterAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processDoubleAttributeAnnotation(Member element, Annotation annotation) {
        DoubleAttributeMetaBuilder builder = new DoubleAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processFloatAttributeAnnotation(Member element, Annotation annotation) {
        FloatAttributeMetaBuilder builder = new FloatAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processIntegerAttributeAnnotation(Member element, Annotation annotation) {
        IntegerAttributeMetaBuilder builder = new IntegerAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processLongAttributeAnnotation(Member element, Annotation annotation) {
        LongAttributeMetaBuilder builder = new LongAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processPasswordAttributeAnnotation(Member element, Annotation annotation) {
        PasswordAttributeMetaBuilder builder = new PasswordAttributeMetaBuilder();
        fillPropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processReferenceAnnotation(Member member, Reference annotation) {
        ReferenceAttribute attribute = annotation.attribute();
        ReferenceAttributeMeta referenceAttribute = new ReferenceAttributeMetaBuilder()
                .withDefaultValue(attribute.defaultValue())
                .withDescription(makeStringNullIfEmpty(attribute.description()))
                .withLabel(makeStringNullIfEmpty(attribute.label()))
                .withMetatype(attribute.metatype())
                .withName(makeStringNullIfEmpty(attribute.name()))
                .withReferenceConfigurationType(convertReferenceAttributeType(attribute.configurationType()))
                .build();

        ReferenceMeta referenceMeta = new ReferenceMetaBuilder()
                .withName(deriveName(member, annotation))
                .withReferenceInterface(deriveReferenceInterface(member, annotation))
                .withBind(makeStringNullIfEmpty(annotation.bind()))
                .withCardinality(convertAnnotationReferenceCardinality(annotation.cardinality()))
                .withDynamic(annotation.dynamic())
                .withUnbind(makeStringNullIfEmpty(annotation.unbind()))
                .withAttribute(referenceAttribute).build();

        attributes.add(referenceMeta);
    }

    private void processShortAttributeAnnotation(Member element, Annotation annotation) {
        ShortAttributeMetaBuilder builder = new ShortAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        attributes.add(builder.build());
    }

    private void processStringAttributeAnnotation(Member element, Annotation annotation) {
        StringAttributeMetaBuilder builder = new StringAttributeMetaBuilder();
        fillSelectablePropertyAttributeBuilder(element, annotation, builder);
        Boolean multiLine = callMethodOfAnnotation(annotation, "multiLine");
        builder.withMultiLine(multiLine);
        attributes.add(builder.build());
    }
}
