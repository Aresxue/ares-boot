package cn.ares.boot.util.common.structure;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: Ares
 * @time: 2023-06-27 20:48:21
 * @description: Split list
 * @version: JDK 1.8
 */
public class SplitList<T> extends AbstractList<List<T>> {

  private final List<T> list;
  private final int size;

  public SplitList(final Collection<T> collection, final int size) {
    this.list = new ArrayList<>(collection);
    this.size = size;
  }

  @Override
  public List<T> get(final int index) {
    final int listSize = size();
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
    }
    if (index >= listSize) {
      throw new IndexOutOfBoundsException("Index " + index + " must be less than size " +
          listSize);
    }
    final int start = index * size;
    final int end = Math.min(start + size, list.size());
    return list.subList(start, end);
  }

  @Override
  public int size() {
    return (int) Math.ceil((double) list.size() / (double) size);
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }
}

