//package team7.hrbank.domain.department.dto;
//
//import lombok.Builder;
//import lombok.Getter;
//import org.springframework.util.StringUtils;
//
//@Builder
//public record RecordBuild(
//        // 여기다가 조건 및 get메서드 재정의해서 default값 정의
//        String nameOrDescription,
//        Integer idAfter, // 이전 페이지 마지막 요소 id
//        String cursor, // 커서 (이전 페이지 마지막 요소 값)
//        Integer size, // 페이지 사이즈(기본값 10)
//        String sortedField, // 정렬 필드(name or establishmentDate)
//        // 정렬 방향(asc or desc, 기본값은 asc)
//        String sortDirection) {
//    public RecordBuild {
//        if (size == null) {
//            size = 10;
//        }
////        if (!StringUtils.hasText(sortedField) || (!sortedField.equals("name"))) {
////            sortedField = "name";
////        }
////        if (!StringUtils.hasText(sortDirection)) {
////            sortDirection = "asc";
////        }
//    }
//}
