package com.hgtech.expensehistory.database.relation.category_user

import androidx.room.Embedded
import androidx.room.Relation
import com.hgtech.expensehistory.database.model.CategoryTable
import com.hgtech.expensehistory.database.model.UserTable

data class UserCategories(

    @Embedded
    var userTable: UserTable? = null,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    var categoryTable: List<CategoryTable>? = null,
)
