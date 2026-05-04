package net.minecraft.item;

public class ItemStack {
    public static final ItemStack EMPTY = new ItemStack();

    private int count = 1;
    private int damage = 0;
    private boolean empty;

    public ItemStack copy() {
        ItemStack copy = new ItemStack();
        copy.count = this.count;
        copy.damage = this.damage;
        copy.empty = this.empty;
        return copy;
    }

    public boolean isEmpty() {
        return empty || count <= 0;
    }

    public int getMetadata() {
        return damage;
    }

    public void setItemDamage(int damage) {
        this.damage = damage;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public ItemStack func_77946_l() {
        return copy();
    }

    public boolean func_190926_b() {
        return isEmpty();
    }

    public int func_77952_i() {
        return getMetadata();
    }

    public void func_77964_b(int damage) {
        setItemDamage(damage);
    }

    public void func_190920_e(int count) {
        setCount(count);
    }

    public int func_190916_E() {
        return getCount();
    }
}
