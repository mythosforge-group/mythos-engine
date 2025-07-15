package mythosforge.chronicle_architect.service;

import jakarta.persistence.EntityNotFoundException;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.chronicle_architect.controller.BookResponseDTO;
import mythosforge.chronicle_architect.models.Book;
import mythosforge.chronicle_architect.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChronicleArchitectService {

    private final BookRepository bookRepository;
    private final PersistencePort persistencePort;

    public ChronicleArchitectService(BookRepository bookRepository, PersistencePort persistencePort) {
        this.bookRepository = bookRepository;
        this.persistencePort = persistencePort;
    }

    public Book createBook(Book book) {
        Entity frameworkEntity = new Entity("Book");
        syncPropertiesToFramework(book, frameworkEntity);
        persistencePort.save(frameworkEntity);
        book.setEntityId(frameworkEntity.getId());
        return bookRepository.save(book);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + id));
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public List<BookResponseDTO> findAllBooksAsDTO() {
        return bookRepository.findAll().stream()
            .map(BookResponseDTO::new)
            .collect(Collectors.toList());
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = findBookById(id);
        Entity frameworkEntity = persistencePort.findById(existingBook.getEntityId())
                .orElseThrow(() -> new IllegalStateException("Framework entity not found for book: " + id));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setDescription(bookDetails.getDescription());

        syncPropertiesToFramework(existingBook, frameworkEntity);
        persistencePort.save(frameworkEntity);

        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        Book bookToDelete = findBookById(id);
        persistencePort.deleteById(bookToDelete.getEntityId());
        bookRepository.deleteById(id);
    }

    private void syncPropertiesToFramework(Book book, Entity entity) {
        entity.addProperty("title", book.getTitle());
        entity.addProperty("description", book.getDescription());
    }
}