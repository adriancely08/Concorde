// Configuración a nivel de proyecto. Las versiones de los plugins se
// resuelven desde el repositorio de Google y Maven Central (ver
// settings.gradle.kts) la primera vez que abras el proyecto en
// Android Studio -- necesitas internet en ese primer "Gradle Sync".
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
