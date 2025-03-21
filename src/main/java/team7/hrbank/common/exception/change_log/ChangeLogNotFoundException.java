package team7.hrbank.common.exception.change_log;

import java.util.NoSuchElementException;

public class ChangeLogNotFoundException extends NoSuchElementException {
  public ChangeLogNotFoundException() {
    super("수정 이력 로그를 찾을 수 없습니다.");
  }
}
