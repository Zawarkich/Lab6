package wikisearch.wiki_search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wikisearch.wiki_search.cache.SimpleCache;
import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import wikisearch.wiki_search.repository.WikiArticleRepository;
import java.util.List;

@Service
public class SearchHistoryService {
    private final SearchHistoryRepository historyRepo;
    private final WikiArticleRepository articleRepo;
    private final SimpleCache cache;

    @Autowired
    public SearchHistoryService(SearchHistoryRepository historyRepo, WikiArticleRepository articleRepo, SimpleCache cache) {
        this.historyRepo = historyRepo;
        this.articleRepo = articleRepo;
        this.cache = cache;
    }

    @Transactional
    public SearchHistory saveHistoryWithArticles(SearchHistory history) {
        history.getArticles().forEach(article -> article.setHistory(history));
        SearchHistory saved = historyRepo.save(history);
        cache.put("history_" + saved.getId(), saved);
        return saved;
    }

    @SuppressWarnings("unchecked")
    public List<SearchHistory> getAllHistories() {
        String key = "all_histories";
        List<SearchHistory> cached = (List<SearchHistory>) cache.get(key);
        if (cached != null) {
            return cached;
        }
        List<SearchHistory> histories = historyRepo.findAll();
        cache.put(key, histories);
        return histories;
    }

    @Transactional
    public List<WikiArticleDto> findByTermAndSaveHistory(String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("term:" + term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticle> articles = articleRepo.findByTerm(term);
        if (!articles.isEmpty()) {
            SearchHistory history = historyRepo.findBySearchTerm(term);
            if (history == null) {
                history = new SearchHistory();
                history.setSearchTerm(term);
                history.setTimestamp(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            for (WikiArticle article : articles) {
                article.setHistory(history);
            }
            history.setArticles(articles);
            historyRepo.save(history);
        }
        List<WikiArticleDto> result = articles.stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(java.util.stream.Collectors.toList());
        cache.put("term:" + term, result);
        return result;
    }
}
