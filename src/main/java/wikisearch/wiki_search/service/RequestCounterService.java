package wikisearch.wiki_search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wikisearch.wiki_search.cache.SimpleCache;

@Service
public class RequestCounterService {
    private int count = 0;
    private final SimpleCache cache;

    @Autowired
    public RequestCounterService(SimpleCache cache) {
        this.cache = cache;
        Object cached = cache.get("request_count");
        if (cached instanceof Integer) {
            this.count = (Integer) cached;
        }
    }

    public synchronized void increment(int n) {
        count += n;
        cache.put("request_count", count);
    }

    public synchronized void increment() {
        count++;
        cache.put("request_count", count);
    }

    public synchronized int getCount() {
        Object cached = cache.get("request_count");
        if (cached instanceof Integer) {
            return (Integer) cached;
        }
        return count;
    }
}
