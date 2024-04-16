package hhplus.concert.domain.concert.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class Concert {

    private Long concertId;
    private String title;
    private String organizer;
    private List<ConcertOption> concertOptions;

    @Builder
    private Concert(Long id, String title, String organizer, @Singular List<ConcertOption> concertOptions) {
        this.concertId = id;
        this.title = title;
        this.organizer = organizer;
        this.concertOptions = concertOptions;
    }
}
