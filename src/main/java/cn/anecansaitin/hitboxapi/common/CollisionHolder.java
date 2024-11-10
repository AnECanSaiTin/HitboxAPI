package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.common.colliders.*;
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
        hurtBox.put("test", new OBB(new Vector3f(0, 1, 0), new Vector3f(1.5f, 1.5f, 1.5f), new Quaternionf()));
        hitBox.put("test", new Sphere(new Vector3f(0, 1, 0), 1));
    }

    private OBB getTestU(){
        return (OBB) hurtBox.get("test");
    }

    private Sphere getTestI(){
        return (Sphere) hitBox.get("test");
    }

    private boolean test(){
        return getTestU().isColliding(getTestI());
    }
}
