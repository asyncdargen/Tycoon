package ru.dargen.tycoon.modules.command.args;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class IntegerArgument implements Argument<Integer> {

    private @Getter String name;
    private @Getter boolean required;
    private List<Integer> filter;

    public IntegerArgument(String name, boolean required, Integer... filter) {
        this(name, required, Arrays.asList(filter));
    }

    public IntegerArgument(String name, boolean required) {
        this.name = name;
        this.required = required;
        this.filter = null;
    }

    public IntegerArgument(String name, boolean required, List<Integer> filter) {
        this.name = name;
        this.required = required;
        this.filter = filter;
    }
    public Integer get(String arg) throws Exception {
        if (filter != null && !filter.isEmpty())
            if (!filter.contains(Integer.parseInt(arg)))
                throw new IllegalArgumentException("Not in filter");
        return Integer.parseInt(arg);
    }

}
