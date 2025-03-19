package team7.hrbank.domain.binary;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class BinaryController {

    private final LocalBinaryContentStorage localBinaryContentStorage;
    private final BinaryContentService binaryContentService;

    @GetMapping("/api/files/{id}/download")
    public ResponseEntity<Resource> downLoad(@PathVariable Long id) {
        return localBinaryContentStorage.downloadTmp(id, binaryContentService.findFileTypeById(id));

    }
}
