package cn.anecansaitin.hitboxapi;

public interface ICollision {
    boolean isColliding(ICollision other);

    Collision getType();
}
