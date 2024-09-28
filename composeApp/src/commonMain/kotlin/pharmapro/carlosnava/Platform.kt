package pharmapro.carlosnava

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform