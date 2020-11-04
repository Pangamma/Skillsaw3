package com.lumengaming.skillsaw.commands.chat;

import com.lumengaming.skillsaw.BungeeMain;
import com.lumengaming.skillsaw.commands.BungeeCommand;
import com.lumengaming.skillsaw.config.ConfigHelper;
import com.lumengaming.skillsaw.models.User;
import com.lumengaming.skillsaw.utility.CText;
import com.lumengaming.skillsaw.utility.C;
import com.lumengaming.skillsaw.utility.Permissions;
import com.lumengaming.skillsaw.wrappers.BungeePlayer;
import com.lumengaming.skillsaw.wrappers.IPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author Taylor
 */
public class LanguageTranslateCommand extends BungeeCommand {

  public LanguageTranslateCommand(BungeeMain plugin) {
    super(plugin, "languagetranslate", null, "lang", "translatelanguage", "translang", "autotranslate", "locale");
    super.addSyntax(Permissions.TRANSLATE_SELF, false, false, "/lang [user] <off/en/es/nl/pl/ru/de/cs/ja/ko>", "Sets your preferred language.");
    super.addSyntax(Permissions.TRANSLATE_SELF, false, true, "/lang [user] 0/off ", "Disables the locale converter");
    super.addSyntax(Permissions.TRANSLATE_SELF, false, true, "/lang [user] auto ", "Automatically detects locales based on client settings.");
    super.addSyntax(Permissions.TRANSLATE_SELF, false, true, "/lang list", "List available locales.");
    super.addSyntax(Permissions.TRANSLATE_SELF, false, true, "/lang stats", "See which locales are most popular.");
  }

  private HashMap<String, String> _supportedLocales = null;

  private HashMap<String, String> getSupportedLocales() {
    HashMap<String, String> output = _supportedLocales;
    if (_supportedLocales == null) {
      String json = "{\"eu\":\"Basque\",\"af\":\"Afrikaans\",\"sq\":\"Albanian\",\"am\":\"Amharic\",\"ar\":\"Arabic\",\"hy\":\"Armenian\",\"az\":\"Azerbaijani\",\"be\":\"Belarusian\",\"bn\":\"Bengali\",\"bs\":\"Bosnian\",\"bg\":\"Bulgarian\",\"ca\":\"Catalan\",\"ceb\":\"Cebuano\",\"ny\":\"Chichewa\",\"zh-CN\":\"Chinese (Simplified)\",\"co\":\"Corsican\",\"hr\":\"Croatian\",\"cs\":\"Czech\",\"da\":\"Danish\",\"nl\":\"Dutch\",\"en\":\"English\",\"eo\":\"Esperanto\",\"et\":\"Estonian\",\"tl\":\"Filipino\",\"fi\":\"Finnish\",\"fr\":\"French\",\"fy\":\"Frisian\",\"gl\":\"Galician\",\"ka\":\"Georgian\",\"de\":\"German\",\"el\":\"Greek\",\"gu\":\"Gujarati\",\"ht\":\"Haitian Creole\",\"ha\":\"Hausa\",\"haw\":\"Hawaiian\",\"iw\":\"Hebrew\",\"hi\":\"Hindi\",\"hmn\":\"Hmong\",\"hu\":\"Hungarian\",\"is\":\"Icelandic\",\"ig\":\"Igbo\",\"id\":\"Indonesian\",\"ga\":\"Irish\",\"it\":\"Italian\",\"ja\":\"Japanese\",\"jw\":\"Javanese\",\"kn\":\"Kannada\",\"kk\":\"Kazakh\",\"km\":\"Khmer\",\"rw\":\"Kinyarwanda\",\"ko\":\"Korean\",\"ku\":\"Kurdish (Kurmanji)\",\"ky\":\"Kyrgyz\",\"lo\":\"Lao\",\"la\":\"Latin\",\"lv\":\"Latvian\",\"lt\":\"Lithuanian\",\"lb\":\"Luxembourgish\",\"mk\":\"Macedonian\",\"mg\":\"Malagasy\",\"ms\":\"Malay\",\"ml\":\"Malayalam\",\"mt\":\"Maltese\",\"mi\":\"Maori\",\"mr\":\"Marathi\",\"mn\":\"Mongolian\",\"my\":\"Myanmar (Burmese)\",\"ne\":\"Nepali\",\"no\":\"Norwegian\",\"or\":\"Odia (Oriya)\",\"ps\":\"Pashto\",\"fa\":\"Persian\",\"pl\":\"Polish\",\"pt\":\"Portuguese\",\"pa\":\"Punjabi\",\"ro\":\"Romanian\",\"ru\":\"Russian\",\"sm\":\"Samoan\",\"gd\":\"Scots Gaelic\",\"sr\":\"Serbian\",\"st\":\"Sesotho\",\"sn\":\"Shona\",\"sd\":\"Sindhi\",\"si\":\"Sinhala\",\"sk\":\"Slovak\",\"sl\":\"Slovenian\",\"so\":\"Somali\",\"es\":\"Spanish\",\"su\":\"Sundanese\",\"sw\":\"Swahili\",\"sv\":\"Swedish\",\"tg\":\"Tajik\",\"ta\":\"Tamil\",\"tt\":\"Tatar\",\"te\":\"Telugu\",\"th\":\"Thai\",\"tr\":\"Turkish\",\"tk\":\"Turkmen\",\"uk\":\"Ukrainian\",\"ur\":\"Urdu\",\"ug\":\"Uyghur\",\"uz\":\"Uzbek\",\"vi\":\"Vietnamese\",\"cy\":\"Welsh\",\"xh\":\"Xhosa\",\"yi\":\"Yiddish\",\"yo\":\"Yoruba\",\"zu\":\"Zulu\",\"zh-TW\":\"Chinese (Traditional)\"}";
      _supportedLocales = ConfigHelper.getGson().fromJson(json, HashMap.class);
    }
    return _supportedLocales;
  }

