package triphub.media.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaMetadataDto {
    private UUID authorId;
    private UUID tripId;
    private Boolean isOpenedForPublish;
    private String geodata;
    private String description;
    private String contentType;
    private Long fileSize;
}