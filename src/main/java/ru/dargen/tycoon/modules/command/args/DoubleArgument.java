package ru.dargen.tycoon.modules.command.args;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class DoubleArgument implements Argument<Double> {

    private @Getter final String name;
    private @Getter final boolean required;
    private @Getter final List<Double> filter;

    public DoubleArgument(String name, boolean required, Double... filter) {
        this(name, required, Arrays.asList(filter));
    }

    public DoubleArgument(String name, boolean required) {
        this.name = name;
        this.required = required;
        this.filter = null;
    }

    public DoubleArgument(String name, boolean required, List<Double> filter) {
        this.name = name;
        this.required = required;
        this.filter = filter;
    }

    public Double get(String arg) throws Exception {
        if (filter != null && !filter.isEmpty())
            if (!filter.contains(Double.parseDouble(arg)))
                throw new IllegalStateException("Not in filter");
        return Double.parseDouble(arg);
    }

}
