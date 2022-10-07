package com.iwdael.dbroom.compiler.maker

import androidx.room.Database
import com.iwdael.annotationprocessorparser.Method
import com.iwdael.dbroom.compiler.Generator
import com.iwdael.dbroom.compiler.compat.charLower
import com.iwdael.dbroom.compiler.compat.write
import com.iwdael.dbroom.compiler.maker.Maker.Companion.ROOT_PACKAGE
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class DbRoomMaker(
    private val entities: List<Generator>,
    private val dao: List<Generator>,
    private val method: Method?
) : Maker {

    override fun classFull() = "$ROOT_PACKAGE.${className()}"
    override fun className() = "DbRoom"
    override fun packageName() = ROOT_PACKAGE
    override fun make(filer: Filer) {
        val init = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .returns(Void.TYPE)
            .addParameter(ClassName.get("android.content", "Context"), "context")
            .addStatement("if (instance != null) return")
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("synchronized (DbRoom.class)")
                    .addStatement(CodeBlock.of("if (instance != null) return"))
                    .apply {
                        if (method == null)
                            addStatement(
                                CodeBlock
                                    .builder()
                                    .add(
                                        "instance = \$T.databaseBuilder(context.getApplicationContext(),DbRoom.class,\"lite.db\").build()",
                                        ClassName.get("androidx.room", "Room")
                                    )
                                    .build()
                            )
                        else
                            addStatement(
                                CodeBlock
                                    .builder()
                                    .add(
                                        "instance = (DbRoom)${method.owner}.${method.name}(context)",
                                        ClassName.get("androidx.room", "Room")
                                    )
                                    .build()
                            )
                    }

                    .endControlFlow()
                    .build()
            )
            .build()


        val instance = MethodSpec.methodBuilder("instance")
            .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
            .returns(ClassName.get(packageName(), className()))
            .addStatement("if (instance == null) throw new RuntimeException(\"Please initialize DbRoom first\")")
            .addStatement("return instance")
            .build()

        val store = MethodSpec.methodBuilder("store")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addParameter(Object::class.java, "value")
            .addStatement("instance().store().store(name, Converter.toString(value))")
            .build()

        val obtain = MethodSpec.methodBuilder("obtain")
            .addAnnotation(NotNull::class.java)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addTypeVariable(TypeVariableName.get("T"))
            .addParameter(
                TypeVariableName.get("T")
                    .annotated(
                        AnnotationSpec.builder(NotNull::class.java)
                            .build()
                    ),
                "_default"
            )
            .returns(TypeVariableName.get("T"))
            .addStatement("Store store = instance().store().obtain(name)")
            .addStatement("if (store == null) return _default")
            .addStatement("T val = (T)Converter.toObject(store.value, _default.getClass())")
            .addStatement("if (val == null) return _default")
            .addStatement("return val")
            .build()

        val obtain2 = MethodSpec.methodBuilder("obtain")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(
                ParameterSpec.builder(String::class.java, "name")
                    .addAnnotation(NotNull::class.java)
                    .build()
            )
            .addTypeVariable(TypeVariableName.get("T"))
            .addParameter(
                TypeVariableName.get("Class<T>")
                    .annotated(
                        AnnotationSpec.builder(NotNull::class.java)
                            .build()
                    ),
                "clazz"
            )
            .returns(TypeVariableName.get("T"))
            .addStatement("Store store = instance().store().obtain(name)")
            .addStatement("if (store == null) return null")
            .addStatement("T val = (T)Converter.toObject(store.value, clazz)")
            .addStatement("if (val == null) return null")
            .addStatement("return val")
            .build()

        val classTypeSpec = TypeSpec.classBuilder(className())
            .addModifiers(Modifier.ABSTRACT)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(
                AnnotationSpec.builder(Database::class.java)
                    .addMember(
                        "entities",
                        CodeBlock.builder()
                            .apply {
                                val fmt = entities.joinToString(
                                    separator = ",",
                                    transform = { "\$T.class" },
                                    postfix = if (entities.isNotEmpty()) ",Store.class}" else "Store.class}",
                                    prefix = "{"
                                )
                                add(
                                    fmt,
                                    *entities
                                        .map {
                                            ClassName.get(it.packageName, it.cn)
                                        }
                                        .toTypedArray()
                                )
                            }
                            .build()
                    )
                    .addMember("version", CodeBlock.builder().add("1").build())
                    .addMember("exportSchema", CodeBlock.of("false"))
                    .build()
            )
            .addField(
                ClassName.get(packageName(), className()),
                "instance",
                Modifier.PRIVATE,
                Modifier.STATIC,
                Modifier.VOLATILE
            )
            .superclass(ClassName.get("androidx.room", "RoomDatabase"))
            .addMethod(init)
            .addMethod(instance)
            .addMethod(store)
            .addMethod(obtain)
            .addMethod(obtain2)
            .apply {
                entities.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.cn.charLower())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(ClassName.get(it.packageNameGenerator, it.classNameGenerator))
                            .addStatement("return instance()._${it.cn.charLower()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.cn.charLower())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(ClassName.get(it.packageNameGenerator, it.classNameGenerator))
                            .build()
                    )
                }
                dao.forEach {
                    addMethod(
                        MethodSpec.methodBuilder(it.cn.charLower())
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .returns(ClassName.get(it.packageName, it.cn))
                            .addStatement("return instance()._${it.cn.charLower()}()")
                            .build()
                    )
                    addMethod(
                        MethodSpec.methodBuilder("_" + it.cn.charLower())
                            .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                            .returns(ClassName.get(it.packageName, it.cn))
                            .build()
                    )
                }
            }
            .apply {
                addMethod(
                    MethodSpec.methodBuilder("store")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                        .returns(ClassName.get(ROOT_PACKAGE, "StoreRoom"))
                        .build()
                )
            }
            .build()
        JavaFile
            .builder(packageName(), classTypeSpec)
            .addFileComment("author : iwdael\ne-mail : iwdael@outlook.com")
            .build()
            .write(filer)

    }


}