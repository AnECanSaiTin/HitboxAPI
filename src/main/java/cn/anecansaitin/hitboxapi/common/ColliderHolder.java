package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.common.colliders.*;

import java.util.HashMap;
import java.util.Map;

public class ColliderHolder {
    /**
     * 受击判定
     */
    public Map<String, ICollider> hurtBox = new HashMap<>();

    /**
     * 攻击判定
     */
    public Map<String, ICollider> hitBox = new HashMap<>();

    public BoxPoseStack poseStack = new BoxPoseStack();
}
