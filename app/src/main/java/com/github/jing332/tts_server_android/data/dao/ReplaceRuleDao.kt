package com.github.jing332.tts_server_android.data.dao

import androidx.room.*
import com.github.jing332.tts_server_android.data.entities.AbstractListGroup.Companion.DEFAULT_GROUP_ID
import com.github.jing332.tts_server_android.data.entities.replace.GroupWithReplaceRule
import com.github.jing332.tts_server_android.data.entities.replace.ReplaceRule
import com.github.jing332.tts_server_android.data.entities.replace.ReplaceRuleGroup
import kotlinx.coroutines.flow.Flow
import kotlin.math.min

@Dao
interface ReplaceRuleDao {
    @get:Query("SELECT * FROM ReplaceRule ORDER BY `order` ASC")
    val all: List<ReplaceRule>

    @get:Query("SELECT * FROM ReplaceRule WHERE isEnabled = '1' ORDER BY `order` ASC")
    val allEnabled: List<ReplaceRule>

    @Query("SELECT * FROM ReplaceRule ORDER BY `order` ASC")
    fun flowAll(): Flow<List<ReplaceRule>>

    @get:Query("SELECT count(*) FROM ReplaceRule")
    val count: Int

    @Query("SELECT * FROM ReplaceRule WHERE id = :id")
    fun get(id: Long): ReplaceRule?

    @get:Query("SELECT * FROM replaceRuleGroup ORDER by `order` ASC")
    val allGroup: List<ReplaceRuleGroup>

    @Transaction
    @Query("SELECT * FROM replaceRuleGroup ORDER BY `order` ASC")
    fun flowAllGroupWithReplaceRules(): Flow<List<GroupWithReplaceRule>>

    @Query("SELECT * FROM replaceRuleGroup WHERE id = :id")
    fun getGroup(id: Long = DEFAULT_GROUP_ID): ReplaceRuleGroup?

    @Query("SELECT * FROM ReplaceRule WHERE groupId = :groupId")
    fun getListInGroup(groupId: Long = DEFAULT_GROUP_ID): List<ReplaceRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(vararg data: ReplaceRuleGroup)

    @Update
    fun updateGroup(vararg data: ReplaceRuleGroup)

    @Delete
    fun deleteGroup(vararg data: ReplaceRuleGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg data: ReplaceRule)

    @Delete
    fun delete(vararg data: ReplaceRule)

    @Query("DELETE from ReplaceRule WHERE groupId = :groupId")
    fun deleteAllByGroup(groupId: Long)

    @Update
    fun update(vararg data: ReplaceRule)

    /**
     * 更新数据并刷新排序索引
     */
    fun updateDataAndRefreshOrder(data: ReplaceRule) {
        val list = getListInGroup(data.groupId).toMutableList()
        list.removeIf { it.id == data.id }
        list.add(min(data.order, list.size), data)
        list.forEachIndexed { index, value -> value.apply { order = index } }

        update(*list.toTypedArray())
    }
}