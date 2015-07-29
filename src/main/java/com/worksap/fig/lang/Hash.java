package com.worksap.fig.lang;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Elegant supplement for Map in JDK
 */
public interface Hash<K, V> extends Map<K, V> {
    default boolean containsAny(BiPredicate<K, V> condition) {
        for (Entry<K, V> entry : entrySet()) {
            if (condition.test(entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    default Seq<Entry<K, V>> entrySeq() {
        return Seq.of(entrySet());
    }

    Seq<V> values();

    Seq<K> keys();

    /**
     * Returns a new hash created by using hash’s values as keys, and the keys as values. If there are duplicated values, the last key is kept.
     * Since it is hash map, the order of keys is decided by hash table.
     */
    default Hash<V, K> invert() {
        Hash<V, K> result = newHash();
        forEach((k, v) -> {
            result.put(v, k);
        });
        return result;
    }

    /**
     * Returns a new hash consisting of entries for which the condition returns false.
     */
    default Hash<K, V> reject(BiPredicate<K, V> condition) {
        Hash<K, V> result = newHash();
        forEach((k, v) -> {
            if (!condition.test(k, v)) {
                result.put(k, v);
            }
        });
        return result;
    }

    /**
     * Deletes every key-value pair from hash for which condition evaluates to false.
     */
    default Hash<K, V> filter(BiPredicate<K, V> condition) {
        Hash<K, V> result = newHash();
        forEach((k, v) -> {
            if (condition.test(k, v)) {
                result.put(k, v);
            }
        });
        return result;
    }

    default Hash<K, V> set(K key, V value) {
        put(key, value);
        return this;
    }

    static <K, V> Hash<K, V> newHash() {
        return new HashImpl<>();
    }

    static <K, V> Hash<K, V> of(Map<? extends K, ? extends V> map) {
        return new HashImpl<>(map);
    }

    /**
     * Removes all key-value pairs which satisfy the condition on the hash itself.
     * @param condition the condition used to filter key-value pairs by passing the key and value of the pair,
     *                  returns true if the key-value pair satisfies the condition, otherwise returns false.
     * @return this hash itself with all key-value pairs which satisfy the condition removed
     * @throws NullPointerException if condition is null
     */
    default Hash<K, V> reject$(BiPredicate<K, V> condition) {
        Objects.requireNonNull(condition);
        Seq<K> toBeRemoved = Seq.newSeq();
        this.forEach((k, v) -> {
            if (condition.test(k, v)) {
                toBeRemoved.add(k);
            }
        });
        if (!toBeRemoved.isEmpty()) {
            toBeRemoved.forEach(k -> this.remove(k));
        }
        return this;
    }

    /**
     * Removes all the key-value pairs which don't satisfy the condition on the hash itself.
     * @param condition the condition used to filter key-value pairs by passing the key and value of the pair,
     *                  returns true if the key-value pair satisfies the condition, otherwise returns false.
     * @return this hash itself with all key-value pairs which don't satisfy the condition removed
     * @throws NullPointerException if condition is null
     */
    default Hash<K, V> filter$(BiPredicate<K, V> condition) {
        Objects.requireNonNull(condition);
        Seq<K> toBeRemoved = Seq.newSeq();
        this.forEach((k, v) -> {
            if (!condition.test(k, v)) {
                toBeRemoved.add(k);
            }
        });
        if (!toBeRemoved.isEmpty()) {
            toBeRemoved.forEach(k -> this.remove(k));
        }
        return this;
    }

    /**
     * Returns the keys of the given value.
     * @param value the value
     * @return the collection of keys whose value is the given value
     */
    default Seq<K> getKey(V value) {
        Seq<K> result = Seq.newSeq();
        this.forEach((k, v) -> {
            if (value == null && v == null)
                result.add(k);
            else if(value != null && v != null && v.equals(value))
                result.add(k);
        });
        return result;
    }

    /**
     * Returns a new hash containing the mappings of the specified hash and this hash itself.
     * The value for entries with duplicate keys will be that of the specified hash.
     * @param another the specified hash to be merged
     * @return the new hash containing all the mappings of the specified hash and this hash itself
     */
    default Hash<K, V> merge(Hash<? extends K, ? extends V> another) {
        Hash<K, V> result = Hash.of(this);
        if (another != null)
            result.putAll(another);
        return result;
    }
    /**
     * Copies all of the mappings from the specified hash to this hash.
     * The value for entries with duplicate keys will be that of the specified hash.
     * <p>
     *     Similar to {@link #merge(Hash)}, the difference is that this function takes effect on this hash itself.
     * </p>
     * @param another the specified hash to be merged
     * @return the hash itself containing all the mappings of the specified hash
     */
    default Hash<K, V> merge$(Hash<? extends K, ? extends V> another) {
        if (another != null)
            this.putAll(another);
        return this;
    }
}
