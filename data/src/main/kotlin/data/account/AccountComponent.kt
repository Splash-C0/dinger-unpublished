package data.account

import dagger.Component
import javax.inject.Singleton

@Component(modules = [AccountModule::class])
@Singleton
internal interface AccountComponent {
  fun inject(dingerAccountManagerService: DingerAccountManagerService)
}
