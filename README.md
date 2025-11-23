
# Runnables

[![GitHub release](https://img.shields.io/github/v/release/Hihelloy-main/Runnables)](https://github.com/Hihelloy-main/Runnables/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Runnables** is a lightweight Paper/Folia-compatible plugin API that provides a drop-in replacement for `BukkitRunnable` with full support for:

- Folia async, region, and entity scheduling
- Paper synchronous and asynchronous tasks
- Task IDs and task cancellation
- Cross-platform compatibility (Paper/Bukkit/Folia)

This API allows you to write scheduled tasks in a **BukkitRunnable-style API** while automatically supporting Folia's threaded regions without rewriting your code.

---

## Dependency and repository

Add the following dependency and repository to your Maven project:

**Repository:**
```xml
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
````

**Dependency:**

```xml
<dependency>
    <groupId>com.github.Hihelloy-main</groupId>
    <artifactId>Runnables</artifactId>
    <version>v1.0</version>
    <scope>provided</scope>
</dependency>
````

---

## Supported Platforms

* Paper 1.20.6+
* Folia (threaded regions enabled)
* Bukkit/Spigot (async tasks supported via fallback)

---

## Usage

### Creating a task

Extend `PaperRunnable` just like `BukkitRunnable`:

```java
PaperRunnable task = new PaperRunnable() {
    @Override
    public void run() {
        // Your code here
        System.out.println("Task executed!");
    }
};
```

---

### Scheduling tasks

#### Global / synchronous tasks

```java
// Run immediately
task.runTask(plugin);

// Run after delay (ticks)
task.runTaskLater(plugin, 20L);

// Run repeating task (delay, period in ticks)
task.runTaskTimer(plugin, 10L, 20L);
```

#### Async tasks (works with Folia and Bukkit fallback)

```java
// Run immediately asynchronously
task.runAsync(plugin);

// Run async after delay
task.runAsyncLater(plugin, 20L);

// Run async repeating
task.runAsyncTimer(plugin, 10L, 20L);
```

#### Region-based scheduling (Folia only)

```java
// Run at a specific location
task.runAtLocation(plugin, location);

// Delayed location task
task.runAtLocationLater(plugin, location, 20L);

// Repeating location task
task.runAtLocationTimer(plugin, location, 10L, 20L);
```

#### Entity-based scheduling (Folia only)

```java
// Run at an entity
task.runAtEntity(plugin, entity);

// Run at entity with delayed start
task.runAtEntityLater(plugin, entity, 20L);

// Run repeating at entity
task.runAtEntityTimer(plugin, entity, 10L, 20L);

// With a retired callback when the task ends
task.runAtEntity(plugin, entity, () -> System.out.println("Task retired"));
```

---

### Task management

* **Get task ID**

```java
int id = task.getTaskId();
int foliaId = task.getFoliaTaskId();
int bukkitId = task.getBukkitTaskId();
```

* **Check if running**

```java
boolean running = task.isRunning();
```

* **Cancel a task**

```java
task.cancel(); // cancels this task
PaperRunnable.cancelTask(id); // cancels a task by ID
```

---

## Features

* Full **BukkitRunnable replacement** with Folia compatibility
* **Async scheduling** works on both Folia and vanilla Paper
* **Region scheduling** using Folia region scheduler
* **Entity scheduling** with optional retired callback
* Task IDs for both Folia and Bukkit tasks
* Automatic detection of Folia environment (`isFolia()`)

---

## Example

```java
public class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PaperRunnable runnable = new PaperRunnable() {
            @Override
            public void run() {
                getLogger().info("Hello from PaperRunnable!");
            }
        };

        // Run async after 20 ticks
        runnable.runAsyncLater(this, 20L);
    }
}
```

---

## License

MIT License © Hihelloy

---

## Notes

* **Folia region and entity scheduling only works on Folia**. Attempting to use these on vanilla Paper/Bukkit will throw an `IllegalStateException`.
* **Async tasks fallback on Bukkit** if Folia isn’t detected.
* 1 tick = 50ms. Async timers automatically convert ticks to milliseconds for Folia’s `AsyncScheduler`.

---

## API Reference

* `PaperRunnable`

  * `run()`
  * `runTask(plugin)`
  * `runTaskLater(plugin, delayTicks)`
  * `runTaskTimer(plugin, delayTicks, periodTicks)`
  * `runAsync(plugin)`
  * `runAsyncLater(plugin, delayTicks)`
  * `runAsyncTimer(plugin, delayTicks, periodTicks)`
  * `runAtLocation(plugin, location)`
  * `runAtLocationLater(plugin, location, delayTicks)`
  * `runAtLocationTimer(plugin, location, delayTicks, periodTicks)`
  * `runAtEntity(plugin, entity)`
  * `runAtEntity(plugin, entity, Runnable retired)`
  * `runAtEntityLater(plugin, entity, delayTicks)`
  * `runAtEntityLater(plugin, entity, delayTicks, Runnable retired)`
  * `runAtEntityTimer(plugin, entity, delayTicks, periodTicks)`
  * `runAtEntityTimer(plugin, entity, delayTicks, periodTicks, Runnable retired)`
  * `cancel()`
  * `cancelTask(int taskId)`
  * `getTaskId()`
  * `getBukkitTaskId()`
  * `getFoliaTaskId()`
  * `isRunning()`
  * `isFolia()`

```
```
