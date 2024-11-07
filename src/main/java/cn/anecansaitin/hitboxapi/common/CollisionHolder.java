package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;
import cn.anecansaitin.hitboxapi.common.colliders.OBB;
import cn.anecansaitin.hitboxapi.common.colliders.Sphere;
import net.minecraft.world.phys.AABB;
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
        hurtBox.put("test", new Sphere(new Vector3f(0, 0, 0), 1));
        hitBox.put("test", new AABB(0, 0, 0, 1, 1, 1));
    }

    private Sphere getTestU(){
        return (Sphere) hurtBox.get("test");
    }

    private AABB getTestI(){
        return (AABB) hitBox.get("test");
    }

    private boolean test(){
        return getTestU().isColliding(getTestI());
    }
}
