package codex.mmthaumbridge;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("mods.codexmmthaumbridge.AspectEntry")
public class AspectEntry {
    private final String tag;
    private final String localizedName;
    private final int amount;

    public AspectEntry(String tag, String localizedName, int amount) {
        this.tag = tag;
        this.localizedName = localizedName;
        this.amount = amount;
    }

    @ZenGetter("tag")
    public String getTag() {
        return tag;
    }

    @ZenGetter("localizedName")
    public String getLocalizedName() {
        return localizedName;
    }

    @ZenGetter("amount")
    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return tag + "=" + amount;
    }
}
