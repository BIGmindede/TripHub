package triphub.media.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String objectName;
    private int chunkNumber;
    private boolean lastChunk;
}