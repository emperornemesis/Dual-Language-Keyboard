import { Feather, Ionicons } from "@expo/vector-icons";
import * as Haptics from "expo-haptics";
import React, { useCallback } from "react";
import {
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import Colors from "@/constants/colors";
import { Language } from "@/constants/dictionaries";
import {
  ALPHA_ROWS,
  KeyboardMode,
  NUMERIC_ROW,
  SPECIAL_CHARS_PAGE1,
  SPECIAL_CHARS_PAGE2,
} from "@/constants/keyboardLayouts";

interface KeyboardProps {
  onKeyPress: (key: string) => void;
  onDelete: () => void;
  onReturn: () => void;
  onSpace: () => void;
  capsLock: boolean;
  onCapsToggle: () => void;
  mode: KeyboardMode;
  onModeChange: (mode: KeyboardMode) => void;
  language: Language;
  onLanguageToggle: () => void;
  keyboardHeight: number;
}

function triggerHaptic() {
  if (Platform.OS !== "web") {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light).catch(() => {});
  }
}

function Key({
  label,
  onPress,
  style,
  textStyle,
  icon,
  flex,
  disabled,
}: {
  label?: string;
  onPress: () => void;
  style?: object;
  textStyle?: object;
  icon?: React.ReactNode;
  flex?: number;
  disabled?: boolean;
}) {
  return (
    <TouchableOpacity
      style={[styles.key, flex ? { flex } : {}, style]}
      onPress={() => {
        triggerHaptic();
        onPress();
      }}
      activeOpacity={0.6}
      disabled={!!disabled}
    >
      {icon ? icon : <Text style={[styles.keyText, textStyle]}>{label}</Text>}
    </TouchableOpacity>
  );
}

