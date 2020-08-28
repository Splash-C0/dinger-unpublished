package domain.interactor

internal interface UseCase<out T> {
  fun buildUseCase(): T
}
