package yoyo.inventory.constants;

public final class ErrorCode {
    public final static String SUCCESS = "SUC-00001";
    public final static String GENERAL_ERROR = "ERR-00002";
    public final static String BAD_REQUEST = "ERR-00001";
    public final static String INTERNAL_SERVER_ERROR = "ERR-500";
    public final static String NAME_ALREADY_USE = "ERR-003";
    public final static String USER_NOT_PERMISSION = "ERR-004";
    public final static String USER_UNAUTHORIZED = "ERR-401";
    public final static String INVALID_REQ_ERROR = "ERR-001";
    public final static String ALREADY_EXIST_LOGIN_ID = "ERR-0001";
    public final static String ACCOUNT_NOT_FOUND = "ERR-0002";
    public final static String CONFIRM_PASSWORD_NOT_MATCH = "ERR-0003";
    public final static String CONFIRM_CURRENT_PASSWORD_NOT_MATCH = "ERR-0004";
    public final static String ACCOUNT_NOT_ENOUGH_BALANCE = "ERR-0005";
    public final static String CREATE_ACCOUNT_SUCCESS = "SUC-001";
    public final static String UPDATE_ACCOUNT_SUCCESS = "SUC-002";
    public final static String CHANGE_PASSWORD_ACCOUNT_SUCCESS = "SUC-003";
    public final static String CREATE_DATA_SUCCESS = "SUC-003";
    public final static String UPDATE_DATA_SUCCESS = "SUC-004";
    public final static String DELETE_DATA_SUCCESS = "SUC-005";
    public final static String UPDATE_PASSWORD_SUCCESS = "SUC-006";

    public final static String USERNAME_ALREADY_EXIST = "ERR-0006";
    public final static String EMAIL_ALREADY_EXIST = "ERR-0007";
    public final static String PHONE_ALREADY_EXIST = "ERR-0008";
    public static final String UNAUTHORIZED = "ERR-00003";
    public static final String LOGIN_SUCCESS = "ERR-00007";
}
