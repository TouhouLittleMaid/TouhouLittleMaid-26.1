package com.github.tartaricacid.touhoulittlemaid.component.codegen;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class MaidComponentProcessor extends AbstractProcessor {
    private static final String ANNOTATION =
            "com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef";
    private static final String PACKAGE = "com.github.tartaricacid.touhoulittlemaid.entity.passive.component";
    private static final ClassName ENTITY_MAID =
            ClassName.get("com.github.tartaricacid.touhoulittlemaid.entity.passive", "EntityMaid");
    private static final ClassName MAID_COMPONENT = ClassName.get(PACKAGE, "MaidComponent");
    private static final ClassName MAID_COMPONENTS = ClassName.get(PACKAGE, "MaidComponents");
    private static final ClassName MAID_COMPONENTS_SUPPORT = ClassName.get(PACKAGE, "MaidComponentsSupport");
    private static final ClassName LIFECYCLE_LISTS =
            ClassName.get(PACKAGE, "MaidComponentsSupport", "LifecycleLists");
    private static final ClassName BASE_TICK = ClassName.get(PACKAGE + ".lifecycle", "BaseTickComponent");
    private static final ClassName AI_STEP = ClassName.get(PACKAGE + ".lifecycle", "AiStepComponent");
    private static final ClassName SAVE = ClassName.get(PACKAGE + ".lifecycle", "SaveComponent");
    private static final ClassName VALUE_INPUT = ClassName.get("net.minecraft.world.level.storage", "ValueInput");
    private static final ClassName VALUE_OUTPUT = ClassName.get("net.minecraft.world.level.storage", "ValueOutput");
    private static final ClassName LIST = ClassName.get("java.util", "List");

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(ANNOTATION);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        TypeElement annotationType = processingEnv.getElementUtils().getTypeElement(ANNOTATION);
        if (annotationType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Missing annotation type: " + ANNOTATION);
            return false;
        }

        List<ComponentEntry> entries = new ArrayList<>();
        Map<String, Element> fieldNameOwners = new LinkedHashMap<>();
        boolean hasErrors = false;

        for (Element element : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@MaidComponentDef can only be applied to classes",
                        element
                );
                hasErrors = true;
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            TypeElement maidComponentType = processingEnv.getElementUtils().getTypeElement(MAID_COMPONENT.canonicalName());
            if (maidComponentType == null || !processingEnv.getTypeUtils().isAssignable(typeElement.asType(), maidComponentType.asType())) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        typeElement.getSimpleName() + " must implement MaidComponent",
                        element
                );
                hasErrors = true;
                continue;
            }
            String fieldName = resolveFieldName(typeElement);
            if (fieldName == null) {
                hasErrors = true;
                continue;
            }
            Element previousOwner = fieldNameOwners.putIfAbsent(fieldName, element);
            if (previousOwner != null) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Duplicate @MaidComponentDef field name \"" + fieldName + "\"",
                        element
                );
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Field name \"" + fieldName + "\" is already used here",
                        previousOwner
                );
                hasErrors = true;
                continue;
            }
            entries.add(new ComponentEntry(fieldName, ClassName.get(typeElement)));
        }

        if (hasErrors) {
            return true;
        }

        if (entries.isEmpty()) {
            return false;
        }

        entries.sort(Comparator.comparing(entry -> entry.fieldName));

        try {
            writeMaidComponents(entries);
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate MaidComponents: " + exception.getMessage()
            );
        }

        return true;
    }

    private String resolveFieldName(TypeElement typeElement) {
        var annotationMirror = processingEnv.getElementUtils().getAllAnnotationMirrors(typeElement).stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(ANNOTATION))
                .findFirst()
                .orElseThrow();

        for (var entry : annotationMirror.getElementValues().entrySet()) {
            if ("value".equals(entry.getKey().getSimpleName().toString())) {
                String fieldName = entry.getValue().getValue().toString();
                if (fieldName.isEmpty()) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@MaidComponentDef value must not be empty",
                            typeElement
                    );
                    return null;
                }
                if (!isValidFieldName(fieldName)) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "@MaidComponentDef value is not a valid field name: " + fieldName,
                            typeElement
                    );
                    return null;
                }
                return fieldName;
            }
        }
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "@MaidComponentDef value is required",
                typeElement
        );
        return null;
    }

    private static boolean isValidFieldName(String fieldName) {
        if (fieldName.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(fieldName.charAt(0))) {
            return false;
        }
        for (int i = 1; i < fieldName.length(); i++) {
            if (!Character.isJavaIdentifierPart(fieldName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void writeMaidComponents(List<ComponentEntry> entries) throws IOException {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(MAID_COMPONENTS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Generated by {@code MaidComponentProcessor}. Do not edit.\n");

        typeBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(LIST, BASE_TICK),
                "baseTickComponents",
                Modifier.PRIVATE,
                Modifier.FINAL
        ).build());
        typeBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(LIST, AI_STEP),
                "aiStepComponents",
                Modifier.PRIVATE,
                Modifier.FINAL
        ).build());
        typeBuilder.addField(FieldSpec.builder(
                ParameterizedTypeName.get(LIST, SAVE),
                "saveComponents",
                Modifier.PRIVATE,
                Modifier.FINAL
        ).build());

        for (ComponentEntry entry : entries) {
            typeBuilder.addField(FieldSpec.builder(entry.typeName, entry.fieldName, Modifier.PUBLIC, Modifier.FINAL).build());
        }

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder();

        CodeBlock.Builder constructorBody = CodeBlock.builder();

        for (ComponentEntry entry : entries) {
            constructor.addParameter(entry.typeName, entry.fieldName);
            constructorBody.addStatement("this.$L = $L", entry.fieldName, entry.fieldName);
        }

        CodeBlock.Builder listBuilder = CodeBlock.builder().add("java.util.List.of(\n");
        for (int i = 0; i < entries.size(); i++) {
            listBuilder.add("$L", entries.get(i).fieldName);
            if (i + 1 < entries.size()) {
                listBuilder.add(",\n");
            }
        }
        listBuilder.add("\n)");

        constructorBody.addStatement("$T<$T> components = $L", LIST, MAID_COMPONENT, listBuilder.build());
        constructorBody.addStatement("$T<$T> ordered = $T.sortComponents(components)", LIST, MAID_COMPONENT, MAID_COMPONENTS_SUPPORT);
        constructorBody.addStatement("$T lifecycleLists = $T.classifyLifecycleComponents(ordered)", LIFECYCLE_LISTS, MAID_COMPONENTS_SUPPORT);
        constructorBody.addStatement("this.baseTickComponents = lifecycleLists.baseTickComponents()");
        constructorBody.addStatement("this.aiStepComponents = lifecycleLists.aiStepComponents()");
        constructorBody.addStatement("this.saveComponents = lifecycleLists.saveComponents()");
        constructorBody.beginControlFlow("for ($T component : ordered)", MAID_COMPONENT);
        constructorBody.addStatement("component.init(this)");
        constructorBody.endControlFlow();

        constructor.addCode(constructorBody.build());
        typeBuilder.addMethod(constructor.build());

        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(MAID_COMPONENTS)
                .addParameter(ENTITY_MAID, "maid");
        for (ComponentEntry entry : entries) {
            createMethod.addStatement("$T $L = new $T(maid)", entry.typeName, entry.fieldName, entry.typeName);
        }
        CodeBlock.Builder constructorArgs = CodeBlock.builder().add("new $T(\n", MAID_COMPONENTS);
        for (int i = 0; i < entries.size(); i++) {
            constructorArgs.add("$L", entries.get(i).fieldName);
            if (i + 1 < entries.size()) {
                constructorArgs.add(",\n");
            }
        }
        constructorArgs.add("\n)");
        createMethod.addStatement("return $L", constructorArgs.build());
        typeBuilder.addMethod(createMethod.build());

        typeBuilder.addMethod(MethodSpec.methodBuilder("baseTick")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .beginControlFlow("for ($T component : baseTickComponents)", BASE_TICK)
                .addStatement("component.baseTick()")
                .endControlFlow()
                .build());

        typeBuilder.addMethod(MethodSpec.methodBuilder("aiStep")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .beginControlFlow("for ($T component : aiStepComponents)", AI_STEP)
                .addStatement("component.aiStep()")
                .endControlFlow()
                .build());

        typeBuilder.addMethod(MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(VALUE_OUTPUT, "output")
                .beginControlFlow("for ($T component : saveComponents)", SAVE)
                .addStatement("component.save(output)")
                .endControlFlow()
                .build());

        typeBuilder.addMethod(MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(VALUE_INPUT, "input")
                .beginControlFlow("for ($T component : saveComponents)", SAVE)
                .addStatement("component.load(input)")
                .endControlFlow()
                .build());

        JavaFile.builder(PACKAGE, typeBuilder.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(filer);
    }

    private record ComponentEntry(String fieldName, ClassName typeName) {
    }
}
