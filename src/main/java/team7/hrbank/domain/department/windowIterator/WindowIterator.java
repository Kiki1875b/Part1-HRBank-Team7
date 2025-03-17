package team7.hrbank.domain.department.windowIterator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WindowIterator<T> implements Iterator<List<T>> {

    private final Iterator<T> iterator;
    private final int windowSize;
    private final LinkedList<T> window;

    public WindowIterator(Iterator<T> iterator, int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("윈도우 크기는 1 이상이어야 합니다.");
        }

        this.iterator = iterator;
        this.windowSize = windowSize;
        this.window = new LinkedList<>();

        while (this.iterator.hasNext() && window.size() < windowSize) {
        this.window.add(this.iterator.next());
        }
    }
    //todo 구현 마저 하기
    @Override
    public boolean hasNext() {
        return !window.isEmpty();
    }

    @Override
    public List<T> next() {
        return List.of();
    }

}
