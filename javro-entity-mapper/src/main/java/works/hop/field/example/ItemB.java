package works.hop.field.example;

import java.util.Map;

public class ItemB {

    Long id;
    ItemC itemC;
    Map<String, ItemC> cItemMap;
    Map<ItemC, Integer> cIntMap;

    public ItemB() {
        super();
    }

    public ItemB(Long id, ItemC itemC, Map<String, ItemC> cItemMap, Map<ItemC, Integer> cIntMap) {
        this.id = id;
        this.itemC = itemC;
        this.cItemMap = cItemMap;
        this.cIntMap = cIntMap;
    }
}
