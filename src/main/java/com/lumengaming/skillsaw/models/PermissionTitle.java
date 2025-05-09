package com.lumengaming.skillsaw.models;

import java.util.HashMap;


public class PermissionTitle extends Title{
    
    private final String permission;
	
	/** Pass in the skill map from the User. This is passed by reference, so
	 * any changes to the skill map will immediately show up in the master skill
	 * title map. 
	 */
	public PermissionTitle(String permission, String shortTitle, String longTitle){
		super(shortTitle, longTitle);
        this.permission = permission;
	}
	
	/** A title to be used on mouse hover events on the chat format. Automatically
     * @return  **/
    @Override
	public String getLongTitle(){
		return longTitle.replace("&", "§");
	}

	/** The title to use for chat.
     * @return  **/
    @Override
	public String getShortTitle(){
		return shortTitle.replace("&", "§");
	}

    public String getPermission() {
        return permission;
    }
}
