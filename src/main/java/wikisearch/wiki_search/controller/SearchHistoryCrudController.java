package wikisearch.wiki_search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import wikisearch.wiki_search.service.RequestCounterService;

import java.util.List;

@RestController
@RequestMapping("/api/histories")
public class SearchHistoryCrudController {
    private final SearchHistoryRepository historyRepo;

    @Autowired
    private RequestCounterService requestCounterService;

    public SearchHistoryCrudController(SearchHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    @GetMapping
    public List<SearchHistory> getAll() {
        requestCounterService.incrementOnAnyRequest();
        return historyRepo.findAll();
    }

    @GetMapping("/by-history-term")
    public List<SearchHistory> getByTerm(@RequestParam String term) {
        requestCounterService.incrementOnAnyRequest();
        return historyRepo.findAll().stream()
            .filter(h -> h.getSearchTerm() != null && h.getSearchTerm().contains(term))
            .toList();
    }

    @GetMapping("/{id}")
    public SearchHistory getById(@PathVariable Long id) {
        requestCounterService.incrementOnAnyRequest();
        return historyRepo.findById(id).orElse(null);
    }

    @PostMapping
    public SearchHistory create(@RequestBody SearchHistory history) {
        requestCounterService.incrementOnAnyRequest();
        return historyRepo.save(history);
    }

    @PutMapping("/{id}")
    public SearchHistory update(@PathVariable Long id, @RequestBody SearchHistory history) {
        requestCounterService.incrementOnAnyRequest();
        history.setId(id);
        return historyRepo.save(history);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        requestCounterService.incrementOnAnyRequest();
        historyRepo.deleteById(id);
    }
}
