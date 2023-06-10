package com.poemcollection.data.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PoemCategoryJunctionTable : IntIdTable() {
    val poemId = reference("poemId", PoemsTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Poems table this is enough.
    val categoryId = reference("categoryId", CategoriesTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Categories table this is enough.

    init {
        // Only a single pair can exist, duplicates are not allowed/necessary
        uniqueIndex(poemId, categoryId)
    }
}