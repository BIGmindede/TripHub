package triphub.trip.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triphub.trip.models.Trip;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripStatisticsDTO {
    private long totalTrips;                    // Всего путешествий
    private long uniqueDestinations;            // Уникальных мест
    private Trip longestTrip;                   // Самое долгое путешествие
    private long totalDaysInTrips;              // Всего дней в путешествиях
    private Trip lastTrip;                      // Последняя поездка
}
