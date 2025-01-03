# Hitbox API
[中文](README_ZH.md)

**Adding more common collision boxes for Minecraft**

- Oriented Bounding Box (OBB)
- Sphere
- Capsule
- Ray
- Compound
  - Multiple collision boxes can be combined into one.
  - Composite collision boxes can be nested.

### How to Use

In any place where collision detection is needed, create a collision box object and use the methods in [ColliderUtil](src/main/java/cn/anecansaitin/hitboxapi/api/common/collider/ColliderUtil.java) to determine if a collision has occurred.

For entities (Entity), you can achieve collision box caching by attaching **HitboxDataAttachments#COLLISION**. The cached collision box can be rendered by pressing **F3 + B**. Note that this Data does not have a persistence implementation.

### Performance

Testing Method
- JMH Benchmark

Testing Environment
- CPU: AMD R5 5600G
- Memory: 32GB
- JDK: Microsoft OpenJDK 21.0

Testing Settings
- Warm-up Rounds: 5
- Test Rounds: 5

Testing Items
- Collision detection of the same type of collision box

|         Collision Box         |     Score      |     Error     | Unit  |
|:-----------------------------:|:--------------:|:-------------:|:-----:|
|   Axis-Aligned Bounding Box   | 3561837865.930 | 157982649.213 | ops/s |
|            Capsule            |  22350681.198  |  238993.658   | ops/s |
|        Rotated Capsule        |  16842309.523  |  103959.918   | ops/s |
|     Oriented Bounding Box     |  5528493.224   |   64548.996   | ops/s |
| Rotated Oriented Bounding Box |  4648963.750   |  111369.535   | ops/s |
|            Sphere             | 119556589.705  |  1711146.560  | ops/s |

Note: Higher scores indicate better performance. **Rotated** means the collision box is rotated and the relevant vectors are recalculated.