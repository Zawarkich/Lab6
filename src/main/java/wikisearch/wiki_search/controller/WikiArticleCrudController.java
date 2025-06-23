package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.WikiArticleRepository;
import wikisearch.wiki_search.cache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.service.WikiArticleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class WikiArticleCrudController {
    private final WikiArticleRepository articleRepo;
    private final SimpleCache cache;
    private final SearchHistoryRepository historyRepo;
    private final WikiArticleService articleService;

    @Autowired
    public WikiArticleCrudController(WikiArticleRepository articleRepo, SimpleCache cache, SearchHistoryRepository historyRepo, WikiArticleService articleService) {
        this.articleRepo = articleRepo;
        this.cache = cache;
        this.historyRepo = historyRepo;
        this.articleService = articleService;
    }

    @GetMapping
    public List<WikiArticleDto> getAll() {
        return articleRepo.findAll().stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id:\\d+}")
    public WikiArticleDto getById(@PathVariable Long id) {
        return articleRepo.findById(id)
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .orElse(null);
    }

    @PostMapping
    public WikiArticleDto create(@RequestBody WikiArticle article) {
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @PostMapping("/bulk")
    public List<WikiArticleDto> createBulk(@RequestBody List<WikiArticle> articles) {
        return articleService.createArticlesBulk(articles);
    }

    @PutMapping("/{id:\\d+}")
    public WikiArticleDto update(@PathVariable Long id, @RequestBody WikiArticle article) {
        article.setId(id);
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable Long id) {
        articleRepo.deleteById(id);
    }

    @Transactional
    @GetMapping("/by-term")
    public List<WikiArticleDto> getByTerm(@RequestParam String term) {
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
                history.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            for (WikiArticle article : articles) {
                article.setHistory(history);
            }
            history.setArticles(articles);
            historyRepo.save(history);
        }
        List<WikiArticleDto> result = articles.stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
        cache.put("term:" + term, result);
        return result;
    }
}
