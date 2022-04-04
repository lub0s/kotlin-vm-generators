package com.example.anvils

import androidx.lifecycle.ViewModel
import com.example.annotations.ContributesViewModel
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>


//@ContributesMultibinding(AppScope::class)
//@ViewModelKey(MainViewModel::class)
@ContributesViewModel(ViewModelComponent::class)
class MainViewModel @Inject constructor(
  val provider: StringsProvider
): ViewModel()

@ContributesViewModel(ViewModelComponent::class)
class MainViewModelB @Inject constructor(
  val provider: StringsProvider
): ViewModel()

// interface ViewModelFactory<VM : ViewModel> {
//   fun create(): VM
// }
