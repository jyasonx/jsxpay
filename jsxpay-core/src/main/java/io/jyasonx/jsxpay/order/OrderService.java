package io.jyasonx.jsxpay.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderService {

    public static void main(String... args) {
        System.out.println(new CreateOrderCommand("123"));
    }
}
