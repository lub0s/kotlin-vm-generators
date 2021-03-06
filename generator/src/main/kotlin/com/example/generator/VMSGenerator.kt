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
import com.squareup.anvil.compiler.internal.hasAnnotation
import com.squareup.anvil.compiler.internal.requireFqName
import com.squareup.anvil.compiler.internal.requireTypeReference
import com.squareup.anvil.compiler.internal.scope
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
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
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.allConstructors
import java.io.File

@OptIn(ExperimentalAnvilApi::class)
@AutoService(CodeGenerator::class)
class VMSGenerator : CodeGenerator {

  override fun isApplicable(context: AnvilContext): Boolean = true

  override fun generateCode(
    codeGenDir: File,
    module: ModuleDescriptor,
    projectFiles: Collection<KtFile>
  ): Collection<GeneratedFile> {
    return projectFiles.classesAndInnerClass(module)
      .filter { it.hasAnnotation(ContributesViewModel::class.fqName, module) }
      .flatMap {
        listOf(
          generateModule(it, codeGenDir, module),
          generateAssistedFactory(it, codeGenDir, module)
        )
      }
      .toList()
  }

  private fun generateModule(
    vmClass: KtClassOrObject,
    codeGenDir: File,
    module: ModuleDescriptor
  ): GeneratedFile {
    val generatedPackage = vmClass.containingKtFile.packageFqName.toString()
    val moduleClassName = "${vmClass.name}_Module"
    val scope = vmClass.scope(ContributesViewModel::class.fqName, module)
    val factoryName = ClassName(generatedPackage, "${vmClass.name}_AssistedFactory")

    val content = FileSpec.buildFile(generatedPackage, moduleClassName) {
      addType(
        TypeSpec.classBuilder(moduleClassName)
          .addModifiers(KModifier.ABSTRACT)
          .addAnnotation(Module::class)
          .addAnnotation(
            AnnotationSpec.builder(ContributesTo::class)
              .addMember("%T::class", scope.asClassName(module))
              .build()
          )
          .addFunction(
            FunSpec.builder("bind${vmClass.name}Factory")
              .addModifiers(KModifier.ABSTRACT)
              .addParameter("factory", factoryName)
              .returns(viewModelFactoryFqName.asClassName(module).parameterizedBy(STAR))
              .addAnnotation(Binds::class)
              .addAnnotation(IntoMap::class)
              .addAnnotation(
                AnnotationSpec.builder(viewModelKeyFqName.asClassName(module))
                  .addMember("%T::class", vmClass.asClassName())
                  .build()
              )
              .build(),
          )
          .build(),
      )
    }

    return createGeneratedFile(codeGenDir, generatedPackage, moduleClassName, content)
  }

  private fun generateAssistedFactory(
    vmClass: KtClassOrObject,
    codeGenDir: File,
    module: ModuleDescriptor
  ): GeneratedFile {
    val generatedPackage = vmClass.containingKtFile.packageFqName.toString()
    val assistedFactoryClassName = "${vmClass.name}_AssistedFactory"
    val assistedConstructor = vmClass.allConstructors.singleOrNull {
      it.hasAnnotation(
        AssistedInject::class.fqName,
        module
      )
    }
    val vmClassName = vmClass.asClassName()

    if (assistedConstructor == null) {
      throw AnvilCompilationException(
        "${vmClass.requireFqName()} must have an @AssistedInject constructor",
        element = vmClass.identifyingElement,
      )
    }

    val assistedProperties = assistedConstructor
      ?.getValueParameters()
      ?.filter { it.hasAnnotation(Assisted::class.fqName, module) } ?: emptyList()

    require(assistedProperties.isNotEmpty()) {
      throw AnvilCompilationException(
        "Every ViewModel should request SavedStateHandle",
        element = vmClass.identifyingElement,
      )
    }

    val content = FileSpec.buildFile(generatedPackage, assistedFactoryClassName) {
      addType(
        TypeSpec.interfaceBuilder("${vmClass.name}_AssistedFactory")
          .addAnnotation(AssistedFactory::class)
          .addSuperinterface(
            viewModelFactoryFqName.asClassName(module)
              .parameterizedBy(vmClassName)
          )
          .addFunction(
            FunSpec.builder("create")
              .addModifiers(KModifier.OVERRIDE, KModifier.ABSTRACT)
              .apply {
                val savedStateHandle = assistedProperties.first()

                addParameter(
                  savedStateHandle.name ?: "savedStateHandle",
                  savedStateHandle.requireTypeReference(module)
                    .requireFqName(module)
                    .asClassName(module)
                )
              }
              .returns(vmClassName)
              .build(),
          )
          .build(),
      )
    }

    return createGeneratedFile(codeGenDir, generatedPackage, assistedFactoryClassName, content)
  }

  companion object {
    private val viewModelFactoryFqName = FqName("com.example.anvils.ViewModelFactory")
    private val viewModelKeyFqName = FqName("com.example.anvils.ViewModelKey")
  }
}
