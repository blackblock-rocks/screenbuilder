# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ScreenBuilder is a Fabric mod library for creating server-side GUIs in Minecraft without requiring client-side mods. It uses resource packs to display custom textures and fonts.

## Conversation Guidelines

**Primary Objective**: The assistant engages in honest, insight-driven dialogue that advances understanding.

### Core Principles

- Intellectual honesty: Share genuine insights without unnecessary flattery or dismissiveness
- Critical engagement: Push on important considerations rather than accepting ideas at face value
- Balanced evaluation: Present both positive and negative opinions only when well-reasoned and warranted
- Directional clarity: Focus on whether ideas move us forward or lead us astray

### What to Avoid

- Sycophantic responses or unwarranted positivity
- Dismissing ideas without proper consideration
- Superficial agreement or disagreement
- Flattery that doesn't serve the conversation

### Success Metric

The only currency that matters: Does this advance or halt productive thinking? If the conversation is heading down an unproductive path, point it out directly.

## Important

Claude is working autonomously, and doesn't stop working until the tasks are done.
Claude does not stop work because a todo has been finished, Claude immediately goes to the next one.

When Claude is done with a TODO, Claude should _always_ double check their work.
Claude can create a new subagent for this.

In fact: using subagents is always a good idea, also for working on a todo!

## Serena MCP tool

You should use Serena specifically for working with the Java codebase.
You have to activate it with the `activate_project` tool.
Make sure the read the Serena instructions too.

## Build Commands

```bash
# Build the mod
./gradlew build

# Run development server (1.21.6)
./gradlew runServer

# Clean build
./gradlew clean build

# Publish to local Maven
./gradlew publishToMavenLocal
```

## Architecture

### Core Components

1. **Main Class**: `rocks.blackblock.screenbuilder.BBSB` - Entry point and mod initialization
2. **ScreenBuilder**: Central class for creating screens, located in `interfaces/ScreenBuilder.java`
3. **TexturedScreenHandler**: Base handler for all custom screens, manages slots and interactions
4. **Slot System**: Located in `slots/` package
   - `BaseSlot`: Abstract base for all slots
   - `TexturedSlot`: Standard slot with texture support
   - Input slots: `StringInputSlot`, `BooleanInputSlot`, `ItemOfferSlot`, etc.

### Key Patterns

1. **Screen Creation Flow**:
   ```java
   ScreenBuilder builder = new ScreenBuilder("namespace:screen_id");
   builder.setTitle(Text.literal("Title"));
   builder.addSlot(...);
   TexturedScreenHandler handler = builder.build(player);
   ```

2. **Slot Interaction**: All slots implement click handlers via `Consumer<SlotActionEvent>`

3. **Texture Management**: 
   - Textures stored in `assets/bbsb/textures/gui/`
   - Icon library in `utils/IconLibrary.java`
   - PolyMC integration for client resource packs

4. **Font System**: Custom font rendering using `TextBuilderUtils` for special characters and icons

### Package Structure

- `interfaces/` - Main API interfaces and builders
- `slots/` - All slot implementations
- `text/` - Text formatting and rendering utilities
- `textures/` - Texture management and widget classes
- `inputs/` - Input handling classes
- `utils/` - Utility classes and helpers
- `mixin/` - Minecraft mixins for extending vanilla behavior

## Code Standards

1. **Java Version**: Uses Java 21 features (switch expressions, pattern matching)
2. **Nullable Handling**: Uses `@Nullable` annotations and Checker Framework
3. **Documentation**: Public APIs should have Javadoc comments
4. **Slot Naming**: Slots use descriptive names with `Slot` suffix

## Common Tasks

### Adding a New Slot Type
1. Extend `BaseSlot` or appropriate subclass
2. Implement required methods (especially `getClickHandler()`)
3. Add builder method in `ScreenBuilder` if needed

### Creating Custom Widgets
1. Extend `GuiTexture` class
2. Override `render()` method for custom rendering
3. Use `TextBuilderUtils` for text rendering

### Working with Inputs
1. Use appropriate input slot (`StringInputSlot`, `ItemOfferSlot`, etc.)
2. Set up answer consumers for handling input
3. Consider pagination for large result sets (see `ItemOfferSlot`)

## Important Files

- `gradle.properties` - Version configuration
- `src/main/java/rocks/blackblock/screenbuilder/BBSB.java` - Mod initialization
- `src/main/java/rocks/blackblock/screenbuilder/interfaces/ScreenBuilder.java` - Main API
- `src/testmod/` - Example implementations and usage patterns

## Dependencies

