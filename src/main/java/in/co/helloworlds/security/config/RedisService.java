package in.co.helloworlds.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Method to set a key-value pair in Redis
    public void setValue(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.info("Value set in Redis for key: {}", key);
        } catch (Exception e) {
            log.error("Error setting value in Redis for key: {}", key, e);
        }
    }

    // Method to set a key-value pair in Redis with expiration time
    public void setValueWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.info("Value set in Redis for key: {} with expiration time {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting value in Redis for key: {}", key, e);
        }
    }

    // Method to get value from Redis for a given key
    public Object getValue(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.info("Retrieved value from Redis for key: {} : {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Error getting value from Redis for key: {}", key, e);
            return null;
        }
    }

    // Method to delete a key from Redis
    public void deleteKey(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Key deleted from Redis: {}", key);
        } catch (Exception e) {
            log.error("Error deleting key from Redis: {}", key, e);
        }
    }
}
