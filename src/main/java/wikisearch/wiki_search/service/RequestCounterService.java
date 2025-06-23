package wikisearch.wiki_search.service;

import org.springframework.stereotype.Service;

@Service
public class RequestCounterService {
    private int count = 0;

    public synchronized void increment(int n) {
        count += n;
    }

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
