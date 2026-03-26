export type Language = "english" | "afrikaans";

export const ENGLISH_WORDS = new Set([
  "a", "an", "the", "and", "or", "but", "if", "in", "on", "at", "to", "for",
  "of", "with", "by", "from", "up", "about", "into", "through", "during",
  "is", "are", "was", "were", "be", "been", "being", "have", "has", "had",
  "do", "does", "did", "will", "would", "could", "should", "may", "might",
  "shall", "can", "need", "dare", "ought", "used", "not", "no", "nor",
  "it", "its", "i", "he", "she", "we", "they", "you", "me", "him", "her",
  "us", "them", "my", "his", "your", "our", "their", "this", "that", "these",
  "those", "who", "what", "which", "when", "where", "why", "how",
  "all", "each", "every", "both", "few", "more", "most", "other", "some",
  "such", "than", "then", "too", "very", "just", "also", "only", "own",
  "same", "so", "over", "again", "any", "because", "as", "until",
  "while", "although", "though", "after", "before", "since", "unless",
  "hello", "world", "good", "great", "please", "thank", "thanks", "sorry",
  "yes", "yeah", "no", "ok", "okay", "well", "now", "here", "there",
  "home", "work", "time", "day", "year", "people", "man", "woman", "child",
  "life", "hand", "part", "place", "case", "week", "company", "system",
  "program", "question", "government", "number", "night", "point", "city",
  "small", "always", "feel", "large", "family", "name", "friend", "love",
  "know", "think", "come", "look", "want", "give", "use", "find", "tell",
  "ask", "seem", "turn", "start", "show", "hear", "call", "write", "read",
  "speak", "go", "get", "make", "take", "see", "say",
  "big", "long", "little", "right", "old", "new", "high", "free",
  "real", "best", "able", "last", "early", "true", "far", "wrong", "hard",
  "easy", "open", "next", "left", "sure", "ready", "full", "light", "dark",
  "type", "test", "text", "word", "letter", "key", "keyboard", "input",
  "language", "english", "check", "spell", "correct", "error", "suggest",
  "play", "run", "back", "way", "down", "first", "long", "think", "help",
  "off", "keep", "kind", "even", "take", "put", "old", "end", "move",
  "live", "mean", "hold", "bring", "try", "let", "set", "lose", "must",
  "still", "never", "why", "found", "become", "leave", "actually", "often",
  "things", "without", "around", "between", "something", "nothing", "everything",
  "school", "house", "book", "door", "car", "water", "food", "money", "head",
  "face", "eye", "mouth", "arm", "leg", "feet", "heart", "mind", "body",
  "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
]);

export const AFRIKAANS_WORDS = new Set([
  "die", "van", "in", "is", "ek", "het", "nie", "en", "aan", "wat",
  "op", "vir", "met", "sy", "dat", "hy", "te", "dit", "word",
  "na", "by", "se", "kan", "was", "hul", "om", "maar", "ook", "sal",
  "jy", "as", "of", "uit", "haar", "ons", "hulle", "jou", "my", "oor",
  "deur", "nou", "so", "wie", "nog", "baie", "moet", "hom", "mee",
  "wil", "toe", "al", "hoe", "man", "vrou", "kind", "huis",
  "dag", "jaar", "tyd", "werk", "plek", "mense", "land", "lewe",
  "goed", "groot", "klein", "nuwe", "ander", "eerste", "laaste",
  "kom", "gaan", "sien", "maak", "neem", "gee",
  "weet", "dink", "lees", "skryf", "praat", "vra", "loop",
  "hallo", "goeie", "more", "aand", "dankie", "asseblief",
  "totsiens", "ja", "nee", "okay", "wel", "hier", "daar", "waar",
  "hart", "liefde", "vriend", "familie", "naam", "stad", "dorp",
  "skool", "kerk", "winkel", "pad", "straat", "motor", "trein", "vliegtuig",
  "kos", "water", "brood", "melk", "vleis", "groente", "vrugte",
  "rooi", "blou", "groen", "geel", "wit", "swart", "grys", "pienk",
  "een", "twee", "drie", "vier", "vyf", "ses", "sewe", "agt", "nege", "tien",
  "afrikaans", "taal", "woord", "sleutelbord", "teks", "speltoets",
  "regte", "verkeerde", "beter", "beste", "sleg", "nuut", "oud",
  "mooi", "lelik", "vinnig", "stadig", "ver", "naby", "hoog", "laag",
  "voor", "agter", "links", "regs", "bo", "onder", "binne", "buite",
  "maand", "week", "uur", "minuut", "sekonde", "vandag", "more", "gister",
  "altyd", "nooit", "soms", "dikwels", "baiekeer", "dadelik",
  "hy", "sy", "hulle", "ons", "julle", "self", "andere", "almal",
  "iets", "niks", "alles", "iemand", "niemand", "elke", "enige",
  "dié", "daardie", "hierdie", "welke", "hoeveel", "hoekom", "wanneer",
]);

export function spellCheck(word: string, language: Language): boolean {
  const lower = word.toLowerCase().replace(/[^a-zàáâäãåèéêëìíîïòóôöõùúûüý]/gi, "");
  if (!lower || lower.length < 2) return true;
  if (language === "english") return ENGLISH_WORDS.has(lower);
  return AFRIKAANS_WORDS.has(lower);
}

export function getSuggestions(word: string, language: Language): string[] {
  const dict = language === "english" ? ENGLISH_WORDS : AFRIKAANS_WORDS;
  const lower = word.toLowerCase();
  const suggestions: string[] = [];
  for (const w of dict) {
    if (w.startsWith(lower) && w !== lower) {
      suggestions.push(w);
      if (suggestions.length >= 6) break;
    }
  }
  return suggestions;
}
