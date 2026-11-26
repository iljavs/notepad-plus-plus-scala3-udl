# Scala 3 syntax highlighting for Notepad++

A User Defined Language (UDL) file that teaches Notepad++ to highlight Scala 3, registered for
`.scala`, `.sc` and `.sbt`. It ships a light and a dark theme in one file. Nothing to compile, and
nothing to install beyond dropping one XML file in a folder.

It assumes you have Notepad++ and want readable Scala in it. It is not a language server; see
[Known limitations](#known-limitations) for what a UDL cannot do.

## Why this exists

The [official Notepad++ UDL collection](https://github.com/notepad-plus-plus/userDefinedLanguages)
does carry a Scala definition, `Scala_byMihailJP.xml`. Its own declared version is
`Sat, 19 Jan 2013 10:26:35 GMT`, and its keyword content has not been edited since it entered the
collection in 2020; the two commits touching it since were an attribute-schema normalisation and an
XML prolog fix. 2013 predates Scala 3.0.0 by eight years.

That matters because Scala 3 changed the keyword set substantially. It added four regular keywords
(`enum`, `export`, `given`, `then`) and ten word-form soft keywords (`as`, `derives`, `end`,
`extension`, `infix`, `inline`, `opaque`, `open`, `transparent`, `using`), and it dropped existential
types, so `forSome` no longer appears in usable code. Against a Scala 2 definition, `given`, `using`,
`extension` and `enum` are left unstyled, and those are most of what distinguishes Scala 3 source.

Every other public Scala UDL I could find targets Scala 2 as well. The closest to Scala 3 is a file
inside an Akka examples repository that bolts four Dotty-era tokens onto that same 2013 list.

## Install

Two routes. The first keeps the file as its own file, so updating later is a copy. The second is
quicker but folds the definition into Notepad++'s own configuration.

### Drop it in the folder (recommended)

1. Download [`Scala3.xml`](Scala3.xml).
2. In Notepad++, choose **Language > User Defined Language > Open User Defined Language folder...**
   That opens the right folder whichever way Notepad++ is installed. For a standard Windows install
   it is `%AppData%\Notepad++\userDefineLangs\`; for a portable or local-configuration install it is
   `userDefineLangs\` under the program directory. If the folder does not exist yet, create it.
3. Copy `Scala3.xml` into it. The filename is not significant, because the names shown in the menu
   come from inside the file.
4. Close Notepad++ completely and start it again. Closing one window is not always enough, since
   Notepad++ runs single-instance by default, and the folder is read only at startup.
5. Open a `.scala` file. To confirm it loaded, look at the bottom of the **Language** menu, where
   `Scala 3` and `Scala 3 (Dark)` are listed.

### Import it from inside Notepad++

1. **Language > User Defined Language > Define your language...**
2. Click **Import...** and select `Scala3.xml`. A dialog confirms `Import successful.`
3. It is available immediately, with no restart.

This copies the definition into Notepad++'s single `userDefineLang.xml`, so the file in this repo
stops being the live copy. To update later, remove the old definition first with the **Remove**
button in that same dialog.

### Light and dark

Both themes declare the same extensions. When Notepad++ opens a file and picks the language from its
extension, it selects the theme matching the current mode; `Scala 3 (Dark)` carries the
`darkModeTheme="yes"` marker that drives this. Both stay listed in the Language menu if you want to
force one.

Switching Notepad++ between dark and light does not restyle a document that is already open. Reopen
the file. A fix for that landed upstream on 2026-11-24 and is not in a release as of v8.9.9.1.

### Requirements

Notepad++ 8.1.3 or later, which is the release that added the `darkModeTheme` attribute. Earlier
versions load the file but ignore that marker, so both themes appear in the menu and you pick one
yourself.

To remove it again, delete `Scala3.xml` from the folder and restart Notepad++, or use **Remove** in
the UDL dialog if you imported it.

## What it highlights

Keywords, in six separately coloured categories, so that `given` does not look like `if`:

- All 41 regular Scala 3 keywords and all ten word-form soft keywords, checked against the official
  Scala 3 reference.
- The experimental soft modifiers `into`, `erased`, `tracked`, `consume` and `update`, so code using
  `language.experimental` features still highlights.
- Declarations, control flow, modifiers, soft keywords, literals (`true`, `false`, `null`) and the
  19 built-in types (`Any`, `AnyKind`, `AnyRef`, `AnyVal`, `Matchable`, `Nothing`, `Null`,
  `Singleton`, `Unit` and the primitives) are each a distinct colour. Standard-library types such as
  `List` and `Option` are deliberately absent: the list covers the language's own types, not the
  library's.
- Case-sensitive, as Scala is, so the literal `null` and the type `Null` are styled differently.

Strings, which is where most hand-written highlighters come apart:

- Triple-quoted strings, including Scala's rule that the closing run may be longer than three, so
  `"""ends with a quote""""` terminates in the right place.
- The `s`, `f` and `raw` interpolator prefixes, in both the `s"..."` and `s"""..."""` forms.
- `$name` and `${ expr }` are highlighted inside interpolated strings and, correctly, not inside
  plain `"..."` strings.
- Code inside `${ ... }` is highlighted as code. Keywords, numbers, operators and nested strings all
  apply there, and the block itself takes the normal text colour rather than the string colour.
- A single-quoted string ends at the line break, so one unterminated quote does not miscolour the
  rest of the file, while triple-quoted strings span lines as they should.
- An apostrophe inside a comment or a string cannot open a stray character literal, so `// don't` and
  `"it's"` do not break everything after them.
- Scala 3 macro quotation, `'{ ... }`, is not mistaken for a character literal, which is the trap
  most hand-written highlighters fall into.

The rest:

- Backtick-quoted identifiers get their own colour, distinct from strings.
- Nested block comments, so `/* outer /* inner */ outer */` is handled.
- Numbers: decimal, hex `0xFF`, binary `0b0010_0000`, underscore digit separators, `L`/`F`/`D`
  suffixes and signed exponents.
- Scala-specific operators including `<:<`, `=:=`, `=>>`, `?=>`, `<-`, `<:`, `>:`, `=>`, `::` and
  `:=`, plus around 40 Unicode mathematical glyphs (⊕, ∘, ∈, ≤, ⟹ and so on) for libraries that
  define operators spelled with symbols. Those glyphs are legal Scala operator identifiers rather
  than built-in operators, so an `extension (a: Int) def ⊕ (b: Int)` of your own is coloured as the
  operator it is.
- Brace folding, and comment folding.

The two themes are the same definition twice over: their keyword lists and settings are
byte-identical, and all 24 style entries agree on everything except colour.

[`sample.scala`](sample.scala) exercises the constructs above. Open it after installing to see the
whole set at once.

## Known limitations

A UDL is a lexer with no parser and no symbol table. It colours tokens by their shape and their
position in the character stream, never by meaning. That bounds what any file of this kind can do,
and the following are not bugs that can be fixed here:

- **Soft keywords are soft because they remain legal identifiers.** `as`, `end`, `open`, `using`,
  `extension`, `infix`, `into` and `update` are coloured wherever they appear, including as a
  variable or a method name, so `val end = 3` and `list.as(x)` colour them as keywords.
- **Indentation syntax cannot be folded.** Folding works on `{` and `}` only, so a file written in
  Scala 3's brace-less style has no fold points. `end` markers are recognised as keywords but cannot
  close a fold.
- **Custom interpolators are an open set.** Only `s`, `f` and `raw` can be enumerated, so `sql"..."`
  or `json"$x"` is coloured as a plain string and the interpolations inside it are not highlighted.
- **Colouring cannot be position-sensitive.** `case` in `case class` and `case` in a match arm share
  one colour. So do `match` the expression and `match` the match type, and `+` as a variance
  annotation and `+` as addition.
- **The Unicode operators are a subset, not a specification.** Scala lets you build operators out of
  Unicode symbol characters, so no fixed list can be complete.
- **Colours are fixed in the file** rather than inherited from the active Notepad++ theme, so a
  third-party theme may clash with them.

One real gap, which is fixable and not yet fixed:

- **Character literals are only partly highlighted.** Escape sequences, digits, `'"'` and the
  backtick are coloured, so `'\n'`, `'\''`, `'\\'` and `'0'` are fine, but a literal holding a
  letter, a space or ordinary punctuation is not, which leaves `'a'`, `' '` and `'+'` at the default
  colour.

And no semantic features at all: no go-to-definition, no error squiggles, no auto-import, no type
information. For those, use Metals in an LSP-capable editor. This file is for reading and quick edits
in Notepad++.

It was built and checked by inspection rather than against a large corpus of real Scala 3, so
unusual constructs may still surprise it.

## Reporting a problem

Open an issue with a short snippet that reproduces it, the Notepad++ version
(**? > Debug Info...**), and which of the two themes you are using. A snippet is far more useful than
a description, because the question is almost always which token the lexer opened, and where.

## Credits

The definition is hand-written. Reference material: the
[Scala 3 reference](https://docs.scala-lang.org/scala3/reference/) for the keyword set, and the
[Notepad++ user manual](https://npp-user-manual.org/docs/user-defined-language-system/) for the UDL
format.

| date | change |
|---|---|
| 2026-11-26 | written |
