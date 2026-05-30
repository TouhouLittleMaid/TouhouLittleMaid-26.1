package com.github.tartaricacid.touhoulittlemaid.manager.codegen;

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
public class MaidManagerProcessor extends AbstractProcessor {
    private static final String ANNOTATION =
            "com.github.tartaricacid.touhoulittlemaid.entity.passive.MaidManagerDef";
    private static final String PASSIVE_PACKAGE =
            "com.github.tartaricacid.touhoulittlemaid.entity.passive";
    private static final ClassName ENTITY_MAID = ClassName.get(PASSIVE_PACKAGE, "EntityMaid");
    private static final ClassName MAID_MANAGERS = ClassName.get(PASSIVE_PACKAGE, "MaidManagers");
    private static final ClassName MAID_MANAGER_HOST = ClassName.get(PASSIVE_PACKAGE, "MaidManagerHost");
    private static final ClassName MAID_MANAGER_BOOTSTRAP =
            ClassName.get(PASSIVE_PACKAGE, "MaidManagerBootstrap");

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

        List<ManagerEntry> entries = new ArrayList<>();
        Map<String, Element> aliasOwners = new LinkedHashMap<>();
        boolean hasErrors = false;

        for (Element element : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "@MaidManagerDef can only be applied to classes",
                        element
                );
                hasErrors = true;
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            Metadata metadata = resolveMetadata(typeElement);
            if (metadata == null) {
                hasErrors = true;
                continue;
            }
            Element previousAliasOwner = aliasOwners.putIfAbsent(metadata.alias(), element);
            if (previousAliasOwner != null) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Duplicate @MaidManagerDef alias \"" + metadata.alias() + "\"",
                        element
                );
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Alias \"" + metadata.alias() + "\" is already used here",
                        previousAliasOwner
                );
                hasErrors = true;
                continue;
            }
            entries.add(new ManagerEntry(
                    metadata.alias(),
                    ClassName.get(typeElement),
                    deriveGetter(metadata.alias()),
                    metadata.exposeView()
            ));
        }

        if (hasErrors) {
            return true;
        }

        if (entries.isEmpty()) {
            return false;
        }

        if (checkDuplicateSyms(entries)) {
            return true;
        }

        entries.sort(Comparator.comparing(ManagerEntry::alias));

        try {
            writeMaidManagers(entries);
            writeMaidManagerBootstrap(entries);
            writeMaidManagerHost(entries);
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate maid manager types: " + exception.getMessage()
            );
        }

        return true;
    }

    private static String deriveGetter(String alias) {
        return "get" + Character.toUpperCase(alias.charAt(0)) + alias.substring(1);
    }

    private Metadata resolveMetadata(TypeElement typeElement) {
        var annotationMirror = processingEnv.getElementUtils().getAllAnnotationMirrors(typeElement).stream()
                .filter(mirror -> mirror.getAnnotationType().toString().equals(ANNOTATION))
                .findFirst()
                .orElseThrow();

        String alias = null;
        boolean exposeView = false;

        for (var entry : annotationMirror.getElementValues().entrySet()) {
            String key = entry.getKey().getSimpleName().toString();
            Object value = entry.getValue().getValue();
            switch (key) {
                case "alias" -> alias = value.toString();
                case "exposeView" -> exposeView = (Boolean) value;
                default -> {
                }
            }
        }

        if (alias == null || alias.isEmpty()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@MaidManagerDef alias is required",
                    typeElement
            );
            return null;
        }
        if (!isValidIdentifier(alias)) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@MaidManagerDef alias is not a valid field name: " + alias,
                    typeElement
            );
            return null;
        }

        return new Metadata(alias, exposeView);
    }

    private boolean checkDuplicateSyms(List<ManagerEntry> entries) {
        TypeElement entityMaid = processingEnv.getElementUtils().getTypeElement(ENTITY_MAID.canonicalName());
        if (entityMaid == null) {
            return false;
        }

        boolean hasErrors = false;
        for (Element member : entityMaid.getEnclosedElements()) {
            if (member.getKind() != ElementKind.FIELD) {
                continue;
            }
            String fieldName = member.getSimpleName().toString();
            for (ManagerEntry entry : entries) {
                if (entry.alias().equals(fieldName)) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Field name \"" + fieldName + "\" conflicts with a @MaidManagerDef alias.",
                            member
                    );
                    hasErrors = true;
                    break;
                }
            }
        }
        return hasErrors;
    }

    private static boolean isValidIdentifier(String name) {
        if (name.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static CodeBlock commaSeparatedAliases(List<ManagerEntry> entries) {
        CodeBlock.Builder builder = CodeBlock.builder();
        for (int i = 0; i < entries.size(); i++) {
            builder.add("$L", entries.get(i).alias());
            if (i + 1 < entries.size()) {
                builder.add(",\n");
            }
        }
        return builder.build();
    }

    private void writeMaidManagers(List<ManagerEntry> entries) throws IOException {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(MAID_MANAGERS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Generated by {@code MaidManagerProcessor}. Do not edit.\n");

        for (ManagerEntry entry : entries) {
            typeBuilder.addField(FieldSpec.builder(entry.typeName(), entry.alias(), Modifier.PUBLIC, Modifier.FINAL).build());
        }

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        CodeBlock.Builder constructorBody = CodeBlock.builder();

        for (ManagerEntry entry : entries) {
            constructor.addParameter(entry.typeName(), entry.alias());
            constructorBody.addStatement("this.$L = $L", entry.alias(), entry.alias());
        }

        constructor.addCode(constructorBody.build());
        typeBuilder.addMethod(constructor.build());

        JavaFile.builder(PASSIVE_PACKAGE, typeBuilder.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(filer);
    }

    private void writeMaidManagerBootstrap(List<ManagerEntry> entries) throws IOException {
        MethodSpec.Builder create = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(MAID_MANAGERS)
                .addParameter(ENTITY_MAID, "maid");
        for (ManagerEntry entry : entries) {
            create.addStatement("$T $L = new $T(maid)", entry.typeName(), entry.alias(), entry.typeName());
        }
        create.addStatement("return new $T(\n$L\n)", MAID_MANAGERS, commaSeparatedAliases(entries));

        TypeSpec type = TypeSpec.classBuilder(MAID_MANAGER_BOOTSTRAP)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Generated by {@code MaidManagerProcessor}. Do not edit.\n")
                .addMethod(create.build())
                .build();

        JavaFile.builder(PASSIVE_PACKAGE, type)
                .skipJavaLangImports(true)
                .build()
                .writeTo(filer);
    }

    private void writeMaidManagerHost(List<ManagerEntry> entries) throws IOException {
        ClassName tamableAnimal = ClassName.get("net.minecraft.world.entity", "TamableAnimal");
        ClassName entityType = ClassName.get("net.minecraft.world.entity", "EntityType");
        ClassName level = ClassName.get("net.minecraft.world.level", "Level");

        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(MAID_MANAGER_HOST)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .superclass(tamableAnimal)
                .addJavadoc("Generated by {@code MaidManagerProcessor}. Do not edit.\n")
                .addJavadoc("Manager 字段（如 {@code itemManager}）由注解自动生成，{@link EntityMaid} 请勿重复声明。\n");

        for (ManagerEntry entry : entries) {
            if (entry.exposeView()) {
                typeBuilder.addSuperinterface(entry.typeName().nestedClass("View"));
            }
        }

        typeBuilder.addField(FieldSpec.builder(MAID_MANAGERS, "managers", Modifier.PROTECTED).build());

        for (ManagerEntry entry : entries) {
            typeBuilder.addField(FieldSpec.builder(entry.typeName(), entry.alias(), Modifier.PROTECTED).build());
        }

        typeBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ParameterizedTypeName.get(entityType, ENTITY_MAID), "type")
                .addParameter(level, "world")
                .addStatement("super(type, world)")
                .build());

        typeBuilder.addMethod(MethodSpec.methodBuilder("managers")
                .addModifiers(Modifier.PUBLIC)
                .returns(MAID_MANAGERS)
                .addStatement("return managers")
                .build());

        for (ManagerEntry entry : entries) {
            MethodSpec.Builder getter = MethodSpec.methodBuilder(entry.getter())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(entry.typeName())
                    .addStatement("return $L", entry.alias());
            if (entry.exposeView()) {
                getter.addAnnotation(Override.class);
            }
            typeBuilder.addMethod(getter.build());
        }

        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("initMaidManagers")
                .addModifiers(Modifier.PROTECTED, Modifier.FINAL)
                .returns(void.class)
                .addParameter(ENTITY_MAID, "maid")
                .addStatement("this.managers = $T.create(maid)", MAID_MANAGER_BOOTSTRAP);
        for (ManagerEntry entry : entries) {
            initMethod.addStatement("this.$L = this.managers.$L", entry.alias(), entry.alias());
        }
        typeBuilder.addMethod(initMethod.build());

        JavaFile.builder(PASSIVE_PACKAGE, typeBuilder.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(filer);
    }

    private record Metadata(String alias, boolean exposeView) {
    }

    private record ManagerEntry(String alias, ClassName typeName, String getter, boolean exposeView) {
    }
}
