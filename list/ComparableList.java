package list;

import java.util.StringJoiner;

/**
 * Creates a growable array for Comparable elements. ComparableList allows duplicates, but not nulls.
 * It is possible to find min, max, sort the elements etc.
 * @param <E> type of elements the list will contain
 */
public class ComparableList<E extends Comparable<E>> {
    private int capacity = 10;                                         // number of elements underlying array can hold
    private int firstNullIndex;                                        // marks the beginning of the unused part
    @SuppressWarnings("unchecked") private E[] values = (E[]) new Comparable[capacity];   // array of elements

    /**
     * Checks if the list is empty.
     * @return {@code true} if the list is empty, otherwise {@code false}
     */
    public boolean isEmpty() {
        return firstNullIndex == 0;
    }

    /**
     * Checks if the elements are sorted in the ascending order.
     * @return {@code true} if the elements are sorted in the ascending order, otherwise {@code false}
     */
    public boolean isSortedAscending() {
        boolean sorted = true;
        for (int i = 0; i < firstNullIndex - 1; i++) {
            if (values[i].compareTo(values[i + 1]) > 0) {
                sorted = false;
                break;
            }
        }
        return sorted;
    }

    /**
     * Checks if the elements are sorted in the descending order.
     * @return {@code true} if the elements are sorted in the descending order, otherwise {@code false}
     */
    public boolean isSortedDescending() {
        boolean sorted = true;
        for (int i = 0; i < firstNullIndex - 1; i++) {
            if (values[i].compareTo(values[i + 1]) < 0) {
                sorted = false;
                break;
            }
        }
        return sorted;
    }

    /**
     * Adds the element to the end of the list. If the underlying
     * array becomes full, then the new array will be created.
     * @param element the element to be added to the list
     * @return {@code true} in all valid cases, {@code false} in case element is {@code null}
     */
    public boolean add(E element) {
        if (element == null) {
            return false;
        }
        values[firstNullIndex++] = element;
        if (firstNullIndex == capacity) {
            grow();
        }
        return true;
    }

