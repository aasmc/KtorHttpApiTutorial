package aasmc.ru.playground.filtering.callback

class Mail: ArrayList<String>() {
    companion object {
        val INSTANCE = Mail()
    }

    fun send(message: String) {
        add(message)
    }
}