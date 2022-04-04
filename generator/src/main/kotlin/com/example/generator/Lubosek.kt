package com.example.generator

import com.example.annotations.ContributesViewModel
import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilCompilationException
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.fqName
import com.squareup.anvil.compiler.internal.fqNameOrNull
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.anvil.compiler.internal.requireFqName
import com.squareup.anvil.compiler.internal.requireTypeReference
import com.squareup.anvil.compiler.internal.scope
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.allConstructors
import java.io.File


// https://gist.github.com/gpeal/d29fc2e6e4ebd551865390826412493e
// https://gpeal.medium.com/dagger-anvil-learning-to-love-dependency-injection-on-android-8fad3d5530c9

@OptIn(ExperimentalAnvilApi::class)
@AutoService(CodeGenerator::class)
class Lubosek : CodeGenerator {

  override fun isApplicable(context: AnvilContext): Boolean  = true

  override fun generateCode(codeGenDir: File, module: ModuleDescriptor, projectFiles: Collection<KtFile>): Collection<GeneratedFile> {
    return projectFiles.classesAndInnerClass(module)
      .filter { it.hasAnnotation(ContributesViewModel::class.fqName, module) }
      .flatMap { listOf(generateModule(it, codeGenDir, module), generateAssistedFactory(it, codeGenDir, module)) }
      .toList()
  }

  private fun generateModule(vmClass: KtClassOrObject, codeGenDir: File, module: ModuleDescriptor): GeneratedFile {
    val generatedPackage = vmClass.containingKtFile.packageFqName.toString()
    val moduleClassName = "${vmClass.name}_Module"
    val scope = vmClass.scope(ContributesViewModel::class.fqName, module)
    val content = FileSpec.buildFile(generatedPackage, moduleClassName) {
      addType(
        TypeSpec.classBuilder(moduleClassName)
          .addModifiers(KModifier.ABSTRACT)
          .addAnnotation(Module::class)
          .addAnnotation(AnnotationSpec.builder(ContributesTo::class).addMember("%T::class", scope.asClassName(module)).build())
          .addFunction(
            FunSpec.builder("bind${vmClass.name}Factory")
              .addModifiers(KModifier.ABSTRACT)
              .addParameter("factory", ClassName(generatedPackage, "${vmClass.name}_AssistedFactory"))
              .returns(viewModelFactoryFqName.asClassName(module).parameterizedBy(STAR, STAR))
              .addAnnotation(Binds::class)
              .addAnnotation(IntoMap::class)
              .addAnnotation(AnnotationSpec.builder(viewModelKeyFqName.asClassName(module)).addMember("%T::class", vmClass.asClassName()).build())
              .build(),
          )
          .build(),
      )
    }
    return createGeneratedFile(codeGenDir, generatedPackage, moduleClassName, content)
  }

  private fun generateAssistedFactory(vmClass: KtClassOrObject, codeGenDir: File, module: ModuleDescriptor): GeneratedFile {
    val generatedPackage = vmClass.containingKtFile.packageFqName.toString()
    val assistedFactoryClassName = "${vmClass.name}_AssistedFactory"
    val constructor = vmClass.allConstructors.singleOrNull { it.hasAnnotation(AssistedInject::class.fqName, module) }
    val assistedParameter = constructor?.valueParameters?.singleOrNull { it.hasAnnotation(Assisted::class.fqName, module) }
    if (constructor == null || assistedParameter == null) {
      throw AnvilCompilationException(
        "${vmClass.requireFqName()} must have an @AssistedInject constructor with @Assisted initialState: S parameter",
        element = vmClass.identifyingElement,
      )
    }
    if (assistedParameter.name != "initialState") {
      throw AnvilCompilationException(
        "${vmClass.requireFqName()} @Assisted parameter must be named initialState",
        element = assistedParameter.identifyingElement,
      )
    }
    val vmClassName = vmClass.asClassName()
    val stateClassName = assistedParameter.requireTypeReference(module).requireFqName(module).asClassName(module)
    val content = FileSpec.buildFile(generatedPackage, assistedFactoryClassName) {
      addType(
        TypeSpec.interfaceBuilder(assistedFactoryClassName)
          .addSuperinterface(viewModelFactoryFqName.asClassName(module).parameterizedBy(vmClassName, stateClassName))
          .addAnnotation(AssistedFactory::class)
          .addFunction(
            FunSpec.builder("create")
              .addModifiers(KModifier.OVERRIDE, KModifier.ABSTRACT)
              .addParameter("initialState", stateClassName)
              .returns(vmClassName)
              .build(),
          )
          .build(),
      )
    }
    return createGeneratedFile(codeGenDir, generatedPackage, assistedFactoryClassName, content)
  }

  companion object {
    private val viewModelFactoryFqName = FqName("com.example.generator.ExampleViewModelFactory")
    private val viewModelKeyFqName = FqName("com.example.generator.ViewModelKey")
  }
}
