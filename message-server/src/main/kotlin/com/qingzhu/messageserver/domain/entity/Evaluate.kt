package com.qingzhu.messageserver.domain.entity


/**
 * 会话评价
 */
class Evaluate(
    /** 评价模型 */
    var evaluationType: Int,
    /**
     * evaluationType:(evaluation)=>
     * 2:(100满意1不满意);
     * 3(100满意50一般1不满意); 5(100非常满意75满意50一般25不满意1非常不满意)
     * 否则未评价
     */
    var evaluation: Int,
    /** 评价内容 */
    var evaluationRemark: String,
    /** 用户标记的解决状态，0=未选择 1=已解决 2=未解决 */
    var userResolvedStatus: Int
) : java.io.Serializable {

    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }
}