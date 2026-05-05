package org.example.storage.saveLoad

interface SaveLoad<T, ID> {
    fun save(entities: Collection<T>)
    fun load(): Map<ID, T>
    fun exists(): Boolean
}