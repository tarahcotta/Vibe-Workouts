# Vitality Strength — AI Workout Tracker for Women's Longevity

An AI-powered strength training assistant built using **Google AI Studio**. *Vitality Strength* delivers personalized, longevity-focused workout programs tailored for women—prioritizing bone density, muscle retention, joint health, and functional strength for healthy aging.

---

## 📌 Project Overview

As women age, maintaining lean muscle mass, joint mobility, and bone density becomes critical for long-term independence and vitality. Traditional workout apps often rely on low-load cardio or generic routines that lack the stimulus required for bone mineral density and muscle hypertrophy.

**Vitality Strength** bridges this gap by leveraging Google's Gemini models via AI Studio to act as an expert strength and longevity coach. It provides structured progressive overload, tracks workout logs, and intelligently adapts training volume over time.

---

## ✨ Key Features

- **Bone Density & Load-Bearing Focus:** Prioritizes multi-joint compound movements (squats, deadlifts/hinges, presses, carries) proven to stimulate bone adaptation.
- **Longevity & Functional Mobility:** Integrates balance, single-leg stability, grip strength, and postural health into every weekly cycle.
- **Personalized Adaptive Logic:** Adjusts volume, weight recommendations, and RPE based on user feedback, fatigue, and recovery needs.
- **Midlife & Hormonal Awareness:** Tailored considerations for pre-menopausal, perimenopausal, and post-menopausal strength needs.
- **Structured Tracking & Logging:** Clean, data-ready format for logging sets, reps, weight used, and rate of perceived exertion (RPE).
- **Zero "Pink Dumbbell" Stereotypes:** Grounded in real exercise science—empowering women to lift heavy with confidence and precision.

---

## 🛠️ Repository Structure

```text
├── system-prompts/
│   ├── system_prompt_v1.md       # Core AI Studio system prompt
│   └── adaptive_coaching_rules.md # Rules for load progression & deloads
├── templates/
│   ├── initial_assessment.json    # User onboarding survey layout
│   └── workout_log_schema.json   # Structural output schema for logs
├── docs/
│   ├── longevity_science.md       # Research references on bone health & strength
│   └── app_architecture.md        # Technical breakdown & API integration guide
└── README.md
```

---

## 🚀 Quick Start Guide

### 1. Set Up Google AI Studio

1. Navigate to [Google AI Studio](https://aistudio.google.com/).
2. Create a new **Chat Prompt** or **System Instructions** session.
3. Select the latest **Gemini** model (e.g., `Gemini 1.5 Pro` or `Gemini 1.5 Flash`).

### 2. Add System Prompt

Copy the core system instructions into the **System Instructions** field in AI Studio:

```markdown
You are an expert strength coach specializing in women's health, bone density, and functional longevity. Your job is to build personalized, strength-focused workout routines designed to support healthy aging, preserve lean muscle, and improve functional strength.

## Core Directives
1. Prioritize heavy compound movements, joint safety, and progressive overload.
2. Structure output into structured tables: [Exercise | Primary Goal | Sets | Reps | Rest | Focus/Cues].
3. Incorporate single-leg balance, grip strength, and core stability into routines.
4. Adapt routines dynamically based on user logs and reported fatigue.
```

### 3. Run Initial User Onboarding

Paste the following sample user intake prompt to start generating routines:

```text
Hi! I'm a 48-year-old female, intermediate lifter. I have access to dumbbells and resistance bands at home. My goal is to build bone density and stay strong as I age. I can train 3 days a week for 45 minutes.
```

---

## 📊 Sample Output Format

When interacting with the AI, workouts are structured into clear, actionable tables:

| Exercise | Primary Goal | Sets | Reps | Rest | Focus / Cues |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Goblet Squat** | Bone Density & Quad Strength | 3 | 8–10 | 90s | Keep chest tall, drive up through full foot |
| **Single-Leg Romanian Deadlift** | Balance & Posterior Chain | 3 | 8 per side | 60s | Hinge at hips, maintain flat back |
| **Dumbbell Overhead Press** | Shoulder & Upper Body Strength | 3 | 8–10 | 90s | Brace core, avoid arching lower back |
| **Farmer's Carry** | Grip Strength & Core Stability | 3 | 40 meters | 60s | Walk tall, heavy load, smooth cadence |

---

## 🧬 Science & Philosophy

- **Wolf's Law:** Bone adapts to the loads under which it is placed. High-impact or heavy load-bearing exercises are necessary to trigger osteogenesis.
- **Sarcopenia Prevention:** Age-related muscle loss accelerates after age 40. Progressive resistance training is the single most effective intervention to maintain functional independence.
- **RPE-Based Auto-regulation:** Using Rate of Perceived Exertion ensures users train hard enough to stimulate adaptation while minimizing injury risk.

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve prompt performance, submit schema updates, or refine coaching logic:

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/prompt-enhancement`).
3. Commit your changes (`git commit -m 'Add perimenopause volume adjustments'`).
4. Push to the branch (`git push origin feature/prompt-enhancement`).
5. Open a Pull Request.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
