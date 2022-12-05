package aasmc.ru.playground.filtering.callback

class CurrentUser private constructor(): ThreadLocal<User>() {
    companion object {
        val INSTANCE = CurrentUser()
    }
}