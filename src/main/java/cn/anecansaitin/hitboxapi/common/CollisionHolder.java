package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.common.colliders.ICollision;

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
}
