package com.ItsAZZA.LaunchPads;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LaunchCache {
    private static final Cache<UUID, Long> launches = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public static void put(UUID uuid, long time) {
        launches.put(uuid, System.currentTimeMillis() + time);
    }

    public static boolean check(UUID uuid) {
        if (launches.getIfPresent(uuid) == null)
            return true;
        if (launches.getIfPresent(uuid) > System.currentTimeMillis())
            return false;
        launches.invalidate(uuid);
        return true;
    }
}
