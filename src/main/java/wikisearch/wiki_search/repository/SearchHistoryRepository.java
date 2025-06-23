package wikisearch.wiki_search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import wikisearch.wiki_search.entity.SearchHistory;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    SearchHistory findBySearchTerm(String searchTerm);
}