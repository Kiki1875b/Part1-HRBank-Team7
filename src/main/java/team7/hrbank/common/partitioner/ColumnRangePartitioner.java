package team7.hrbank.common.partitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team7.hrbank.domain.employee.repository.EmployeeRepository;

/**
 * This partitioner divides database records into equally sized partitions based on ID
 * <br>
 * This partitioner is used to process parallel BackupBatch
 * <br>
 * Each Partition is assigned unique ID for identification
 */
@Component
@RequiredArgsConstructor
public class ColumnRangePartitioner implements Partitioner {

  //  private final DataSource dataSource;
  private final EmployeeRepository employeeRepository;


  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> partitions = new HashMap<>();
    long totalCount = employeeRepository.count(); // 전체 데이터 개수를 가져오는 메서드
    long partitionSize = (totalCount + gridSize - 1) / gridSize; // 파티션당 예상 데이터 개수
    long minId = employeeRepository.findMinId();
    long partitionIndex = 0;
    long maximumId = getMaxId();
    while (minId <= maximumId) {
      ExecutionContext context = new ExecutionContext();
      context.put("partitionId", "P" + partitionIndex);

      long maxId = getActualMaxId(minId, partitionSize);

      if (maxId == -1) {
        minId += partitionSize;
        continue; // 해당 파티션은 데이터가 없으므로 건너뜀
      }

      context.put("minId", minId);
      context.put("maxId", maxId);

      partitions.put("partition" + partitionIndex, context);

      minId = maxId + 1;
      partitionIndex++;

      if (partitionIndex >= gridSize) {
        break;
      }
    }

    return partitions;
  }

  /**
   *
   * @param minId minimum id in range
   * @param rangeSize size of each range
   * @return Actual Maximum Id In Range
   */
  private long getActualMaxId(long minId, long rangeSize) {
    Optional<Long> maxId = employeeRepository.findMaxIdBetween(minId, minId + rangeSize);
    if (maxId.isPresent()) {
      return maxId.get();
    } else {
      return -1;
    }
  }

  private long getMaxId() {
    return employeeRepository.findMaxId();
  }
}
