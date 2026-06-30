package siozy.dev.lunaspring.API.util.utilities;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@UtilityClass
public class LunaMath {

    public int getIndex(int row, int col) {
        return (row - 1) * 9 + col - 1;
    }

    public int toInt(String text) {
        return toInt(text, 0);
    }

    public int toInt(String text, int defaultNum) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public int toInt(double num) {
        return (int) num;
    }

    public byte toByte(int num) {
        return (byte) num;
    }

    public double toDouble(String text) {
        return toDouble(text, 0);
    }

    public double toDouble(String text, double defaultNum) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public long toLong(String text, long defaultNum) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public long toLong(String text) {
        return toLong(text, 0);
    }

    public byte toByte(String text, byte defaultNum) {
        try {
            return Byte.parseByte(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public byte toByte(String text) {
        return toByte(text, (byte) 0);
    }

    public short toShort(String text, short defaultNum) {
        try {
            return Short.parseShort(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public short toShort(String text) {
        return toShort(text, (short) 0);
    }

    public float toFloat(String text, float defaultNum) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultNum;
        }
    }

    public float toFloat(String text) {
        return toFloat(text, 0f);
    }

    public boolean toBoolean(String text, boolean defaultVal) {
        if ("true".equalsIgnoreCase(text)) {
            return true;
        }

        if  ("false".equalsIgnoreCase(text)) {
            return false;
        }

        return defaultVal;
    }

    public boolean toBoolean(String text) {
        return toBoolean(text, false);
    }

    public boolean toBoolean(int value, boolean defaultVal) {
        if (value == 0) {
            return false;
        }

        if (value == 1) {
            return true;
        }

        return defaultVal;
    }

    public boolean toBoolean(int value) {
        return toBoolean(value, false);
    }

    public boolean isEven(int num) {
        return num % 2 == 0;
    }

    public int getRandomInt(int minValue, int maxValue) {
        if (minValue >= maxValue) return maxValue;
        return ThreadLocalRandom.current().nextInt(maxValue - minValue) + minValue;
    }

    public int getRandomInt(String numerical, String splitRegex) {
        String[] split = numerical.split(splitRegex);
        if (split.length < 2) return toInt(split[0]);
        return getRandomInt(toInt(split[0]), toInt(split[1]));
    }

    public int getRandomInt(String numerical) {
        return getRandomInt(numerical, "-");
    }

    public double getRandomDouble(double minValue, double maxValue) {
        if (minValue >= maxValue) return maxValue;
        return minValue + Math.random() * (maxValue - minValue);
    }

    public double getRandomDouble(String numerical, String splitRegex) {
        String[] split = numerical.split(splitRegex);
        if (split.length < 2) return toDouble(split[0]);
        return getRandomDouble(toDouble(split[0]), toDouble(split[1]));
    }

    public double getRandomDouble(String numerical) {
        return getRandomDouble(numerical, "-");
    }

    public @Nullable <T> T getRandom(@Nullable List<T> collection) {
        if (collection == null || collection.isEmpty()) return null;
        if (collection.size() == 1) return collection.get(0);
        return collection.get(getRandomInt(0, collection.size()));
    }

    public @NotNull <T> T getRandomIfPresent(@Nullable List<T> collection, @NotNull Supplier<T> returner) {
        T t = getRandom(collection);
        return t == null ? returner.get() : t;
    }

    public @Nullable <T> T getRandom(Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }

        if (set.size() <= 1000) {
            List<T> list = new ArrayList<>(set);
            return getRandom(list);
        }

        int randomIndex = getRandomInt(0, set.size());
        int i = 0;
        for (T value : set) {
            if (i == randomIndex) {
                return value;
            }
            i++;
        }

        return null;
    }

    public @NotNull <T> T getRandomIfPresent(Set<T> set, @NotNull Supplier<T> returner) {
        T t = getRandom(set);
        return t == null ? returner.get() : t;
    }

    public double round(double notRoundedNum, int roundLength) {
        if (roundLength < 0) roundLength = 0;
        return BigDecimal.valueOf(notRoundedNum)
                .setScale(roundLength, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public int sum(Integer... integers) {
        return integers.length == 0 ? 0 : Arrays.stream(integers)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public double sum(Double... doubles) {
        return doubles.length == 0 ? 0 : Arrays.stream(doubles)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public long sum(Long... longs) {
        return longs.length == 0 ? 0 : Arrays.stream(longs)
                .mapToLong(Long::longValue)
                .sum();
    }

    public int avg(Integer... integers) {
        return sum(integers) / integers.length;
    }

    public double avg(Double... doubles) {
        return sum(doubles) / doubles.length;
    }

    public long avg(Long... longs) {
        return sum(longs) / longs.length;
    }
}
