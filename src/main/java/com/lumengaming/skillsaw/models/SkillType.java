package com.lumengaming.skillsaw.models;

import java.util.ArrayList;

/**
 *
 * @author Taylor Love (Pangamma)
 */
public class SkillType extends AbstractSkillType {

    //<editor-fold defaultstate="collapsed" desc="ENUM">

    public final static SkillType Redstone = new SkillType(
        "redstone", // key (for DB) 
        "Redstone", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&cR" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&cRedstonerT" + SkillType.LEVEL_VAR_STR // title-format-long
    );

    public final static SkillType Organics = new SkillType(
        "organics", // key (for DB) 
        "Organics", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&9O" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&9OrganicsT" + SkillType.LEVEL_VAR_STR // title-format-long
    );

    public final static SkillType PixelArt = new SkillType(
        "pixelart", // key (for DB) 
        "PixelArt", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&aP" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&aPixelArtistT" + SkillType.LEVEL_VAR_STR // title-format-long
    );

    public final static SkillType Architecture = new SkillType(
        "architecture", // key (for DB) 
        "Architecture", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&2A" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&2ArchitectT" + SkillType.LEVEL_VAR_STR // title-format-long
    );

    public final static SkillType Terraforming = new SkillType(
        "terraforming", // key (for DB) 
        "Terraforming", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&dT" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&dTerraformerT" + SkillType.LEVEL_VAR_STR // title-format-long
    );

    public final static SkillType Vehicles = new SkillType(
        "vehicles", // key (for DB) 
        "Vehicles", // list-name
        0, // default-level
        0, // min-level
        10,// max-level
        5, // min-instruct-level
        "&3N" + SkillType.LEVEL_VAR_STR, // title-format-short
        "&3NavigatorT" + SkillType.LEVEL_VAR_STR // title-format-long
    );
    //</editor-fold>

    /**
     * Replace this string with the level of the skill passed into the getTitle method. %lvl%
     */
    public static final String LEVEL_VAR_STR = "%lvl%";
    private final String shortTitleFormat;
    private final String longTitleFormat;
    private final int minInstructLevel;
    private final String listName;

    public SkillType(String p_typeKey, String p_listName, int p_defaultLevel, int p_minLevel, int p_maxLevel, int p_minInstructLevel, String p_shortTitleFormat, String p_longTitleFormat) {
        super(p_typeKey, p_defaultLevel, p_minLevel, p_maxLevel);
        this.shortTitleFormat = p_shortTitleFormat;
        this.longTitleFormat = p_longTitleFormat;
        this.minInstructLevel = p_minInstructLevel;
        this.listName = p_listName;
    }

    /**
     * Returns a title with § characters, and with %level% replaced by the input level.
     *
     * @param p_skillLevel
     * @return
     */
    @Override
    public Title getTitle(int p_skillLevel) {
        Title t = new Title(
            this.shortTitleFormat.replace(SkillType.LEVEL_VAR_STR, "" + p_skillLevel),
            this.longTitleFormat.replace(SkillType.LEVEL_VAR_STR, "" + p_skillLevel)
        );
        return t;
    }

    /**
     * Minimum level required to allow someone to instruct in this category. Also requires the instructor permission.
     * Can be overidden with an override permission node.
     *
     * @return
     */
    public int getMinInstructLevel() {
        return this.minInstructLevel;
    }

    /**
     * The name to use for command menus and stuff. *
     */
    public String getListName() {
        return this.listName;
    }

    public static ArrayList<SkillType> getTypes() {
        ArrayList<SkillType> skillTypes = new ArrayList<>();
        skillTypes.add(SkillType.Redstone);
        skillTypes.add(SkillType.Organics);
        skillTypes.add(SkillType.PixelArt);
        skillTypes.add(SkillType.Architecture);
        skillTypes.add(SkillType.Terraforming);
        skillTypes.add(SkillType.Vehicles);
        
//        skillTypes.add(new SkillType(
//            "decoration", // key (for DB) 
//            "Decoration", // list-name
//            0, // default-level
//            0, // min-level
//            10,// max-level
//            5, // min-instruct-level
//            "&5D"+SkillType.LEVEL_VAR_STR, // title-format-short
//            "&5DecoratorT"+SkillType.LEVEL_VAR_STR // title-format-long
//            ));
        
        return skillTypes;
    }
    
}
