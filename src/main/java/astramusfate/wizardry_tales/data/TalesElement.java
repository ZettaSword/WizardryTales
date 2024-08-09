package astramusfate.wizardry_tales.data;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public enum TalesElement {
    BLOOD(new Style().setColor(TextFormatting.DARK_RED), 101);


    /** Display colour for this element */
    private final Style style;
    /** God id for doing magic. **/
    private final int id;
    TalesElement(Style style, int id) {
        this.style=style;
        this.id = id;
    }

    /** Returns the {@link Style} object representing the colour of this element. */
    public Style getStyle(){
        return style;
    }

    /**
     * Returns the integer object representing the id of god.
     */
    public int id(){
        return id;
    }
}
