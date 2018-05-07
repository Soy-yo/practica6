package es.ucm.fdi.util;

import java.util.*;

/**
 * A TreeMap that supports multiple values for the same key, via ArrayLists.
 * <p>
 * Values for the same key will be returned and traversed in order of insertion;
 * that is, newer values with the same key will be stored after any other values
 * with the same key.
 */
public class MultiTreeMap<K, V> extends TreeMap<K, ArrayList<V>> {

  private int valueCount = 0;

  public MultiTreeMap() {
  }

  public MultiTreeMap(Comparator<K> comparator) {
    super(comparator);
  }

  @Override
  public void clear() {
    super.clear();
    valueCount = 0;
  }

  /**
   * Adds a value at the end of the list of values for the specified key.
   *
   * @param key   to add the value under
   * @param value to add
   */
  public void putValue(K key, V value) {
    if (!containsKey(key)) {
      put(key, new ArrayList<>());
    }
    get(key).add(value);
    valueCount++;
  }

  /**
   * Removes the first occurrence of a value from the list found at
   * a given key. Efficiency is O(size-of-that-list)
   *
   * @param key   to look into
   * @param value within the list found at that key to remove. The first
   *              element that is equals to this one will be removed.
   * @return true if removed, false if not found
   */
  public boolean removeValue(K key, V value) {
    if (!containsKey(key)) {
      return false;
    }
    ArrayList<V> bucket = get(key);
    boolean removed = bucket.remove(value);
    if (removed) {
      valueCount--;
    }
    if (bucket.isEmpty()) {
      remove(key);
    }
    return removed;
  }

  /**
   * Returns the total number of values stored in this multimap
   */
  public int sizeOfValues() {
    return valueCount;
  }

  /**
   * Returns the values as a read-only list. Changes to this structure
   * will be immediately reflected in the list.
   */
  public List<V> valuesList() {
    return new InnerList();
  }

  /**
   * Allows iteration by base values.
   *
   * @return iterable values, ordered by key and then by order-of-insertion
   */
  public Iterable<V> innerValues() {
    return InnerIterator::new;
  }

  /**
   * A logical, read-only list containing all elements in correct order. As it is usual to get
   * elements in order, it has an O(1) get() when iterating the list. Otherwise it is O(n) where
   * n is the number of buckets in the map.
   */
  private class InnerList extends AbstractList<V> {

    int start;
    int previousIndex = 0;
    Iterator<ArrayList<V>> it;
    ArrayList<V> current;

    InnerList() {
      resetIterators();
    }

    private void resetIterators() {
      it = values().iterator();
      if (it.hasNext()) {
        start = 0;
        current = it.next();
      }
    }

    @Override
    public V get(int index) {
      if (index < 0 || index >= sizeOfValues()) {
        throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
      }
      if (index < previousIndex) {
        resetIterators();
      }
      while (index >= (start + current.size())) {
        start += current.size();
        current = it.next();
      }
      previousIndex = index;
      return current.get(index - start);
    }

    @Override
    public int size() {
      return sizeOfValues();
    }

  }

  /**
   * Iterates through all internal values
   * (not the arraylists themselves), first by key order,
   * and within each bucket, by insertion order.
   */
  private class InnerIterator implements Iterator<V> {

    private Iterator<ArrayList<V>> arrayIterator;
    private Iterator<V> valueIterator;
    private boolean finished = false;
    private V nextElement;

    private InnerIterator() {
      arrayIterator = values().iterator();
      advance();
    }

    private void advance() {
      if (valueIterator == null || !valueIterator.hasNext()) {
        if (arrayIterator.hasNext()) {
          valueIterator = arrayIterator.next().iterator();
          if (valueIterator.hasNext()) {
            nextElement = valueIterator.next();
          }
        } else {
          finished = true;
        }
      } else {
        nextElement = valueIterator.next();
      }
    }

    @Override
    public boolean hasNext() {
      return !finished;
    }

    @Override
    public V next() {
      V current = nextElement;
      advance();
      return current;
    }

  }

}
