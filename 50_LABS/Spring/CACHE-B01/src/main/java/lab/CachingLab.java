package lab;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class CachingLab {

    private CachingLab() {
    }

    public static void main(String[] args) {
        runCaffeineExperiment();

        if (Boolean.parseBoolean(System.getenv("RUN_REDIS"))) {
            runRedisExperiment();
        } else {
            System.out.println("\nRedis experiment skipped.");
            System.out.println("Run: docker compose up -d redis");
            System.out.println("Then: RUN_REDIS=true mvn clean compile exec:java");
        }
    }

    private static void runCaffeineExperiment() {
        System.out.println("\n=== CAFFEINE LOCAL CACHE ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CaffeineConfiguration.class)) {

            ProductCatalog catalog = context.getBean(ProductCatalog.class);
            CountingProductRepository repository =
                    context.getBean(CountingProductRepository.class);
            CacheManager cacheManager = context.getBean(CacheManager.class);

            ProductDto first = catalog.find(1L);
            ProductDto second = catalog.find(1L);

            System.out.println("first  = " + first);
            System.out.println("second = " + second);
            System.out.println("repository loads after two external reads = " +
                    repository.loadCount());

            System.out.println("\n-- self invocation bypasses cache advice --");
            int beforeSelf = repository.loadCount();
            catalog.findTwiceInternally(2L);
            System.out.println("repository loads caused by internal pair = " +
                    (repository.loadCount() - beforeSelf));

            System.out.println("\n-- @CachePut updates the cache --");
            ProductDto updated = catalog.update(
                    new ProductDto(1L, "Laptop Pro", new BigDecimal("1999.00"))
            );
            ProductDto afterUpdate = catalog.find(1L);
            System.out.println("updated     = " + updated);
            System.out.println("afterUpdate = " + afterUpdate);
            System.out.println("repository loads unchanged after cached read = " +
                    repository.loadCount());

            System.out.println("\n-- @CacheEvict forces the next read to load --");
            int beforeEvictRead = repository.loadCount();
            catalog.evict(1L);
            catalog.find(1L);
            System.out.println("repository loads after eviction = " +
                    (repository.loadCount() - beforeEvictRead));

            printCaffeineStats(cacheManager, "productById");
        }
    }

    private static void runRedisExperiment() {
        System.out.println("\n=== REDIS SHARED CACHE ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(RedisConfiguration.class)) {

            ProductCatalog catalog = context.getBean(ProductCatalog.class);
            CountingProductRepository repository =
                    context.getBean(CountingProductRepository.class);
            CacheManager cacheManager = context.getBean(CacheManager.class);

            Cache cache = requiredCache(cacheManager, "productById");
            cache.clear();

            ProductDto first = catalog.find(42L);
            ProductDto second = catalog.find(42L);

            System.out.println("first  = " + first);
            System.out.println("second = " + second);
            System.out.println("repository loads = " + repository.loadCount());
            System.out.println("Redis key prefix = lab:v1:productById::");
            System.out.println("Redis value serializer = JSON");
            System.out.println("Redis TTL for productById = 5 minutes");
        } catch (RuntimeException error) {
            System.out.println("Redis experiment failed: " +
                    error.getClass().getSimpleName() + ": " + error.getMessage());
            System.out.println("Verify that Redis is running on localhost:6379.");
        }
    }

    private static void printCaffeineStats(
            CacheManager cacheManager,
            String cacheName
    ) {
        Cache springCache = requiredCache(cacheManager, cacheName);
        CaffeineCache caffeineCache = (CaffeineCache) springCache;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                caffeineCache.getNativeCache();
        CacheStats stats = nativeCache.stats();

        System.out.println("\nCaffeine estimated size = " + nativeCache.estimatedSize());
        System.out.println("Caffeine hit count      = " + stats.hitCount());
        System.out.println("Caffeine miss count     = " + stats.missCount());
        System.out.println("Caffeine hit rate       = " + stats.hitRate());
        System.out.println("Caffeine eviction count = " + stats.evictionCount());
    }

    private static Cache requiredCache(
            CacheManager cacheManager,
            String cacheName
    ) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache not found: " + cacheName);
        }
        return cache;
    }
}

interface ProductCatalog {
    ProductDto find(Long id);

    ProductDto update(ProductDto product);

    void evict(Long id);

    void findTwiceInternally(Long id);
}

class CachedProductCatalog implements ProductCatalog {
    private final CountingProductRepository repository;

    CachedProductCatalog(CountingProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(
            cacheNames = "productById",
            key = "#id",
            unless = "#result == null",
            sync = true
    )
    public ProductDto find(Long id) {
        System.out.println("repository load for product " + id);
        return repository.load(id);
    }

    @Override
    @CachePut(cacheNames = "productById", key = "#result.id")
    public ProductDto update(ProductDto product) {
        System.out.println("repository update for product " + product.getId());
        return repository.save(product);
    }

    @Override
    @CacheEvict(cacheNames = "productById", key = "#id")
    public void evict(Long id) {
        System.out.println("cache eviction requested for product " + id);
    }

    @Override
    public void findTwiceInternally(Long id) {
        find(id);
        find(id);
    }
}

class CountingProductRepository {
    private final AtomicInteger loads = new AtomicInteger();
    private final Map<Long, ProductDto> storage = new HashMap<Long, ProductDto>();

    CountingProductRepository() {
        storage.put(
                1L,
                new ProductDto(1L, "Laptop", new BigDecimal("1499.00"))
        );
        storage.put(
                2L,
                new ProductDto(2L, "Monitor", new BigDecimal("499.00"))
        );
        storage.put(
                42L,
                new ProductDto(42L, "Redis Product", new BigDecimal("42.00"))
        );
    }

    ProductDto load(Long id) {
        loads.incrementAndGet();
        ProductDto value = storage.get(id);
        return value == null ? null : value.copy();
    }

    ProductDto save(ProductDto product) {
        storage.put(product.getId(), product.copy());
        return product.copy();
    }

    int loadCount() {
        return loads.get();
    }
}

@Configuration
@EnableCaching(proxyTargetClass = false)
class CaffeineConfiguration {

    @Bean
    CountingProductRepository productRepository() {
        return new CountingProductRepository();
    }

    @Bean
    ProductCatalog productCatalog(CountingProductRepository repository) {
        return new CachedProductCatalog(repository);
    }

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager manager =
                new CaffeineCacheManager("productById");

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1_000)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .recordStats()
        );
        manager.setAllowNullValues(false);
        return manager;
    }
}

@Configuration
@EnableCaching(proxyTargetClass = false)
class RedisConfiguration {

    @Bean
    CountingProductRepository productRepository() {
        return new CountingProductRepository();
    }

    @Bean
    ProductCatalog productCatalog(CountingProductRepository repository) {
        return new CachedProductCatalog(repository);
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer json =
                new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration defaults =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .computePrefixWith(
                                cacheName -> "lab:v1:" + cacheName + "::"
                        )
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(json)
                        );

        Map<String, RedisCacheConfiguration> caches =
                new HashMap<String, RedisCacheConfiguration>();
        caches.put(
                "productById",
                defaults.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(caches)
                .transactionAware()
                .build();
    }
}

class ProductDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal price;

    public ProductDto() {
    }

    ProductDto(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    ProductDto copy() {
        return new ProductDto(id, name, price);
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
