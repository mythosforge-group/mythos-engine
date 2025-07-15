package mythosforge.chronicle_architect.repository;

import mythosforge.chronicle_architect.model.BookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookChapterRepository extends JpaRepository<BookChapter, Long> {}