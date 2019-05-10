package io.jyasonx.jsxpay.trade.query;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.common.OrderStatus;
import io.jyasonx.jsxpay.common.PaymentType;
import io.jyasonx.jsxpay.common.TransactionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "jp_order",
        indexes = {
                @Index(columnList = "merchant_no, merchant_order_no"),
                @Index(columnList = "status, created_time"),
                @Index(columnList = "payment_type, transaction_type"),
                @Index(columnList = "user_id"),
                @Index(columnList = "channel_type")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_no"})
)
@ApiModel("订单")
@EqualsAndHashCode(of = {"orderNo"})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Order {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Version
    @JsonIgnore
    private Long version;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_no", length = 32, nullable = false)
    private String merchantNo;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_no", length = 64, nullable = false)
    private String merchantOrderNo;

    @ApiModelProperty(value = "订单号")
    @Column(name = "order_no", length = 64, nullable = false)
    private String orderNo;

    @ApiModelProperty(value = "原订单号")
    @Column(name = "origin_order_no", length = 64)
    private String originOrderNo;

    @ApiModelProperty(value = "订单状态")
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private OrderStatus status;

    @ApiModelProperty(value = "支付类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 16, nullable = false)
    private PaymentType paymentType;

    @ApiModelProperty(value = "交易类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 16, nullable = false)
    private TransactionType transactionType;

    @ApiModelProperty(value = "渠道类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", length = 16)
    private ChannelType channelType;

    @ApiModelProperty(value = "创建时间")
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @ApiModelProperty(value = "执行时间")
    @Column(name = "executed_time")
    private LocalDateTime executedTime;

    @ApiModelProperty(value = "完成时间")
    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @ApiModelProperty(value = "超时时间")
    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @ApiModelProperty(value = "结算日期")
    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @ApiModelProperty(value = "用户ID")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "order_amount", nullable = false)
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "账户金额")
    @Column(name = "account_amount", nullable = false)
    private BigDecimal accountAmount;

    @ApiModelProperty(value = "支付金额")
    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    @ApiModelProperty(value = "已支付金额")
    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "首付金额")
    @Column(name = "down_payment_amount", nullable = false)
    private BigDecimal downPaymentAmount;

    @ApiModelProperty(value = "分期金额")
    @Column(name = "installment_amount", nullable = false)
    private BigDecimal installmentAmount;

    @ApiModelProperty(value = "冻结金额")
    @Column(name = "freeze_amount", nullable = false)
    private BigDecimal freezeAmount;

    @ApiModelProperty(value = "退款金额")
    @Column(name = "refunded_amount", nullable = false)
    private BigDecimal refundedAmount;

    @ApiModelProperty(value = "分期期数")
    @Column(name = "number_of_installments")
    private Integer numberOfInstallments;

    @ApiModelProperty(value = "返回地址")
    @Column(name = "return_url", length = 2048)
    private String returnUrl;

    @ApiModelProperty(value = "回调地址")
    @Column(name = "callback_url", length = 2048)
    private String callbackUrl;

    @ApiModelProperty(value = "备注")
    @Column(name = "remark", length = 2048)
    private String remark;

    @ApiModelProperty(value = "响应码")
    @Column(name = "response_code", length = 64)
    private String responseCode;

    @ApiModelProperty(value = "响应消息")
    @Column(name = "response_message", length = 2048)
    private String responseMessage;

    @ApiModelProperty(value = "渠道响应码")
    @Column(name = "channel_response_code", length = 64)
    private String channelResponseCode;

    @ApiModelProperty(value = "渠道响应消息")
    @Column(name = "channel_response_message", length = 2048)
    private String channelResponseMessage;

}