export default function Keyboard({
  onKeyPress,
  onDelete,
  onReturn,
  onSpace,
  capsLock,
  onCapsToggle,
  mode,
  onModeChange,
  language,
  onLanguageToggle,
  keyboardHeight,
}: KeyboardProps) {
  const getRows = useCallback(() => {
    if (mode === "alpha") return ALPHA_ROWS;
    if (mode === "special1") return SPECIAL_CHARS_PAGE1;
    return SPECIAL_CHARS_PAGE2;
  }, [mode]);

  const displayChar = (ch: string) =>
    mode === "alpha" && capsLock ? ch.toUpperCase() : ch;

  const modeLabel =
    mode === "alpha" ? "!#1" : mode === "special1" ? "ABC" : "!#1";

  const rows = getRows();

  const rowHeight = (keyboardHeight - 40 - 50) / (rows.length + 1);

  return (
    <View style={[styles.container, { height: keyboardHeight }]}>
      {/* Numeric Row */}
      <View style={[styles.row, { height: rowHeight }]}>
        {NUMERIC_ROW.map((num) => (
          <Key
            key={num}
            label={num}
            onPress={() => onKeyPress(num)}
            style={styles.numKey}
            textStyle={styles.numKeyText}
          />
        ))}
      </View>

      {/* Alpha / Symbol Rows */}
      {rows.map((row, rowIdx) => (
        <View key={rowIdx} style={[styles.row, { height: rowHeight }]}>
          {rowIdx === rows.length - 1 && mode === "alpha" && (
            <TouchableOpacity
              style={[styles.key, styles.fnKey, styles.capsKey]}
              onPress={() => {
                triggerHaptic();
                onCapsToggle();
              }}
              activeOpacity={0.6}
            >
              <Ionicons
                name={capsLock ? "arrow-up-circle" : "arrow-up-circle-outline"}
                size={20}
                color={capsLock ? Colors.light.primary : Colors.light.keyText}
              />
            </TouchableOpacity>
          )}
          {rowIdx === rows.length - 1 && mode !== "alpha" && (
            <Key
              label={mode === "special1" ? "pg2" : "pg1"}
              onPress={() =>
                onModeChange(mode === "special1" ? "special2" : "special1")
              }
              style={[styles.fnKey, styles.capsKey]}
              textStyle={styles.fnKeyText}
            />
          )}

          {row.map((char, charIdx) => (
            <Key
              key={`${rowIdx}-${charIdx}`}
              label={displayChar(char)}
              onPress={() => onKeyPress(displayChar(char))}
              style={styles.alphaKey}
            />
          ))}

          {rowIdx === rows.length - 1 && (
            <TouchableOpacity
              style={[styles.key, styles.fnKey, styles.deleteKey]}
              onPress={() => {
                triggerHaptic();
                onDelete();
              }}
              activeOpacity={0.6}
            >
              <Feather name="delete" size={18} color={Colors.light.keyText} />
            </TouchableOpacity>
          )}
        </View>
      ))}

      {/* Bottom Row */}
      <View style={[styles.row, styles.bottomRow, { height: 50 }]}>
        {/* Mode Toggle */}
        <Key
          label={modeLabel}
          onPress={() => {
            if (mode === "alpha") onModeChange("special1");
            else onModeChange("alpha");
          }}
          style={[styles.fnKey, { width: 48 }]}
          textStyle={styles.fnKeyText}
        />

        {/* Language Toggle */}
        <TouchableOpacity
          style={[styles.key, styles.fnKey, { width: 56 }]}
          onPress={() => {
            triggerHaptic();
            onLanguageToggle();
          }}
          activeOpacity={0.6}
        >
          <View style={styles.langBadge}>
            <Ionicons name="globe-outline" size={14} color={Colors.light.keyText} />
            <Text style={styles.langText}>
              {language === "english" ? "EN" : "AF"}
            </Text>
          </View>
        </TouchableOpacity>

        {/* Space */}
        <TouchableOpacity
          style={[styles.key, styles.spaceKey, { flex: 1 }]}
          onPress={() => {
            triggerHaptic();
            onSpace();
          }}
          activeOpacity={0.6}
        >
          <Text style={styles.spaceLabel}>
            {language === "english" ? "space" : "spasie"}
          </Text>
        </TouchableOpacity>

        {/* Return */}
        <TouchableOpacity
          style={[styles.key, styles.returnKey, { width: 82 }]}
          onPress={() => {
            triggerHaptic();
            onReturn();
          }}
          activeOpacity={0.6}
        >
          <Ionicons name="return-down-back" size={18} color="#fff" />
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.light.keyboardBg,
    paddingHorizontal: 3,
    paddingBottom: 4,
    paddingTop: 4,
    gap: 4,
  },
  row: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    gap: 5,
    paddingHorizontal: 2,
  },
  bottomRow: {
    gap: 5,
    paddingHorizontal: 4,
  },
  key: {
    backgroundColor: Colors.light.keyBg,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    flex: 1,
    height: "100%",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.15,
    shadowRadius: 0,
    elevation: 2,
    minHeight: 36,
  },
  keyText: {
    fontSize: 17,
    color: Colors.light.keyText,
    fontFamily: "Inter_400Regular",
  },
  alphaKey: {
    flex: 1,
  },
  fnKey: {
    backgroundColor: Colors.light.keyFnBg,
    flex: 0,
  },
  numKey: {
    flex: 1,
    backgroundColor: Colors.light.keyFnBg,
  },
  numKeyText: {
    fontSize: 15,
    color: Colors.light.keyText,
    fontFamily: "Inter_400Regular",
  },
  fnKeyText: {
    fontSize: 13,
    color: Colors.light.keyText,
    fontFamily: "Inter_500Medium",
  },
  capsKey: {
    width: 42,
  },
  deleteKey: {
    width: 42,
  },
  spaceKey: {
    backgroundColor: Colors.light.keyBg,
  },
  spaceLabel: {
    fontSize: 14,
    color: Colors.light.muted,
    fontFamily: "Inter_400Regular",
  },
  returnKey: {
    backgroundColor: Colors.light.primary,
    borderRadius: 5,
  },
  langBadge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 2,
  },
  langText: {
    fontSize: 11,
    fontFamily: "Inter_600SemiBold",
    color: Colors.light.keyText,
  },
});
