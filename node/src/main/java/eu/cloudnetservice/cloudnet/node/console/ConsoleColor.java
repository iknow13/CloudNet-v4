/*
 * Copyright 2019-2022 CloudNetService team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.node.console;

import java.util.regex.Pattern;
import lombok.NonNull;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;
import org.jetbrains.annotations.Nullable;

public enum ConsoleColor {

  BLACK("black", '0', Ansi.ansi().reset().fg(Color.BLACK).toString()),
  DARK_BLUE("dark_blue", '1', Ansi.ansi().reset().fg(Color.BLUE).toString()),
  GREEN("green", '2', Ansi.ansi().reset().fg(Color.GREEN).toString()),
  CYAN("cyan", '3', Ansi.ansi().reset().fg(Color.CYAN).toString()),
  DARK_RED("dark_red", '4', Ansi.ansi().reset().fg(Color.RED).toString()),
  PURPLE("purple", '5', Ansi.ansi().reset().fg(Color.MAGENTA).toString()),
  ORANGE("orange", '6', Ansi.ansi().reset().fg(Color.YELLOW).toString()),
  GRAY("gray", '7', Ansi.ansi().reset().fg(Color.WHITE).toString()),
  DARK_GRAY("dark_gray", '8', Ansi.ansi().reset().fg(Color.BLACK).bold().toString()),
  BLUE("blue", '9', Ansi.ansi().reset().fg(Color.BLUE).bold().toString()),
  LIGHT_GREEN("light_green", 'a', Ansi.ansi().reset().fg(Color.GREEN).bold().toString()),
  AQUA("aqua", 'b', Ansi.ansi().reset().fg(Color.CYAN).bold().toString()),
  RED("red", 'c', Ansi.ansi().reset().fg(Color.RED).bold().toString()),
  PINK("pink", 'd', Ansi.ansi().reset().fg(Color.MAGENTA).bold().toString()),
  YELLOW("yellow", 'e', Ansi.ansi().reset().fg(Color.YELLOW).bold().toString()),
  WHITE("white", 'f', Ansi.ansi().reset().fg(Color.WHITE).bold().toString()),
  OBFUSCATED("obfuscated", 'k', Ansi.ansi().a(Attribute.BLINK_SLOW).toString()),
  BOLD("bold", 'l', Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString()),
  STRIKETHROUGH("strikethrough", 'm', Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString()),
  UNDERLINE("underline", 'n', Ansi.ansi().a(Attribute.UNDERLINE).toString()),
  ITALIC("italic", 'o', Ansi.ansi().a(Attribute.ITALIC).toString()),
  DEFAULT("default", 'r', Ansi.ansi().reset().toString());

  private static final ConsoleColor[] VALUES = values();
  private static final String LOOKUP = "0123456789abcdefklmnor";
  private static final String RGB_ANSI = "\u001B[38;2;%d;%d;%dm";

  private final String name;
  private final String ansiCode;
  private final char index;

  ConsoleColor(String name, char index, String ansiCode) {
    this.name = name;
    this.index = index;
    this.ansiCode = ansiCode;
  }

  public static @NonNull String toColouredString(char triggerChar, @NonNull String text) {
    var content = convertRGBColors(triggerChar, text);

    var breakIndex = content.length() - 1;
    for (var i = 0; i < breakIndex; i++) {
      if (content.charAt(i) == triggerChar) {
        var format = LOOKUP.indexOf(content.charAt(i + 1));
        if (format != -1) {
          var ansiCode = VALUES[format].ansiCode();

          content.delete(i, i + 2).insert(i, ansiCode);
          breakIndex += ansiCode.length() - 2;
        }
      }
    }

    return content.toString();
  }

  private static @NonNull StringBuffer convertRGBColors(char triggerChar, @NonNull String input) {
    var matcher = Pattern.compile(triggerChar + "#([0-9a-fA-F]){6}").matcher(input);
    var stringBuffer = new StringBuffer();

    while (matcher.find()) {
      var temp = matcher.group().replace(String.valueOf(triggerChar), "");
      var color = java.awt.Color.decode(temp);

      matcher.appendReplacement(stringBuffer,
        String.format(RGB_ANSI, color.getRed(), color.getGreen(), color.getBlue()));
    }

    matcher.appendTail(stringBuffer);
    return stringBuffer;
  }

  public static @NonNull String stripColor(char triggerChar, @NonNull String input) {
    var content = stripRGBColors(triggerChar, input);

    var breakIndex = content.length() - 1;
    for (var i = 0; i < breakIndex; i++) {
      if (content.charAt(i) == triggerChar && LOOKUP.indexOf(content.charAt(i + 1)) != -1) {
        content.delete(i, i + 2);
        breakIndex -= 2;
      }
    }

    return content.toString();
  }

  private static @NonNull StringBuffer stripRGBColors(char triggerChar, @NonNull String input) {
    var matcher = Pattern.compile(triggerChar + "#([0-9a-fA-F]){6}").matcher(input);
    var stringBuffer = new StringBuffer();

    while (matcher.find()) {
      matcher.appendReplacement(stringBuffer, "");
    }

    matcher.appendTail(stringBuffer);
    return stringBuffer;
  }

  public static @Nullable ConsoleColor byChar(char index) {
    for (var color : VALUES) {
      if (color.index == index) {
        return color;
      }
    }

    return null;
  }

  public static @Nullable ConsoleColor lastColor(char triggerChar, @NonNull String text) {
    text = text.trim();
    if (text.length() > 2 && text.charAt(text.length() - 2) == triggerChar) {
      return byChar(text.charAt(text.length() - 1));
    }

    return null;
  }

  @Override
  public @NonNull String toString() {
    return this.ansiCode;
  }

  public @NonNull String displayName() {
    return this.name;
  }

  public @NonNull String ansiCode() {
    return this.ansiCode;
  }

  public char index() {
    return this.index;
  }
}
