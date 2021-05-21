package services;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Util.
 * Provides consecutive numeration for parts of a specified registryElement;
 *
 * @author Melton Smith
 * @since 01.12.2020
 */
public class PartNumberGenerator {

    public static final PartNumberGenerator INSTANCE = new PartNumberGenerator();
    private final static HashMap<Long, AtomicInteger> controlMap = new HashMap<>();

    private PartNumberGenerator() { }

    public int getPartNumber(Long elementId){
        AtomicInteger atomicInteger = controlMap.computeIfAbsent(elementId, k -> new AtomicInteger(0));
        return atomicInteger.incrementAndGet();
    }
}
