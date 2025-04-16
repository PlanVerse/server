package com.planverse.server.common.config.data

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

class RoutingDataSource(
    writeDataSource: DataSource,
    readDataSourceList: List<DataSource>
) : AbstractRoutingDataSource() {
    private val readKeyList: MutableList<String> = ArrayList()
    private var writeOnly = false
    private var readSize: Int = 0
    private var readIdx = AtomicInteger(0)

    init {
        val targetDataSourceMap: MutableMap<Any, Any> = HashMap()
        targetDataSourceMap["WRITE"] = LazyConnectionDataSourceProxy(writeDataSource)

        if (readDataSourceList.isNotEmpty()) {
            for (i in readDataSourceList.indices) {
                readKeyList.add("READ_$i")
                targetDataSourceMap["READ_$i"] = LazyConnectionDataSourceProxy(readDataSourceList[i])
            }
            readSize = readKeyList.size
        } else {
            this.writeOnly = true
        }

        setTargetDataSources(targetDataSourceMap)
        setDefaultTargetDataSource(targetDataSourceMap["WRITE"]!!)
    }

    override fun determineCurrentLookupKey(): Any {
        if (!writeOnly && TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            val index = readIdx.getAndIncrement() % readSize
            return readKeyList[index]
        }
        return "WRITE"
    }
}