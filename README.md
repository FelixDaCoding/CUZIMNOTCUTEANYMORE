# CAPSTONE-CSIT227-F2

📘 DETAILED SYSTEM DESCRIPTION

LEVELUP – DETAILED SYSTEM DESCRIPTION

LevelUp is a gamified fitness and wellness tracking system designed to motivate users through RPG-style progression, daily quests, automatic penalties, and reward unlocking. Inspired by progression systems similar to Solo Leveling, the platform turns real-life health habits into actionable “leveling” mechanics.

The system encourages users to complete daily tasks—such as completing workouts, eating healthy meals, or hitting activity targets—to increase their XP, advance their level, and maintain streak bonuses. The central mechanic is the Daily Challenge, which must be completed every day to avoid penalties that reduce XP or apply temporary debuffs to the user’s account.

⸻

💠 CORE SYSTEM CONCEPTS

1. User Progression
Each user starts at Level 1 with baseline XP. Users earn experience by:
	•	Completing daily challenges
	•	Logging workouts
	•	Logging meals
	•	Completing quests
	•	Maintaining streaks

As XP increases, the user levels up — unlocking new rewards, earning titles, and improving their account status.

Users who consistently fail daily challenges accumulate Penalty Points, decreasing performance in future XP gains or temporarily locking certain features.

⸻

2. Daily Challenge System (Main Feature)
Every day at midnight, the system assigns a new Daily Challenge to each user. Challenges vary by intensity and type, such as:
	•	“Burn 200 calories today.”
	•	“Complete a 20-minute workout.”
	•	“Log 2 healthy meals.”
	•	“Walk 5,000 steps.”

Daily Challenges must be completed before the day ends.
✔ Completing the daily challenge:
	•	Grants XP
	•	Increases the Streak
	•	May unlock a reward
	•	Boosts the user’s sense of progress

✖ Failing the daily challenge:
Automatically triggers a Penalty, such as:
	•	XP deduction
	•	Streak reset
	•	Temporary decrease in XP gain multiplier
	•	Time-bound penalty (e.g., “−20% XP gain for 12 hours”)

This mechanism promotes consistent daily engagement, similar to daily quests in RPG games.

⸻

3. Workouts Module
Users can log workouts, including:
	•	Workout name
	•	Duration
	•	Intensity
	•	Estimated calories burned

XP calculations are performed based on the difficulty and duration. Workouts can also count toward Daily Challenges or contribute to the completion of quests.

⸻

4. Meal Logging Module
To promote healthier eating habits, the system allows users to log meals with attributes such as:
	•	Meal type (breakfast/lunch/dinner/snack)
	•	Calorie count
	•	Whether it is classified as healthy

Healthy meals reward bonus XP, and unhealthy meals may provide no XP.

Meals may also be part of Daily Challenges (example: “Log 3 healthy meals today”).

⸻

5. Quests
Quests function as longer-term tasks compared to daily challenges.
Examples include:
	•	“Complete 5 workouts this week.”
	•	“Maintain a 7-day streak.”
	•	“Burn 5,000 calories in one month.”

Quests offer:
	•	XP
	•	Exclusive rewards
	•	Titles or account perks

Failing a quest may trigger an optional penalty, depending on the quest type.

⸻

6. Rewards System
Users receive rewards for completing milestones. Rewards may include:
	•	Badges
	•	Titles
	•	Boosters (XP multiplier for 24 hours)
	•	Cosmetic achievements
	•	Unlockable features

Rewards are stored in each user’s inventory once claimed. Some rewards unlock automatically upon reaching certain levels.

⸻

7. Streak System
The system track how many consecutive days the user completes Daily Challenges.
Each day the streak increases:
	•	XP gain multiplier increases
	•	Greater chance of unlocking special rewards

Failing a daily challenge:
	•	Resets the Streak
	•	Applies a Streak Penalty

High streaks create strong habit-reinforcement by rewarding consistency.

⸻

8. Penalty System (Failure Management)
Penalties are applied automatically when:
	•	The user fails a Daily Challenge
	•	The user fails a Quest
	•	The user breaks a Streak

Possible penalties include:
	•	XP deduction
	•	Streak reset
	•	Temporary debuffs (e.g., Reduced XP gain)
	•	Cooldown timers
	•	Increased difficulty of next Daily Challenge

Penalties escalate the more often the user fails, emphasizing habit-building and responsibility.

⸻

💠 OVERALL SYSTEM FLOW
	1.	User logs into LevelUp
	2.	System assigns a Daily Challenge
	3.	User logs workouts/meals or performs required tasks
	4.	System updates progress in real-time
	5.	User either:
✔ Completes the Daily Challenge → Gains XP, extends Streak
✖ Fails the Daily Challenge → Penalty is automatically applied
	6.	XP is evaluated → user may Level Up
	7.	New Challenges and Quests refresh automatically
	8.	User receives rewards for achievements

⸻

💠 SYSTEM SIGNIFICANCE

LevelUp provides a practical and innovative solution for:
	•	Behavior reinforcement
	•	Fitness adherence
	•	Daily habit formation
	•	Health data monitoring
	•	Motivation through gamification