  /**
   * Just as it sounds. Returns array with one fewer argument. Missing the first
   * arg. *
   */
  private String[] stripArg(String[] origArray) {
    String[] nArray = new String[origArray.length - 1];
    if (origArray.length > 0) {
      for (int i = 1; i < origArray.length; i++) {
        nArray[i - 1] = origArray[i];
      }
    }
    return nArray;
  }

  @Override
  public Iterable<String> onTabCompleteBeforeFiltering(CommandSender arg0, String[] args) {
    HashSet<String> set = new HashSet<>();
    if (args.length == 2 || args.length == 1) {
      set.add("off");
      set.add("auto");
      set.add("<locale code>");
    }

    if (args.length == 1) {
      set.add("list");
      set.add("stats");
      set.addAll(this.getOnlinePlayerNames());
    }

    return set;
  }

  private void printHelp(IPlayer cs) {
    cs.sendMessage(CText.hoverText("§c/lang [user] <en/es/nl/pl/ru/de/cs/ja/ko>", "Sets your preferred language."));
    cs.sendMessage(CText.hoverText("§c/lang [user] 0/off ", "Disables the locale converter"));
    cs.sendMessage(CText.hoverText("§c/lang [user] auto ", "Automatically detects locales based on client settings."));
    cs.sendMessage(CText.hoverText("§c/lang list", "List available locales."));
    cs.sendMessage(CText.hoverText("§c/lang stats", "See which locales are most popular."));
  }

