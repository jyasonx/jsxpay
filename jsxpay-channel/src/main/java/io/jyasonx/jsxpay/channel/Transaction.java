package io.jyasonx.jsxpay.channel;

import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.common.IdType;
import io.jyasonx.jsxpay.common.TransactionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@Setter
@ToString(exclude = {"cvv2", "validThru"})
@NoArgsConstructor
public class Transaction {
    private String serialNo;

    private String channelNo;
    private ChannelType channelType;
    private String channelSerialNo;
    private String thirdpartySerialNo;

    private String bankAcronym;
    private String bankCode;
    private String bankAccountNo;
    private String bankAccountName;
    private String bankReservedPhone;
    private String cvv2;
    private String validThru;
    private String smsPinCode;
    private String idNo;
    private IdType idType;
    private String branchName;
    private String branchProvince;
    private String branchCityCode;

    private BigDecimal amount;

    private LocalDateTime completedTime;
    private LocalDate settlementDate;
    private TransactionStatus status;

    private String code;
    private String message;

}