package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public enum IdType {
    ID_CARD("身份证"),
    PASSPORT("护照"),
    RESIDENCE_BOOKLET("户口簿"),
    ARMY_ID_CARD("军官证"),
    POLICE_ID_CARD("警官证"),
    SOLDIER_ID_CARD("士兵证"),
    ALIEN_RESIDENCE_PERMIT("外国人居留证"),

    /**
     * Mainland Travel Permit for Hong Kong and Macao Residents.
     */
    MTP_HK_MACAO("港澳居民来往内地通行证"),

    /**
     * Mainland Travel Permit for Taiwan Residents.
     */
    MTP_TAIWAN("台湾同胞来往内地通行证"),
    ;

    private String name;

    IdType(String name) {
        this.name = name;
    }
}
