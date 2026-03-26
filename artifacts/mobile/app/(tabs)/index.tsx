import Slider from "@react-native-community/slider";
import { Feather } from "@expo/vector-icons";
import React, { useCallback, useRef, useState } from "react";
import {
  Animated,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import Keyboard from "@/components/Keyboard";
import SuggestionsBar from "@/components/SuggestionsBar";
import Colors from "@/constants/colors";
import {
  Language,
  getSuggestions,
  spellCheck,
} from "@/constants/dictionaries";
import { KeyboardMode } from "@/constants/keyboardLayouts";

const MIN_HEIGHT = 220;
const MAX_HEIGHT = 380;
const DEFAULT_HEIGHT = 280;

interface Word {
  text: string;
  isMisspelled: boolean;
}

function parseWords(text: string, language: Language): Word[] {
  const tokens = text.split(/(\s+)/);
  return tokens.map((token) => {
    const isWord = /\S+/.test(token);
    if (!isWord) return { text: token, isMisspelled: false };
    const cleaned = token.replace(/[^a-zA-ZàáâäãåèéêëìíîïòóôöõùúûüýñßøœÀÁÂÄÃÅÈÉÊËÌÍÎÏÒÓÔÖÕÙÚÛÜÝÑßØŒ]/g, "");
    const misspelled = cleaned.length > 1 && !spellCheck(cleaned, language);
    return { text: token, isMisspelled: misspelled };
  });
}

function getLastWord(text: string): string {
  const words = text.split(/\s+/);
  return words[words.length - 1] ?? "";
}

export default function KeyboardScreen() {
  const insets = useSafeAreaInsets();
  const [inputText, setInputText] = useState("");
  const [capsLock, setCapsLock] = useState(false);
  const [mode, setMode] = useState<KeyboardMode>("alpha");
  const [language, setLanguage] = useState<Language>("english");
  const [keyboardHeight, setKeyboardHeight] = useState(DEFAULT_HEIGHT);
  const [showSlider, setShowSlider] = useState(false);
  const scrollRef = useRef<ScrollView>(null);

  const words = parseWords(inputText, language);
  const lastWord = getLastWord(inputText);
  const isMisspelled =
    lastWord.length > 1 && !spellCheck(lastWord.replace(/[^a-zA-Zàáâäãåèéêëìíîïòóôöõùúûüýñßøœ]/gi, ""), language);
  const suggestions = isMisspelled ? getSuggestions(lastWord, language) : [];

  const handleKeyPress = useCallback((key: string) => {
    setInputText((prev) => prev + key);
    scrollRef.current?.scrollToEnd({ animated: false });
  }, []);

  const handleDelete = useCallback(() => {
    setInputText((prev) => prev.slice(0, -1));
  }, []);

  const handleReturn = useCallback(() => {
    setInputText((prev) => prev + "\n");
    scrollRef.current?.scrollToEnd({ animated: false });
  }, []);

  const handleSpace = useCallback(() => {
    setInputText((prev) => prev + " ");
    scrollRef.current?.scrollToEnd({ animated: false });
  }, []);

  const handleSuggestion = useCallback((word: string) => {
    setInputText((prev) => {
      const idx = prev.lastIndexOf(lastWord);
      if (idx === -1) return prev + word;
      return prev.slice(0, idx) + word + " ";
    });
  }, [lastWord]);

  const handleClearText = () => setInputText("");

  const topPad = Platform.OS === "web" ? 67 : insets.top;

  return (
    <View style={styles.root}>
      {/* Header */}
      <View
        style={[
          styles.header,
          {
            paddingTop: topPad + 12,
            borderBottomColor: Colors.light.border,
          },
        ]}
      >
        <View style={styles.headerLeft}>
          <Text style={styles.headerTitle}>Keyboard</Text>
          <View style={styles.langPill}>
            <Text style={styles.langPillText}>
              {language === "english" ? "English" : "Afrikaans"}
            </Text>
          </View>
        </View>
        <View style={styles.headerActions}>
          <TouchableOpacity
            style={styles.headerBtn}
            onPress={() => setShowSlider((v) => !v)}
          >
            <Feather
              name="sliders"
              size={20}
              color={showSlider ? Colors.light.primary : Colors.light.muted}
            />
          </TouchableOpacity>
          <TouchableOpacity style={styles.headerBtn} onPress={handleClearText}>
            <Feather name="trash-2" size={20} color={Colors.light.muted} />
          </TouchableOpacity>
        </View>
      </View>

      {/* Height Slider */}
      {showSlider && (
        <View style={styles.sliderRow}>
          <Feather name="chevrons-down" size={16} color={Colors.light.muted} />
          <Slider
            style={styles.slider}
            minimumValue={MIN_HEIGHT}
            maximumValue={MAX_HEIGHT}
            value={keyboardHeight}
            onValueChange={(v) => setKeyboardHeight(Math.round(v))}
            minimumTrackTintColor={Colors.light.primary}
            maximumTrackTintColor={Colors.light.border}
            thumbTintColor={Colors.light.primary}
          />
          <Feather name="chevrons-up" size={16} color={Colors.light.muted} />
          <Text style={styles.sliderValue}>{keyboardHeight}px</Text>
        </View>
      )}

      {/* Text Display */}
      <ScrollView
        ref={scrollRef}
        style={styles.textArea}
        contentContainerStyle={styles.textAreaContent}
        showsVerticalScrollIndicator={false}
        keyboardShouldPersistTaps="always"
      >
        {inputText.length === 0 ? (
          <Text style={styles.placeholder}>
            {language === "english"
              ? "Start typing below..."
              : "Begin hier onder tik..."}
          </Text>
        ) : (
          <Text style={styles.inputText}>
            {words.map((word, idx) =>
              word.isMisspelled ? (
                <Text key={idx} style={styles.misspelled}>
                  {word.text}
                </Text>
              ) : (
                <Text key={idx}>{word.text}</Text>
              )
            )}
          </Text>
        )}
      </ScrollView>

      {/* Bottom Keyboard Area */}
      <View
        style={[
          styles.keyboardWrapper,
          {
            paddingBottom:
              Platform.OS === "web"
                ? 34
                : insets.bottom > 0
                ? insets.bottom
                : 8,
          },
        ]}
      >
        <SuggestionsBar
          suggestions={suggestions}
          onSuggestionPress={handleSuggestion}
          misspelledWord={isMisspelled ? lastWord : undefined}
        />
        <Keyboard
          onKeyPress={handleKeyPress}
          onDelete={handleDelete}
          onReturn={handleReturn}
          onSpace={handleSpace}
          capsLock={capsLock}
          onCapsToggle={() => setCapsLock((v) => !v)}
          mode={mode}
          onModeChange={setMode}
          language={language}
          onLanguageToggle={() =>
            setLanguage((l) => (l === "english" ? "afrikaans" : "english"))
          }
          keyboardHeight={keyboardHeight}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: Colors.light.background,
  },
  header: {
    flexDirection: "row",
    alignItems: "flex-end",
    justifyContent: "space-between",
    paddingHorizontal: 20,
    paddingBottom: 14,
    backgroundColor: Colors.light.card,
    borderBottomWidth: StyleSheet.hairlineWidth,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06,
    shadowRadius: 4,
    elevation: 2,
  },
  headerLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
  },
  headerTitle: {
    fontSize: 22,
    fontFamily: "Inter_700Bold",
    color: Colors.light.text,
  },
  langPill: {
    backgroundColor: Colors.light.primary + "18",
    borderRadius: 20,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  langPillText: {
    fontSize: 12,
    fontFamily: "Inter_600SemiBold",
    color: Colors.light.primary,
  },
  headerActions: {
    flexDirection: "row",
    gap: 4,
  },
  headerBtn: {
    padding: 8,
    borderRadius: 8,
  },
  sliderRow: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: Colors.light.card,
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: Colors.light.border,
    gap: 8,
  },
  slider: {
    flex: 1,
    height: 36,
  },
  sliderValue: {
    fontSize: 12,
    fontFamily: "Inter_500Medium",
    color: Colors.light.muted,
    width: 44,
    textAlign: "right",
  },
  textArea: {
    flex: 1,
    backgroundColor: Colors.light.card,
  },
  textAreaContent: {
    padding: 20,
    minHeight: 120,
  },
  placeholder: {
    fontSize: 16,
    color: Colors.light.muted,
    fontFamily: "Inter_400Regular",
    lineHeight: 24,
  },
  inputText: {
    fontSize: 18,
    fontFamily: "Inter_400Regular",
    color: Colors.light.text,
    lineHeight: 28,
  },
  misspelled: {
    color: Colors.light.error,
    textDecorationLine: "underline",
    textDecorationStyle: "solid",
    textDecorationColor: Colors.light.error,
  },
  keyboardWrapper: {
    backgroundColor: Colors.light.keyboardBg,
  },
  cursor: {
    width: 2,
    height: 20,
    backgroundColor: Colors.light.primary,
    borderRadius: 1,
    marginLeft: 1,
    alignSelf: "flex-end",
    marginBottom: 2,
  },
});
