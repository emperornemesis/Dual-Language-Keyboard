import React from "react";
import {
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import Colors from "@/constants/colors";

interface SuggestionsBarProps {
  suggestions: string[];
  onSuggestionPress: (word: string) => void;
  misspelledWord?: string;
}

export default function SuggestionsBar({
  suggestions,
  onSuggestionPress,
  misspelledWord,
}: SuggestionsBarProps) {
  if (suggestions.length === 0 && !misspelledWord) return null;

  return (
    <View style={styles.container}>
      {misspelledWord && suggestions.length === 0 && (
        <View style={styles.noSuggestions}>
          <Text style={styles.noSuggestionsText}>No suggestions</Text>
        </View>
      )}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="always"
      >
        {suggestions.map((word, idx) => (
          <TouchableOpacity
            key={`${word}-${idx}`}
            style={styles.chip}
            onPress={() => onSuggestionPress(word)}
            activeOpacity={0.7}
          >
            <Text style={styles.chipText}>{word}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: Colors.light.keyboardBg,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: Colors.light.border,
    height: 44,
    paddingHorizontal: 8,
  },
  scrollContent: {
    alignItems: "center",
    paddingHorizontal: 4,
    gap: 8,
  },
  chip: {
    backgroundColor: Colors.light.keyBg,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 7,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: Colors.light.border,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 2,
    elevation: 1,
  },
  chipText: {
    fontSize: 14,
    color: Colors.light.primary,
    fontFamily: "Inter_500Medium",
  },
  noSuggestions: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  noSuggestionsText: {
    fontSize: 13,
    color: Colors.light.muted,
    fontFamily: "Inter_400Regular",
    fontStyle: "italic",
  },
});
