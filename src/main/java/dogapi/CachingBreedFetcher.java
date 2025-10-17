package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class

    private int callsMade = 0;
    // to store pre-existing maps
    private Map<String, List<String>> cache = new HashMap<>();
    private BreedFetcher fetcher; // here im keeping the class as BreedFetcher, to enusre that the user can construct chachingbreedfetcher using either 2 variants (local or api) such that we query the one the user wants to
    // constructor
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = breed.toLowerCase();

        // If already in cache, no need to query the api, just take locally
        if (cache.containsKey(key)) {
            // Return a shallow copy so callers can’t mutate cached list
            return new ArrayList<>(cache.get(key));
        }

        // Not cached --> need to query the fetcher
        callsMade++; // call made
        try {
            List<String> result = fetcher.getSubBreeds(breed);
            // Cache an immutable copy (so our internal cache can’t be mutated)
            cache.put(key, List.copyOf(result));
            // Return a fresh copy to the caller
            return new ArrayList<>(result);
        } catch (BreedNotFoundException e) {
            // Per requirements: do NOT cache failures
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}