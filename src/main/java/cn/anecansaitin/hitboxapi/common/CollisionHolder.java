package cn.anecansaitin.hitboxapi.common;

import cn.anecansaitin.hitboxapi.colliders.ICollision;
import cn.anecansaitin.hitboxapi.colliders.Sphere;

public class CollisionHolder {
    public ICollision collision;

    public CollisionHolder() {
        collision = Sphere.EMPTY;
    }

    public CollisionHolder(ICollision collision) {
        this.collision = collision;
    }
}
