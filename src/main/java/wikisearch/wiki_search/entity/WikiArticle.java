package wikisearch.wiki_search.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "wiki_articles")
public class WikiArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Lob
    @Column(length = 10000)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private SearchHistory history;

    public WikiArticle() {}

    public WikiArticle(String title, String content) {
        this.title = title;
        this.content = content;
    }
}