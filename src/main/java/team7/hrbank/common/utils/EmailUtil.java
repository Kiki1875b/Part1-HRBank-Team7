package team7.hrbank.common.utils;

public class EmailUtil {

    // 이메일 정규식
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // 이메일 유효성 검사
    public static String emailValidation(String email) {
        if (email.matches(EMAIL_REGEX)) {
            return email;
        }

        throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
    }
}
