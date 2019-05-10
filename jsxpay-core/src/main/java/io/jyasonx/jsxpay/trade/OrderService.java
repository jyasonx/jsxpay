package io.jyasonx.jsxpay.trade;

import io.jyasonx.jsxpay.trade.api.CreateOrderCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderService {

    public static void main(String... args) {
        CreateOrderCommand command = new CreateOrderCommand("123");
        command.setMerchantOrderNo("hh");
        System.out.println(command.component1());
        System.out.println(command.getOrderNo());
        System.out.println(command);
        System.out.println(command.getMerchantOrderNo());
    }
}
