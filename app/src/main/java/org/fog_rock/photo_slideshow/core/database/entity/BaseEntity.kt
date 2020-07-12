package org.fog_rock.photo_slideshow.core.database.entity

/**
 * 最低限必要なカラムを定義したインターフェース.
 */
interface BaseEntity {
    /**
     * 自動生成される優先ID
     * @PrimaryKey(autoGenerate = true)
     */
    val id: Long

    /**
     * データが生成された時間 (millisecs)
     * @ColumnInfo(name = "create_date")
     */
    val createDateTimeMillis: Long

    /**
     * データが更新された時間 (millisecs)
     * @ColumnInfo(name = "update_date")
     */
    val updateDateTimeMillis: Long
}