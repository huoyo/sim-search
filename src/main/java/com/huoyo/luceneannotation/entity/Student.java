package com.huoyo.luceneannotation.entity;

import com.huoyo.luceneannotation.annotation.IndexColumn;
import com.huoyo.luceneannotation.annotation.IndexId;
import lombok.Data;

/**
 * @name：
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:13
 */
@Data
public class Student {
    @IndexId
    String studentId;
    @IndexColumn
    String studentName;
    @IndexColumn
    String studentNum;
}
