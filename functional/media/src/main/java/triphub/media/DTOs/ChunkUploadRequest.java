package triphub.media.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChunkUploadRequest {
    @NotBlank
    private String uploadId;
    
    @NotBlank
    private String fileName;
    
    @Min(0)
    private int chunkNumber;
    
    @Min(1)
    private int totalChunks;
}