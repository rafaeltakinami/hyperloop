package br.com.guiabolso.config

private typealias Env = EnvironmentLoader

enum class SchemaStorageType {
    IN_MEMORY, S3, FILESYSTEM
}

fun storageType() = SchemaStorageType.valueOf(Env.get("STORAGE_TYPE", "S3"))

fun s3BucketName() = Env.get("BUCKET_NAME", "ryzen-flows/insecure")

private object EnvironmentLoader {
    fun get(key: String, defaultValue: String): String = when {
        System.getenv(key) != null -> System.getenv(key)
        else -> defaultValue
    }
}

