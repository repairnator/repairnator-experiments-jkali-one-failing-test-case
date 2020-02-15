package ru.job4j.tracker.input;

/**
 * @author Yury Matskevich
 * @since 0.1
 */
public interface Input {
    String ask(String question);

    int ask(String question, int[] range);
}
