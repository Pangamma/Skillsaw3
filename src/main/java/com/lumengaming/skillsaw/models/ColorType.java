/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lumengaming.skillsaw.models;

import org.bukkit.ChatColor;

/**
 *
 * @author prota
 */
public class ColorType {

	/**
	 * DV for brown wool has no matching chat color. If brown is detected, the
	 * data for ORANGE will be returned. (Gold)
	 *
	 * @param dv
	 * @return
	 */
	public static byte dataFromDataValue(int dv) {
		dv %= 16;
		if (dv == 12) {
			return 1;
		} else {
			return (byte) dv;
		}
	}

	/**
	 * DV for brown wool has no matching chat color. If brown is detected, the
	 * data for ORANGE will be returned. (Gold)
	 *
	 * @param dv
	 * @return
	 */
	public static byte dataFromDataValue(byte dv) {
		dv %= 16;
		if (dv == 12) {
			return 1;
		} else {
			return dv;
		}
	}

	/**
	 * Return chatcolor that matches with the dv supplied.
	 *
	 * @param dv
	 * @return
	 */
	public static ChatColor colorFromDataValue(int dv) {
		dv %= 16;
		switch (dv) {
			case 0: return ChatColor.WHITE;
			case 1:
			case 12: return ChatColor.GOLD;
			case 2: return ChatColor.LIGHT_PURPLE;
			case 3: return ChatColor.AQUA;
			case 4: return ChatColor.YELLOW;
			case 5: return ChatColor.GREEN;
			case 6: return ChatColor.RED;
			case 7: return ChatColor.DARK_GRAY;
			case 8: return ChatColor.GRAY;
			case 9: return ChatColor.DARK_AQUA;
			case 10: return ChatColor.DARK_PURPLE;
			case 11: return ChatColor.BLUE;
			case 13: return ChatColor.DARK_GREEN;
			case 14: return ChatColor.DARK_RED;
			case 15: return ChatColor.BLACK;
			default: return ChatColor.WHITE;
		}
	}

	/**
	 * Return chatcolor that matches with the dv supplied.
	 *
	 * @param dv
	 * @return
	 */
	public static byte dataValueFromColor(ChatColor c) {
		switch (c) {
			case WHITE: return 0;
			case GOLD: return 1;
			case LIGHT_PURPLE: return 2;
			case AQUA: return 3;
			case YELLOW: return 4;
			case GREEN: return 5;
			case RED: return 6;
			case DARK_GRAY: return 7;
			case GRAY: return 8;
			case DARK_AQUA: return 9;
			case DARK_PURPLE: return 10;
			case BLUE: return 11;
			case DARK_GREEN: return 13;
			case DARK_RED: return 14;
			case BLACK: return 15;
			default: return 0;
		}
	}
}
