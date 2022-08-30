package com.edocyun.timchat.base.http.entity

/**
 * success: boolean;
code: number;
data: T;
message: string;
errors?: any;
 */
open class BaseEntity<T>(
    var data: T?,
    var success:Boolean?,
    var code: Int?,
    var message: String?,
    var errors: Any?
)


data class BaseListEntity<T> constructor(
    var records: ArrayList<T>?,
    var totalCount: Int,
    var pageIndex: Int,
    var pageSize: Int
)