    /**
     * Adds the element to the list at the specified index. If the underlying array becomes full,
     * then the new array will be created.
     * @param index the index where the element should be added
     * @param element the element to be added to the list
     * @throws IndexOutOfBoundsException if the index is not within the bounds of the current array
     */
    public void add(int index, E element) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException(String.format("Index %d, Size: %d", index, size()));
        }
        if (element == null) {
            return;
        }
        rightShift(index);
        values[index] = element;
        firstNullIndex++;
        if (firstNullIndex == capacity) {
            grow();
        }
    }

    /**
     * Adds the specified elements to the end of the list. If one of them is {@code null}, does nothing.
     * @param elements the elements to be added
     * @return the number of elements added
     */
    @SafeVarargs
    public final int addAll(E... elements) {
        for (E element : elements) {
            if (element == null) {
                return 0;
            }
        }
        int added = 0;
        for (E element : elements) {
            add(element);
            added++;
        }
        return added;
    }

    /**
     * Checks that the index is within the bounds.
     * @param index index to be checked
     * @throws IndexOutOfBoundsException if the index is not within the bounds of the current array
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(String.format("Index %d ouf of bounds for length %d", index, size()));
        }
    }

    /**
     * Clears the list.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        capacity = 10;
        firstNullIndex = 0;
        values = (E[]) new Comparable[capacity];
    }

    /**
     * Checks if the list contains the specified element.
     * @param element the element to search for
     * @return {@code true} if the list contains the element, otherwise {@code false}
     */
    public boolean contains(E element) {
        boolean contains = false;
        for (int i = 0; i < firstNullIndex; i++) {
            if (values[i].equals(element)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Gets the element at the specified index.
     * @param index index of the element to be returned
     * @return the element at the specified index
     */
    public E get(int index) {
        checkIndex(index);
        return values[index];
    }

    /**
     * If the underlying array is full, creates a new one with double its size and copies the elements into it.
     */
    private void grow() {
        @SuppressWarnings("unchecked") E[] another = (E[]) new Comparable[capacity *= 2];
        System.arraycopy(values, 0, another, 0, values.length);
        values = another;
    }

    /**
     * In the case of adding an element in between other elements,
     * shifts the elements from the specified index to the right.
     * @param index index from which the elements will shift
     */
    private void rightShift(int index) {
        if (firstNullIndex - index >= 0)
            System.arraycopy(values, index, values, index + 1, firstNullIndex - index);
    }

    /**
     * In the case of removing an element in between other elements,
     * shifts the elements from the specified index to the left.
     * @param index index from which the elements will shift
     */
    private void leftShift(int index) {
        if (firstNullIndex - index >= 0)
            System.arraycopy(values, index + 1, values, index, firstNullIndex - index);
    }

    /**
     * Gets the number of elements in the list.
     * @return the number of elements in the list
     */
    public int size() {
        return firstNullIndex;
    }

    /**
     * Searches the list for the first occurrence of the specific element.
     * @param element element to search for
     * @return the index of the first occurrence of the element, or -1 in case the element was not found
     */
    public int indexOf(E element) {
        int index = -1;
        for (int i = 0; i < firstNullIndex; i++) {
            if (values[i] == element) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Searches the list for the last occurrence of the specific element.
     * @param element element to search for
     * @return the index of the last occurrence of the element, or -1 in case the element was not found
     */
    public int lastIndexOf(E element) {
        int index = -1;
        for (int i = firstNullIndex - 1; i >= 0; i--) {
            if (values[i] == element) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Utility method to create the ComparableList with the specific elements.
     * @param elements the elements to add to the instance of ComparableList
     * @param <E> the type of elements to be added
     * @return instance of ComparableList with the elements added, {@code null} if
     * one of the elements is {@code null}
     */
    @SafeVarargs
    public static <E extends Comparable<E>> ComparableList<E> of(E... elements) {
        for (E element : elements) {
            if (element == null) {
                return null;
            }
        }
        ComparableList<E> list = new ComparableList<>();
        list.addAll(elements);
        return list;
    }

    /**
     * Gets the maximum element from the list.
     * @return the maximum element or {@code null} if the list is empty
     */
    public E max() {
        if (size() == 0) {
            System.out.println("The list is empty!");
            return null;
        }
        E max = values[0];
        for (int i = 1; i < firstNullIndex; i++) {
            if (values[i].compareTo(max) > 0) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Gets the minimum element from the list.
     * @return the minimum element or {@code null} if the list is empty
     */
    public E min() {
        if (size() == 0) {
            System.out.println("The list is empty!");
            return null;
        }
        E min = values[0];
        for (int i = 1; i < firstNullIndex; i++) {
            if (values[i].compareTo(min) < 0) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Gets the random element from the list.
     * @return the random element or {@code null} if the list is empty
     */
    public E random() {
        if (size() == 0) {
            System.out.println("The list is empty!");
            return null;
        }
        return values[(int) (Math.random() * size())];
    }

    /**
     * Reverses the list.
     */
    public void reverse() {
        @SuppressWarnings("unchecked") E[] another = (E[]) new Comparable[capacity];
        int end = firstNullIndex - 1;
        for (int i = 0; i < firstNullIndex; i++) {
            another[end--] = values[i];
        }
        values = another;
    }

    /**
     * Removes the first occurrence of the specific element from the list.
     * @param element the element to be removed
     * @return {@code true} if the element was removed, otherwise {@code false}
     */
    public boolean remove(E element) {
        boolean removed = false;
        for (int i = 0; i < firstNullIndex; i++) {
            if (values[i].equals(element)) {
                values[i] = null;
                leftShift(i);
                firstNullIndex--;
                removed = true;
                break;
            }
        }
        return removed;
    }

    /**
     * Removes the element from the list at the specific index.
     * @param index the index where the element should be removed
     * @return the element removed
     */
    public E remove(int index) {
        checkIndex(index);
        E removed = values[index];
        values[index] = null;
        leftShift(index);
        firstNullIndex--;
        return removed;
    }

    /**
     * Removes all the occurrences of the specific elements from the list.
     * @param elements elements to be removed
     * @return the number of elements removed
     */
    @SafeVarargs
    public final int removeAll(E... elements) {
        int removed = 0;
        for (E element : elements) {
            removed += removeAllOccurrences(element);
        }
        return removed;
    }

    /**
     * Removes all occurrences of the specific element from the list.
     * @param element the element to be removed
     * @return the number of elements removed
     */
    private int removeAllOccurrences(E element) {
        int removed = 0;
        for (int i = firstNullIndex - 1; i >= 0; i--) {
            if (values[i].equals(element)) {
                values[i] = null;
                leftShift(i);
                removed++;
            }
        }
        firstNullIndex -= removed;
        return removed;
    }

    /**
     * Copies the list to an array of objects.
     * @return list as an array of objects
     */
    public Object[] toArray() {
        Object[] another = new Object[size()];
        if (firstNullIndex >= 0)
            System.arraycopy(values, 0, another, 0, firstNullIndex);
        return another;
    }

    /**
     * Sets the element at the specific index to the provided value.
     * @param index index to be set
     * @param value value to be set to the element
     * @return the old element
     */
    public E set(int index, E value) {
        checkIndex(index);
        E old = values[index];
        values[index] = value;
        return old;
    }

    /**
     * Swaps two elements in the list.
     * @param i the first element
     * @param j the second element
     */
    public void swap(int i, int j) {
        checkIndex(i);
        checkIndex(j);
        E temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    /**
     * Sorts the list in the ascending order.
     */
    public void sortAscending() {
        for (int i = 0; i < firstNullIndex - 1; i++) {
            for (int j = 0; j < firstNullIndex - i - 1; j++) {
                if (values[j].compareTo(values[j + 1]) > 0) {
                    E temp = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Sorts the list in the descending order.
     */
    public void sortDescending() {
        for (int i = 0; i < firstNullIndex - 1; i++) {
            for (int j = 0; j < firstNullIndex - i - 1; j++) {
                if (values[j].compareTo(values[j + 1]) < 0) {
                    E temp = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Produces a readable string representation of the list.
     * @return the string representation of the list
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < firstNullIndex; i++) {
            joiner.add(values[i].toString());
        }
        return joiner.toString();
    }
}
