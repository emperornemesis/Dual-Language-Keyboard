package com.example.afrikaanskeyboard

/**
 * DictionaryManager holds word sets for English and Afrikaans and provides
 * spell-checking and prefix-based suggestion lookup.
 */
object DictionaryManager {

    enum class Language { ENGLISH, AFRIKAANS }

    // ─── English word set ─────────────────────────────────────────────────────
    private val ENGLISH: Set<String> = setOf(
        "a","able","about","above","across","after","again","against","age","ago",
        "ahead","all","also","although","always","among","an","and","another","any",
        "are","around","as","ask","asked","asks","at","back","be","because","been",
        "before","behind","being","below","between","both","bring","but","by","call",
        "called","can","cannot","case","child","children","city","come","comes","could",
        "day","days","did","do","does","doing","done","door","down","during","each",
        "early","easy","end","enough","even","ever","every","eye","eyes","face","far",
        "feel","few","find","first","for","found","free","from","full","get","give",
        "go","going","good","great","had","hand","hands","hard","has","have","he",
        "head","hear","help","her","here","high","him","his","home","house","how",
        "if","in","into","is","it","its","job","just","keep","kind","know","large",
        "last","leave","left","less","let","life","light","like","little","live",
        "long","look","made","make","man","may","me","mean","men","might","mind",
        "money","more","most","move","much","my","name","need","never","new","next",
        "night","no","nor","not","nothing","now","number","of","off","often","old",
        "on","once","one","only","open","or","other","our","out","over","own","part",
        "people","place","play","point","put","question","read","real","right","run",
        "same","say","school","see","seem","set","she","should","show","since","small",
        "so","some","soon","speak","start","still","such","take","tell","than","that",
        "the","their","them","then","there","these","they","thing","things","think",
        "this","those","though","through","time","to","today","too","turn","two","under",
        "until","up","us","use","very","want","was","way","we","week","well","were",
        "what","when","where","which","while","who","why","will","with","without",
        "word","words","work","world","would","write","year","years","yes","yet","you",
        "young","your","hello","thank","thanks","please","sorry","okay","yeah","yes",
        "maybe","probably","actually","really","usually","already","together","everyone",
        "everything","something","nothing","someone","anyone","somewhere","anywhere",
        "language","keyboard","english","spell","check","type","text","letter","button",
        "screen","phone","android","input","method","switch","setting","settings",
        "one","two","three","four","five","six","seven","eight","nine","ten",
        "hundred","thousand","million","billion","first","second","third","fourth",
        "hello","world","test","example","sample","word","typing","words","typing"
    )

    // ─── Afrikaans word set ───────────────────────────────────────────────────
    private val AFRIKAANS: Set<String> = setOf(
        "a","aan","af","afrikaans","ag","agter","al","alle","alles","almal","altyd",
        "ander","as","asseblief","baie","begin","beide","binne","bo","daar","daarna",
        "daarsonder","dag","dae","dan","dankie","dat","die","dié","dink","dit","doen",
        "dorp","deur","drie","duisend","dug","een","eerste","ek","elkeen","elke",
        "end","einde","enige","enkel","familie","feit","gaan","gee","geld","glo",
        "goed","goeder","groen","groot","groter","hallo","hand","hande","hê","help",
        "hier","hierdie","hoe","hoekom","hoeveel","hom","honderd","hoor","hou","hulle",
        "huis","hy","in","jaar","jare","ja","jou","julle","kan","kind","kinders",
        "klink","kom","kos","laag","lank","lees","lewe","lief","liefde","loop","lui",
        "maar","maak","man","meer","mense","miljoen","miljard","mooi","moet","my",
        "na","naam","nee","nie","niks","nog","nommer","nou","nuwe","of","om","ons",
        "onder","ook","oor","op","ouer","plek","praat","put","reeds","regs","roep",
        "saam","se","sê","sewe","ses","self","sien","skool","skryf","sleg","so",
        "soek","soms","stad","steeds","sterk","sy","taal","te","tien","time","toe",
        "totsiens","tyd","twee","van","ver","vier","vind","vir","vrou","vyf","waarheen",
        "wanneer","waar","wat","week","weet","wereld","werk","wie","wil","word","agt",
        "nege","drie","vier","vyf","ses","sewe","agt","nege","tien","een","twee",
        "môre","goeiemore","goeiemiddag","goeienaand","totsiens","asseblief","dankie",
        "ja","nee","moontlik","gewoonlik","reeds","saam","almal","alles","iemand",
        "iets","êrens","nêrens","iewers","nerens","altyd","nooit","dikwels",
        "sleutelbord","teks","tik","woord","woorde","taal","spel","kontrole","android",
        "invoer","metode","skakel","instelling","instellings","toets","voorbeeld"
    )

    fun getWords(language: Language): Set<String> =
        if (language == Language.ENGLISH) ENGLISH else AFRIKAANS

    /**
     * Returns true if the word is spelled correctly (or is unknown — short words
     * and proper nouns are given the benefit of the doubt).
     */
    fun isCorrect(word: String, language: Language): Boolean {
        val clean = word.lowercase().trimPunctuation()
        if (clean.length <= 2) return true
        return getWords(language).contains(clean)
    }

    /**
     * Returns up to [limit] suggestions that start with the given prefix.
     */
    fun getSuggestions(prefix: String, language: Language, limit: Int = 8): List<String> {
        val lower = prefix.lowercase().trimPunctuation()
        if (lower.isEmpty()) return emptyList()
        return getWords(language)
            .filter { it.startsWith(lower) && it != lower }
            .sortedBy { it.length }
            .take(limit)
    }

    private fun String.trimPunctuation(): String =
        trimStart { !it.isLetter() }.trimEnd { !it.isLetter() }
}
