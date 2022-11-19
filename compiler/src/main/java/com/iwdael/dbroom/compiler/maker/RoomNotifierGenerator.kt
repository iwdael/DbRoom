package com.iwdael.dbroom.compiler.maker

import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER
import com.iwdael.dbroom.compiler.JavaClass.ROOM_NOTIFIER_NOTIFIER
import com.iwdael.dbroom.compiler.compat.FILE_COMMENT
import com.iwdael.dbroom.compiler.compat.charLower
import com.iwdael.dbroom.compiler.compat.write
import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
class RoomNotifierGenerator : Generator {
    override val simpleClassNameGen: String = ROOM_NOTIFIER.simpleName()
    override val packageNameGen: String = ROOM_NOTIFIER.packageName()
    override val classNameGen: String = "$packageNameGen.$simpleClassNameGen"
    private val handlerClassName: ClassName = ClassName.get("android.os", "Handler")
    private val handlerThreadClassName: ClassName = ClassName.get("android.os", "HandlerThread")
    override fun generate(filer: Filer) {
        JavaFile
            .builder(
                packageNameGen, TypeSpec.classBuilder(simpleClassNameGen)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(
                        FieldSpec.builder(
                            handlerClassName,
                            handlerClassName.simpleName().charLower()
                        )
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("checkAndInit")
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                            .beginControlFlow("if (handler == null)")
                            .beginControlFlow("synchronized (\$T.class)", ROOM_NOTIFIER)
                            .beginControlFlow("if (handler == null)")
                            .addStatement(
                                "\$T thread = new \$T(\"room-update\")",
                                handlerThreadClassName,
                                handlerThreadClassName
                            )
                            .addStatement("thread.start()")
                            .addStatement("handler = new Handler(thread.getLooper())")
                            .endControlFlow()
                            .endControlFlow()
                            .endControlFlow()
                            .build()
                    )
                    .addMethod(
                        MethodSpec.methodBuilder("notifyRoom")
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addParameter(
                                ROOM_NOTIFIER_NOTIFIER,
                                ROOM_NOTIFIER_NOTIFIER.simpleName().charLower()
                            )
                            .addStatement("checkAndInit()")
                            .addStatement("handler.post(notifier::notifier)")
                            .build()
                    )
                    .addType(
                        TypeSpec.interfaceBuilder(ROOM_NOTIFIER_NOTIFIER)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(
                                MethodSpec
                                    .methodBuilder("notifier")
                                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .addFileComment(FILE_COMMENT)
            .build()
            .write(filer)
    }
}