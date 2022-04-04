package com.example.anvils

import androidx.lifecycle.ViewModel
// import com.example.annotations.ContributesViewModel
// import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.multibindings.IntoMap
import javax.inject.Inject
import kotlin.reflect.KClass

//@ContributesViewModel(AppComponent::class)
// @ViewModelKey(MainViewModel::class) // todo this could work as well
class MainViewModel @Inject constructor(
  private val provider: StringsProvider
): ViewModel()

@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
//@ContributesTo(AppComponent::class)
abstract class ViewModelsModule {

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  abstract fun bindMainViewModel(factory: MainViewModel): ViewModel
}

// @AssistedFactory
// interface MainViewModelFactory : ViewModelFactory<MainViewModel>

// interface ViewModelFactory<VM : ViewModel> {
//   fun create(): VM
// }
