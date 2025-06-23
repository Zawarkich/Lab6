package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import java.util.List;

@RestController
@RequestMapping("/api/histories")
public class SearchHistoryCrudController {
    private final SearchHistoryRepository historyRepo;

    public SearchHistoryCrudController(SearchHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    @GetMapping
    public List<SearchHistory> getAll() {
        return historyRepo.findAll();
    }

    @GetMapping("/by-history-term")
    public List<SearchHistory> getByTerm(@RequestParam String term) {
        return historyRepo.findAll().stream()
            .filter(h -> h.getSearchTerm() != null && h.getSearchTerm().contains(term))
            .toList();
    }

    @GetMapping("/{id}")
    public SearchHistory getById(@PathVariable Long id) {
        return historyRepo.findById(id).orElse(null);
    }

    @PostMapping
    public SearchHistory create(@RequestBody SearchHistory history) {
        return historyRepo.save(history);
    }

    @PutMapping("/{id}")
    public SearchHistory update(@PathVariable Long id, @RequestBody SearchHistory history) {
        history.setId(id);
        return historyRepo.save(history);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        historyRepo.deleteById(id);
    }
}
