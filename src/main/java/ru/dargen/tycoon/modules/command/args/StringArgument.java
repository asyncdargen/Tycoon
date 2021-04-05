package ru.dargen.tycoon.modules.command.args;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringArgument implements Argument<String> {

    private @Getter String name;
    private @Getter boolean required;
    private List<String> filter;

    public StringArgument(String name, boolean required, String... filter) {
        this(name, required, Arrays.asList(filter));
    }

    public StringArgument(String name, boolean required) {
        this.name = name;
        this.required = required;
        this.filter = null;
    }

    public StringArgument(String name, boolean required, List<String> filter) {
        this.name = name;
        this.required = required;
        this.filter = filter.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public String get(String arg) throws Exception {
        if (filter != null && !filter.isEmpty())
            if (!filter.contains(arg.toLowerCase()))
                throw new IllegalArgumentException("Not in filter");
        return arg;
    }

}