  @Override
  public void execute(BungeePlayer cs, String[] args) {

    if (args.length > 2 || args.length < 1) {
      printHelp(cs);
    }

    if (args.length == 1 && !Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_SELF, true)) {
      return;
    } else if (args.length >= 2 && !Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_OTHERS, true)) {
      return;
    }

    if (args.length == 1 && !cs.isPlayer()) {
      cs.sendMessage(C.ERROR_PLAYERS_ONLY);
      return;
    }

    ArrayList<User> us = new ArrayList<User>();
    UUID csUUID = cs.isPlayer() ? cs.getUniqueId() : new UUID(0, 0);

    if (args.length == 2 && args[0].equals("*") && Permissions.USER_HAS_PERMISSION(cs, Permissions.TRANSLATE_OTHERS, false)) {
      us = plugin.getApi().getOnlineUsersReadOnly();
    } else {

      // TODO: Make it so * works for everyone.
      User u = args.length == 1 ? plugin.getApi().getUser(csUUID) : plugin.getApi().getUserBestOnlineMatch(args[0]);
      if (u != null) {
        us.add(u);
      } else {
        cs.sendMessage(C.ERROR_P_NOT_FOUND);
        return;
      }
    }

    try {
      // Otherwise see if they used the in-command thing with arg[0]
      boolean isSelf = false;
      switch (args[args.length - 1].toLowerCase()) {
        case "stats":
        case "?": {
          HashMap<String, Integer> uniqueLocales = new HashMap<>();
          for (User u : plugin.getApi().getOnlineUsersReadOnly()) {
            IPlayer p = u.p();
            if (p == null) {
              continue;
            }
            String k = p.getLocale();
            if (uniqueLocales.containsKey(k)) {
              Integer i = uniqueLocales.get(k);
              uniqueLocales.put(k, i + 1);
            } else {
              uniqueLocales.put(k, 1);
            }
          }

          cs.sendMessage(C.C_DIV_LINE);
          cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Locale stats");
          cs.sendMessage(C.C_DIV_LINE);
          boolean b = false;
          for (String k : uniqueLocales.keySet()) {
            cs.sendMessage((b ? C.C_MENU_CONTENT : C.C_MENU_CONTENT2) + k + ":    " + uniqueLocales.get(k));
          }
          cs.sendMessage(C.C_DIV_LINE);
          break;
        }
        case "l":
        case "list": {
          cs.sendMessage(C.C_DIV_LINE);
          cs.sendMessage(C.C_DIV_TITLE_PREFIX + "Locale List");
          cs.sendMessage(C.C_DIV_LINE);
          HashMap<String, String> langs = getSupportedLocales();
          ArrayList<String> keySet = new ArrayList(langs.keySet());
          Collections.sort(keySet);
          int maxPerLine = 16;

//          int rawLineLength = 0;
          BaseComponent[] lineContent = new BaseComponent[0];
          for (int i = 0; i < keySet.size(); i++) {
            String code = keySet.get(i);
            String text = langs.get(code);

            lineContent = CText.merge(lineContent, CText.hoverTextForce("§a" + code, text, "/lang " + code));

            if (i < keySet.size() - 1) {
              lineContent = CText.merge(lineContent, CText.legacy("§7, "));
            }

            if (i == keySet.size() - 1 || i % maxPerLine == maxPerLine - 1) {
              cs.sendMessage(lineContent);
              lineContent = new BaseComponent[0];
            }
          }
          cs.sendMessage(C.C_DIV_LINE);
        }
        break;
        case "0":
        case "off":
          for (User u : us) {
            u.setLocale(null);
            if (u.getUniqueId().equals(csUUID)) {
              cs.sendMessage(C.MSG_TRANSLATE_DISABLED);
              isSelf = true;
            } else {
              u.sendMessage("§aTranslation service has been §cdisabled§a by §f" + cs.getDisplayName());
            }
          }

          if (!isSelf) {
            cs.sendMessage("§aTranslation service §cdisabled§a for §f" + us.toString());
          }
          break;
        case "1":
        case "auto": {

          HashMap<String, String> supportedLocales = getSupportedLocales();
          for (User u : us) {
            if (u.p() != null) {
              String locale = u.p().getLocale();
              if (locale == null) {
                continue;
              }
              String lang = locale.substring(0, 2);
              String localeText = supportedLocales.get(lang);
              u.setLocale(lang);
              if (u.getUniqueId().equals(csUUID)) {
                u.sendMessage(String.format(C.MSG_TRANSLATE_UPDATED_0_1, localeText, lang));
                isSelf = true;
              } else {
                u.sendMessage(String.format(C.MSG_TRANSLATE_ENABLED_BY_0_1_2, localeText, cs.getDisplayName(), lang));
              }
            }
          }

          if (!isSelf) {
            cs.sendMessage("§aTranslation service §2automatically set§a for §f" + us.toString() + "§a. (auto)");
          }

          if (us.size() > 1 && plugin.getApi().getOnlineUsersLocales().size() < 2) {
            for (User u : us) {
              u.setLocale(null);
              if (u.getUniqueId().equals(csUUID)) {
                u.sendMessage(C.MSG_TRANSLATE_DISABLED);
                isSelf = true;
              } else {
                u.sendMessage("§aTranslations §cdisabled§a by §f" + cs.getDisplayName() + "§a.");
              }
            }
          }
        }
        break;
        default: {
          HashMap<String, String> supportedLocales = getSupportedLocales();
          if (supportedLocales.containsKey(args[args.length - 1])) {
            String localeText = supportedLocales.get(args[args.length - 1]);
            for (User u : us) {
              u.setLocale(args[args.length - 1]);
              if (u.getUniqueId().equals(csUUID)) {
                u.sendMessage(String.format(C.MSG_TRANSLATE_UPDATED_0_1, localeText, args[args.length - 1]));
                isSelf = true;
              } else {
                u.sendMessage(String.format(C.MSG_TRANSLATE_ENABLED_BY_0_1_2, localeText, cs.getDisplayName(), args[args.length - 1]));
              }
            }

            if (!isSelf) {
              cs.sendMessage(String.format(C.MSG_TRANSLATE_ENABLED_FOR_0_1_2, localeText, us.toString(), args[args.length - 1]));
            }
          } else {
            cs.sendMessage(C.ERROR_INVALID_LOCALE);
          }
        }
        break;
      }
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
      printHelp(cs);
    }
  }
}