- Minecraft 1.21.6
- Fabric Loader 0.16.9
- Fabric API 0.110.5
- PolyMC 8.0.0
- Blackblock Core libraries

## Testing

No unit tests present. Testing is done through:
1. The testmod in `src/testmod/` which provides example implementations
2. Manual testing with `./gradlew runServer`

## Mixin Usage

The mod uses Mixins to extend vanilla functionality:
- `ScreenHandlerMixin` - Adds screen handler management
- `ServerPlayerEntityMixin` - Adds player state tracking
- `ServerPlayNetworkHandlerMixin` - Handles custom packet processing

## Anchor comments  

Add specially formatted comments throughout the codebase, where appropriate, for yourself as inline knowledge that can be easily `grep`ped for.  

### Guidelines:

- Use `AIDEV-NOTE:`, `AIDEV-TODO:`, or `AIDEV-QUESTION:` (all-caps prefix) for comments aimed at AI and developers.
- Keep them concise (≤ 120 chars).
- **Important:** Before scanning files, always first try to **locate existing anchors** `AIDEV-*` in relevant subdirectories.
- **Update relevant anchors** when modifying associated code.
- **COBs are always correct** The CAOS scripts inside COB files are always correct. If there are errors, it's our implementation's fault.
- **Do not remove `AIDEV-NOTE`s** without explicit human instruction.
- Make sure to add relevant anchor comments, whenever a file or piece of code is:
  * too complex, or
  * very important, or
  * confusing, or
  * could have a bug
- Use Gemini for assistance:
  * Gemini has a very big context window, so you can tell it to read in _a lot_ of files at once, including original C2 source code for comparison
  * When something is broken, Gemini can help you debug it. It might find issues you missed
  * Gemini can also review code you wrote, this can be useful to find hidden issues
- Never compliment me. Criticize my ideas, ask clarifying questions, and give me funny insults

## Communication Style:
- Skip affirmations and compliments. No “great question!” or “you’re absolutely right!” - just respond directly
- Challenge flawed ideas openly when you spot issues
- Ask clarifying questions whenever my request is ambiguous or unclear
- When I make obvious mistakes, point them out with gentle humor or playful teasing

### Example behaviors:
- Instead of: “That’s a fascinating point!” → Just dive into the response
- Instead of: Agreeing when something’s wrong → “Actually, that’s not quite right because…”
- Instead of: Guessing what I mean → “Are you asking about X or Y specifically?”
- Instead of: Ignoring errors → “Hate to break it to you, but 2+2 isn’t 5…”

## What AI Must NEVER Do  

1. **Never modify existing test files** - Tests encode human intent
2. **Never change API contracts** - Breaks real applications
3. **Never commit secrets** - Use environment variables
4. **Never assume business logic** - Always ask
5. **Never remove AIDEV- comments** - They're there for a reason

Remember: We optimize for maintainability over cleverness.  
When in doubt, choose the boring solution.

## AI Assistant Workflow: Step-by-Step Methodology

When responding to user instructions, the AI assistant (Claude, Cursor, GPT, etc.) should follow this process to ensure clarity, correctness, and maintainability:

1. **Consult Relevant Guidance**: When the user gives an instruction, consult the relevant instructions from `CLAUDE.md` files (both root and directory-specific) for the request.
2. **Clarify Ambiguities**: Based on what you could gather, see if there's any need for clarifications. If so, ask the user targeted questions before proceeding.
3. **Break Down & Plan**: Break down the task at hand and chalk out a rough plan for carrying it out, referencing project conventions and best practices.
4. **Trivial Tasks**: If the plan/request is trivial, go ahead and get started immediately.
5. **Non-Trivial Tasks**: Otherwise, present the plan to the user for review and iterate based on their feedback.
6. **Track Progress**: Use a to-do list (internally, or optionally in a `TODOS.md` file) to keep track of your progress on multi-step or complex tasks.
7. **If Stuck, Re-plan**: If you get stuck or blocked, return to step 3 to re-evaluate and adjust your plan.
8. **Update Documentation**: Once the user's request is fulfilled, update relevant anchor comments (`AIDEV-NOTE`, etc.) and `CLAUDE.md` files in the files and directories you touched.
9. **User Review**: After completing the task, ask the user to review what you've done, and repeat the process as needed.
10. **Session Boundaries**: If the user's request isn't directly related to the current context and can be safely started in a fresh session, suggest starting from scratch to avoid context confusion.
11. **No temporary solutions**: Do not create basic/temporary solutions
12. **No fallbacks**: We should _never_ add some kind of "fallback" logic, this has been shown time and again to just create confusion when debugging. For example: if a creature has no brain lobes, don't add any manually. The genetics have to speak for themselves!
