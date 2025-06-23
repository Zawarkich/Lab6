package wikisearch.wiki_search.controller;

import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.service.WikiArticleService;
import wikisearch.wiki_search.service.RequestCounterService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WikiController {
    private final WikiArticleService wikiArticleService;
    private final RequestCounterService requestCounterService;

    @Autowired
    public WikiController(WikiArticleService wikiArticleService, RequestCounterService requestCounterService) {
        this.wikiArticleService = wikiArticleService;
        this.requestCounterService = requestCounterService;
    }

    @GetMapping("/search")
    public WikiArticle search(@RequestParam String term) {
        if ("Java".equalsIgnoreCase(term)) {
            requestCounterService.increment();
        }
        return wikiArticleService.search(term);
    }

    @PostMapping("/search/bulk")
    public List<WikiArticle> bulkSearch(@RequestBody List<BulkSearchRequest> requests) {
        int javaCount = (int) requests.stream().filter(r -> r.getTitle() != null && r.getTitle().toLowerCase().contains("java")).count();
        if (javaCount > 0) {
            requestCounterService.increment(javaCount);
        }
        return requests.stream().map(r -> wikiArticleService.search(r.getTitle())).toList();
    }

    @GetMapping("/search/java-count")
    public int getJavaSearchCount() {
        return requestCounterService.getCount();
    }

    public static class BulkSearchRequest {
        private String title;
        private String content;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}