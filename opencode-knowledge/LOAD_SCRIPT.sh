#!/usr/bin/env bash
# opencode-load-knowledge.sh — Load all Kalc session knowledge into OpenCode context
# Usage: Run this once when starting OpenCode, or paste the contents as a system prompt.

set -euo pipefail

KNOWLEDGE_DIR="$(cd "$(dirname "$0")/opencode-knowledge" && pwd)"
CALC_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== Kalc OpenCode Knowledge Loader ==="
echo "Knowledge dir: $KNOWLEDGE_DIR"
echo "Kalc dir: $CALC_DIR"
echo ""

# 1. Read and display the index
echo "--- INDEX ---"
cat "$KNOWLEDGE_DIR/INDEX.md"
echo ""

# 2. Read Kraft Standards
echo "--- KRAFT STANDARDS ---"
cat "$KNOWLEDGE_DIR/KRAFT_STANDARDS.md"
echo ""

# 3. Read Kalc audit
echo "--- AUDIT ---"
cat "$CALC_DIR/AUDIT.md"
echo ""

# 4. Read PRIVACY.md
echo "--- PRIVACY POLICY ---"
cat "$CALC_DIR/PRIVACY.md"
echo ""

# 5. Read CHANGELOG
echo "--- CHANGELOG ---"
cat "$CALC_DIR/CHANGELOG.md"
echo ""

# 6. Read session summary (Hermes knowledge gathered during session)
echo "--- SESSION SUMMARY ---"
cat "$KNOWLEDGE_DIR/HERMES_SESSION_SUMMARY.md"
echo ""

echo "=== Knowledge loaded ==="
echo "OpenCode now has full context of:"
echo "  - Kraft Standards (6 documents)"
echo "  - Kalc v2.2.0 audit (784 lines, score 83/100)"
echo "  - Kalc privacy policy"
echo "  - Kalc changelog"
echo "  - Full session summary (what was done, what remains)"
echo "  - GitHub repo: ssh://git@github.com/kedharsairam/kalc.git"
echo ""
echo "To resume work, open the Kalc project at: $CALC_DIR"
echo "Key files to read first:"
echo "  - app/src/main/java/com/kraft/calculator/presentation/CalculatorButton.kt"
echo "  - app/src/main/java/com/kraft/calculator/presentation/CalculatorDisplay.kt"
echo "  - app/src/main/java/com/kraft/calculator/ui/theme/Theme.kt"
echo "  - app/src/main/java/com/kraft/calculator/presentation/CalculatorScreen.kt"
echo "  - app/src/main/java/com/kraft/calculator/presentation/SettingsScreen.kt"
