package team7.hrbank.common.exception.change_log;

public class ChangeLogNoDetectedException extends IllegalStateException {
  public ChangeLogNoDetectedException() {
    super("변경된 사항이 없습니다.");
  }
}
