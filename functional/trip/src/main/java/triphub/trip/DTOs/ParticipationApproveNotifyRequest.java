package triphub.trip.DTOs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipationApproveNotifyRequest {
    private String title;
    private String text;
    private List<Action> actions;
    
    public record Action(String text, String url, String type) {}

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", this.title);
        map.put("text", this.text);
        
        map.put("actions", this.actions.stream()
            .map(action -> Map.of(
                "text", action.text(),
                "url", action.url(),
                "type", action.type()
            ))
            .collect(Collectors.toList()));
        
        return map;
    }
}
