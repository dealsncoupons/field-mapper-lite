package works.hop.field.example;

import java.util.List;
import java.util.Map;

public class ItemA extends ItemB {

    String description;
    ItemC cItem;
    List<ItemC> cItemList;

    public ItemA() {
        super();
    }

    public ItemA(String description, ItemC cItem, List<ItemC> cItemList) {
        this.description = description;
        this.cItem = cItem;
        this.cItemList = cItemList;
    }

    public ItemA(Long id, ItemC itemC, Map<String, ItemC> cItemMap, String description, ItemC cItem, List<ItemC> cItemList, Map<ItemC, Integer> cIntMap) {
        super(id, itemC, cItemMap, cIntMap);
        this.description = description;
        this.cItem = cItem;
        this.cItemList = cItemList;
    }
}
