package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.common.colliders.Capsule;
import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class CollisionHolder {
    /**
     * 受击判定
     */
    public Map<String, ICollision> hurtBox = new HashMap<>();
    /**
     * 攻击判定
     */
    public Map<String, ICollision> hitBox = new HashMap<>();

    private void addTest() {
        hurtBox.put("test", new Capsule(new Vector3f(1, 0, 0), 0.5f, 1, new Quaternionf()));
        hitBox.put("test", new Capsule(new Vector3f(-1, 0, 0), 0.5f, 1, new Quaternionf()));
    }

    private Capsule getTestU(){
        return (Capsule) hurtBox.get("test");
    }

    private Capsule getTestI(){
        return (Capsule) hitBox.get("test");
    }

    private boolean test(){
        return getTestU().isColliding(getTestI());
    }
}
