package wikisearch.wiki_search.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wikisearch.wiki_search.cache.SimpleCache;
import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.WikiArticleRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WikiArticleServiceTest {
    @Mock
    private WikiArticleRepository articleRepo;
    @Mock
    private SimpleCache cache;
    @InjectMocks
    private WikiArticleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new WikiArticleService(articleRepo, cache);
    }

    @Test
    void testCreateArticle() {
        WikiArticle article = new WikiArticle("title", "content");
        WikiArticle saved = new WikiArticle("title", "content");
        saved.setId(1L);
        when(articleRepo.save(article)).thenReturn(saved);
        WikiArticleDto dto = service.createArticle(article);
        assertEquals("title", dto.getTitle());
        assertEquals("content", dto.getContent());
        assertNotNull(dto.getId());
    }

    @Test
    void testCreateArticlesBulk() {
        WikiArticle a1 = new WikiArticle("t1", "c1");
        WikiArticle a2 = new WikiArticle("t2", "c2");
        WikiArticle s1 = new WikiArticle("t1", "c1"); s1.setId(1L);
        WikiArticle s2 = new WikiArticle("t2", "c2"); s2.setId(2L);
        when(articleRepo.save(a1)).thenReturn(s1);
        when(articleRepo.save(a2)).thenReturn(s2);
        List<WikiArticleDto> dtos = service.createArticlesBulk(Arrays.asList(a1, a2));
        assertEquals(2, dtos.size());
        assertEquals("t1", dtos.get(0).getTitle());
        assertEquals("t2", dtos.get(1).getTitle());
    }

    @Test
    void testGetAllArticles() {
        WikiArticle a1 = new WikiArticle("t1", "c1"); a1.setId(1L);
        WikiArticle a2 = new WikiArticle("t2", "c2"); a2.setId(2L);
        when(cache.get("all_articles")).thenReturn(null);
        when(articleRepo.findAll()).thenReturn(Arrays.asList(a1, a2));
        List<WikiArticleDto> dtos = service.getAllArticles();
        assertEquals(2, dtos.size());
    }

    @Test
    void testGetArticleById() {
        WikiArticle a1 = new WikiArticle("t1", "c1"); a1.setId(1L);
        when(cache.get("article:1")).thenReturn(null);
        when(articleRepo.findById(1L)).thenReturn(Optional.of(a1));
        WikiArticleDto dto = service.getArticleById(1L);
        assertEquals("t1", dto.getTitle());
    }

    @Test
    void testUpdateArticle() {
        WikiArticle a1 = new WikiArticle("t1", "c1");
        WikiArticle saved = new WikiArticle("t1", "c1"); saved.setId(1L);
        when(articleRepo.save(a1)).thenReturn(saved);
        WikiArticleDto dto = service.updateArticle(1L, a1);
        assertEquals(1L, dto.getId());
    }

    @Test
    void testDeleteArticle() {
        doNothing().when(articleRepo).deleteById(1L);
        service.deleteArticle(1L);
        verify(articleRepo, times(1)).deleteById(1L);
    }
}
