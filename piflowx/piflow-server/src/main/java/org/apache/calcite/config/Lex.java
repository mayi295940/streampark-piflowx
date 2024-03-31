package org.apache.calcite.config;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

/** StartFlinkFlowMain启动报错，暂添加此枚举类 */
public enum Lex {
  BIG_QUERY(
      Quoting.BACK_TICK,
      Casing.UNCHANGED,
      Casing.UNCHANGED,
      true,
      new CharLiteralStyle[] {CharLiteralStyle.BQ_SINGLE, CharLiteralStyle.BQ_DOUBLE}),
  ORACLE(
      Quoting.DOUBLE_QUOTE,
      Casing.TO_UPPER,
      Casing.UNCHANGED,
      true,
      new CharLiteralStyle[] {CharLiteralStyle.STANDARD}),
  MYSQL(
      Quoting.BACK_TICK,
      Casing.UNCHANGED,
      Casing.UNCHANGED,
      false,
      new CharLiteralStyle[] {CharLiteralStyle.STANDARD}),
  MYSQL_ANSI(
      Quoting.DOUBLE_QUOTE,
      Casing.UNCHANGED,
      Casing.UNCHANGED,
      false,
      new CharLiteralStyle[] {CharLiteralStyle.STANDARD}),
  SQL_SERVER(
      Quoting.BRACKET,
      Casing.UNCHANGED,
      Casing.UNCHANGED,
      false,
      new CharLiteralStyle[] {CharLiteralStyle.STANDARD}),
  JAVA(
      Quoting.BACK_TICK,
      Casing.UNCHANGED,
      Casing.UNCHANGED,
      true,
      new CharLiteralStyle[] {CharLiteralStyle.STANDARD});

  public final Quoting quoting;
  public final Casing unquotedCasing;
  public final Casing quotedCasing;
  public final boolean caseSensitive;
  public final Set<CharLiteralStyle> charLiteralStyles;

  private Lex(
      Quoting quoting,
      Casing unquotedCasing,
      Casing quotedCasing,
      boolean caseSensitive,
      CharLiteralStyle... charLiteralStyles) {
    this.quoting = (Quoting) Objects.requireNonNull(quoting);
    this.unquotedCasing = (Casing) Objects.requireNonNull(unquotedCasing);
    this.quotedCasing = (Casing) Objects.requireNonNull(quotedCasing);
    this.caseSensitive = caseSensitive;
    this.charLiteralStyles = ImmutableSet.copyOf(charLiteralStyles);
  }
}
