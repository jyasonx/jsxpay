package io.jyasonx.jsxpay.trade.api


data class CreateOrderCommand(val orderNo: String) {

    lateinit var merchantOrderNo: String
}