/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

/**
 * The custom array list is implemented in such a way that clear function will
 * have time complexity of O(1).
 *
 * @author PersianDevStudio
 * @param <T>
 */
public class CustomArrayList<T> {

    private Object[] data;

    private int size = 0;

    private int increaseSize = 200;

    public CustomArrayList() {
        data = new Object[100];
    }

    public CustomArrayList(int defaultSize) {
        data = new Object[defaultSize];
    }

    public void add(T item) {
        size++;
        if (size >= data.length) {
            increaseArraySize(increaseSize);
        }
        data[size - 1] = item;
    }

    public void add(T item, int index) {
        size++;
        if (size >= data.length) {
            increaseArraySize(increaseSize);
        }
        for (int i = size() - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }
        data[index] = item;
    }

    public void remove(int index) {
        size--;
        for (int i = index; i < size(); i++) {
            data[i] = data[i + 1];
        }
    }

    public int indexOf(T item) {
        for (int i = 0; i < data.length; i++) {
            if (data[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public void remove(T item) {
        remove(indexOf(item));
    }

    public T get(int index) {
        return (T) data[index];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        size = 0;
    }

    private void increaseArraySize(int increaseSize) {
        Object[] newData = new Object[data.length + increaseSize];

        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }

        data = newData;
    }

}
