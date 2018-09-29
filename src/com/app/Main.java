package com.app;

import java.util.Locale;

// https://www.geeksforgeeks.org/find-the-k-most-frequent-words-from-a-file/amp/
// TODO: fix! feq always 1.
public class Main {

    static String thiers = "Welcome to the world of Geeks \n" +
            "This portal has been created to provide well written well thought and well explained \n" +
            "solutions for selected questions If you like Geeks for Geeks and would like to contribute \n" +
            "here is your chance You can write article and mail your article to contribute at \n" +
            "geeksforgeeks org See your article appearing on the Geeks for Geeks main page and help \n" +
            "thousands of other Geeks";

    static String mine = "lorem ipsum lorem a";

    public static void main(String[] args) {
        String text = thiers;

        text = text.toLowerCase(Locale.ENGLISH);

        String[] array = text.split("\\s");
        int k = 5;
        MinHeap minHeap = MinHeap.createMinHeap(k);
        TrieNodeWrapper trieNodeWrapper = new TrieNodeWrapper();
        for (String word : array) {
            insertTrieAndHeap(word, trieNodeWrapper.node, minHeap);
        }

        displayMinHeap(minHeap);

    }

    static class TrieNodeWrapper {
        TrieNode node;
    }

    static class TrieNode {

        private static final int MAX_CHARS = 26; // English alphabet

        boolean isEnd;
        int frequency;
        int indexMinHeap;
        TrieNode[] child = new TrieNode[MAX_CHARS];
    }

    static class MinHeapNode {

        TrieNode root; // indicates the leaf node of TRIE
        int frequency;
        String word; // the actual word stored
    }

    static class MinHeap {
        int capacity; // the total size a min heap
        int count; // indicates the number of slots filled.
        MinHeapNode[] array; //  represents the collection of minHeapNodes

        // A utility function to create a Min Heap of given capacity
        static MinHeap createMinHeap(int capacity) {
            MinHeap minHeap = new MinHeap();
            minHeap.capacity = capacity;
            minHeap.array = new MinHeapNode[capacity];
            return minHeap;
        }

        // A utility function to swap two min heap nodes. This function
        // is needed in minHeapify
        void swapMinHeapNodes(MinHeapNode[] array, int i, int j) {
            MinHeapNode temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        // This is the standard minHeapify function. It does one thing extra.
        // It updates the minHeapIndex in Trie when two nodes are swapped in
        // in min heap
        void minHeapify(int index) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;
            if (left < count && array[left].frequency < array[smallest].frequency) {
                smallest = left;
            }
            if (right < count && array[right].frequency < array[smallest].frequency) {
                smallest = right;
            }

            if (smallest != index) {
                // Update the corresponding index in Trie node.
                array[smallest].root.indexMinHeap = index; // TODO: what is it?
                array[index].root.indexMinHeap = smallest;

                // Swap nodes in min heap
                swapMinHeapNodes(array, index, smallest);

                minHeapify(smallest);
            }
        }

        void buildMinHeap() {
            for (int i = count / 2 - 1; i >= 0; i--) {
                minHeapify(i);
            }
        }
    }

    // A utility function to create a new Trie node
    static TrieNode newTrieNode() {
        TrieNode trieNode = new TrieNode();
        trieNode.indexMinHeap = -1;
        return trieNode;
    }

    // Inserts a word to heap, the function handles the 3 cases explained above
    static void insertInMinHeap(MinHeap minHeap, TrieNode root, String word) {
        // Case 1: the word is already present in minHeap
        if (root.indexMinHeap != -1) {
            if (minHeap.array[root.indexMinHeap] == null) {
                minHeap.array[root.indexMinHeap] = new MinHeapNode();
            }
            minHeap.array[root.indexMinHeap].frequency++;
        } else if (minHeap.count < minHeap.capacity) {
            // Case 2: Word is not present and heap is not full
            int count = minHeap.count;
            if (minHeap.array[count] == null) {
                minHeap.array[count] = new MinHeapNode();
            }
            minHeap.array[count].frequency = root.frequency;
            minHeap.array[count].word = word;
            minHeap.array[count].root = root;
            root.indexMinHeap = minHeap.count;
            minHeap.count++;
            minHeap.buildMinHeap();
        } else if (root.frequency > minHeap.array[0].frequency) {
            if (minHeap.array[0] == null) {
                minHeap.array[0] = new MinHeapNode();
            }
            // Case 3: Word is not present and heap is full. And frequency of word
            // is more than root. The root is the least frequent word in heap,
            // replace root with new word
            minHeap.array[0].root.indexMinHeap = -1;
            minHeap.array[0].root = root;
            minHeap.array[0].root.indexMinHeap = 0;
            minHeap.array[0].frequency = root.frequency;

            minHeap.array[0].word = word;
            minHeap.minHeapify(0);
        }
    }

    // Inserts a new word to both Trie and Heap
    static void insertUtil(TrieNode root, MinHeap minHeap, String word, int wordIndex) {
        // Base case
        if (root == null) {
            root = newTrieNode();
        }
        // There are still more characters in word
        if (wordIndex < word.length()) {
            int index = (int) word.charAt(wordIndex) - 97;
            insertUtil(root.child[index], minHeap, word, wordIndex + 1);
        } else {
            // The complete word is processed

            // word is already present, increase the frequency
            if (root.isEnd) {
                root.frequency++;
            } else {
                root.isEnd = true;
                root.frequency = 1;
            }

            // Insert in min heap also
            insertInMinHeap(minHeap, root, word);
        }
    }

    static void insertTrieAndHeap(String word, TrieNode root, MinHeap minHeap) {
        insertUtil(root, minHeap, word, 0);
    }

    // A utility function to show results, The min heap
    // contains k most frequent words so far, at any time
    static void displayMinHeap(MinHeap minHeap) {
        for (int i = 0; i < minHeap.count; i++) {
            System.out.println("word: " + minHeap.array[i].word + " freq=" + minHeap.array[i].frequency);
        }
    }
}
