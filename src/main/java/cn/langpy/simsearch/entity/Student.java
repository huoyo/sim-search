package cn.langpy.simsearch.entity;

import cn.langpy.simsearch.annotation.IndexColumn;
import cn.langpy.simsearch.annotation.IndexId;
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
    @IndexColumn
    String address;
}
