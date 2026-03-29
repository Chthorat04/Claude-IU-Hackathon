# Design System Strategy: The Methodical Calm

## 1. Overview & Creative North Star
The "Creative North Star" for this system is **The Clinical Zen**. 

In high-stakes first-aid scenarios, the UI must act as a composed, expert mediator. We are merging the meticulous, airy precision of high-end editorial design with the functional reliability of medical instrumentation. By adopting an "Apple-inspired" ethos for the Android ecosystem, we move away from the heavy shadows and loud FABs (Floating Action Buttons) of standard Material Design. Instead, we embrace a "Flat-Layer" philosophy—where hierarchy is defined by surgically precise typography and subtle shifts in surface tone rather than physical elevation.

This system breaks the "template" look by using a restricted font weight scale (maxing at 500) to ensure the UI feels sophisticated and lightweight, even when delivering critical, life-saving information.

---

## 2. Colors: Tonal Architecture
Our palette uses functional color to provide immediate cognitive anchoring without inducing panic.

### The "No-Line" Rule
While the original specs call for a card border, we are evolving this into a **Tonal Boundary** system. Primary sectioning must be achieved through background shifts (e.g., using `surface-container-low` against a `surface` background). If a boundary is strictly required for accessibility, use the **Ghost Border**: `outline-variant` at 20% opacity. Never use 100% opaque, high-contrast black or grey lines.

### Surface Hierarchy & Nesting
Treat the screen as a stack of fine-grain paper. 
- **Base Layer:** `surface` (#f9f9fb)
- **Content Cards:** `surface-container-lowest` (#ffffff)
- **Input Fields/Search:** `surface-container` (#eeeef0)

### Glass & Gradient (The Premium Polish)
To avoid a "flat-web" look, use **Atmospheric Blurs** for the Top App Bar (56dp). Use a semi-transparent `surface` with a 20px backdrop blur. For the Teal (Primary) CTAs, apply a subtle linear gradient from `primary` (#006162) to `primary-container` (#2c7a7b) at a 135° angle to provide depth without adding visual "weight."

---

## 3. Typography: Editorial Authority
We use **Plus Jakarta Sans** exclusively. By excluding Bold weights, we force a reliance on scale and spacing to create a high-end, "Architectural" hierarchy.

| Level | Size | Weight | Tracking | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| **Display-LG** | 3.5rem | 500 | -0.02em | Emergency Headers / Status |
| **Title-LG** | 1.375rem | 500 | -0.01em | Step-by-step instructions |
| **Title-SM** | 1rem | 500 | 0 | Category headers |
| **Body-MD** | 0.875rem | 400 | +0.01em | General descriptive text |
| **Label-SM** | 0.6875rem | 300 | +0.03em | Micro-data / Offline status |

**Editorial Note:** Use tight tracking on Display sizes to create a "custom-type" feel, and slightly wider tracking on Labels to ensure legibility in low-light emergency environments.

---

## 4. Elevation & Depth: Tonal Layering
Traditional Android shadows are prohibited. Depth is achieved via **The Layering Principle**.

- **Ambient Shadows:** Only for floating voice-activation triggers. Use a 16px blur, 0px offset, and 6% opacity of `on-surface` (#1a1c1d). This mimics a soft glow rather than a harsh drop shadow.
- **The 4dp Accent Strip:** For category cards, use the left-border accent strip in the semantic color (Teal, Red, Amber). This acts as a "Visual Tab," guiding the eye vertically through a list without needing icons for every entry.
- **Interaction Depth:** On press, a card should not "lift." Instead, it should transition from `surface-container-lowest` (#ffffff) to `surface-container` (#eeeef0), creating a "pressed-in" tactile feel.

---

## 5. Components: The Clinical Set

### Cards & Lists
*   **The Zero-Divider Policy:** Lists must never use horizontal divider lines. Separation is achieved through `1.2rem` (Space-3.5) vertical gutters.
*   **Card Anatomy:** 14dp radius, 1dp `outline-variant` at 20% opacity. Every card must have a 20dp internal padding to maintain the "Airy" aesthetic.

### Pill Buttons (Action Primary)
*   **Geometry:** 100px radius (Full pill).
*   **Styling:** Gradient fill (`primary` to `primary-container`). Label-MD (500 weight) in `on-primary` (#ffffff).
*   **Tap Target:** Always 48dp minimum, even if the visual pill is smaller.

### Voice-Enabled Input (Custom Component)
*   **The "Pulse" Surface:** A floating circular button using Glassmorphism (70% opacity `surface-container-lowest`) with a Teal (`primary`) outline that pulses subtly.

### Input Fields
*   **Styling:** No bottom-line-only inputs. Use a fully enclosed box with a `sm` (0.5rem) radius and a `surface-container-low` background. This provides a "recessed" look that feels more modern on Android than standard Material under-lines.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical white space. If a card header is 28sp, give it more bottom margin than top margin to create a "pushed down" editorial look.
*   **Do** use semantic colors (Red, Amber, Green) sparingly. They should be "islands of color" in a sea of #F5F5F7.
*   **Do** ensure all outline icons have a consistent 1.5px or 2px stroke weight. Mixing stroke weights ruins the minimalist "Apple" precision.

### Don't:
*   **Don't** use Bold (600+) weights. If you need more emphasis, increase the font size or use the Teal primary color for the text.
*   **Don't** use Material "Ripple" effects with high opacity. Keep ripples extremely subtle (8% opacity) to maintain the "Zen" feel.
*   **Don't** use decorative illustrations. If the user is looking for "How to stop bleeding," an illustration of a happy person is a cognitive distraction. Use only functional, high-contrast icons.