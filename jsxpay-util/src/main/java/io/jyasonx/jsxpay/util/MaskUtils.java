package io.jyasonx.jsxpay.util;

public class MaskUtils {
    private static final String CARD_NUM_MASKER = " **** **** ";
    private static final String PHONE_MASKER = "****";
    private static final String ID_NO_MASKER = "*******";
    private static final String MASKER = "*";

    private static final int LEN_MOBILE = 11;
    private static final int MIN_LEN_ID_NO = 15;
    private static final int MIN_LEN_CARD_NO = 8;
    private static final int NORMAL_LEN_CARD_NO = 16;
    private static final int MIN_LEN_NAME = 2;

    private static final String CARD_NUM_REGEX = "bankAccountNo=(\\d{4})\\d+(\\d{4})";
    private static final String CARD_NUM_REPLACEMENT = "bankAccountNo=$1 **** **** $2";


    /**
     * mobile: 13761812345 -> 137****2345.
     */
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < LEN_MOBILE) {
            return mobile;
        }
        return mobile.substring(0, 3) + PHONE_MASKER + mobile.substring(mobile.length() - 4);
    }

    /**
     * cardNum：6226123412345678 -> 6226****5678 .
     */
    public static String maskCardNum(String cardNum) {
        return maskCardNum(cardNum, CARD_NUM_MASKER);
    }

    /**
     * cardNum：6226123412345678 -> 6226****5678 .
     */
    private static String maskCardNum(String cardNum,
                                      String maskStr) {
        if (cardNum == null || cardNum.length() < MIN_LEN_CARD_NO) {
            return cardNum;
        }
        int length = cardNum.length();

        if (length < NORMAL_LEN_CARD_NO) {
            maskStr = genMask(length - MIN_LEN_CARD_NO);
        }

        return cardNum.substring(0, 4)
                + maskStr
                + cardNum.substring(cardNum.length() - 4);
    }

    /**
     * idNo: 310702198611165130 -> 310*******5130.
     */
    public static String maskIdNo(String idNo) {
        if (idNo == null || idNo.length() < MIN_LEN_ID_NO) {
            return idNo;
        }
        return idNo.substring(0, 3) + ID_NO_MASKER + idNo.substring(idNo.length() - 4);
    }

    /**
     * name: 张飞 -> 张*,张翼德 -> 张**.
     */
    public static String maskName(String name) {
        if (name == null || name.length() < MIN_LEN_NAME) {
            return name;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name, 0, 1);
        for (int i = 0; i < name.length() - 1; i++) {
            sb.append(MASKER);
        }
        return sb.toString();
    }

    /**
     * 对ToString中的卡号字段加掩码.
     */
    public static String maskCardNum4Str(String str) {
        // bankAccountType=PRIVATE,  bankAccountNo=6666666666666666,->
        // bankAccountType=PRIVATE,  bankAccountNo=6666 **** **** 6666,
        return str.replaceAll(CARD_NUM_REGEX, CARD_NUM_REPLACEMENT);
    }

    private static String genMask(int length) {
        StringBuilder mask = new StringBuilder();
        while (mask.length() < length) {
            mask.append(MASKER);
        }
        return mask.toString();
    }
}
