package triphub.media.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("media_metadata")
public class MediaMetadata {
    @Id
    private UUID id;
    private UUID authorId;
    private String mediaUrl;
    private UUID tripId;
    private Boolean isOpenedForPublish;
    private String geodata;
    private String description;
    private LocalDateTime createdAt;
    private String contentType;
    private Long fileSize;
}