package mythosforge.chronicle_architect.controller;

import lombok.Data;
import mythosforge.chronicle_architect.models.Book;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookResponseDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> chapterTitles;

    public BookResponseDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.description = book.getDescription();
        this.chapterTitles = book.getChapters().stream()
                                 .map(chapter -> chapter.getTitle())
                                 .collect(Collectors.toList());
    }
}