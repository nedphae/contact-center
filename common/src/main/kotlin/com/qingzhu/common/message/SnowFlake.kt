package com.qingzhu.common.message

/**
 * SnowFlake 在转换为 Double 时会丢失低位精度
 */
class SnowFlake(private val dataCenterId: Long, private val serviceId: Long) : SequenceGenerator {
    /**
     * 起始的时间戳
     */
    private val startStamp = 1606071088723L

    /**
     * 时间戳高位截取 63 - 11 = 52
     * 减10保留了符号位
     */
    private val intercept = Long.MAX_VALUE shr 10

    /**
     * 每一部分占用的位数
     */
    // 调低了4位，自增值支持从同一毫秒内同一个节点可以生成 4096 个 ID 改为 256 个
    private val sequenceBit: Int = 8 // 12 //序列号占用的位数
    private val machineBit: Int = 3 // 5   //机器标识占用的位数
    // private val dataCenterBit: Int = 5//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    // private val maxDataCenterNum = -1L xor (-1L shl dataCenterBit)
    private val maxMachineNum = -1L xor (-1L shl machineBit)
    private val maxSequence = -1L xor (-1L shl sequenceBit)

    /**
     * 每一部分向左的位移
     */
    private val machineLeft = sequenceBit

    // private val dataCenterLeft = sequenceBit + machineBit
    private val timestampLeft = sequenceBit + machineBit // dataCenterLeft + dataCenterBit

    private var sequence = 0L //序列号
    private var lastStamp = -1L//上一次时间戳

    init {
        // if (dataCenterId > maxDataCenterNum || dataCenterId < 0) {
        //     throw IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0")
        // }
        if (serviceId > maxMachineNum || serviceId < 0) {
            throw IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0")
        }
    }

    /**
     * 生成 自增 唯一 id
     */
    @Synchronized
    override fun getNextSequenceId(): Long {
        var currStamp = getNewStamp()
        if (currStamp < lastStamp) {
            throw RuntimeException("Clock moved backwards.  Refusing to generate id")
        }

        if (currStamp == lastStamp) {
            //相同毫秒内，序列号自增
            sequence = sequence + 1 and maxSequence
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStamp = getNextMill()
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L
        }

        lastStamp = currStamp

        // 时间一般是 41 位
        return (currStamp - startStamp shl timestampLeft //时间戳部分
                // or (dataCenterId shl dataCenterLeft       //数据中心部分
                // )
                or (serviceId shl machineLeft             //机器标识部分
                )
                or sequence) and intercept               //序列号部分
    }

    private fun getNextMill(): Long {
        var mill = getNewStamp()
        while (mill <= lastStamp) {
            mill = getNewStamp()
        }
        return mill
    }

    private fun getNewStamp(): Long {
        return System.currentTimeMillis()
    }
}