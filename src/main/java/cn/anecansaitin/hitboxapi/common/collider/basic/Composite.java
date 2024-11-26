package cn.anecansaitin.hitboxapi.common.collider.basic;

import cn.anecansaitin.hitboxapi.api.common.collider.ICollider;
import cn.anecansaitin.hitboxapi.api.common.collider.IComposite;

import java.util.List;

public class Composite<C extends ICollider<T, D>, T, D> implements IComposite<C, T, D> {
    private final List<C> colliders;
    private boolean disable;

    public Composite(List<C> colliders) {
        this.colliders = colliders;
    }

    @Override
    public int getCollidersCount() {
        return colliders.size();
    }

    @Override
    public C getCollider(int index) {
        return colliders.get(index);
    }

    @Override
    public void setCollider(int index, C collider) {
        colliders.set(index, collider);
    }

    @Override
    public void addCollider(C collider) {
        colliders.add(collider);
    }

    @Override
    public void removeCollider(int index) {
        colliders.remove(index);
    }

    @Override
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean disable() {
        return disable;
    }

    @Override
    public String toString() {
        return "Composite{" +
                "colliders=" + colliders +
                ", disable=" + disable +
                '}';
    }
}
