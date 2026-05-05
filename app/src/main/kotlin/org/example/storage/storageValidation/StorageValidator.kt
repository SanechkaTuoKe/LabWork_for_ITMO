package org.example.storage.storageValidation


interface StorageValidator<T> {
    fun validate(entity: T): String?
}
