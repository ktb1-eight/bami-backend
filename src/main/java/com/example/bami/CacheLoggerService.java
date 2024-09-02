package com.example.bami;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheLoggerService {

    private final CacheManager cacheManager;

    public void cacheAll() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            System.out.println("cacheName = " + cacheName);
        }

        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            System.out.println(cacheName);
            if (cache != null) {
                ConcurrentHashMap nativeCache = (ConcurrentHashMap) cache.getNativeCache();
                System.out.println("nativeCache = " + nativeCache);
                System.out.println("nativeCache type = " + nativeCache.getClass().getName() );
                ConcurrentHashMap cacheMap = nativeCache;
                cacheMap.forEach((strKey, strValue)->{
                    System.out.println( strKey +" : "+ strValue );
                });
            }
        }

    }
}
