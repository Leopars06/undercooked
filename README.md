# Undercooked (Restaurant Rush) — SC2002 Group Project

A turn-based command-line restaurant management game in Java developed across three evolutionary stages for **SC2002/SC2302 Object-Oriented Design & Programming**.

- **Repository URL:** https://github.com/Leopars06/undercooked

---

## 1. Build & Run Instructions

Ensure **JDK 11+** is installed (`javac -version`). From the project root directory, run:

```bash
# Enable UTF-8 emojis in Windows PowerShell (optional on macOS/Linux)
chcp 65001
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
# Compile all Java source files
javac -encoding UTF-8 *.java
# Run the game
java "-Dfile.encoding=UTF-8" undercookedApp
```

---

## 2. Game Overview & Rules

### Base Setting
- **Duration:** 18 turns maximum
- **Tables:** 2 tables
- **Customer Arrivals:** 1 customer arrives at the start of turns `1, 4, 7, 10, 13, 16` (6 customers in total, starting with `100` satisfaction)
- **Victory Conditions (End of Turn 18):**
  - At least **4 paid customers**, AND
  - Total revenue of at least **$45.00**, AND
  - Average satisfaction of paid customers of at least **60.0**

### Staff Roles
| Staff Role | Stage Introduced | Task Capacity per Turn | Responsibilities |
| :--- | :--- | :--- | :--- |
| **Waiter** | Stage 1 | At most 1 task | Seat a waiting customer, take a seated customer's order, or serve a `READY` order |
| **Chef** | Stage 1 | At most 1 prep unit | Advance a `PENDING` or `COOKING` order by 1 preparation unit |
| **Cashier** | Stage 1 | At most 1 payment | Automatically process payment for the oldest `READY_TO_PAY` customer |
| **Host** | Stage 3 | At most 1 seating | Automatically seat at most one waiting customer at a free table (in addition to the Waiter's task) |

### Customer Types (Stage 2)
| Customer Type | Waiting Decay / Turn | Bill Pricing | Special Behaviour |
| :--- | :--- | :--- | :--- |
| **Regular** (`RegularCustomer`) | `-8` satisfaction | Full price | 30% chance to order a `ComboMeal` in Stage 3; otherwise orders a base dish |
| **VIP** (`VIPCustomer`) | `-5` satisfaction | `10%` discount | Always orders a `ComboMeal` in Stage 3 |
| **Critic** (`CriticCustomer`) | `-12` satisfaction | Full price | Always orders a `ComboMeal` in Stage 3; upon payment, leaves a glowing review granting `+15` satisfaction to all waiting and seated customers |

### Menu Items & Combo Meals (Stage 1 & Stage 3)
| Index | Item | Type | Price | Prep Units |
| :--- | :--- | :--- | :--- | :--- |
| 0 | **Salad** | Base `Food` | `$9.00` | `1` |
| 1 | **Burger** | Base `Food` | `$15.00` | `1` |
| 2 | **Pasta** | Base `Food` | `$18.00` | `2` |
| 3 | **Salad & Burger Combo** | `ComboMeal` (90% of `$9 + $15`) | `$21.60` | `2` (`1 + 1`) |
| 4 | **Salad & Pasta Combo** | `ComboMeal` (90% of `$9 + $18`) | `$24.30` | `3` (`1 + 2`) |
| 5 | **Burger & Pasta Combo** | `ComboMeal` (90% of `$15 + $18`) | `$29.70` | `3` (`1 + 2`) |

### Happy Hour Policy (Stage 3)
- Can be activated **once per game** at the start of a turn and lasts for **2 turns** (that turn and the next).
- **Satisfaction Boost:** During each active turn, all waiting and unserved (`WAITING`, `SEATED`, `ORDERED`) customers recover **`+15` satisfaction** at turn start (capped at `100`).
- **20% Discount & Price Locking:** Orders taken while Happy Hour is active receive **20% off**. For VIP customers, the **10% VIP discount is applied first**, followed by the **20% Happy Hour discount** (`price * 0.9 * 0.8`, rounded to the nearest cent). The final bill is locked into `Order` when the order is taken.

---

## 3. Stage Snapshots & GitHub Comparisons

| Stage | Description | Git Tag | Branch | GitHub Compare Link |
| :--- | :--- | :--- | :--- | :--- |
| **Stage 1** | Basic restaurant operation (`Regular` customers, 3 menu items, `Waiter`, `Chef`, `Cashier`, 18-turn loop) | [`stage-1`](https://github.com/Leopars06/undercooked/tree/stage-1) | [`Stage-1`](https://github.com/Leopars06/undercooked/tree/Stage-1) | — |
| **Stage 2** | Customer behaviour extension (`VIPCustomer`, `CriticCustomer`, polymorphic patience decay & special effects) | [`stage-2`](https://github.com/Leopars06/undercooked/tree/stage-2) | [`Stage-2`](https://github.com/Leopars06/undercooked/tree/Stage-2) | [`stage-1...stage-2`](https://github.com/Leopars06/undercooked/compare/stage-1...stage-2) |
| **Stage 3** | New staff role & menu/pricing policy (`Host`, `ComboMeal`, `HappyHour`, order price locking) | [`stage-3`](https://github.com/Leopars06/undercooked/tree/stage-3) | [`Stage-3`](https://github.com/Leopars06/undercooked/tree/Stage-3) | [`stage-2...stage-3`](https://github.com/Leopars06/undercooked/compare/stage-2...stage-3) |

---

## 4. Evidence of Stage Transitions

### Transition 1: Stage 1 → Stage 2 ([Compare Diff](https://github.com/Leopars06/undercooked/compare/stage-1...stage-2))

#### New Files Added
- **`RegularCustomer.java`**: Concrete subclass of `Customer` representing standard customers (`-8` satisfaction per waiting turn, full price).
- **`VIPCustomer.java`**: Concrete subclass of `Customer` (`-5` satisfaction per waiting turn, `10%` discount on bill).
- **`CriticCustomer.java`**: Concrete subclass of `Customer` (`-12` satisfaction per waiting turn, grants `+15` satisfaction review bonus to waiting and seated customers upon payment).

#### Existing Files Modified (with Reasons)
- **`Customer.java`**: Converted from a concrete class to an `abstract` base class; declared `public abstract void loseSatisfaction()` so each customer subtype encapsulates its own patience decay rate; added `increaseSatisfaction(int amount)` (capped at 100) for the Critic bonus.
- **`Controller.java`**: Updated `arriveCustomers()` to spawn `RegularCustomer`, `VIPCustomer`, and `CriticCustomer` with guaranteed spawn tracking (`vipArrived`, `criticArrived`); updated `updateWaitingAndPatience()` to invoke `c.loseSatisfaction()` polymorphically; updated `processEatingAndPayments()` to handle Critic review bonuses and VIP discounts.

#### What Still Works
- All Stage 1 classes (`Staff`, `Waiter`, `Chef`, `Cashier`, `Table`, `Menu`, `Food`, `Order`, `UI`, `CustomerStatus`, `OrderStatus`) and the 6-step turn sequence continue to work without modification.

---

### Transition 2: Stage 2 → Stage 3 ([Compare Diff](https://github.com/Leopars06/undercooked/compare/stage-2...stage-3))

#### New Files Added
- **`Host.java`**: Extends `Staff` to seat at most one waiting customer per turn at an empty table in addition to the Waiter's task.
- **`ComboMeal.java`**: Extends `Food` (**Inheritance**) and holds two `Food` components `firstItem` and `secondItem` (**Composition**). Calculates preparation units as the sum of its two components and price as `90%` of the combined price rounded to the nearest cent.
- **`HappyHour.java`**: Encapsulates the once-per-game, 2-turn promotional pricing policy (`20%` discount multiplier `0.8` and `+15` start-of-turn satisfaction recovery for waiting/unserved customers).

#### Existing Files Modified (with Reasons)
- **`Menu.java`**: Registered the 3 two-item `ComboMeal` combinations (`Salad & Burger Combo` `$21.60` [2 units], `Salad & Pasta Combo` `$24.30` [3 units], `Burger & Pasta Combo` `$29.70` [3 units]) at indices 3–5 while preserving the 3 base items at indices 0–2.
- **`Order.java`**: Added `private final double finalPrice` and overloaded `Order(Customer, Food, boolean happyHourActive)` to lock the bill when the order is taken (applying the `10%` VIP discount first, then the `20%` Happy Hour discount second, rounded to the nearest cent) so earlier orders are unaffected when Happy Hour starts or ends.
- **`Waiter.java`**: Overloaded `takeOrder(Customer, Food, boolean happyHourActive)` to forward the current Happy Hour state when creating an `Order` while retaining backward compatibility with `takeOrder(Customer, Food)`.
- **`Controller.java`**: Added `Host` to `staffMembers` and `processHostSeating()` at the start of Step 2; added `promptHappyHourActivation()`, `applyHappyHourSatisfactionRecovery()`, and `happyHour.endTurn()`; documented and implemented the combo selection rule in `executeWaiterTakeOrder()` (VIPs and Critics always order a `ComboMeal`; Regular customers have a 30% chance); updated `processEatingAndPayments()` to charge `order.getFinalPrice()`.
- **`UI.java`**: Overloaded `displayState(...)` to display Happy Hour availability, active status, and remaining turns at the start of every turn.

#### What Still Works
- `Chef`, `Cashier`, `Staff`, `Table`, `Food`, `Customer`, `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `CustomerStatus`, and `OrderStatus` required **zero changes** in Stage 3. Because `ComboMeal` is a subclass of `Food` and `Host` is a subclass of `Staff`, existing kitchen cooking, serving, table occupancy, and staff task reset logic work seamlessly.

---

## 5. AI Communication History Links

- **Stage 1 (`Edalene Hiang`):** [`ai-history/Edalene_Hiang`](https://github.com/Leopars06/undercooked/tree/Stage-1/ai-history/Edalene_Hiang)
- **Stage 2 (`Lee Yong Qian / yqq808-eng`):** [`ai-history/Lee_Yong_Qian`](https://github.com/Leopars06/undercooked/blob/Stage-2/ai-history/Lee_Yong_Qian)
- **Stage 3 (`Li Zhe Le / Leopars06`):** [`ai-history/Li_Zhe_Le`](https://github.com/Leopars06/undercooked/tree/Stage-3/ai-history/Li_Zhe_Le)

---

## 6. Recorded Presentation Video & Timestamps

- **Video Link / File:** [Watch 15-Min Presentation Video](https://youtu.be/gZQDXXrq2aA)

| # | Required Segment (Section 6.2 & Slide 11) | Timestamp |
| :--- | :--- | :--- |
| 1 | Edalene - Stage 1 Class Diagram, Boundary–Control–Entity (BCE) Architecture, Encapsulation & SRP | `00:00 - 02:24` |
| 2 | Yong Qian - Stage 2 Class & Sequence Diagrams, Abstraction, Inheritance, Polymorphism, OCP & LSP | `02:24 - 05:53` |
| 3 | Zhe Le - Stage 3 Class Diagram (`ComboMeal`, `Host`, `HappyHour`), Composition, Overloading, Trade-offs & Stage Tag Comparisons | `05:53 - 09:58` |
| 4 | Yu Xuan - Live CLI Demo: Launching Project, Staff Constraints, `READY → SERVED → Payment`, VIP/Critic & Stage 3 Features, Victory & Defeat Runs | `09:58 - 14:53` |

