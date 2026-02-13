# UtilityX Core

## Commands

- `/ux`
  - Shows plugin information including version.

- `/repositories list`
- `/repositories add <url>`
- `/repositories remove <url>`
  - Permission: `utilityx.repositorys`

- `/addons`
  - Opens the addons GUI.
  - Permission: `utilityx.addons`

- `/addons reload`
  - Refreshes repository cache and updates open GUI views.
  - Permission: `utilityx.addons`

## Addon Tutorial

Addons must implement `de.utilityx.core.api.Addon`:

```java
public interface Addon {
   void onLoad(UtilityXCore core);
   void onAddonEnable();
   void onAddonDisable();
}
```

Minimal addon example:

```java
package de.utilityx.example;

import de.utilityx.core.UtilityXCore;
import de.utilityx.core.api.Addon;
import org.bukkit.Server;

public class ExampleAddon implements Addon {

    public static UtilityXCore core;
    private Server server;

    @Override
    public void onLoad(UtilityXCore core) {
        this.core = core;
        //and so on
        server = core.getServer();
    }

    @Override
    public void onAddonEnable() {

    }

    @Override
    public void onAddonDisable() {
        System.out.println( "Addon disabled");
    }
}
```

Register your addon with Java `ServiceLoader` by adding this file to your JAR:

- `META-INF/services/de.utilityx.core.api.Addon`

File content example:

```text
de.utilityx.example.ExampleAddon
```

Install steps:

1. Build your addon JAR.
2. Put it in `plugins/UXCore/addons`.
3. Start or restart the server.

## Repository Tutorial

Add repository URLs to `plugins/UXCore/config.yml`:

```yaml
repositories:
  - https://example.com/addons.json
```

Each repository should return JSON addon entries like:

```json
[
  {
    "name": "Example Addon",
    "description": "Adds example behavior",
    "author": "YourName",
    "item": "DIAMOND",
    "download": "https://example.com/example-addon.jar"
  }
]
```

Then use:

- `/repositories add <url>`
- `/repositories list`
- `/repositories remove <url>`
