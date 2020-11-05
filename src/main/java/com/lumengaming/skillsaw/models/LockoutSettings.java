package com.lumengaming.skillsaw.models;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author prota
 */
public class LockoutSettings {

  @SerializedName("enabled")
  public boolean IsEnabled = false;

  @SerializedName("is-verbose")
  public boolean IsVerbose = false;
//
//  @SerializedName("deny-new-ips")
//  public boolean AreNewIpsDenied = false;
//
//  @SerializedName("deny-new-users")
//  public boolean AreNewUsersDenied = false;

  /**
   * Anyone with a playtime of LESS than the supplied value will be rejected. The default value is:
   * -1
   */
  @SerializedName("deny-newer-than-x-minutes")
  public int DenyNewerThanXMinutes = -1;

  /**
   * Max value of: 840. Min of 0.
   */
  @SerializedName("deny-less-active-than-x")
  public int DenyLessActiveThan = -1;

  @SerializedName("deny-if-rep-level-below")
  public int MinimumRepLevelToAllow = -1;

  @SerializedName("allow-staff")
  public boolean AllowStaff = false;

  @SerializedName("allow-instructors")
  public boolean AllowInstructors;

  public LockoutSettings reset() {
    this.AllowStaff = false;
    this.AllowInstructors = false;
    this.IsEnabled = false;
    this.IsVerbose = false;
    this.DenyNewerThanXMinutes = -1;
    this.MinimumRepLevelToAllow = -1;
    this.DenyLessActiveThan = -1;
    return this;
  }

  @Override
  public String toString() {
    String cmd = "/lockout";
    
    if (this.IsVerbose) cmd += " -v";
    if (this.AllowInstructors) cmd += " -allowInstructors";
    if (this.AllowStaff) cmd += " -allowStaff";
    if (this.MinimumRepLevelToAllow != -1) cmd += (" -replevel<"+this.MinimumRepLevelToAllow);
    if (this.DenyLessActiveThan != -1) cmd += " -a<"+this.DenyLessActiveThan;
    if (this.DenyNewerThanXMinutes != -1) cmd += " -minutes<"+this.DenyNewerThanXMinutes;
    
    if (!this.IsEnabled) cmd = "/lockout off";
    
    return cmd;
  }
}
