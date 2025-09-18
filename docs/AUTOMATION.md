# Baritone Automation

## Automation & GUI Features

The automation system extends Baritone with a powerful GUI-driven automation framework for common Minecraft tasks.

### Key Features

- **In-Game GUI** (Press G)
  - Overview Tab: Start/Stop/Pause automation plans
  - Plan Tab: Configure and select automation plans
  - Settings Tab: Mining strategy, Y-levels, search radius
  - Safety Tab: Configure failsafes (health, food, tools)

- **Automation Plans**
  - JSON-based plan configuration (`config/automation/*.plan.json`)
  - Pre-defined "Full Diamond" progression plan
  - Customizable steps and actions
  - Live plan progress tracking

- **Failsafe System**
  - Health/Hunger monitoring
  - Tool durability checks
  - Inventory management
  - Lava avoidance
  - Emergency pause/resume

### Commands

- `/auto start <plan>` - Start an automation plan
- `/auto stop` - Stop current automation
- `/auto pause/resume` - Pause/Resume automation
- `/auto status` - Show current status
- `/auto set <option> <value>` - Configure options

### Example Plan: Full Diamond

The included `full_diamond.plan.json` automatically progresses from spawn to full diamond gear:

1. Wood Collection: Gathers initial resources
2. Stone Age: Basic tools and coal
3. Iron Tech: Mining and processing
4. Diamond Hunt: Y11 branch mining
5. Gear Crafting: Full diamond equipment

### Configuration

Plans are stored in `config/automation/*.plan.json`. Example structure:

```json
{
  "name": "Plan Name",
  "version": 1,
  "steps": [
    {
      "id": "step_id",
      "actions": [
        {"type": "collect", "item": "...", "count": 1},
        {"type": "craft", "recipe": "...", "count": 1}
      ]
    }
  ],
  "failsafes": {
    "minFood": 6,
    "minHearts": 6,
    "toolMinDurabilityPct": 10
  }
}
```

### Settings

All settings are persistent and can be configured via GUI or commands:

- Mining Strategy: Branch/Strip/Tunnel
- Y-Level: Preferred mining height
- Search Radius: Resource search distance
- Safety Thresholds: Health/Food/Tools
- Torch Placement: Auto/Manual
- Movement: Sprint/Walk
