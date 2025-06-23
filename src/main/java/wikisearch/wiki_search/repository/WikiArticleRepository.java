package wikisearch.wiki_search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wikisearch.wiki_search.entity.WikiArticle;

import java.util.List;

public interface WikiArticleRepository extends JpaRepository<WikiArticle, Long> {
    @Query("SELECT a FROM WikiArticle a WHERE a.title LIKE %:term%")
    List<WikiArticle> findByTerm(@Param("term") String term);
}