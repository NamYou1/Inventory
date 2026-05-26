package yoyo.inventory.utils;

public class StatusMessageUtil {

    public static String onlyStatusCanUpdate(String module, String status) {
        return String.format("Only %s %s can be updated", status, module);
    }

    public static String alreadyCompleted(String module) {
        return String.format("%s already completed", module);
    }

    public static String alreadyCancelled(String module) {
        return String.format("%s already cancelled", module);
    }
}