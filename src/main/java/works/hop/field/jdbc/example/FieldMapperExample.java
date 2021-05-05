package works.hop.field.jdbc.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.field.jdbc.mapper.BuilderFunction;
import works.hop.field.jdbc.mapper.MapperBuilder;
import works.hop.field.jdbc.mapper.MapperContext;

import java.util.*;
import java.util.function.Function;

import static works.hop.field.jdbc.mapper.MapperUtils.mapAToB;
import static works.hop.field.jdbc.mapper.MapperUtils.setAToB;

public class FieldMapperExample {

    final static Logger log = LoggerFactory.getLogger(FieldMapperExample.class);

    public static void main(String[] args) {
//        copyToInstanceOfSameType();
//        copyToInstanceOfMatchingFieldTypes();
        copyToInstanceOfNonMatchingFieldTypes();
    }

    public static ItemA createTestItemA() {
        ItemC itemCinA = new ItemC("read book", false);
        ItemC itemCinB = new ItemC("cook eggs", true);
        ItemC itemC1 = new ItemC("play chess", true);
        ItemC itemC2 = new ItemC("ride bike", false);
        ItemC itemC3 = new ItemC("play piano", true);
        ItemC itemC4 = new ItemC("run a mile", false);
        ItemC itemC5 = new ItemC("make dinner", false);
        ItemC itemC6 = new ItemC("watch movie", true);

        return new ItemA(100L, itemCinA, Map.of("itemC1", itemC1, "itemC2", itemC2, "itemC3", itemC3),
                "pretty good description", itemCinB, List.of(itemC1, itemC2, itemC3),
                Map.of(itemC4, 4, itemC5, 5, itemC6, 6));
    }

    public static void copyToInstanceOfSameType() {
        ItemA itemA = createTestItemA();
        ItemA clone = mapAToB(itemA, ItemA.class);
        System.out.println(clone);
    }

    public static void copyToInstanceOfMatchingFieldTypes() {
        ItemA itemA = createTestItemA();
        MapperContext context = MapperBuilder.newBuilder("")
        .mapAToB("cItem", "cTask")
        .mapAToB("cItemList", "cTaskList")
        .mapAToB("itemC", "taskC")
        .mapAToB("cItemMap", "cTaskMap")
        .mapAToB("cIntMap", "cIntMap")
        .build();

        TaskA clone = mapAToB(itemA, TaskA.class, context);
        System.out.println(clone);
    }

    public static void copyToInstanceOfNonMatchingFieldTypes() {
        ItemA itemA = createTestItemA();
        BuilderFunction cItemContext = mapperBuilder -> {
            mapperBuilder.mapAToB("name", "jina");
            mapperBuilder.mapAToB("done", "maliza");
            return mapperBuilder.build();
        };

        MapperContext context = MapperBuilder.newBuilder("")
                .mapAToB("cItem", "gTask")
                .mapAToB("cItem", "gTask", cItemContext)
                .mapAToB("cItemList", "gTaskList")
                .mapAToB("cItemList", "gTaskList", cItemContext)
                .mapAToB("itemC", "taskG")
                .mapAToB("itemC", "taskG", cItemContext)
                .mapAToB("cItemMap", "gTaskMap")
                .mapAToB("cItemMap", "gTaskMap", cItemContext)
                .mapAToB("cIntMap", "gTaskSet")
                .mapAToB("cIntMap", "gTaskSet", cItemContext)
                .mapAToBResolver("cIntMap", "gTaskSet", input -> ((Map)input).keySet())
                .build();

        TaskE clone = setAToB(itemA, TaskE.class, context);
        System.out.println(clone);
    }
}
