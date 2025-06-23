package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.service.SearchHistoryService;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final SearchHistoryService searchHistoryService;

    public HistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping
    public SearchHistory create(@RequestBody SearchHistory history) {
        return searchHistoryService.saveHistoryWithArticles(history);
    }

    @GetMapping
    public List<SearchHistory> getAll() {
        return searchHistoryService.getAllHistories();
    }
}