package pl.kwadratowamasakra.lightspigot.utils;

import lombok.Getter;

@Getter
public class ItemStack {

    private final boolean isItem;
    private int itemId;
    private int count;
    private int data;

    public ItemStack(final int itemId, final int count, final int data) {
        this.itemId = itemId;
        this.count = count;
        this.data = data;
        isItem = true;
    }

    public ItemStack() {
        isItem = false;
    }

}
